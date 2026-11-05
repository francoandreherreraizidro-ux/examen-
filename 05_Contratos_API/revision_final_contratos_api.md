# Revisión final de contratos API

| Campo | Valor |
|---|---|
| Contrato revisado | `API-SISTEMA-COMERCIAL` |
| Estado | `DRAFT` — no aprobado para implementación |
| OpenAPI | [sistema-comercial.openapi.yaml](../specs/api/sistema-comercial.openapi.yaml) |
| Guía API | [especificacion_contratos_api.md](./especificacion_contratos_api.md) |
| Contratos DTO | [dtos_schemas.md](../03_DTOs_Schemas/dtos_schemas.md), [dto-contracts.schema.json](../03_DTOs_Schemas/dto-contracts.schema.json) |
| Referencia dominio-contratos | [modelo_dominio_contratos.md](../03_DTOs_Schemas/modelo_dominio_contratos.md) |
| Requisitos base | [analisis_requisitos.md](../01_Analisis_Requisitos/analisis_requisitos.md) |

## 1. Resumen ejecutivo

La revisión detectó inconsistencias entre los contratos API y sus documentos de apoyo, principalmente en la actualización parcial de recursos, el momento en que una venta afecta el inventario y la ubicación de la clave de idempotencia. También faltaba una transición explícita para confirmar la venta y se podía interpretar que el endpoint genérico de movimientos aceptaba una salida que duplicara el efecto de una venta.

Se actualizaron los documentos para hacer explícitas las propuestas, separar las decisiones confirmadas de las pendientes, y alinear OpenAPI y DTOs. La propuesta central es registrar la venta inicialmente sin afectar inventario y confirmar la venta en una operación aparte, persistiendo la confirmación y sus movimientos de forma atómica. Esto no es un requisito aprobado: debe revisarse con el responsable del caso.

El contrato permanece `DRAFT`. No recomiendo aprobarlo todavía: siguen abiertas decisiones de negocio, seguridad, idempotencia, migración, inventario y modalidad SUNAT. Tampoco se dispuso de un validador específico de OpenAPI 3.1 para ejecutar una validación normativa completa.

## 2. Problemas detectados y correcciones

| Problema | Corrección realizada |
|---|---|
| La guía describía 20 operaciones en 14 rutas y afirmaba que no había `PATCH`, aunque el borrador estaba siendo ampliado. | Se actualizó la tabla de operaciones. El contrato revisado contiene 25 operaciones en 15 rutas. Todas continúan siendo propuestas. |
| No había una transición explícita para determinar cuándo la venta produce sus movimientos de inventario. La operación de alta de movimientos podía ocasionar un doble registro. | Se agregó `POST /api/v1/ventas/{ventaId}/confirmar` con `ConfirmarVentaDto`; se recomienda que sea la única fuente de salidas originadas por ventas y que el endpoint genérico registre solo entradas/ajustes no originados por venta. Es una propuesta pendiente, junto con catálogo, anulaciones/reversiones y política de stock. |
| Se mencionaba en la guía que la salida de inventario de una venta no estaba decidida, mientras que el contrato OpenAPI ya describía la creación de venta como registro sin efecto de inventario. | Se alinearon guía, OpenAPI y documentación DTO con el flujo de dos etapas propuesto, identificándolo expresamente como decisión arquitectónica pendiente. |
| La idempotencia se incluía como campo opcional del cuerpo de emisión y no se aplicaba consistentemente al resto de las escrituras. | Se retiró la clave de los DTOs. Esta revisión anterior propuso el encabezado común; la revisión documental posterior lo limita provisionalmente a los ocho comandos `POST`, opcional en OpenAPI y pendiente de aprobación. Los cuatro `PATCH` descriptivos no lo incluyen. Alcance, retención y comportamiento concurrente siguen pendientes. |
| Los contratos no describían reglas uniformes de actualización. | Se añadieron `PATCH` propuestos para producto, almacén, cliente y proveedor, con `application/merge-patch+json`; se limitan a datos descriptivos y no habilitan modificar identificadores o historia. No se ofrece borrado físico. |
| El Schema JSON tenía inconsistencias entre DTOs de creación, actualización y respuesta de venta. | Se alinearon las formas de actualización parcial, contacto de terceros y líneas de venta: la entrada no incluye ID de detalle y la salida sí lo incluye para permitir su referencia al confirmar. |
| La autenticación y los roles podían inferirse de actores propuestos como si ya estuvieran aprobados. | Se dejó OpenAPI sin un esquema de autenticación obligatorio y se clasificó OIDC/OAuth2/JWT y los perfiles candidatos como propuestas. Se documentó la necesidad de añadir `401`/`403` cuando se apruebe seguridad. |
| La migración histórica carecía de fuentes y formato; un endpoint genérico habría fijado supuestos prematuros. | Se mantiene sin endpoint público y se propone estudiar un proceso administrativo de migración con staging, IDs de origen, validación, reporte, conciliación y aprobación. |
| No estaba definida la modalidad de integración SUNAT. | Se conserva un contrato de negocio neutral y se propone aislar el proveedor detrás de un adaptador. No se selecciona SEE directo, OSE ni otro proveedor, ni se afirma cumplimiento normativo. |

