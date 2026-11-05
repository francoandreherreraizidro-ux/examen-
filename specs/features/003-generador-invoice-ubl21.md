# SPEC-003: Generador XML Invoice UBL 2.1

---

## Metadatos

| Campo | Valor |
|---|---|
| ID | `SPEC-003` |
| Estado | `IMPLEMENTED` — criterios implementados y verificados |
| Autor | Borrador de diseño |
| Revisor | Responsable del proyecto/examen; aprobación explícita recibida el 2026-11-04 |
| Fecha | 2026-11-04 |
| Hito | Entrega académica indicada para 2026-11-05 |
| Requisitos relacionados | `RF-CPE-001`, `RF-VAL-001`, `RF-TRZ-001`, `RNF-DOC-001`, `RNF-ARQ-001`, `RNF-SDD-001` |
| Documentos relacionados | [Análisis de requisitos](../../01_Analisis_Requisitos/analisis_requisitos.md), [modelo de dominio](../../02_Modelo_Dominio/modelo_dominio.md), [DTOs y Schemas](../../03_DTOs_Schemas/dtos_schemas.md), [arquitectura](../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md), [SPEC-002](./002-sistema-comercial-facturacion.md), [matriz normativa](../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md) |
| API relacionada | No propone ni requiere endpoints HTTP nuevos |

## 1. Resumen y problema

### 1.1 Problema

El examen solicita considerar UBL Invoice 2.1 para comprobantes electrónicos y demostrar la generación de XML mediante pruebas unitarias. Los documentos actuales describen `Venta` y `ComprobanteElectronico` de forma conceptual, pero no especifican un objeto de factura completo ni los datos fiscales y monetarios necesarios para construir un Invoice. Los contratos actuales permanecen en `DRAFT` y no contienen esos datos.

Antes de la aprobación no existían lenguaje, framework, gestor de dependencias, biblioteca XML, runner ni contrato Invoice definidos por el repositorio. El responsable aprobó explícitamente el 2026-11-04 la base descrita en la sección 9, el contrato mínimo de la sección 5 y la incorporación de XSD oficial para validación estructural. La aprobación habilita solamente este generador UBL genérico; no aprueba los DTO/API, el SPEC-002, un perfil SUNAT ni integración externa.

### 1.2 Solución aprobada

Diseñar un componente aislado que reciba una representación de dominio `Invoice`, la transforme a XML UBL Invoice 2.1 y permita inspeccionar y probar el XML generado sin acceso a PostgreSQL, controladores HTTP o servicios SUNAT.

El generador serializa datos provistos por el modelo; no inventa información tributaria, tasas, códigos, importes, redondeos ni reglas nacionales. La salida representa UBL genérico y no equivale por sí misma a cumplimiento o aceptación de SUNAT.

### 1.3 Fuera de alcance

- Envío, firma, autenticación, consulta o reintento contra SUNAT/OSE.
- Declarar que el documento cumple un SEE o un perfil peruano.
- Elegir tipos de comprobantes adicionales, códigos tributarios, tasas o catálogos no aprobados.
- Calcular precios, descuentos, tributos, totales o reglas de redondeo.
- Acceder a PostgreSQL, exponer endpoints, crear controladores o implementar interfaces de usuario.
- Cambiar los requisitos originales, SPEC-002, contratos API o documentos históricos.
- Añadir dependencias antes de inventariar las disponibles y recibir autorización si se necesita instalar alguna.

## 2. Usuario y escenarios de uso

### 2.1 Actor

- **Consumidor del generador:** caso de uso interno que dispone de una factura completa y solicita su representación XML. No se asigna a un rol de seguridad ni a un endpoint.

### 2.2 Historias

- **US-01:** Como consumidor interno, quiero convertir una factura de dominio completa a XML Invoice UBL 2.1 para revisar y transportar el documento por un flujo que se definirá separadamente.
- **US-02:** Como responsable de calidad, quiero verificar la estructura y los valores del XML mediante parseo y pruebas unitarias para detectar errores de transformación antes de cualquier integración externa.

