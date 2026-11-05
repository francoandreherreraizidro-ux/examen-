# Auditoría de coherencia entre decisiones, requisitos y especificaciones

| Metadato | Valor |
|---|---|
| Estado | `DRAFT` — auditoría documental; no aprueba requisitos ni decisiones |
| Fecha | 2026-11-04 |
| Alcance | Preparación para aprobación formal y eventual implementación; no se inspeccionó ni modificó código |
| Fuente de verdad de alcance | [Análisis de requisitos](../../01_Analisis_Requisitos/analisis_requisitos.md) |
| Informe relacionado | [Consolidación de decisiones](./informe_consolidacion_decisiones.md) |

## 1. Resumen ejecutivo

**Resultado: el paquete sirve para iniciar una revisión formal de decisiones, pero todavía no está listo para aprobar en conjunto las especificaciones ni para iniciar la implementación.**

La auditoría inicial encontró que:

- Los códigos RF/RNF vigentes usados por las especificaciones corresponden al análisis de requisitos. Las referencias a códigos antiguos `RF-CAT-*` y `RF-ALM-*` del modelo aparecen explícitamente como ejemplos de una revisión anterior no vigente; las referencias adicionales de la matriz regulatoria antigua están dentro de un documento prominentemente marcado como histórico/supersedido.
- Las propuestas se clasifican generalmente como tales. La revisión posterior en [idempotencia e inventario](./revision_idempotencia_inventario.md) corrigió la discrepancia documental: el encabezado es opcional en OpenAPI y se limita a ocho comandos `POST`; su garantía y obligatoriedad siguen pendientes de aprobación.
- Los 25 métodos OpenAPI tienen `operationId` y `x-requirements` asociados, y la especificación agrupa sus propósitos en servicios lógicos candidatos. Sin embargo, no hay IDs formales de casos de uso ni criterios de aceptación individuales para cada operación. La cobertura de migración se reconoce como no definida.
- La revisión posterior propone asignar las salidas por venta exclusivamente a la confirmación y limitar el endpoint genérico a entradas/ajustes no originados por ventas. El tipo/origen final, reversos y reglas de negocio aún no están aprobados; la propuesta necesita validarse antes de asegurar que no puede haber duplicidad.
- Dominio, DTO y API no contienen la información fiscal suficiente para construir un comprobante UBL perfilado. La emisión está correctamente descrita como pendiente, pero el contrato no puede aprobarse como interfaz fiscal completa.
- Las fuentes SUNAT y OASIS están identificadas con versión, URL y límites. No se verificó un cuerpo normativo consolidado ni el anexo nacional vigente completo, y no se validó una instancia XML contra XSD o perfil SUNAT. No hay evidencia de cumplimiento SUNAT.
- ADR-0002/0003 y el informe de consolidación no se contradicen en sus estados actuales. El análisis, SPEC-002, dominio, DTO/API, arquitectura, ADR y matriz normativa conservan estados de revisión (`DRAFT`/`PROPOSED`).

La matriz regulatoria anterior permanece intacta y contiene una advertencia visible de supersesión, pero no debe citarse como fuente vigente.

## 2. Documentos revisados

