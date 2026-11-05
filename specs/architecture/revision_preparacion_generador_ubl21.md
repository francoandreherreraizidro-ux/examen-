# Revisión de preparación Spec-First: generador Invoice UBL 2.1

**Estado:** informe de revisión; recomendaciones pendientes de aprobación.
**Fecha:** 2026-11-04.
**Alcance:** preparación documental para solicitar aprobación de SPEC-003 y del contrato mínimo de entrada. No autoriza implementación.

## 1. Resumen ejecutivo

El repositorio todavía no está autorizado ni configurado para implementar el generador. [SPEC-003](../features/003-generador-invoice-ubl21.md) permanece `DRAFT`; el único SPEC `APPROVED` encontrado es ajeno a facturación. SPEC-002, DTO, API y ADR relevantes continúan en `DRAFT` o `PROPOSED`. No se halló un contrato de dominio `Invoice` suficiente, una selección tecnológica aprobada, configuración de compilación/pruebas ni los XSD UBL en el repositorio.

En preparación de la revisión se amplió SPEC-003 sin alterar el estado ni los contratos existentes: inventario conceptual de contratos, fuentes y dependencias XSD, recomendaciones tecnológicas marcadas como propuestas, controles de validación local y criterios verificables AC-08–AC-10. El documento continúa expresamente en `DRAFT`; no se aprobó ninguna decisión.

Recomendación de diseño: considerar Java 21 LTS con JAXP/StAX del JDK y pruebas JUnit Jupiter 5, Maven como build propuesto y un generador independiente sin Spring Boot. Ninguna de esas elecciones está aprobada. Spring Boot solo debe incorporarse si una decisión del proyecto exige que la aplicación anfitriona use ese framework; no es necesario para una transformación aislada.

**No comenzar código** hasta aprobar el SPEC, el contrato de entrada, el perfil/alcance UBL y el toolchain, y cumplir los criterios de preparación indicados abajo.

## 2. Estado del repositorio y documentos inspeccionados

| Documento / área | Estado observado | Hallazgo relevante |
|---|---|---|
| [SPEC-003](../features/003-generador-invoice-ubl21.md) | `DRAFT` | Borrador de generador UBL; ahora incorpora inventario conceptual, propuesta tecnológica y criterios de prueba adicionales. Continúa requiriendo decisión humana. |
| [SPEC-002](../features/002-sistema-comercial-facturacion.md) | `DRAFT` | No constituye aprobación del alcance de este generador. No se modificó. |
| [Modelo de dominio](../../02_Modelo_Dominio/modelo_dominio.md) | No aprobado para este contrato | `Venta`, `DetalleVenta` y `ComprobanteElectronico` no contienen los datos suficientes para emitir un Invoice completo. |
| [DTO y schemas](../../03_DTOs_Schemas/dtos_schemas.md) | `DRAFT` | No definen contrato completo de emisor, adquirente, moneda, tributos, importes y totales para Invoice. No se modificaron. |
| [OpenAPI](../../specs/api/sistema-comercial.openapi.yaml) y contratos API | `DRAFT` | No son el contrato de entrada al generador y no deben ampliarse sin decisión relacionada. No se modificaron. |
| [Arquitectura MVC/hexagonal](../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md) | Propuesta documental | Favorece aislar la transformación XML; no obliga a utilizar Spring Boot en un componente puro. |
| [ADR-0002](./adrs/0002-arquitectura-hexagonal-mvc.md) | `PROPOSED` | Propone arquitectura, no fija framework. |
| [ADR-0003](./adrs/0003-persistencia-postgresql-kardex.md) | `PROPOSED` | No decide lenguaje, framework o build. PostgreSQL no es parte del generador XML aislado. |
| `src/` y `tests/` | Marcadores/README | No se encontró código Java ni configuración de pruebas. |
| Build/dependencias | Sin selección | No se encontraron `pom.xml`, wrapper Maven, archivos Gradle, dependencias XML ni JUnit. |
| XSD UBL | No existe copia local identificada | La fuente consultada es OASIS; no se descargó ni añadió al repositorio. |

En la máquina se observó `java` runtime 24.0.1. No se verificó un JDK completo/`javac`, Maven o Gradle disponibles para el proyecto. El runtime local no acredita que el repositorio esté compilable ni establece la versión que se debe usar.

## 3. Revisión de SPEC-003

### Cubre como propuesta