## 3. Requisitos funcionales de esta especificación

Estos identificadores `SPEC-003-FR-*` son locales a este borrador; no reemplazan ni crean códigos `RF-*` del análisis de requisitos.

| ID | Requisito de esta especificación | Relación | Estado |
|---|---|---|---|
| `SPEC-003-FR-01` | El generador recibirá un objeto de dominio `Invoice` y devolverá un documento XML bien formado con raíz `Invoice` en `urn:oasis:names:specification:ubl:schema:xsd:Invoice-2`. | `RF-CPE-001`, `RNF-DOC-001` | Aprobado; XML también se validará contra el XSD oficial en pruebas. |
| `SPEC-003-FR-02` | El XML declarará los namespaces UBL necesarios, incluyendo CommonBasicComponents (`cbc`) y CommonAggregateComponents (`cac`) donde correspondan. | `RNF-DOC-001` | Aprobado. |
| `SPEC-003-FR-03` | Los datos de emisor, cliente, líneas, cantidades, precios, moneda, importes y tributos que estén presentes en el objeto se mapearán a los elementos UBL correspondientes, sin derivar valores que no proporcione el dominio. | `RF-CPE-001`, `RF-VAL-001` | Aprobado según el contrato mínimo de la sección 5. |
| `SPEC-003-FR-04` | Cada línea recibida se representará una sola vez, conservando el orden o identificador que defina el contrato aprobado. | `RF-CPE-001`, `RF-TRZ-001` | Aprobado; orden de lista del dominio, identificador de línea obligatorio. |
| `SPEC-003-FR-05` | El serializador escapará caracteres XML usando una API XML adecuada; no se concatenarán valores de usuario directamente como marcado. | `RF-VAL-001` | Aprobado; StAX `XMLStreamWriter`. |
| `SPEC-003-FR-06` | Los importes y cantidades usarán tipos decimales exactos del lenguaje seleccionado y formato léxico independiente de la configuración regional. El generador no realizará redondeos comerciales o tributarios. | `RF-VAL-001` | Aprobado; Java `BigDecimal`, representación decimal sin redondeo. |
| `SPEC-003-FR-07` | Si falta un dato marcado obligatorio por el contrato aprobado o por el XSD aplicable, el generador devolverá un error explícito y no generará un documento parcial o aparentemente exitoso. | `RF-VAL-001` | Aprobado; los campos requeridos del contrato se validan en construcción. |
| `SPEC-003-FR-08` | Se incluirá una factura ilustrativa de prueba, identificada como datos sintéticos, junto con pruebas que analicen el XML y comprueben estructura y valores. | `RNF-SDD-001` | Aprobado. |

## 4. Requisitos de calidad y restricciones

| ID | Requisito |
|---|---|
| `SPEC-003-NFR-01` | El dominio no dependerá de tipos XML, namespaces, bibliotecas de serialización ni infraestructura. El mapeo UBL quedará en un adaptador/componente de salida aislable. |
| `SPEC-003-NFR-02` | Las pruebas unitarias verificarán el documento parseado y sus elementos/valores, no únicamente que el método retorne una cadena. |
| `SPEC-003-NFR-03` | La implementación usará Java 21 como nivel de compilación, Maven, APIs JAXP/StAX del JDK y JUnit Jupiter 5; no añadirá Spring Boot ni dependencias XML de terceros. |
| `SPEC-003-NFR-04` | El componente seguirá aislado según MVC/hexagonal: dominio sin dependencia XML, generador como adaptador de salida, sin base de datos, controlador ni comunicación externa. |
| `SPEC-003-NFR-05` | Validar contra el paquete oficial UBL 2.1 será una comprobación estructural complementaria. No equivale a validar reglas nacionales ni a obtener aceptación SUNAT/OSE. |
| `SPEC-003-NFR-06` | El documento de ejemplo y sus datos identificatorios serán explícitamente ficticios; no se usarán datos fiscales reales. |