| Documento | Estado observado | Uso en la auditoría |
|---|---|---|
| [Análisis de requisitos](../../01_Analisis_Requisitos/analisis_requisitos.md) | Borrador para revisión | Fuente de verdad de códigos, alcance y certeza; RF/RNF no modificados. |
| [Modelo de dominio](../../02_Modelo_Dominio/modelo_dominio.md), [Mermaid](../../02_Modelo_Dominio/diagrama_dominio.mmd), [PlantUML](../../02_Modelo_Dominio/diagrama_dominio.puml) y [trazabilidad](../../02_Modelo_Dominio/trazabilidad_dominio.csv) | Propuesta no aprobada | Entidades, cardinalidades, invariantes, referencias de requisitos y supuestos. |
| [DTOs y Schemas](../../03_DTOs_Schemas/dtos_schemas.md), [JSON Schema](../../03_DTOs_Schemas/dto-contracts.schema.json) y [mapeo dominio-contratos](../../03_DTOs_Schemas/modelo_dominio_contratos.md) | `DRAFT` | Campos, validaciones estructurales, datos ausentes y consistencia con el dominio. |
| [SPEC-002](../features/002-sistema-comercial-facturacion.md) | `DRAFT` | Fuente funcional secundaria y criterios preliminares. |
| [Especificación de contratos API](../../05_Contratos_API/especificacion_contratos_api.md), [OpenAPI](../api/sistema-comercial.openapi.yaml) y [revisión API](../../05_Contratos_API/revision_final_contratos_api.md) | `DRAFT` | Operaciones, requisitos anotados, idempotencia, seguridad y limitaciones de migración/CPE. |
| [Arquitectura MVC/hexagonal](../../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md) | `DRAFT` | Límites de módulos, adaptadores y flujos conceptuales. |
| [ADR-0001](./adrs/0001-record-architecture-decisions.md) | `ACCEPTED` | Gobierno de los registros de decisión; no se tomó como aprobación de ADR posteriores. |
| [ADR-0002](./adrs/0002-arquitectura-hexagonal-mvc.md) y [ADR-0003](./adrs/0003-persistencia-postgresql-kardex.md) | `PROPOSED` | Clasificación de decisiones arquitectónicas y de persistencia. |
| [Revisión ADR-0002/0003](./revision_ADR_0002_0003.md) | `DRAFT` | Comparación con requisitos, contradicciones previas y decisiones pendientes. |
| [Consolidación de decisiones](./informe_consolidacion_decisiones.md) | `DRAFT` | Alternativas, dependencias, aprobadores propuestos e impactos. |
| [Revisión de idempotencia e inventario](./revision_idempotencia_inventario.md) | `DRAFT` | Corrección documental de C-01/C-02; recomendaciones pendientes de aprobación. |
| [Informe de brechas normativas](../../06_Matriz_Trazabilidad_Normativa/informe_brechas_normativas.md), [registro de fuentes oficiales](../../06_Matriz_Trazabilidad_Normativa/registro_fuentes_oficiales.md), [marco normativo](../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md) y [matriz CSV vigente](../../06_Matriz_Trazabilidad_Normativa/matriz_trazabilidad_normativa.csv) | `DRAFT` | Reglas condicionales, fuentes primarias, evidencias y límites de verificación. |
| [Matriz regulatoria histórica](../../06_Matriz_Trazabilidad_Regulatoria/matriz_trazabilidad_regulatoria.md) | `HISTÓRICO / SUPERSEDIDO` | Verificación de advertencia y no confusión con la matriz vigente; se conserva. |

## 3. Verificación de códigos RF/RNF

El inventario de códigos encontrados en el análisis vigente se contrastó con las referencias de los documentos revisados. Las anotaciones OpenAPI tienen 25 operaciones, 25 `operationId` únicos y 25 listas `x-requirements`; las referencias funcionales usadas corresponden al análisis.

| Grupo | Resultado |
|---|---|
| Requisitos funcionales vigentes | `RF-PRO-001`, `RF-INV-001`, `RF-INV-002`, `RF-COM-001`, `RF-VEN-001`, `RF-VEN-002`, `RF-CPE-001`, `RF-SUN-001`, `RF-MIG-001`, `RF-VAL-001`, `RF-TRZ-001`. |
| Requisitos no funcionales vigentes | `RNF-TEC-001`, `RNF-ARQ-001`, `RNF-SDD-001`, `RNF-DOC-001`, `RNF-SEG-001`, `RNF-REL-001`, `RNF-PER-001`, `RNF-MIG-001`. |
| Códigos no vigentes encontrados | `RF-CAT-001`, `RF-ALM-003` y otros `RF-*` antiguos aparecen en la matriz histórica o como ejemplos explícitos de identificadores de una revisión anterior en el modelo. No se usan como requisitos activos en los contratos actuales. |
| Clasificación que requiere cuidado | `RF-VEN-002` existe, pero el análisis lo clasifica como inferencia. `RNF-SEG-001`, `RNF-REL-001`, `RNF-PER-001` y `RNF-MIG-001` existen como propuestas, no como exigencias expresas. |

