# SPEC-003: Generador XML Invoice UBL 2.1

---

## Metadatos

| Campo | Valor |
|---|---|
| ID | `SPEC-003` |
| Estado | `DRAFT` — requiere revisión y aprobación; no habilita implementación |
| Autor | Borrador de diseño |
| Revisor | Responsable del proyecto/examen, pendiente de confirmación |
| Fecha | 2026-11-04 |
| Hito | Entrega académica indicada para 2026-11-05 |
| Requisitos relacionados | `RF-CPE-001`, `RF-VAL-001`, `RF-TRZ-001`, `RNF-DOC-001`, `RNF-ARQ-001`, `RNF-SDD-001` |
| Documentos relacionados | [Análisis de requisitos](../../01_Analisis_Requisitos/analisis_requisitos.md), [modelo de dominio](../../02_Modelo_Dominio/modelo_dominio.md), [DTOs y Schemas](../../03_DTOs_Schemas/dtos_schemas.md), [arquitectura](../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md), [SPEC-002](./002-sistema-comercial-facturacion.md), [matriz normativa](../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md) |
| API relacionada | No propone ni requiere endpoints HTTP nuevos |

## 1. Resumen y problema

### 1.1 Problema

El examen solicita considerar UBL Invoice 2.1 para comprobantes electrónicos y demostrar la generación de XML mediante pruebas unitarias. Los documentos actuales describen `Venta` y `ComprobanteElectronico` de forma conceptual, pero no especifican un objeto de factura completo ni los datos fiscales y monetarios necesarios para construir un Invoice. Los contratos actuales permanecen en `DRAFT` y no contienen esos datos.

El repositorio no fija lenguaje, framework, gestor de dependencias, biblioteca XML ni runner de pruebas. Tampoco contiene un XSD UBL ni código de aplicación. Por tanto, no es posible implementar respetando una selección tecnológica existente hasta que el responsable la determine.

### 1.2 Solución propuesta

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

| ID | Requisito propuesto | Relación | Estado |
|---|---|---|---|
| `SPEC-003-FR-01` | El generador recibirá un objeto de dominio `Invoice` y devolverá un documento XML bien formado con raíz `Invoice` en `urn:oasis:names:specification:ubl:schema:xsd:Invoice-2`. | `RF-CPE-001`, `RNF-DOC-001` | Propuesto; estructura exacta debe verificarse contra UBL 2.1. |
| `SPEC-003-FR-02` | El XML declarará los namespaces UBL necesarios, incluyendo CommonBasicComponents (`cbc`) y CommonAggregateComponents (`cac`) donde correspondan. | `RNF-DOC-001` | Propuesto. |
| `SPEC-003-FR-03` | Los datos de emisor, cliente, líneas, cantidades, precios, moneda, importes y tributos que estén presentes en el objeto se mapearán a los elementos UBL correspondientes, sin derivar valores que no proporcione el dominio. | `RF-CPE-001`, `RF-VAL-001` | Pendiente de cerrar modelo y mapeo. |
| `SPEC-003-FR-04` | Cada línea recibida se representará una sola vez, conservando el orden o identificador que defina el contrato aprobado. | `RF-CPE-001`, `RF-TRZ-001` | Propuesto; orden y campos por aprobar. |
| `SPEC-003-FR-05` | El serializador escapará caracteres XML usando una API XML adecuada; no se concatenarán valores de usuario directamente como marcado. | `RF-VAL-001` | Propuesto. |
| `SPEC-003-FR-06` | Los importes y cantidades usarán tipos decimales exactos del lenguaje seleccionado y formato léxico independiente de la configuración regional. El generador no realizará redondeos comerciales o tributarios. | `RF-VAL-001` | Propuesto; lenguaje, escala y representación pendientes. |
| `SPEC-003-FR-07` | Si falta un dato marcado obligatorio por el contrato aprobado o por el XSD aplicable, el generador devolverá un error explícito y no generará un documento parcial o aparentemente exitoso. | `RF-VAL-001` | Propuesto; lista de campos obligatorios pendiente de verificación. |
| `SPEC-003-FR-08` | Se incluirá una factura ilustrativa de prueba, identificada como datos sintéticos, junto con pruebas que analicen el XML y comprueben estructura y valores. | `RNF-SDD-001` | Propuesto. |

## 4. Requisitos de calidad y restricciones

| ID | Requisito |
|---|---|
| `SPEC-003-NFR-01` | El dominio no dependerá de tipos XML, namespaces, bibliotecas de serialización ni infraestructura. El mapeo UBL quedará en un adaptador/componente de salida aislable. |
| `SPEC-003-NFR-02` | Las pruebas unitarias verificarán el documento parseado y sus elementos/valores, no únicamente que el método retorne una cadena. |
| `SPEC-003-NFR-03` | No se añadirán dependencias hasta identificar el lenguaje, gestor y librerías existentes. |
| `SPEC-003-NFR-04` | Se utilizarán nombres y formato consistentes con el lenguaje y estilo que se aprueben; la selección de lenguaje/framework aún está pendiente porque el repositorio no tiene implementación ni manifiestos. |
| `SPEC-003-NFR-05` | Validar contra XSD, si se autoriza y se obtiene el paquete oficial compatible, será una comprobación estructural complementaria. No equivale a validar reglas nacionales ni a obtener aceptación SUNAT/OSE. |
| `SPEC-003-NFR-06` | El documento de ejemplo y sus datos identificatorios serán explícitamente ficticios; no se usarán datos fiscales reales. |