## 3. Decisiones y nivel de certeza

### Confirmadas por los requisitos fuente

- Gestionar productos y almacén; registrar movimientos; administrar clientes/proveedores y ventas; emitir comprobantes electrónicos; integrar con SUNAT; migrar datos históricos; validar datos y conservar trazabilidad.
- Los códigos `RF-*` se conservaron. La relación venta-comprobante continúa identificada como inferencia cuando corresponde a `RF-VEN-002`.
- El contrato y `SPEC-002` siguen en `DRAFT`; no hay autorización para implementar.
- El requerimiento de integrar con SUNAT no establece protocolo, operador, ambiente, formato de intercambio ni modalidad.
- No se identifican en el análisis reglas aprobadas sobre verbos HTTP, actualización, roles, stock no negativo, costo promedio, idempotencia técnica o atomicidad de la venta.

### Propuestas arquitectónicas

| Tema | Propuesta documentada |
|---|---|
| Métodos HTTP y cambios | `GET` para consulta, `POST` para alta/transición y `PATCH` para cambios parciales descriptivos. Sin `PUT` general ni eliminación física. La semántica de `PATCH` distingue propiedad ausente (sin cambio) y `null` (eliminar el campo opcional documentado). |
| Venta e inventario | Crear una venta inicial sin movimiento y usar una confirmación explícita que aplique los movimientos asociados. La propuesta recomienda que confirmar venta sea la única fuente de sus salidas y que el movimiento genérico no registre salidas originadas por ventas. Atomicidad, ciclo de confirmación y reversos siguen pendientes de aprobación. |
| Seguridad | Evaluar un proveedor OIDC/OAuth2 con JWT o equivalente, autorización de mínimo privilegio y permisos por capacidad. Los perfiles de administrador, almacén, vendedor/cajero y responsable contable son solo candidatos. |
| Idempotencia | Ofrecer `Idempotency-Key` opcional para ocho comandos `POST` con efectos duplicables; excluye los cuatro `PATCH` descriptivos. Si se aprueba, igual clave/huella en el mismo ámbito no repetiría el efecto y distinta huella produciría conflicto. Resultado almacenado, ámbito, obligatoriedad, expiración y solicitudes en curso siguen pendientes. |
| Migración | Mantenerla fuera de la API de negocio por ahora; evaluar proceso administrativo por lotes/CLI con staging, identificadores de origen, validación, errores conciliables y aprobación antes de activar datos. |
| SUNAT | Desacoplar el proveedor mediante un adaptador y mantener la API externa neutral respecto del protocolo hasta decidir modalidad y validar fuentes oficiales aplicables. |

### Pendientes de aprobación

- Verbo, campos editables y política de desactivación por recurso; identidad y formatos.
- Etapas de la venta, momento comercial de confirmación, almacenes permitidos, stock insuficiente, concurrencia, cancelación y reversos.
- Catálogo de tipos y orígenes de movimientos y regla que impida duplicar una salida de venta.
- Proveedor de identidad, autenticación, permisos, claims/scopes, aprovisionamiento y matriz de autorización. `401`/`403` no están incorporados aún a las respuestas.
- Alcance y vencimiento de idempotencia, tratamiento de solicitudes concurrentes/en curso y comportamiento luego de una caída.
- Fuentes legadas, volumen, periodo, mapeos, deduplicación, conciliación, autorizador y estrategia de reversa.
- Modalidad SUNAT, autenticación y ambiente, formato de intercambio, estados y manejo de reintentos. También está pendiente definir el perfil UBL aplicable y validarlo contra documentación oficial vigente.
- Campos DTO, importes, precisión decimal, catálogos de estado, paginación y catálogo de errores.