**Conclusión:** no se identificó un código RF/RNF inventado presentado como requisito vigente en los documentos activos revisados. Conviene conservar un control automatizable/manual que diferencie códigos de alcance activo de menciones históricas y confirme que la certeza de cada requisito no se eleve accidentalmente.

## 4. Contradicciones y brechas encontradas

| ID / prioridad | Hallazgo | Evidencia y consecuencia | Corrección recomendada |
|---|---|---|---|
| C-01 — Alta; corrección documental aplicada | La guía y consolidación trataban idempotencia como propuesta, mientras OpenAPI la declaraba obligatoria en escrituras y en los `PATCH`. | Corregido documentalmente: OpenAPI ahora marca el parámetro opcional, lo conserva solo en ocho comandos `POST`, y la guía/DTO/consolidación explican el alcance. No se ofrece garantía común sin clave. La política técnica todavía no está aprobada. | Antes de aprobar contratos, decidir obligatoriedad, alcance, ámbito, retención, replay, concurrencia y solicitudes en curso; distinguir reintentos HTTP de externos. Ver [revisión](./revision_idempotencia_inventario.md). |
| C-02 — Alta; propuesta armonizada, no aprobada | El endpoint genérico y la confirmación de venta podían interpretarse como dos fuentes de salidas. | Corregido documentalmente: OpenAPI, API, DTO, dominio y SPEC-002 proponen confirmación como única fuente de salidas por venta, y entradas/ajustes no originados por venta por el endpoint genérico. Catálogo de tipos/orígenes y reversos siguen pendientes; la propuesta no prueba por sí sola prevención técnica de duplicidad. | Aprobar el ciclo y origen único; definir tipos, referencias venta/línea, anulaciones/reversiones, tratamiento de solicitudes repetidas y pruebas de duplicidad. Ver [revisión](./revision_idempotencia_inventario.md). |
| C-03 — Bloqueante para contrato fiscal | Los contratos no permiten producir un comprobante UBL/SUNAT completo ni definir su resultado. | `CrearVentaDto` no incluye precios, moneda, descuentos, tributos ni totales; `SolicitarEmisionComprobanteDto` tiene `ventaId` y `tipoDocumento`; falta DTO de emisor/partes y documento fiscal detallado. Los estados/campos externos son genéricos. El informe de brechas ya documenta esta limitación. | No aprobar los contratos como completos para emisión fiscal. Primero acordar SEE y documentos, verificar perfil/fuentes vigentes y definir snapshot/documento fiscal, estados, identificador y respuesta conforme a esos contratos. |
| C-04 — Alta | La cardinalidad Venta–Comprobante está deliberadamente abierta y no se refleja como una regla contractual explícita. | Dominio: una venta puede tener `0..*` comprobantes y cada comprobante `0..1` venta. API solicita emisión con `ventaId`, pero no define unicidad, duplicidad permitida ni excepciones históricas. | Confirmar cardinalidad/ciclo al aprobar alcance documental; mantener estados locales y externos separados. No asumir emisión obligatoria ni aceptación inmediata. |
| C-05 — Alta | La migración tiene requisito expreso, pero aún no tiene contrato de datos o interfaz de ejecución definido. | `RF-MIG-001` existe; el servicio lógico API dice “sin operación pública definida”, el DTO declara migración pendiente y la arquitectura solo muestra un adaptador conceptual. Puede ser correcto no exponer una ruta pública, pero falta definir interfaz, entrada, reporte y aceptación antes de implementar. | Elegir interfaz interna/archivo/CLI/API solo tras obtener fuente y muestra legada; definir contratos de lote, errores, procedencia, repetición y conciliación. |
| C-06 — Media | La trazabilidad de endpoints es por RF, no por casos de uso identificados. | Todas las operaciones tienen `operationId`, finalidad y `x-requirements`, y la prosa las agrupa en servicios lógicos. No existen IDs UC estables ni criterios de aceptación verificables para cada operación. El mapeo permite justificar intención general pero dificulta revisión de cobertura y pruebas. | Incorporar una matriz endpoint → caso de uso → RF → criterio de aceptación/pruebas; no inventar un UC si la operación aún no se aprueba. |
| C-07 — Media | Algunas relaciones del dominio son propuestas pero su origen no está representado en DTOs. | `MovimientoInventario` puede referir a venta y recepción, pero la solicitud genérica solo transporta producto/almacén/tipo/cantidad/fecha/referencia textual; no se valida referencia de dominio. Esto limita trazabilidad y conciliación. | Aprobar tipos/orígenes y cómo se relacionan movimientos; decidir referencias tipadas sin exponer claves foráneas persistentes. |
| C-08 — Media | Puede confundirse validación estructural con aceptación normativa, aunque los documentos explican el límite. | La matriz y el marco distinguen XSD OASIS, reglas nacionales y respuesta externa; sin embargo, no existe instancia XML ni validación del perfil nacional y la vigencia consolidada es pendiente. | Mantener las advertencias; no usar “cumple SUNAT” como criterio de aprobación sin verificación normativa actual y evidencia de pruebas externas aplicables. |
| C-09 — Baja | La matriz histórica conserva contenido interno que antes se rotulaba aprobado y reglas no verificadas. | El encabezado, metadato y aviso superior indican que está `HISTÓRICO / SUPERSEDIDO`, prohíben usarla y remiten a la matriz vigente. Aun así, copias o enlaces profundos podrían exponer afirmaciones internas sin contexto. | Conservarla según la restricción del proyecto; enlazarla siempre mediante aviso y no citar sus filas como autoridad. |