- Propósito, alcance general, exclusiones respecto de PostgreSQL, HTTP y transmisión a SUNAT.
- Brecha del modelo existente y necesidad de una representación `Invoice` de entrada.
- Requisitos de serialización, escape, datos decimales y tratamiento explícito de errores.
- Pruebas que parsean el XML para comprobar su contenido, además de la posibilidad condicionada de validación XSD.
- Diferencia entre estructura XML, reglas de negocio/perfil y aceptación SUNAT/OSE.

### Se completó documentalmente, aún pendiente de aprobación

- Contratos candidatos para `Invoice`, `Party`, roles `Supplier`/`Customer`, `Currency`, `InvoiceLine`, `Quantity`, `Price`, `MonetaryAmount`, `Tax`, `TaxSubtotal` y `TaxTotal`.
- Requisito de conservar el orden UBL `xs:sequence`, criterio verificable de tributos/totales y validación complementaria contra XSD.
- Fuentes OASIS consultadas y advertencia de obtener el paquete completo para resolver imports.
- Evaluación de Java/JDK, XML, Spring Boot, build y runner de pruebas, diferenciando las propuestas de los hechos observados.

### Brechas que permanecen abiertas

1. Identidad, campos y cardinalidades exactas de emisor/receptor y la fuente de sus datos.
2. Si `Invoice` es agregado independiente o snapshot/proyección de una `Venta`, además de su trazabilidad y momento de creación.
3. Contrato completo de importes, escala decimal, cantidades, precios, impuestos y totales; no se debe dejar que el serializador calcule tributos.
4. Perfil UBL genérico o perfil peruano/tipo de comprobante específico. UBL 2.1 por sí solo no define la integración tributaria nacional.
5. Catálogos, unidades, códigos y reglas de negocio aprobadas. No se incluyen valores inventados.
6. Versiones del toolchain y sus restricciones en el ambiente evaluador.
7. Datos ficticios específicos que se autoricen para un ejemplo posterior.

## 4. Propuesta tecnológica

| Elemento | Propuesta para aprobación | No aprobado / condición |
|---|---|---|
| Lenguaje/runtime | Java 21 LTS como línea conservadora; Java 25 LTS es alternativa si el curso/toolchain lo permite. | Debe aprobarlo el responsable y verificarse disponibilidad del JDK. En el repositorio no hay elección. |
| Serialización y XSD | `XMLStreamWriter` y JAXP (`SchemaFactory`/Validation API) del módulo estándar `java.xml`. | No se validó ningún código; las APIs estándar no eliminan la necesidad de pruebas de parseo ni de resolver XSD. |
| Spring Boot | No usarlo para el generador independiente; considerar Boot solo si una decisión sobre la aplicación anfitriona lo exige. | La arquitectura MVC/hexagonal no significa que todo adaptador deba depender de Spring. Si el docente exige Spring, deben aprobarse versión y compatibilidad exactas antes de crear proyecto. |
| Build | Maven/Maven Wrapper propuesto por simplicidad convencional; Gradle es una alternativa válida. | Elegir herramienta, versión y soporte en la evaluación. No hay manifiesto ni wrapper hoy. |
| Tests | JUnit Jupiter 5 propuesto para unitarias; sumar prueba estructural XSD solo tras autorización del paquete y configuración local. | No hay runner o dependencia existente. No instalar en esta fase. |
| Tipos decimales | `BigDecimal` propuesto para importes, precios y cantidades decimales en Java. | Tipo, escala y reglas para cada atributo requieren cierre contractual. Evitar `float`/`double` para dinero. |
| Dependencias | Evitar librerías XML de terceros si basta `java.xml`; incluir únicamente dependencias de test/build aprobadas. | No hay dependencias ya configuradas ni instaladas en el proyecto. |

Fuentes oficiales para la evaluación:

- Oracle, [Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html): identifica Java 21 y Java 25 como versiones LTS. Esto justifica considerarlas, pero no decide cuál corresponde al examen.
- Oracle, [módulo `java.xml` de Java SE 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.xml/module-summary.html): describe APIs JAXP, StAX y validación XML. StAX escribe contenido XML escapado; eso no sustituye parsear el resultado ni validarlo contra XSD.
- Spring, [requisitos del sistema de Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html): referencia oficial que deberá consultarse de nuevo para la versión exacta que se apruebe. No se recomienda fijar un patch basándose solo en el estado actual de la documentación.

## 5. Contrato mínimo propuesto y aprobaciones requeridas

La tabla de atributos candidatos, relaciones y cautelas de obligatoriedad está en la sección 5.3 de SPEC-003. Resume lo que el responsable debe revisar:

