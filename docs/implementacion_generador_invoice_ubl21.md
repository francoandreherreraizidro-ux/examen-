# Generador XML Invoice UBL 2.1

## Objetivo y alcance

Implementa el adaptador `InvoiceXmlGenerator`, que transforma un objeto inmutable `Invoice` del dominio en un documento XML UBL Invoice 2.1. Es una implementación del alcance aprobado en [SPEC-003](../specs/features/003-generador-invoice-ubl21.md).

El generador no persiste datos, no expone HTTP ni envía, firma o autentica documentos ante SUNAT/OSE. El resultado representa UBL genérico: XML bien formado y válido contra el XSD no implica cumplimiento de un perfil peruano ni aceptación por SUNAT.

## Lenguaje y herramientas

- Java 21 es el nivel de API/bytecode definido en Maven (`maven.compiler.release=21`).
- Validación final ejecutada con Eclipse Temurin JDK/Javac 21.0.13+9 LTS.
- Maven 3.9.9.
- JUnit Jupiter 5.11.4.
- StAX `XMLStreamWriter` y JAXP del módulo estándar `java.xml`; sin dependencia XML de terceros y sin Spring Boot.

## Arquitectura

```text
com.comercial.domain.billing.Invoice
                 |
                 v
com.comercial.adapters.ubl.InvoiceXmlGenerator
                 |
                 v
           XML Invoice 2.1
```

Los records del dominio no importan APIs XML. El adaptador solo transforma y serializa los datos suministrados. El ejemplo ejecutable está separado bajo `com.comercial.examples`.

## Modelo Invoice implementado

| Tipo | Campos | Reglas de construcción |
|---|---|---|
| `Invoice` | identificador, `LocalDate`, código de moneda, supplier, customer, líneas, `TaxTotal` opcional y `MonetaryTotal` | Identidad/moneda no vacías; fecha, partes y totales requeridos; una o más líneas. La lista se copia de forma inmutable. |
| `Party` | nombre | Requerido y no vacío; el rol sale de la posición supplier/customer en UBL. |
| `InvoiceLine` | id, cantidad, nombre del item, precio unitario e importe de línea | Campos requeridos. Cantidad y montos son `BigDecimal`. |
| `TaxTotal` | importe tributario | Opcional; `BigDecimal` provisto por quien invoca el generador. |
| `MonetaryTotal` | importe de líneas, total sin tributos, total con tributos, total pagable | Los cuatro importes requeridos como `BigDecimal`; el serializador los copia. |

No se validan signos, escalas, aritmética entre precio/cantidad/totales ni catálogos de moneda. No se agrega código de producto, identificación fiscal, dirección ni `TaxSubtotal`, categoría, esquema, código o tasa de impuesto porque no hay un perfil y una fuente aprobados para esos datos. Los montos del ejemplo/pruebas son ficticios y no representan cálculo fiscal.

## Funcionamiento y estructura XML

`InvoiceXmlGenerator.generate(Invoice)` usa StAX para escribir la declaración XML 1.0 UTF-8 y la secuencia de elementos Invoice. Los valores textuales se escriben como texto XML (con escape de caracteres especiales); caracteres fuera del rango permitido por XML 1.0 se rechazan con `IllegalArgumentException`. El modelo rechaza datos obligatorios nulos o vacíos antes de emitir un documento.

Namespaces:

- Invoice: `urn:oasis:names:specification:ubl:schema:xsd:Invoice-2`
- CBC: `urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2`
- CAC: `urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2`

La secuencia utilizada, verificada con el XSD, es: `UBLVersionID`, `ID`, `IssueDate`, `DocumentCurrencyCode`, `AccountingSupplierParty`, `AccountingCustomerParty`, `TaxTotal` opcional, `LegalMonetaryTotal` e instancias `InvoiceLine`. Cada `Amount` y `PriceAmount` recibe el atributo `currencyID` igual al código documental. Las líneas mantienen el orden de entrada.

## XSD oficial UBL 2.1