Como propuesta inicial no vinculante, el administrador podría gestionar catálogos y usuarios; almacén, movimientos no generados por venta; vendedor/cajero, consultas y registro de ventas; y el responsable contable, solicitudes y consultas de comprobantes. Se dejan explícitamente pendientes las capacidades que cruzan esos límites, en particular quién confirma una venta, quién puede emitir/reintentar y quién consulta datos tributarios. Los actores `ACT-P02`–`ACT-P05` son candidatos, no roles de seguridad confirmados.

## 4. Revisión de coherencia de los contratos

- **Trazabilidad:** los 25 métodos tienen `x-requirements` en OpenAPI y la guía asocia sus operaciones con códigos RF. La confirmación de venta y la relación de emisión con venta son propuestas y no deben interpretarse como requisitos aprobados.
- **DTOs:** las operaciones usan DTOs explícitos y no exponen entidades de persistencia. Los cuerpos de creación no aceptan estado interno; la confirmación usa IDs de línea de la respuesta de venta.
- **Respuestas y errores:** las respuestas exitosas distinguen creación (`201`), confirmación (`200`) y aceptación de solicitud de emisión (`202`). Los errores documentados usan `application/problem+json`; las causas de `409`/`422` requieren catálogo de negocio. La seguridad pendiente impide cerrar todavía los errores `401`/`403`.
- **Venta e inventario:** el DTO de confirmación asigna cantidades de líneas a almacenes. La cobertura exacta, inexistencia de asignaciones duplicadas y atomicidad deben validarse semánticamente; JSON Schema no puede probar esas invariantes.
- **Comprobantes:** emisión local y resultado de integración permanecen separados. Una respuesta `202` no significa envío o aceptación SUNAT. No se presupone que toda venta tenga un comprobante aceptado de inmediato ni se expone una operación de reintento externo.
- **Reintentos:** la clave HTTP propuesta previene duplicados de API si se implementa correctamente. Un reintento hacia SUNAT es un flujo distinto y necesita identidad documental persistida y estados que permitan evitar crear otro documento.
- **Conflicto documental:** la versión anterior de `ADR-0003` incluía costo promedio, stock no negativo y atomicidad/Outbox, que no aparecen aprobados en el análisis actual. La ADR fue reconciliada y pasó a `PROPOSED`; PostgreSQL se mantiene como restricción expresa, y sus detalles de persistencia requieren autorización. La [revisión de ADR-0002/0003](../specs/architecture/revision_ADR_0002_0003.md) documenta las preguntas y los contratos afectados.

## 5. Validación realizada y pendiente

### Ejecutado

1. El panel de diagnósticos de VS Code no informó errores para `sistema-comercial.openapi.yaml` ni para `dto-contracts.schema.json`. Este resultado solo informa los diagnósticos disponibles en el editor; no equivale a validar OpenAPI 3.1.
2. Se parseó `dto-contracts.schema.json` con `ConvertFrom-Json`: correcto.
3. Se realizaron verificaciones estáticas del texto OpenAPI y DTO:
   - 15 rutas, 25 operaciones y 25 `operationId` únicos.
   - 12 operaciones mutables y 12 referencias al parámetro `Idempotency-Key`.
   - 47 referencias externas a `$defs` del DTO y ninguna definición ausente.
   - La línea de entrada de venta no requiere ID; la línea de salida sí lo expone.
4. Se comprobó disponibilidad de herramientas de línea de comandos: no se encontraron `redocly`, `swagger-cli`, `openapi-generator-cli`, `vacuum`, `spectral`, `yq`, Node.js ni npm. No se instalaron dependencias.

### No ejecutado / por validar

- No se ejecutó validador formal de OpenAPI 3.1, parser YAML dedicado, resolutor completo de referencias OpenAPI, validador JSON Schema Draft 2020-12 ni validación de ejemplos contra los schemas.
- El chequeo estático de referencias externas no prueba la validez integral de la especificación, sus restricciones semánticas ni la interoperabilidad con una herramienta de generación/documentación.
- No se validaron reglas de negocio en runtime ni contratos SUNAT/UBL contra fuentes oficiales; no existe implementación en esta etapa.

## 6. Recomendación

**No aprobar todavía los contratos.** Mantener `API-SISTEMA-COMERCIAL`, OpenAPI y DTOs en `DRAFT` hasta resolver los pendientes críticos de venta/inventario, seguridad, idempotencia y migración, y hasta elegir y verificar la modalidad SUNAT. Después, ejecutar un validador OpenAPI 3.1 aprobado, resolver todas las referencias externas y revisar el contrato actualizado con los responsables del dominio.

Esta revisión no implementa código, no crea endpoints en una aplicación, no instala dependencias y no avanza a la matriz normativa.