### 4.1 Coherencia entre capas

- **Dominio ↔ DTO:** las referencias base (producto/almacén en movimiento, líneas de venta y solicitud de comprobante) son generalmente compatibles a nivel conceptual, pero el dominio expone relaciones/invariantes opcionales no transportadas por DTOs. Es aceptable para un borrador, no para cerrar contratos.
- **DTO ↔ API:** el OpenAPI referencia los DTOs existentes. Los datos mínimos del DTO de venta no bastan para totales fiscales, lo que coincide con la limitación declarada por el propio documento. La cabecera de idempotencia es una excepción de estado: la descripción de cabecera impone comportamiento a pesar de que su aprobación siga pendiente.
- **API ↔ arquitectura:** HTTP/REST y el adaptador MVC son propuestas. No se fijó autenticación, ni la arquitectura afirma protocolo SUNAT. Las operaciones de venta y comprobante tienen casos lógicos candidatos, no casos de uso aprobados.
- **Dominio ↔ arquitectura:** la separación comercial/documental es compatible con los adaptadores planteados. No deben añadirse a las entidades el protocolo, CDR o XML firmado sin decisión de retención/alcance.

## 5. Endpoints y relación con casos de uso

Los 25 métodos del OpenAPI tienen identificación y requisito anotado; las rutas listadas en la especificación API dan una finalidad legible. La agrupación propuesta es:

| Grupo lógico candidato | Operaciones/rutas | Requisitos | Evaluación |
|---|---|---|---|
| Catálogo | Listar/crear/obtener/actualizar productos | `RF-PRO-001` | Propósito trazable; operaciones HTTP y actualización son diseño propuesto. |
| Almacén | Listar/crear/obtener/actualizar almacenes | `RF-INV-001` | Trazable en alto nivel; número, unicidad y desactivación de almacenes pendientes. |
| Inventario | Listar/registrar/obtener movimientos | `RF-INV-002`, `RF-TRZ-001`, `RF-VAL-001` | Trazable; origen y relación con confirmación de venta no cerrados. |
| Terceros | CRUD parcial de clientes y proveedores | `RF-COM-001` | Trazable; criterios de actualización, identidad y duplicados pendientes. |
| Ventas | Listar/crear/obtener/confirmar venta | `RF-VEN-001`; `RF-VEN-002` inferido; `RF-INV-002` | Crear/consultar respaldados en intención; confirmar y efecto de inventario propuestos. |
| Comprobantes | Solicitar emisión y consultar estado local/externo | `RF-CPE-001`, `RF-SUN-001`, `RF-TRZ-001` | Intención trazable; no completa transmisión, documento fiscal, protocolo ni estados normativos. |
| Migración | Sin endpoint público ni operación de carga especificada | `RF-MIG-001`, `RF-VAL-001`, `RF-TRZ-001` | El requisito está reconocido, pero la interfaz/caso de uso de migración no está definido. No se debe concluir que necesariamente requiere endpoint público. |

**Conclusión de endpoint:** finalidad general y RF se identifican, pero no se satisface aún el criterio más fuerte de “cada endpoint relacionado a un caso de uso aprobado y verificable”. Antes de aprobación API deben añadirse identificadores/criterios de casos de uso o documentarse por qué cada operación es una opción explícitamente propuesta.

## 6. Requisitos con cobertura incompleta o sin contrato cerrado

| Requisito | Estado de cobertura | Evidencia / trabajo pendiente |
|---|---|---|
| `RF-MIG-001` | Capacidad expresada; sin DTO de carga aprobado ni interfaz/caso de uso operativo definido. | Inventariar fuente, formato, datos/periodo, errores, identificador de origen, conciliación y modo de ejecución. |
| `RF-CPE-001` | Intención documentada; contrato fiscal parcial. | Definir documentos y datos requeridos, numeración, importes, estados y mapeo al perfil UBL aplicable. |
| `RF-SUN-001` | Adaptador/puerto conceptual y consulta de estado local; integración efectiva no especificada. | Elegir SEE, contrato externo, autenticación, ambiente, respuestas y fallos con fuentes vigentes. |
| `RF-VAL-001` | JSON Schema cubre forma/tipos, no reglas semánticas. | Reglas por proceso, referencia válida, cantidades/unidades, duplicidad, importes y errores comprobables. |
| `RF-TRZ-001` | Hay referencias de origen y movimientos; granularidad, autoría y retención pendientes. | Definir qué entidades/eventos se trazan, procedencia migrada, correcciones, consulta y conservación. |
| `RF-VEN-001` / `RF-INV-002` | Operaciones propuestas; ciclo/consistencia no aprobados. | Definir estados, momento de contabilización, venta sin stock, salida por almacén, fallos/reversos y duplicidad. |
| `RNF-SEG-001`, `RNF-REL-001`, `RNF-PER-001`, `RNF-MIG-001` | Propuestas, no requisitos expresos, sin objetivos verificables. | Confirmar si el responsable las requiere; si se aceptan, establecer métricas y criterios medibles. |
| `RNF-TEC-001`, `RNF-ARQ-001`, `RNF-SDD-001`, `RNF-DOC-001` | Restricciones expresas reconocidas. | Aprobación formal de cómo se satisfacen sin convertir versión, protocolo o módulos propuestos en requisitos. |

## 7. Decisiones pendientes y dependencias

| Decisión | Bloquea o condiciona |
|---|---|
| Alcance de tipos de comprobantes | Modelo/cardinalidad Venta–Comprobante, DTO fiscal, endpoints de emisión, reglas y pruebas UBL. |
| Modalidad SEE | Reglas SUNAT aplicables, protocolo/adaptador, autenticación, estados, plazos y matriz normativa. |
| Alcance/tipos de venta y momento de efecto de inventario | Agregados, relaciones, comando de confirmación, endpoint de movimientos, transacciones y pruebas de concurrencia/fallo. |
| Actores y política de acceso | Roles/permisos, seguridad OpenAPI, atribución de auditoría y casos de autorización. |
| Fuente y alcance de migración | Mapeo de legado, DTO/interfaz de importación, trazabilidad, estrategia de repetición y conciliación. |
| Módulos/límites MVC y hexagonales | Responsabilidad de controladores/casos de uso/adaptadores; afecta organización de contratos y pruebas de dependencias. |
| Política de persistencia PostgreSQL | Modelo de identidad, atomicidad, concurrencia, conservación histórica, artefactos y criterios de recuperación. |
| Idempotencia de API y reintentos externos | Headers/respuestas, identidad de solicitud/documento y pruebas de replays/timeouts; API HTTP y SUNAT son problemas distintos. |
| Perfil normativo consolidado | Campos y validaciones fiscales, matrices y casos de prueba de conformidad. Depende de tipo documental y SEE. |