Los requisitos base conservan su certeza original: `RF-CPE-001`, `RF-VAL-001`, `RF-TRZ-001`, `RNF-DOC-001`, `RNF-ARQ-001` y `RNF-SDD-001` no se amplían ni se modifican por este borrador.

## 5. Modelo de dominio y contrato de entrada aprobado

### 5.1 Brecha actual

El modelo vigente tiene `Venta`, `DetalleVenta` y `ComprobanteElectronico`, pero:

- los importes, precios, moneda e impuestos de venta son opcionales o no están definidos;
- el detalle de venta actual requiere producto y cantidad, pero no descripción contractual de línea, precio ni tributos suficientes para factura;
- cliente/proveedor usa datos genéricos opcionales, sin contrato aprobado de identidad fiscal, dirección o rol como emisor;
- `ComprobanteElectronico` representa estado y referencias, no el documento fiscal completo ni sus totales.

En consecuencia, no existe hoy información suficiente para construir un Invoice genérico completo sin inventar datos.

### 5.2 Contrato de entrada aprobado

Se aprobó un modelo de entrada `Invoice` independiente del DTO de venta y del estado de integración. El contrato de esta entrega es el mínimo necesario para serializar UBL genérico:

- **Invoice:** `id` no vacío; `issueDate: LocalDate`; `currencyCode` no vacío; emisor y adquirente; al menos una línea; totales; total de tributos opcional.
- **Party:** nombre no vacío. No contiene identificador fiscal ni dirección en este alcance.
- **InvoiceLine:** identificador y nombre del ítem no vacíos; cantidad, precio unitario e importe de línea como `BigDecimal`.
- **TaxTotal:** importe como `BigDecimal`, opcional. Se serializa únicamente como `cac:TaxTotal/cbc:TaxAmount`; no se asigna categoría, esquema, código, tasa ni subtotal fiscal.
- **MonetaryTotal:** importes provistos como `BigDecimal`: total de líneas, total sin tributos, total con tributos y total pagable. Todos son entradas; el generador no deriva ni reconcilia sus valores.
- **Importes:** cada `cbc:Amount` emitido usa `currencyID` igual al código de moneda del Invoice. No se realiza validación contra catálogo ni normalización distinta de exigir campo no vacío.

La obligatoriedad descrita es la del contrato interno de este generador, no una afirmación de obligatoriedad fiscal SUNAT. Las validaciones del modelo rechazan referencias nulas, texto requerido vacío, totales o cantidades/importes ausentes y facturas sin líneas. No se imponen signos, límites de cantidad, escalas, relaciones aritméticas ni reglas tributarias no aprobadas. Texto que no sea representable en XML 1.0 se rechaza al generarse, con error explícito.

`Invoice` es un modelo de entrada independiente; no cambia `Venta` para que dependa de XML. El origen/mapeo entre venta y factura queda fuera de esta entrega, conservando separada cualquier futura salida de emisión/transmisión.

### 5.3 Correspondencia del contrato con UBL

En UBL, `SupplierParty` y `CustomerParty` son roles documentales que contienen una `Party`; no implican agregar entidades comerciales distintas del `Proveedor` y `Cliente` actuales.