Se incorporó el árbol completo de 79 XSD OASIS en [tests/resources/ubl/2.1/xsd](../tests/resources/ubl/2.1/xsd), preservando rutas de `maindoc/` y `common/` para resolver imports locales. La prueba usa `SchemaFactory` y limita los esquemas externos a `file`; no se resuelven recursos de red desde los documentos XML.

Fuente oficial: [OASIS UBL 2.1 release](https://docs.oasis-open.org/ubl/os-UBL-2.1/) y [paquete UBL-2.1.zip](https://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.zip). SHA-256 del archivo ZIP descargado: `60B80D76394A8A2ADD90723ECB8E0E2E9D826775DE9749DF37A72D60703F86ED`. El detalle de procedencia está en [README de recursos XSD](../tests/resources/ubl/2.1/README.md).

El XSD comprueba estructura y tipos genéricos UBL. No valida por sí solo reglas comerciales, perfil SUNAT, códigos fiscales ni aceptación del comprobante.

## Pruebas

Las unitarias en `tests/unit/` parsean el XML con DOM namespace-aware y verifican:

- raíz, namespaces y XML bien formado;
- identidad, fecha y moneda;
- emisor, adquirente, una y varias líneas;
- cantidad, precio, importes, atributos de moneda y totales;
- `TaxTotal` opcional sin clasificación fiscal inventada;
- escape de caracteres y texto recuperado;
- serialización decimal bajo locale distinto;
- rechazo de campos requeridos ausentes y caracteres XML 1.0 inválidos.

La prueba complementaria en `tests/integration/` valida documentos generados y el XML de ejemplo contra el XSD OASIS oficial.

## Ejecutar

Desde la raíz del repositorio, con JDK y Maven instalados:

```powershell
mvn test
```

El resultado probado en esta máquina fue **9 pruebas: 9 exitosas, 0 fallidas, 0 errores, 0 omitidas** (7 unitarias y 2 validaciones estructurales), con Maven 3.9.9 y Eclipse Temurin JDK 21.0.13+9. Se ejecutó una compilación limpia:

```powershell
mvn -B clean test
```

Java 21 y Maven 3.9.9 se prepararon temporalmente para esa ejecución; no se añadieron al repositorio. En un equipo con Java 21 y Maven instalados en `PATH`, basta con `mvn test`.

Para generar de nuevo el XML ilustrativo:

```powershell
mvn -q -DskipTests package
java -cp target\classes com.comercial.examples.InvoiceXmlExample docs\examples\invoice-ubl21-ejemplo.xml
```

El archivo incluido es [invoice-ubl21-ejemplo.xml](./examples/invoice-ubl21-ejemplo.xml); una prueba compara su contenido con la salida real del generador.

## Archivos de implementación

- `pom.xml`
- `src/main/java/com/comercial/domain/billing/Invoice.java`
- `src/main/java/com/comercial/domain/billing/Party.java`
- `src/main/java/com/comercial/domain/billing/InvoiceLine.java`
- `src/main/java/com/comercial/domain/billing/TaxTotal.java`
- `src/main/java/com/comercial/domain/billing/MonetaryTotal.java`
- `src/main/java/com/comercial/adapters/ubl/InvoiceXmlGenerator.java`
- `src/main/java/com/comercial/examples/InvoiceXmlExample.java`
- `tests/unit/com/comercial/ubl/InvoiceXmlGeneratorTest.java`
- `tests/integration/InvoiceUblSchemaValidationTest.java`
- `tests/resources/ubl/2.1/xsd/` y su README de procedencia
- `docs/examples/invoice-ubl21-ejemplo.xml`

## Limitaciones conocidas

- Implementa un subconjunto deliberado de UBL Invoice 2.1 y valida contra el XSD genérico; no genera un perfil SUNAT.
- No emite identificación fiscal, dirección, unidad/código de unidad, categorías de impuestos o subtotales fiscales.
- No calcula ni reconcilia cantidades, precios, impuestos o totales; son datos de entrada.
- El XML generado puede ser estructuralmente válido sin ser aceptable tributariamente.