Orden recomendado: (1) autoridad y alcance de aprobación; (2) tipos de documentos y ciclo venta/inventario; (3) SEE y verificación normativa; (4) actores, permisos y migración; (5) límites arquitectónicos; (6) persistencia e idempotencia; (7) revisión conjunta de modelo, DTO/API, trazabilidad y pruebas. No es necesario alterar RF/RNF para responder preguntas de diseño, salvo autorización formal de cambio.

## 8. Fuentes SUNAT/UBL y límites de verificación

Las referencias y ubicaciones se encuentran en el [registro oficial](../../06_Matriz_Trazabilidad_Normativa/registro_fuentes_oficiales.md) y el [marco normativo](../../06_Matriz_Trazabilidad_Normativa/marco_normativo_ubl_sunat.md).

| Evidencia | Resultado de auditoría |
|---|---|
| RS N.° 117-2017/SUNAT | URL oficial, versión original y artículos utilizados identificados; sus disposiciones se declaran condicionales al SEE-OSE y modificaciones posteriores pendientes. |
| RS N.° 193-2020/SUNAT | URL oficial y artículos modificatorios identificados; el registro advierte que no es texto consolidado y no generaliza reglas de un SEE a otro. |
| RS N.° 097-2012/SUNAT | URL oficial indicada; el PDF original aparece escaneado/no legible en la extracción registrada. No se atribuyen reglas no verificadas. |
| OASIS UBL 2.1 / XSD Invoice | URLs oficiales, estándar y ubicaciones estructurales identificadas; el marco aclara que no representa el perfil nacional SUNAT. |
| Anexo nacional y vigencia consolidada | Pendientes de obtención/verificación; bloquean una conclusión de cumplimiento por tipo/SEE. |
| XML de instancia y servicio de pruebas | No hay instancia validada contra paquete XSD ni perfil nacional; no se transmitieron documentos a un entorno oficial. |

**Conclusión normativa:** la trazabilidad de las fuentes está documentada de forma suficiente para una revisión de diseño preliminar, no para afirmar cumplimiento. Toda regla normativa debe conservar ubicación, versión/fecha, SEE y tipo documental aplicable, resultado de verificación y prueba. Validar XSD OASIS no equivale a aceptación SUNAT/OSE.

## 9. Estado de la matriz histórica

La matriz de `06_Matriz_Trazabilidad_Regulatoria`:

- mantiene una advertencia al inicio que prohíbe usarla como fuente normativa o especificación aprobada;
- revoca explícitamente la etiqueta aprobada antigua;
- indica `HISTÓRICO / SUPERSEDIDO` en sus metadatos;
- enlaza la matriz y documentos normativos vigentes.

La condición de supersesión está clara en el archivo revisado. El riesgo residual es que enlaces profundos, copias exportadas o fragmentos tomados fuera de contexto expongan reglas antiguas. No se recomienda borrarla; se recomienda conservar siempre la advertencia y citar solo la matriz vigente.

## 10. Duplicación/contradicción entre ADR y consolidación