| Contrato | Decisión que falta |
|---|---|
| `Invoice` | Identificador, fecha, moneda, partes, líneas, totales y relación con `Venta`; definir el dato recibido y qué invariantes valida el generador. |
| `Party` / `Supplier` / `Customer` | Datos de identidad y dirección admitidos; diferenciar roles UBL de las entidades comerciales internas; determinar snapshot y origen. |
| `Currency` / `MonetaryAmount` | Código, fuente del catálogo, `currencyID`, escala y consistencia entre documento y montos. `BigDecimal` es recomendación, no aprobación. |
| `InvoiceLine` / `Quantity` / `Price` | Campos mínimos, unidad, precisión, importes de línea y orden de salida; prohibir cálculo implícito dentro del serializador. |
| `Tax` / `TaxSubtotal` / `TaxTotal` | Fuente autorizada de categorías/tasas/códigos y reglas de coherencia. No agregar campos tributarios sin fuente verificable. |
| Errores | Campos obligatorios según el perfil aprobado, reporte de error y conducta ante entrada incompleta; no producir XML parcial con forma de éxito. |

Los DTO existentes siguen en `DRAFT` y no se modificaron. Aprobar el contrato de entrada del generador no cambia automáticamente esos DTO ni los contratos API.

## 6. Fuentes UBL 2.1 y XSD

Fuentes verificadas:

- [OASIS UBL 2.1 Standard](https://docs.oasis-open.org/ubl/os-UBL-2.1/UBL-2.1.html), publicación OASIS de 04-11-2013.
- [Directorio oficial del release UBL 2.1](https://docs.oasis-open.org/ubl/os-UBL-2.1/), con el archivo ZIP oficial y los directorios de esquemas.
- [XSD Invoice 2.1](https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/maindoc/UBL-Invoice-2.1.xsd).
- XSD comunes importados directamente: [Common Aggregate Components](https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/common/UBL-CommonAggregateComponents-2.1.xsd), [Common Basic Components](https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/common/UBL-CommonBasicComponents-2.1.xsd), [Common Extension Components](https://docs.oasis-open.org/ubl/os-UBL-2.1/xsd/common/UBL-CommonExtensionComponents-2.1.xsd).

La revisión del XSD de Invoice confirmó `cbc:ID` y `cbc:IssueDate` con cardinalidad `1..1` en el esquema genérico. El mapeo completo debe verificar en el paquete exacto las cardinalidades/orden de las partes, moneda, líneas, totales y componentes fiscales antes de fijar el contrato. No se debe deducir la obligatoriedad SUNAT a partir de la cardinalidad genérica UBL.

La rama de dependencias pasa por CBC/CAC, tipos cualificados y no cualificados, tipos centrales y componentes de extensión/contenido; los componentes de extensión pueden incluir dependencias de firma XML/XAdES y otras referencias. Para una validación fiable se necesita el paquete completo `UBL-2.1.zip`, conservando estructura y rutas relativas. El ZIP no está en el repositorio y no se descargó durante esta fase.

Interpretación de resultados, siempre separada:

1. Parser XML: el documento está bien formado.
2. XSD OASIS: la estructura/tipos genéricos cumplen el esquema cargado.
3. Reglas de dominio/perfil: validaciones adicionales aprobadas y respaldadas.
4. SUNAT/OSE: aceptación externa, que no se demuestra con generación ni con XSD.

La futura validación local debería resolver imports desde el paquete incorporado y bloquear acceso externo no autorizado a DTD/esquemas; no debe permitir que datos del XML desencadenen descargas externas.

## 7. Criterios de aceptación futuros

SPEC-003 define AC-01–AC-10. En la futura implementación, pruebas/evidencia deberán demostrar:

1. Generación XML parseable y raíz `Invoice` en namespace correcto.
2. Namespaces CBC/CAC para los componentes efectivamente utilizados.
3. Emisor, adquirente y todas las líneas con valores del objeto de entrada.
4. Cantidades, precio, moneda, fechas e importes serializados sin dependencia de locale ni precisión binaria monetaria.
5. Orden de elementos conforme a `xs:sequence` del esquema.
6. Tributos, subtotales y totales proporcionados copiados sin cálculos, redondeo o datos por defecto.
7. Escape de caracteres y recuperación fiel de texto tras parsear el XML.
8. Rechazo explícito de campos obligatorios ausentes definidos por el contrato aprobado.
9. Ejecución unitaria aislada de PostgreSQL, HTTP y SUNAT.
10. Validación complementaria contra XSD OASIS completo/local cuando su incorporación se apruebe, registrando resultado/procedencia. No equivale a aprobación SUNAT.

## 8. Plan para implementación tras aprobación

1. Obtener respuestas del responsable a las preguntas de la sección 11 de SPEC-003.
2. Aprobar explícitamente SPEC-003, los contratos `Invoice` y partes/líneas/totales, y las decisiones técnicas; registrar aprobadores y fecha sin editar el estado unilateralmente.
3. Confirmar alcance UBL/perfil, fuentes regulatorias y campos/validaciones; evitar códigos fiscales especulativos.
4. Crear o aprobar contrato de datos y casos de prueba trazables sin modificar DTO/API ajenos al alcance.
5. Definir la tarea de implementación y el toolchain aprobado (JDK, Maven/Gradle, JUnit y XSD oficial si aplica).
6. Escribir primero pruebas unitarias que parseen y verifiquen el XML; añadir validación XSD complementaria si autorizada.
7. Implementar modelo/adaptador en el módulo acordado, separado de persistencia, controladores y comunicaciones externas.
8. Validar criterios, ejecutar pruebas y documentar resultados reales y limitaciones.

## 9. Bloqueos para comenzar programación

- SPEC-003 está `DRAFT`; no hay aprobación formal.
- Falta aprobación del modelo/contrato de factura y su relación con `Venta`.
- No se ha definido perfil/alcance UBL vs. perfil peruano, ni los datos fiscales y reglas con fuentes aplicables.
- No están aprobados lenguaje, versión JDK, framework (incluida la decisión explícita de no usar Spring Boot), build y runner de pruebas.
- No existe JDK/build/test harness configurado ni dependencias locales identificadas.
- XSD oficial no está incorporado; inclusión y política de uso/licencia/tamaño requieren autorización.
- Reglas de obligatoriedad, precision/escala, consistencia de totales y errores permanecen abiertas.

La fecha límite académica proporcionada (05/11/2026 a medianoche) no constituye aprobación ni justifica asumir estas decisiones.

## 10. Documentos cambiados en esta revisión

| Archivo | Cambio |
|---|---|
| [SPEC-003](../features/003-generador-invoice-ubl21.md) | Ampliación del borrador `DRAFT` con inventario conceptual de contratos, fuentes/dependencias UBL/XSD, propuesta tecnológica explícitamente pendiente, medidas propuestas para resolver XSD localmente y criterios verificables AC-08–AC-10. No se cambió su estado ni se editaron DTO, API, requisitos originales, `src/` o `tests/`. |
| Este informe | Documento nuevo para consolidar evidencia, brechas, recomendaciones, bloqueos y próximos pasos de aprobación. |

## 11. Preguntas para aprobación humana

1. ¿Se autoriza Java 21 LTS, Java 25 LTS u otra versión? ¿Qué JDK completo estará disponible para evaluación?
2. ¿Se aprueba el generador como módulo Java independiente sin Spring Boot? Si el curso exige Spring Boot, ¿qué versión y requisito documentado lo sustentan?
3. ¿Se aprueba Maven/Maven Wrapper o Gradle? ¿Se acepta JUnit Jupiter 5?
4. ¿Invoice será un agregado independiente o un snapshot/proyección de `Venta`? ¿Qué campos y origen se aprueban para emisor, adquirente y datos de línea?
5. ¿El alcance es UBL 2.1 genérico o un perfil/tipo SUNAT específico? ¿Qué fuente oficial define los campos y validaciones de ese perfil?
6. ¿Se autoriza incorporar el ZIP oficial UBL 2.1 completo y una validación XSD local?
7. ¿Qué códigos, reglas tributarias y datos sintéticos concretos se permiten, con qué fuentes y quién los aprueba?
8. ¿Quién aprueba formalmente SPEC-003 y los contratos antes de crear tareas de implementación?

## 12. Criterios objetivos para solicitar aprobación

- Toda decisión listada arriba tiene respuesta registrada o se mantiene explícitamente fuera del alcance.
- Cada atributo/campo obligatorio está asociado a un elemento UBL identificado y a su fuente; perfil SUNAT solo si hay evidencia oficial específica.
- Las invariantes y cálculos están asignados a un responsable del dominio, no al serializador por defecto.
- Se acuerdan runtime/build/test toolchain y uso/ausencia de Spring.
- XSD objetivo, paquete y forma de resolver dependencias están identificados, o se documenta que la prueba XSD no forma parte del alcance.
- Los criterios AC-01–AC-10 son aceptados y su evidencia esperada está clara.
- El responsable revisa y marca explícitamente SPEC-003 como aprobado mediante el proceso del repositorio.

Hasta completar estos puntos y recibir la aprobación, no se debe escribir código de producción, crear pruebas, añadir dependencias ni cambiar estados de SPEC.