Los requisitos base conservan su certeza original: `RF-CPE-001`, `RF-VAL-001`, `RF-TRZ-001`, `RNF-DOC-001`, `RNF-ARQ-001` y `RNF-SDD-001` no se amplían ni se modifican por este borrador.

## 5. Modelo de dominio y contrato de entrada propuestos

### 5.1 Brecha actual

El modelo vigente tiene `Venta`, `DetalleVenta` y `ComprobanteElectronico`, pero:

- los importes, precios, moneda e impuestos de venta son opcionales o no están definidos;
- el detalle de venta actual requiere producto y cantidad, pero no descripción contractual de línea, precio ni tributos suficientes para factura;
- cliente/proveedor usa datos genéricos opcionales, sin contrato aprobado de identidad fiscal, dirección o rol como emisor;
- `ComprobanteElectronico` representa estado y referencias, no el documento fiscal completo ni sus totales.

En consecuencia, no existe hoy información suficiente para construir un Invoice genérico completo sin inventar datos.

### 5.2 Ampliación mínima propuesta

Introducir, solo tras aprobación, un modelo de entrada `Invoice` separado del actual DTO de venta y del estado de integración. El modelo conceptual a revisar incluiría:

- **Identificación y emisión:** identificador documental y fecha de emisión con tipos conceptuales acordados.
- **Partes:** emisor y cliente/receptor como estructuras con nombre y los datos de identificación/documentación que exija el perfil que se apruebe.
- **Moneda:** código/representación que se acuerde para el documento.
- **Líneas:** identificador o secuencia, descripción, cantidad/unidad, precio e importe de línea; campos adicionales solo si el perfil y las reglas aplicables lo requieren.
- **Totales:** importes totales recibidos explícitamente desde el dominio o proceso responsable. El generador no los recalcula.
- **Tributos:** representación tipada solo después de confirmar el perfil y fuentes aplicables; no se inventan categorías, códigos, tasas ni reglas de cálculo.

Estos son grupos de datos candidatos, no campos fiscales aprobados ni una declaración de obligatoriedad normativa. Antes de cerrar el modelo se debe contrastar cada campo con la especificación UBL 2.1 y el XSD oficial aplicable, además de decidir si el ámbito académico usa solo UBL genérico o un perfil nacional específico.

`Invoice` sería el dato de entrada de la transformación; no se propone cambiar `Venta` para que dependa de XML. La correspondencia entre venta y factura y el origen de cada dato se definirían después, conservando separada la salida de emisión/transmisión.

## 6. Mapeo XML y comportamiento esperado

El mapeo debe verificar en UBL 2.1 la estructura exacta de raíz, namespaces y elementos antes de considerarse estable. Candidatos conceptuales incluyen componentes de identificación/fecha, partes contables, líneas, moneda, cantidades, precios, importes y totales. No se fijan nombres de campos de entrada ni se afirma que un elemento o dato sea obligatorio para un perfil SUNAT sin revisar la fuente/version aplicable.

Reglas de transformación propuestas:

1. Construir un árbol XML con namespaces explícitos mediante la biblioteca del lenguaje seleccionada.
2. Convertir valores del dominio a su forma léxica conforme al tipo UBL; fechas sin hora se emitirán como fecha cuando el contrato así las defina.
3. Usar decimal exacto e invariantemente localizado; no usar coma decimal dependiente de configuración regional ni coma flotante binaria para importes.
4. Copiar líneas e importes del objeto aprobado; no completar importes o tributos ausentes con cero ni calcularlos.
5. Rechazar entrada incompleta o inválida con errores identificables; no ocultar fallos ni emitir XML parcial.
6. Usar datos ficticios en el ejemplo, dejando claro que la coherencia matemática del ejemplo no establece reglas tributarias.

La selección de XML declaration, encoding/BOM, indentación, orden de elementos, validación de caracteres XML, política para campos opcionales y tipos de error debe quedar fijada en el contrato de implementación derivado de esta especificación.

## 7. Casos límite y errores

| Caso | Resultado propuesto |
|---|---|
| Falta un dato obligatorio del contrato aprobado | Error de validación explícito con identificación del campo; no se devuelve XML parcial. |
| La factura no tiene líneas | Rechazo si el contrato aprobado exige al menos una; el requisito de cardinalidad debe validarse contra el modelo y XSD, no asumirse aquí. |
| Hay múltiples líneas | Se generan todas una vez; pruebas verifican conteo e identidad. |
| Texto contiene `&`, `<`, `>`, comillas o caracteres Unicode válidos | La biblioteca XML los escapa/serializa; al parsear, el valor de texto recuperado coincide con el de entrada. |
| Importe o cantidad usa decimales | La serialización conserva el valor y formato acordados sin conversión regional ni redondeo implícito. |
| Importe ausente o total inconsistente | No se sustituye ni recalcula; se rechaza solo conforme a invariantes que se aprueben. |
| Fecha fuera del tipo/rango o no convertible | Error explícito de validación; no sustituir por fecha actual. |
| XML no pasa XSD | Si hay validador/XSD disponible y la validación se habilita, informar error estructural; no presentar el resultado como aceptación SUNAT. |