| Solapamiento | Resultado |
|---|---|
| ADR-0002 y decisiones D-07/D-01/D-05 | ADR-0002 contiene la propuesta de articulación MVC/hexagonal y adaptadores; la consolidación resume alternativas e impactos. No se encontró una selección aprobada contradictoria. |
| ADR-0003 y decisiones D-03/D-06 | ADR-0003 trata PostgreSQL y persistencia detallada; la consolidación retiene PostgreSQL como expreso y las estrategias como pendientes. Sus estados y clasificación actuales coinciden. |
| Matriz de brechas e informe de consolidación | Ambos señalan SEE, documentos, migración, DTO fiscal e idempotencia como pendientes. El informe de consolidación amplía dependencias y aprobadores, no sustituye la matriz normativa. |
| ADR-0001 | Su aceptación cubre el uso de ADR como mecanismo documental; no aprueba ADR-0002/0003 ni concede autoridad al equipo mencionado sin confirmación del responsable del proyecto. |

No se observó duplicación que cambie el sentido de una decisión, pero sí repetición de preguntas y pendientes. Para evitar deriva, el análisis debe seguir siendo fuente de requisitos, cada ADR fuente del rationale de su decisión, el informe de consolidación índice de decisiones, y la matriz normativa fuente de trazabilidad legal/técnica. Al aprobar, registrar autoridad y fecha en el ADR correspondiente y actualizar los resúmenes derivados.

## 11. Riesgos de iniciar implementación antes de aprobar

1. Implementar una interfaz fiscal que no contiene campos suficientes o no corresponde al SEE/tipo elegido.
2. Duplicar salidas de inventario por permitir tanto el registro genérico como la confirmación de venta.
3. Codificar una transacción atómica, bloqueo o prohibición de stock negativo que el negocio no ha aprobado.
4. Tratar como decidida la garantía idempotente o la fuente única de movimientos solo porque los borradores ahora expresan recomendaciones coherentes; ambas requieren aprobación y semántica/pruebas completas.
5. Migrar un subconjunto incorrecto, perder procedencia, duplicar registros o no poder conciliar la carga.
6. Diseñar roles/claims/seguridad basados en actores propuestos, o dejar expuestos datos comerciales/tributarios al no decidir controles.
7. Convertir reglas de resoluciones aisladas, material histórico o XSD genérico en supuestos de cumplimiento vigente.
8. Congelar contratos y diagramas mientras decisiones de tipos, identidad, cardinalidad y estados siguen abiertas, elevando cambios incompatibles y retrabajo.
9. Crear tablas, controladores, servicios, workers o infraestructura antes de completar la secuencia Spec-First exigida.

## 12. Correcciones recomendadas por prioridad

1. **P0 — Resolver para aprobar el contrato fiscal:** confirmar tipo(s) documental(es), SEE y responsable de validación normativa; verificar texto consolidado y anexos oficiales específicos. Mantener el estado actual mientras no se resuelva.
2. **P0 — Resolver el flujo de inventario:** decidir quién origina movimientos de venta y cómo se evita duplicidad; armonizar `MovimientoInventario`, DTO, API y caso de uso.
3. **P0 — Alinear idempotencia:** retirar el carácter obligatorio en OpenAPI hasta autorización o aprobar formalmente la garantía y completar sus reglas. Separar retry API de retry externo.
4. **P1 — Definir interfaz de migración:** no necesariamente un endpoint público; especificar caso de uso, entradas, validaciones, errores, repetición y conciliación.
5. **P1 — Trazar casos de uso:** asociar cada `operationId` a un caso de uso y criterio de aceptación; marcar explícitamente las operaciones que son propuestas, especialmente confirmación y emisión.
6. **P1 — Acordar seguridad:** confirmar actores/roles o documentar que el examen no requiere autenticación concreta; no incluir esquemas/roles en estado de obligación sin aprobación.
7. **P1 — Completar consistencia dominio/DTO/API:** resolver cardinalidad de comprobantes, origen del movimiento, estados locales/externos, identidad y reglas de actualización.
8. **P2 — Definir calidad y persistencia:** si se aprueban RNF propuestos, fijar métricas; definir después versión/configuración PostgreSQL, transacciones, concurrencia y retención.
9. **P2 — Mantener señalización normativa:** conservar advertencias de XSD/base vs perfil SUNAT y matriz histórica supersedida, y registrar evidencia concreta al completar validaciones.
10. **P2 — Normalizar gobierno de decisión:** confirmar quién aprueba; actualizar ADR y los resúmenes derivados con aprobador, fecha, estado, requisito/fuente y decisiones rechazadas.