| Tipo conceptual | Responsabilidad / relación aprobada | Atributos del contrato de esta entrega | Obligatoriedad / validación |
|---|---|---|---|
| `Invoice` | Raíz de dominio; compone identidad, fecha, moneda, partes, líneas, tributos opcionales y totales. | `id: String`; `issueDate: LocalDate`; `currencyCode: String`; `supplier/customer: Party`; `lines: List<InvoiceLine>`; `taxTotal: TaxTotal?`; `monetaryTotal: MonetaryTotal`. | `ID`, fecha, supplier, customer, legal total y una o más invoice lines obligatorios en XSD. El contrato exige todos los componentes citados. |
| `Party` | Datos de una parte; el rol emisor/adquirente se representa por la ubicación del agregado UBL. | `name: String`. | No vacío por contrato; se mapea a `cac:Party/cac:PartyName/cbc:Name`. Sin identificación ni dirección en esta entrega. |
| `Currency` | Moneda documental. | `code: String`. | No vacío; va a `cbc:DocumentCurrencyCode` y a atributo `currencyID` de cada importe. No se aplica catálogo. |
| `InvoiceLine` | Línea comercial en el orden de la lista. | `id`, `itemName: String`; `quantity`, `unitPrice`, `lineExtensionAmount: BigDecimal`. | Los cinco atributos son obligatorios por contrato. Mapea ID, InvoicedQuantity, LineExtensionAmount, Item/Name y Price/PriceAmount. Sin unidad/código de producto. |
| `TaxTotal` | Total tributario recibido sin clasificación fiscal. | `amount: BigDecimal`. | Opcional; se mapea a TaxTotal/TaxAmount. `TaxSubtotal`, categoría, esquema, código y tasa quedan excluidos por falta de perfil y fuente autorizados. |
| `MonetaryTotal` | Totales provistos por el consumidor. | `lineExtensionAmount`, `taxExclusiveAmount`, `taxInclusiveAmount`, `payableAmount: BigDecimal`. | Obligatorios por contrato; el XSD exige PayableAmount y permite los otros tres. No se recalculan ni comparan entre sí. |

`BigDecimal` representa decimales sin aproximación binaria; no se redondean los valores. Las cantidades se serializan sin exponentes y con el valor decimal recibido.

### 5.4 Fuentes UBL y XSD requeridos

Fuentes oficiales consultadas:

- [OASIS UBL 2.1 Standard (04-11-2013)](https://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html).
- [Índice oficial del paquete OASIS UBL 2.1](https://docs.oasis-open.org/ubl/os-UBL-2.1/), que publica `UBL-2.1.zip` y el directorio `xsd/`.
- [XSD principal Invoice 2.1](https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/maindoc/UBL-Invoice-2.1.xsd).
- [XSD Common Aggregate Components](https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/common/UBL-CommonAggregateComponents-2.1.xsd), [Common Basic Components](https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/common/UBL-CommonBasicComponents-2.1.xsd) y [Common Extension Components](https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/common/UBL-CommonExtensionComponents-2.1.xsd).

El XSD principal importa directamente `UBL-CommonAggregateComponents-2.1.xsd`, `UBL-CommonBasicComponents-2.1.xsd` y `UBL-CommonExtensionComponents-2.1.xsd`. La cadena complementaria inspeccionada incluye componentes de extensión/contenido, tipos cualificados y no cualificados, módulo de tipos centrales y componentes de firma; estos a su vez importan otros XSD (incluidos XML Signature/XAdES en la rama de firma). La cadena puede depender de extensiones incluso si el ejemplo no las utiliza. Por ello, para validar debe obtenerse el paquete oficial `UBL-2.1.zip` completo (55 MB según el índice consultado) y conservar los subdirectorios `xsd/maindoc`, `xsd/common` y demás rutas relativas exactamente como vienen; no basta con descargar solo el XSD Invoice ni con recrear imports manualmente.

Los XSD OASIS describen estructura y tipos genéricos UBL. No contienen por sí solos el perfil peruano, obligaciones fiscales completas, reglas semánticas del dominio ni resultado de aceptación SUNAT. El paquete completo fue autorizado e incorporado bajo `tests/resources/ubl/2.1/xsd/`; la procedencia/hash se registra en la guía de implementación.

## 6. Mapeo XML y comportamiento esperado

El mapeo de este contrato se verificó con el XSD Invoice 2.1 oficial. Incluye versión UBL, identificador y fecha, moneda, `AccountingSupplierParty`, `AccountingCustomerParty`, `TaxTotal` opcional, `LegalMonetaryTotal` y líneas. No incorpora datos obligatorios de un perfil SUNAT.

Reglas de transformación aprobadas:

1. Construir el documento con namespaces explícitos y respetar el orden de la secuencia `xs:sequence` del XSD Invoice 2.1; no ordenar elementos alfabéticamente ni escribirlos según conveniencia del modelo.
2. Convertir valores del dominio a su forma léxica conforme al tipo UBL; fechas sin hora se emitirán como fecha cuando el contrato así las defina.
3. Usar decimal exacto e invariantemente localizado; no usar coma decimal dependiente de configuración regional ni coma flotante binaria para importes.
4. Copiar líneas e importes del objeto aprobado; no completar importes o tributos ausentes con cero ni calcularlos.
5. Rechazar entrada incompleta o inválida con errores identificables; no ocultar fallos ni emitir XML parcial.
6. Usar datos ficticios en el ejemplo, dejando claro que la coherencia matemática del ejemplo no establece reglas tributarias.

El documento declara XML 1.0/UTF-8, sin BOM obligatorio ni indentación contractual. Los textos se escriben con StAX y se rechazan caracteres fuera de XML 1.0. El `TaxTotal` es opcional; las excepciones de entrada inválida se identifican con el campo correspondiente y no se devuelve XML parcial.

## 7. Casos límite y errores

| Caso | Resultado propuesto |
|---|---|
| Falta un dato obligatorio del contrato aprobado | Error de validación explícito con identificación del campo; no se devuelve XML parcial. |
| La factura no tiene líneas | Rechazo: el contrato aprobado y el XSD Invoice requieren una o más líneas. |
| Hay múltiples líneas | Se generan todas una vez; pruebas verifican conteo e identidad. |
| Texto contiene `&`, `<`, `>`, comillas o caracteres Unicode válidos | La biblioteca XML los escapa/serializa; al parsear, el valor de texto recuperado coincide con el de entrada. |
| Importe o cantidad usa decimales | La serialización conserva el valor y formato acordados sin conversión regional ni redondeo implícito. |
| Importe ausente o total inconsistente | Un importe requerido ausente se rechaza; totales inconsistentes se copian sin recalcular ni validar aritmética, fuera del alcance aprobado. |
| Fecha fuera del tipo/rango o no convertible | Error explícito de validación; no sustituir por fecha actual. |
| XML no pasa XSD | La prueba de integración informa el error estructural; eso no representa aceptación ni rechazo SUNAT. |

## 8. Criterios de aceptación y pruebas

Los siguientes criterios están aprobados y se verifican con pruebas automatizadas.

### AC-01 — XML y raíz

- **Dado** un objeto `Invoice` válido según el contrato aprobado,
- **cuando** se transforma,
- **entonces** se produce XML bien formado cuya raíz local es `Invoice` y pertenece al namespace UBL Invoice 2.1 indicado en `SPEC-003-FR-01`.

### AC-02 — Namespaces y estructura

- **Dado** una factura válida,
- **cuando** se analiza el resultado con un parser XML,
- **entonces** la raíz y los componentes usados se resuelven a los namespaces UBL acordados, incluidos CBC/CAC cuando correspondan.

### AC-03 — Partes y líneas

- **Dado** una factura ilustrativa con emisor, receptor y más de una línea,
- **cuando** se genera y analiza el XML,
- **entonces** los valores de partes coinciden con la entrada y el conteo/orden de líneas coincide con el objeto.

### AC-04 — Importes, cantidades, fechas y moneda

- **Dado** valores decimales y fechas válidos conforme al contrato,
- **cuando** se genera y parsea el XML,
- **entonces** los valores lexicales respetan el formato aprobado, son independientes de cultura regional y los totales reflejan los valores provistos, sin cálculos implícitos.

### AC-05 — Escape de caracteres

- **Dado** un campo textual con caracteres XML especiales y Unicode válido,
- **cuando** se serializa y vuelve a parsear,
- **entonces** el XML permanece bien formado y el texto recuperado es igual al valor original.

### AC-06 — Datos obligatorios ausentes

- **Dado** una factura sin un dato que el contrato/XSD aprobado defina como obligatorio,
- **cuando** se invoca el generador,
- **entonces** se devuelve un error explícito y no se produce una factura truncada o exitosa.

### AC-07 — Independencia de infraestructura

- **Dado** una prueba unitaria del adaptador,
- **cuando** se genera el documento,
- **entonces** el caso puede ejecutarse sin base PostgreSQL, servidor HTTP ni conexión SUNAT.

### AC-08 — Orden del esquema

- **Dado** un objeto completo que incluye los campos de contrato aprobados,
- **cuando** se genera el XML y se examina su secuencia de hijos por namespace,
- **entonces** los elementos aparecen en el orden declarado por la secuencia del XSD Invoice 2.1, comprobado con expectativas explícitas y, si está aprobado, con validación XSD.

### AC-09 — Tributo total opcional y totales recibidos

- **Dado** un importe opcional de `TaxTotal` y los totales suministrados en el objeto `Invoice`,
- **cuando** se genera y se analiza el documento,
- **entonces** los valores se conservan en su ubicación UBL, el XML no inventa código/categoría/tasa/`TaxSubtotal` y el generador no calcula, redondea ni añade valores por defecto.

### AC-10 — Validación XSD

- **Dado** el paquete XSD OASIS UBL 2.1 oficial completo y una factura cuyo mapeo se aprobó,
- **cuando** se valida el XML con un procesador W3C XML Schema 1.0,
- **entonces** se registra si es estructuralmente válido contra `UBL-Invoice-2.1.xsd`, con dependencias resueltas localmente; el resultado no se reporta como validación tributaria ni aceptación SUNAT.

### Plan de pruebas

- Unitarias: parsear XML; comprobar raíz, namespace, rutas UBL y valores; probar varias líneas, precisión/formato decimal, fecha, moneda, escaping Unicode y rechazo de obligatorios ausentes.
- Unitarias adicionales: comprobar orden de elementos y que importes/totales proporcionados se copian sin re-cálculo, además de entradas decimales/cultura local y rechazo de obligatorios ausentes.
- Prueba estructural complementaria: validar contra el paquete XSD oficial UBL 2.1 completo, resolver imports desde recursos locales y registrar el paquete/versión/hash. No sustituye las pruebas unitarias.
- Pruebas de perfil SUNAT/OSE: fuera de alcance hasta elegir modalidad, tipo de documento, fuentes vigentes y ambiente autorizado.

## 9. Decisiones tecnológicas aprobadas

El responsable aprobó explícitamente estas decisiones el 2026-11-04:

| Decisión | Base aprobada | Aplicación |
|---|---|---|
| Lenguaje/nivel | Java 21 como nivel de compilación (`--release 21`). | El JDK disponible en la máquina es 24.0.1; permite producir bytecode/API compatible con Java 21. La ejecución de las pruebas se documentará con el runtime realmente usado. |
| Build | Maven. | POM único en la raíz y comandos `mvn test`; no crear otro proyecto. |
| XML | JAXP y StAX del JDK (`XMLStreamWriter`). | Sin biblioteca XML de terceros; salida probada con parser independiente y esquema OASIS. |
| Pruebas | JUnit Jupiter 5. | Pruebas unitarias en `tests/unit/`; comprobación XSD complementaria en `tests/integration/`. |
| Framework web | No incorporar Spring Boot. | El generador es un adaptador aislado y no requiere servidor, inyección ni transporte HTTP. |
| Perfil | UBL 2.1 genérico, no un perfil SUNAT. | No agregar códigos, categorías o reglas fiscales no aprobadas; no afirmar aceptación SUNAT/OSE. |
| XSD | Incluir la carpeta completa `xsd/` del paquete oficial OASIS UBL 2.1, preservando rutas. | Se almacena como recurso de test para validación estructural offline; la descarga ZIP se usa solo para obtenerlos y registrar su hash. |

### 9.1 Validación XSD

La prueba carga `UBL-Invoice-2.1.xsd` con `SchemaFactory` y resuelve las importaciones relativas desde la copia local del árbol oficial. Se restringe el acceso externo a DTD/esquemas y no se permite que el XML de instancia seleccione un esquema. La validación es estructural, no de reglas nacionales, negocio ni SUNAT.

## 10. Arquitectura y flujo

`Invoice` del dominio → adaptador `InvoiceXmlGenerator` → XML UBL Invoice 2.1.

El dominio no depende de bibliotecas XML. El generador solo recibe el modelo, serializa en el orden de la secuencia XSD y devuelve XML. No accede a PostgreSQL, no se invoca desde controlador HTTP y no transmite a SUNAT.

La estructura Java respeta las carpetas existentes del repositorio: producción bajo `src/main/java/`, pruebas unitarias bajo `tests/unit/`, validación XSD bajo `tests/integration/`, esquema oficial en `tests/resources/ubl/2.1/xsd/`, XML demostrativo en `docs/examples/` y guía en `docs/`.

## 11. Límites aprobados

- No hay transformación automática desde `Venta`; el consumidor debe entregar un `Invoice` completo.
- No se emiten identificación fiscal, dirección ni `TaxSubtotal`/categorías/esquemas tributarios porque no se aprobaron sus datos/códigos/fuentes.
- `TaxTotal` es opcional y contiene únicamente un importe suministrado. El XML genérico puede tener estructura XSD válida sin representar un perfil SUNAT.
- No se recalculan cantidad × precio, importes de línea, impuestos ni totales y no se verifica coherencia matemática entre ellos.
- Los datos del ejemplo y de pruebas son sintéticos; los montos de prueba de tributos no expresan tasas o reglas fiscales.
- XSD válido y XML bien formado no significan cumplimiento/aceptación SUNAT u OSE.

## 12. Registro de aprobación

El usuario aprobó explícitamente en conversación el 2026-11-04:

1. Java 21 como nivel de compatibilidad; Maven; JAXP/StAX; JUnit 5.
2. No usar Spring Boot para el generador aislado.
3. El contrato mínimo de la sección 5 como modelo Invoice independiente de DTO/API existentes.
4. UBL 2.1 genérico y ausencia de afirmaciones de cumplimiento SUNAT.
5. Obtener e incorporar el paquete completo de XSD oficial para validación estructural complementaria.

La aprobación habilitó exclusivamente la implementación y pruebas del generador descrito aquí. No modifica SPEC-002, requisitos base, DTO, OpenAPI ni otros ADR. La especificación avanzó por `APPROVED` e `IN_PROGRESS` y quedó `IMPLEMENTED` tras superar las pruebas.

## 13. Historial

| Fecha | Versión | Estado | Nota |
|---|---|---|---|
| 2026-11-04 | 0.1 | `DRAFT` | Borrador inicial para revisar brechas y decisiones del generador UBL. |
| 2026-11-04 | 0.2 | `DRAFT` | Se añadieron inventario de contratos, dependencias XSD, propuesta tecnológica y criterios de aceptación ampliados; todo sujeto a revisión. |
| 2026-11-04 | 1.0 | `APPROVED` | El responsable aprobó el contrato mínimo, la base Java/Maven/JAXP/StAX/JUnit, el alcance UBL genérico y el uso de XSD oficial. |
| 2026-11-04 | 1.1 | `IN_PROGRESS` | Inició la implementación y las pruebas conforme a la aprobación registrada. |
| 2026-11-04 | 1.2 | `IMPLEMENTED` | Generador, pruebas unitarias, validación XSD y ejemplo completados; `mvn clean test` pasó con Java 21. |