## 8. Criterios de aceptación y pruebas

Los siguientes criterios son propuestos y deberán derivar pruebas después de aprobar SPEC-003 y resolver las decisiones bloqueantes.

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

### Plan de pruebas

- Unitarias: parsear XML; comprobar raíz, namespace, rutas UBL y valores; probar varias líneas, precisión/formato decimal, fecha, moneda, escaping Unicode y rechazo de obligatorios ausentes.
- Prueba estructural complementaria: validar contra XSD oficial UBL 2.1 solo si el archivo y validador compatibles se incorporan/aprueban. No sustituye las pruebas unitarias.
- Pruebas de perfil SUNAT/OSE: fuera de alcance hasta elegir modalidad, tipo de documento, fuentes vigentes y ambiente autorizado.

## 9. Arquitectura y archivos propuestos tras aprobación

Sin autorizar todavía estructura concreta, la separación conceptual es:

`Invoice` de dominio → puerto/adaptador de salida documental → mapeador UBL/serializador XML.

El dominio no depende de la biblioteca XML. El generador no accede a PostgreSQL, no se invoca desde un controlador HTTP y no transmite el XML a SUNAT. El nombre de paquetes y archivos se definirá cuando se seleccione el lenguaje/proyecto.

Archivos candidatos para la fase de implementación, sujetos a lenguaje y estructura aprobados:

- modelo/valor `Invoice` y tipos de línea/partes/totales definidos por contrato;
- adaptador generador UBL Invoice 2.1;
- pruebas unitarias en `tests/unit/`;
- ejemplo XML sintético inspeccionable;
- guía `docs/implementacion_generador_invoice_ubl21.md` con límites y resultados de pruebas ejecutadas.

No se deben crear como si existieran hasta aprobar la especificación y el plan de tareas.

## 10. Decisiones bloqueantes y preguntas para revisión

1. ¿Qué lenguaje y versión debe usar el examen? El repositorio no contiene código ni manifiestos que establezcan uno.
2. ¿Se mantiene el framework sin seleccionar, o existe una decisión externa documentable?
3. ¿Qué entorno/runner de pruebas debe usarse? No existe configuración de pruebas ni dependencias identificadas.
4. ¿Se permite incorporar el XSD oficial OASIS UBL 2.1 y qué herramienta aprobada validará el XML? Actualmente no hay XSD en el repositorio.
5. ¿El objetivo es Invoice UBL genérico o se solicita un perfil/tipo SUNAT específico? La selección del perfil no puede inferirse de UBL 2.1.
6. ¿Qué estructura autorizada representará al emisor? El dominio actual no tiene una entidad/datos definidos para el emisor.
7. ¿Qué datos de identidad de emisor/receptor, dirección, moneda, productos, unidades y tributos pertenecen al ejemplo y al contrato? No inventar identificadores fiscales ni códigos tributarios.
8. ¿Los importes/totales se reciben calculados por una capa de dominio/fuente aprobada o se autoriza algún cálculo? La propuesta de este SPEC es solo serializar valores provistos.
9. ¿Qué campos son obligatorios para el alcance aprobado y cómo se representan opcionales y errores?
10. ¿Se permite crear una factura de dominio independiente o debe mapearse una `Venta` existente? El dominio actual no contiene un agregado `Invoice`.
11. ¿Se puede usar `RF-CPE-001`, `RF-VAL-001`, `RF-TRZ-001` y `RNF-DOC-001` como trazabilidad de este componente sin modificar su texto? Este borrador no cambia sus definiciones.
12. ¿Quién revisa y aprueba el contrato de entrada y sus criterios antes de crear tareas/código?

## 11. Recomendación de aprobación

No declarar `SPEC-003` listo para implementación hasta que:

1. se elijan lenguaje/versión y herramienta de pruebas compatible con el proyecto;
2. se apruebe el modelo mínimo `Invoice` y su relación (o independencia) respecto de `Venta`;
3. se cierre el mapeo UBL genérico y se verifique con la especificación/XSD 2.1 aplicable;
4. se identifiquen los campos requeridos y los límites respecto de un perfil SUNAT;
5. se aprueben los criterios AC-01–AC-07 y la política decimal/fechas/errores;
6. el responsable marque explícitamente el documento `APPROVED`.

La fecha límite académica no sustituye la aprobación ni autoriza a inventar datos o decisiones tributarias. Hasta esa aprobación no se modifica `src/`, `tests/`, contratos de datos/API ni requisitos originales.

## 12. Historial

| Fecha | Versión | Estado | Nota |
|---|---|---|---|
| 2026-11-04 | 0.1 | `DRAFT` | Borrador inicial para revisar brechas y decisiones del generador UBL. |