## 13. Criterios objetivos para declarar especificaciones listas para aprobación

Una especificación puede pasar a aprobación formal solo si cumple los criterios que le aplican:

1. **Trazabilidad:** 100% de los códigos RF/RNF activos referenciados existen en el análisis; las inferencias y propuestas conservan su clasificación.
2. **Requisitos:** cada requisito tiene criterio de aceptación verificable o una pregunta pendiente explícita con responsable y fecha/condición de cierre.
3. **Decisiones:** cada decisión contiene una alternativa seleccionada, rationale, consecuencias y aprobador/autoridad; ninguna queda seleccionada solo por repetirse en varios documentos.
4. **Dominio:** entidades, cardinalidades e invariantes están alineadas con reglas aprobadas; supuestos opcionales y datos históricos están diferenciados.
5. **DTO/Schema:** cada contrato corresponde al dominio y tiene validación estructural comprobable; reglas semánticas, precisión, catálogos y errores están especificados donde apliquen.
6. **API:** cada endpoint tiene propósito, caso de uso identificado, RF válido, DTO, estados/respuestas y criterio de aceptación. Las operaciones no aprobadas están marcadas como propuestas también en OpenAPI; propiedades `required` coinciden con la decisión aprobada.
7. **Consistencia:** venta-inventario no permite doble contabilización; cardinalidades de comprobantes y estados de emisión/transmisión/resultado están explícitos; repetición/timeout está definido si se ofrece idempotencia.
8. **Migración:** está definido el contrato de entrada, procedencia, validación, reporte de error, deduplicación, conciliación, repetición y aceptación; no se exige endpoint público salvo decisión expresa.
9. **Seguridad:** actores, autenticación/autorización y permisos son confirmados o la ausencia de requisito queda aceptada explícitamente; OpenAPI y casos de prueba reflejan el estado acordado.
10. **Arquitectura y persistencia:** diagramas reflejan decisiones aprobadas sin fijar protocolos o mecanismos pendientes; PostgreSQL se mantiene y su configuración concreta se justifica por requisitos/entorno.
11. **Normativa:** cada regla legal/técnica utilizada tiene fuente oficial, versión/fecha, ubicación, aplicabilidad documental/SEE y límite de verificación; se han inspeccionado las fuentes consolidadas/anexos requeridos o se declara formalmente qué cobertura no se puede aprobar.
12. **Pruebas futuras:** existe una matriz de regla/requisito → caso de prueba para negocio, API, migración y validación UBL/SEE aplicables; los casos no ejecutados están rotulados como propuestos, no como evidencia.
13. **Gobierno:** todos los documentos que requieren aprobación mantienen `DRAFT` hasta autorización explícita, con responsable y fecha; la matriz histórica permanece claramente supersedida.
14. **Control Spec-First:** los contratos, decisiones y criterios de aceptación quedan aprobados antes de planificar o autorizar implementación; no hay una autorización implícita derivada de un ADR `PROPOSED`.

## 14. Veredicto

- **Listo para iniciar revisión formal de decisiones:** sí, con el [informe de consolidación](./informe_consolidacion_decisiones.md) como agenda y sin tratar sus alternativas como aprobadas.
- **Listo para aprobación formal de todos los contratos/especificaciones:** no. Persisten bloqueos P0 y brechas de contrato fiscal, consistencia de movimientos e idempotencia.
- **Listo para implementar:** no. `SPEC-002`, dominio, DTOs, API, arquitectura y matriz normativa están en revisión; los contratos fiscales y de migración no están cerrados, y el análisis mismo continúa siendo borrador sujeto a confirmación.

Este veredicto no cambia ningún estado ni requisito. La auditoría es documental y no afirma cumplimiento tributario.
