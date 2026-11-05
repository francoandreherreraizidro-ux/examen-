# Revisión de idempotencia e inventario

| Campo | Valor |
|---|---|
| Estado | `DRAFT` — recomendaciones documentales, no aprobadas |
| Fecha | 2026-11-04 |
| Alcance | Resolver discrepancias documentales de idempotencia y origen de movimientos por ventas |
| Requisitos base | `RF-VEN-001`, `RF-INV-002`, `RF-VAL-001`, `RF-TRZ-001`; ninguna de estas referencias expresa por sí sola una garantía técnica de idempotencia |
| Referencias principales | [Análisis de requisitos](../../01_Analisis_Requisitos/analisis_requisitos.md), [SPEC-002](../features/002-sistema-comercial-facturacion.md), [consolidación de decisiones](./informe_consolidacion_decisiones.md), [contrato API](../../05_Contratos_API/especificacion_contratos_api.md), [OpenAPI](../api/sistema-comercial.openapi.yaml) |

## 1. Resumen ejecutivo

Se identificaron dos problemas de coherencia:

1. La prosa describía la idempotencia como propuesta, mientras OpenAPI hacía obligatorio `Idempotency-Key` en doce operaciones, incluidos cuatro `PATCH` descriptivos.
2. El endpoint genérico de movimientos aceptaba un tipo libre, mientras confirmar una venta también proponía generar movimientos, sin que el contrato asignara de manera inequívoca quién crea una salida de venta.

La corrección documental propuesta es:

- Recomendar idempotencia mediante clave para los ocho comandos `POST` con efectos de creación o transición. Mantener el encabezado opcional en OpenAPI hasta que se aprueben obligatoriedad, alcance y semántica. Excluir los cuatro `PATCH` de la propuesta común: repetir un Merge Patch de asignación de campos no crea un segundo recurso; el control de concurrencia es una cuestión distinta.
- Asignar como única fuente propuesta de salidas de inventario por venta a la operación de confirmación de venta. El registro genérico quedaría para entradas y ajustes no originados por ventas. Las anulaciones/reversiones deberán ser eventos compensatorios ligados al movimiento original y, cuando aplique, a la venta; no se borrará ni se registrará una segunda salida manual por el mismo hecho.
- No decidir stock negativo, valoración/costo, momento comercial de confirmación, política de cancelación, periodo de retención de claves ni el comportamiento de llamadas concurrentes o de resultados externos inciertos.

Estas son recomendaciones arquitectónicas sujetas a aprobación. No modifican requisitos ni elevan `RF-VEN-002`, que sigue siendo una inferencia relativa al vínculo venta-comprobante.

## 2. Evidencia y problemas encontrados

| Tema | Evidencia inspeccionada antes de esta corrección | Problema |
|---|---|---|
| Idempotencia en OpenAPI | `Idempotency-Key` se referencia desde `crearProducto`, `actualizarProducto`, `crearAlmacen`, `actualizarAlmacen`, `registrarMovimientoInventario`, `crearCliente`, `actualizarCliente`, `crearProveedor`, `actualizarProveedor`, `crearVenta`, `confirmarVenta` y `solicitarEmisionComprobante`. El parámetro común decía `required: true`. | El contrato imponía una cabecera y una garantía aún no aprobadas. Además, incluía `PATCH` de atributos descriptivos junto con comandos que sí pueden duplicar recursos o efectos. |
| Idempotencia en documentación | La guía API la describía como obligatoria en escrituras y en otra sección como propuesta; DTOs indicaban que se aplicaba a operaciones mutables. La consolidación dejaba la decisión pendiente. | Clientes podrían interpretar obligatoriedad diferente según el documento consultado. |
| Reintento externo | La documentación diferencia el reintento HTTP de la comunicación con SUNAT, pero no define una identidad documental/reconciliación aprobada. | Repetir una llamada API y volver a transmitir un documento son problemas distintos; una clave HTTP no resuelve por sí sola un timeout externo incierto. |
| Dos orígenes para salida de venta | `POST /api/v1/inventario/movimientos` acepta un `tipo` como texto y `referenciaOrigen` opcional. `POST /api/v1/ventas/{ventaId}/confirmar` propone aplicar movimientos. El modelo permite relacionar movimientos con una venta. | Un consumidor podría enviar una salida manual por la venta y luego confirmar la venta, o reintentar uno de los comandos y duplicar el efecto. El DTO de entrada genérico no fija un origen de venta, pero tampoco restringe el tipo libre. |
| Ajustes y reversiones | El requisito `RF-INV-002` expresa historial de ingresos, salidas y otros movimientos; tipos, correcciones y reversiones siguen pendientes. | No hay contrato aprobado para distinguir ajustes manuales, entradas, ventas y anulaciones/reversiones. |

## 3. Idempotencia

### 3.1 Problema que resuelve

Un consumidor puede perder la respuesta por timeout o desconexión después de que el servidor haya aplicado una operación. Si la repite sin identidad de solicitud, el servidor podría crear otro recurso, registrar otro movimiento, otra venta o una segunda solicitud de emisión. La idempotencia permite reconocer una repetición de la misma intención y devolver/identificar el resultado previo en vez de volver a aplicar el efecto.

No sustituye validación de negocio, restricciones de unicidad, autorización, control de concurrencia ni reconciliación de una operación externa. Tampoco convierte en seguro un reintento con una clave nueva.

### 3.2 Operaciones que requieren protección contra duplicados

**Recomendación propuesta:** ofrecer `Idempotency-Key` opcional en el borrador para los ocho `POST` siguientes. Si se aprueba la garantía, definir si la clave será obligatoria o si solo se ofrece protección cuando se envía:

| Operación | Protección recomendada | Motivo |
|---|---|---|
| `crearProducto` | Sí, candidata | Evitar dos altas si la primera respuesta se pierde; las reglas de identidad comercial/unicidad siguen pendientes. |
| `crearAlmacen` | Sí, candidata | Evitar altas repetidas ante reintento; operación aún propuesta. |
| `registrarMovimientoInventario` | Sí, prioritaria | Una repetición puede duplicar un evento histórico y su efecto en existencias. |
| `crearCliente` | Sí, candidata | Evitar duplicar un alta ante pérdida de respuesta; no asumir identificador único obligatorio. |
| `crearProveedor` | Sí, candidata | Evitar duplicar un alta ante pérdida de respuesta; no asumir identificador único obligatorio. |
| `crearVenta` | Sí, prioritaria | Evitar registrar dos ventas por la repetición de una solicitud cuyo resultado se desconoce. |
| `confirmarVenta` | Sí, prioritaria | Evitar volver a aplicar los movimientos de una confirmación ya procesada. Además se necesita una regla de transición/estado que impida contabilizar dos veces aun si cambia la clave. |
| `solicitarEmisionComprobante` | Sí, prioritaria | Evitar crear dos solicitudes/documentos lógicos por una repetición API. La repetición de transmisión externa requiere identidad y reconciliación separadas. |

Los cuatro `PATCH` (`actualizarProducto`, `actualizarAlmacen`, `actualizarCliente`, `actualizarProveedor`) quedan fuera de esta recomendación común. Repetir el mismo Merge Patch de asignación descriptiva no crea por sí mismo un duplicado. Si se necesita proteger actualizaciones concurrentes o detectar una versión obsoleta, debe diseñarse control de concurrencia separado; no asumir que la clave de idempotencia lo resuelve.

### 3.3 Alternativas

1. **Sin garantía técnica compartida:** mantener comandos sin cabecera; prevenir duplicados con reglas específicas (identidad de operación, unicidad y estados). Es lo más simple, pero no permite recuperar con certeza el resultado de una petición perdida y deja mayor carga a cada caso de uso.
2. **Clave opcional en `POST` seleccionados (recomendación):** el cliente envía clave para los comandos que requieren reintento seguro; la obligatoriedad puede añadirse solo después de aprobar contrato y consumidores. Balancea alcance y protección, pero el comportamiento sin clave debe quedar explícito.
3. **Clave obligatoria en todos los comandos mutables:** cobertura uniforme en todas las escrituras. Aumenta requisitos para consumidores y operaciones de almacenamiento/expiración; no aporta el mismo valor a un `PATCH` descriptivo que a una creación o contabilización.
4. **Protección específica por operación sin encabezado común:** usar claves de negocio/transición para ventas, movimientos y documentos. Puede expresar mejor la identidad natural, pero genera contratos y semánticas distintas por operación.

La selección final y la obligatoriedad necesitan aprobación del responsable. OpenAPI refleja provisionalmente la alternativa 2 como propuesta no obligatoria; no afirma una garantía operativa si no se envía clave.

### 3.4 Información que se necesitaría conservar

Si se aprueba la alternativa de clave común, almacenar como mínimo, durante un periodo por definir:

- clave recibida y ámbito de unicidad (por identidad de cliente/actor si existe, operación/ruta y versión del contrato; identidad/titularidad aún pendientes);
- huella del contenido normalizado de la solicitud, incluyendo método y recurso, excluyendo la propia clave y datos volátiles que se acuerde ignorar;
- estado de procesamiento (por ejemplo, en curso o finalizado), resultado HTTP y una referencia al recurso/resultado creado; decidir si se conserva el cuerpo de respuesta completo;
- fecha de inicio/cierre, expiración y datos de correlación necesarios para diagnóstico, sin almacenar innecesariamente el cuerpo sensible en claro.

Regla propuesta: misma clave y mismo ámbito con huella igual no repite el efecto y permite recuperar el resultado previo; misma clave y ámbito con huella diferente produce conflicto. La reserva de clave y el efecto de negocio deben coordinarse para evitar duplicados concurrentes. Si la primera solicitud sigue en curso, la respuesta y política de espera/reintento están pendientes. La retención, limpieza y tratamiento tras expiración requieren aprobación.

Para una emisión SUNAT, la clave API solo identifica la solicitud local. También se necesita una identidad estable del documento y registro del estado/resultado externo para consultar o reconciliar un timeout antes de retransmitir. No se debe inferir el mecanismo remoto hasta seleccionar SEE y verificar sus contratos.

## 4. Origen y consistencia de movimientos de inventario

### 4.1 Recomendación de responsabilidad única

**Propuesta:** la confirmación de la venta es el único caso de uso que crea movimientos de salida originados por esa venta. El movimiento generado debe poder rastrearse a `ventaId` y, cuando sea posible, a `detalleVentaId`; la operación genérica no debe aceptar una salida etiquetada como venta.

La confirmación sería idempotente por operación lógica y su estado debe impedir una segunda contabilización incluso si el cliente envía otra clave. Si el mismo comando se repite, se recupera el resultado previo; si pretende una transición incompatible, se devuelve un conflicto según regla a aprobar. No se fija aquí atomicidad, política de saldo, reserva, costo o momento comercial fuera de la propuesta de confirmación ya documentada.

### 4.2 Tipos y origen propuestos

| Hecho de inventario | Origen/responsabilidad propuesta | Tratamiento contractual propuesto |
|---|---|---|
| Entrada no originada por venta | Registro de movimiento genérico o, si se aprueba, un caso de uso de recepción. | Distinguir tipo de entrada y referencia de origen. El vínculo obligatorio con proveedor/recepción no está decidido. |
| Ajuste manual positivo o negativo | Caso de uso genérico de ajuste, con motivo y trazabilidad del actor cuando la identidad se defina. | Tipo/origen explícitos; no interpretar el signo como política de existencias ni de valoración. |
| Salida por venta | Exclusivamente la confirmación de la venta. | Generada desde las líneas/asignaciones validadas; devolver/consultar IDs de movimientos asociados. No aceptar la misma salida por el endpoint genérico. |
| Anulación de venta o reversión de movimiento | Caso de uso explícito de cancelación/reversión, ligado al movimiento original y a la venta cuando aplique. | Registrar evento compensatorio rastreable; no borrar ni editar el movimiento original. No se propone aún una ruta pública ni se decide cuándo se permite revertir. |

El catálogo definitivo de tipos/orígenes, la granularidad por línea/almacén, si los ajustes requieren autorización, las cantidades de compensación y los estados válidos quedan pendientes de aprobación. No se asume prohibición de stock negativo ni método de costo.

### 4.3 Alternativas consideradas

1. **Confirmación de venta como única fuente de sus salidas (recomendación):** elimina el doble origen y mantiene la relación venta–movimiento en el caso comercial que conoce las líneas. El endpoint genérico se limita a hechos no originados por venta.
2. **Todos los movimientos ingresados por el endpoint genérico:** confirmación de venta solo cambia estado; requiere que el cliente construya y envíe todos los movimientos y que exista correlación transaccional/identidad común entre ambas llamadas. Aumenta el riesgo de confirmación sin salida o doble salida.
3. **Permitir ambos flujos con deduplicación posterior:** podría aceptar más interfaces, pero exige una identidad natural obligatoria, reglas de reconciliación y resolución de carreras; no es recomendable sin requerimientos y contratos específicos.

La recomendación no aprueba la operación `confirmarVenta`, atomicidad ni su calendario. Si el responsable elige otra alternativa, deberá actualizar modelo, DTO, API, arquitectura y pruebas conjuntamente.

## 5. Requisitos relacionados y clasificación

| Código | Relación | Clasificación |
|---|---|---|
| `RF-VEN-001` | Registrar ventas; no fija su transición ni la garantía de repetición. | Expreso para la capacidad; flujo de confirmación propuesto. |
| `RF-INV-002` | Mantener historial de ingresos, salidas y otros movimientos. | Expreso para el historial; tipos, origen único y reversos pendientes. |
| `RF-VAL-001` | Validar datos tratados. | Expreso; huella/idempotencia y regla de transición son decisiones de diseño propuestas, no validaciones ya exigidas. |
| `RF-TRZ-001` | Conservar trazabilidad acordada. | Expreso; ligar movimiento a venta/línea y registrar correlación son propuestas que requieren concretar alcance. |
| `RF-VEN-002` | Relacionar venta y datos de comprobante. | Inferido; no fundamenta por sí solo ni la idempotencia ni el movimiento de salida. |
| `RNF-TEC-001` | PostgreSQL como motor. | Expreso; no determina el esquema o mecanismo de idempotencia. |

## 6. Impacto documental

| Documento | Ajuste necesario |
|---|---|
| `specs/api/sistema-comercial.openapi.yaml` | Hacer opcional el parámetro compartido hasta aprobación; aplicarlo solo a los ocho `POST`; aclarar el alcance propuesto y que el endpoint genérico no genera salidas de venta. |
| `05_Contratos_API/especificacion_contratos_api.md` | Enumerar operaciones cubiertas, omitir `PATCH`, describir información/semántica por acordar y distinguir reintento API de retransmisión externa; precisar origen único de salida. |
| `03_DTOs_Schemas/dtos_schemas.md` y `dto-contracts.schema.json` | Mantener clave fuera del cuerpo; describir el tipo/origen genérico como pendiente y excluyente de salidas de venta; identificar referencias de venta en movimientos generados por confirmación. No cerrar catálogos ni añadir reglas de stock/costo. |
| `02_Modelo_Dominio/modelo_dominio.md` | Explicitar como propuesta el origen único de movimientos de venta y la relación trazable con venta/línea; documentar reversiones compensatorias como alternativa, no invariante aprobada. |
| `specs/features/002-sistema-comercial-facturacion.md` | Registrar las dos propuestas junto a los pendientes, sin crear requisitos nuevos ni cambiar certeza. |
| `specs/architecture/informe_consolidacion_decisiones.md` | Refinar D-03/D-09 y mantenerlas pendientes de aprobación. |
| `05_Contratos_API/revision_final_contratos_api.md` | Alinear el registro de correcciones con el estado documental actual, preservando que fue una revisión previa. |
| `specs/architecture/auditoria_coherencia_especificaciones.md` | Marcar C-01/C-02 como discrepancias documentales corregidas y conservar las decisiones de fondo pendientes. |
| Pruebas futuras | Proponer pruebas de repetición, concurrencia, cambio de huella, timeout, re-confirmación, duplicidad de movimiento y reverso; no son evidencia ejecutada. |

### Cambios por elemento de diseño si se aprueba

| Elemento | Idempotencia | Inventario/ventas |
|---|---|---|
| Entidades y relaciones del dominio | No agregar la clave HTTP como atributo comercial por defecto; si se necesita persistencia, definir un registro técnico de solicitudes/resultado separado del agregado de negocio. | Expresar tipo/origen; relacionar las salidas generadas con `Venta` y, de ser posible, `DetalleVenta`; enlazar reversión al movimiento original. La relación `Venta 1 — 0..* MovimientoInventario` sigue como propuesta. |
| DTO / Schema | Mantener la clave en header, nunca en el body. Documentar qué comandos la admiten y no prometer comportamiento si se omite. | Restringir semánticamente el DTO genérico a entradas/ajustes; incluir referencias de venta/línea solo en movimientos generados por confirmación. Definir un DTO separado de reversión solo si se aprueba su caso de uso. No fijar todavía enum, stock, costo o campos de autorización. |
| Endpoints / OpenAPI | Header opcional en los ocho `POST`; definir respuesta replay, conflicto por huella y clave reutilizada tras expiración antes de cerrar contrato. | `POST /inventario/movimientos` para eventos no originados por ventas; `POST /ventas/{ventaId}/confirmar` como única fuente propuesta de salidas. No hay endpoint de cancelación/reversión definido; no añadirlo sin aprobarlo. |
| Casos de uso | Reservar/consultar clave, comparar huella, ejecutar una vez y recuperar resultado previo; definir el comportamiento de llamadas concurrentes/en curso. | Confirmar venta y generar sus movimientos; registrar entradas/ajustes; cancelar/revertir mediante evento compensatorio según política. La secuencia y límite transaccional siguen pendientes. |
| Pruebas futuras | Misma clave/huella no duplica; misma clave con contenido distinto genera conflicto; solicitudes concurrentes no aplican dos veces; pérdida de respuesta y expiración; timeout externo separado de replay HTTP. | Rechazo de salida por venta desde endpoint genérico; confirmación repetida no crea segunda salida incluso con clave distinta; rastreo a venta/línea; reversión enlazada sin editar original. No afirmar rechazo por stock insuficiente ni probar política de costo no aprobada. |

ADR-0002 no necesita cambio: no selecciona estas semánticas. ADR-0003 ya indica que idempotencia y reintentos son decisiones de contrato separadas y que atomicidad/concurrencia están pendientes; no se cambia su estado.

## 7. Preguntas pendientes de aprobación

1. ¿Se adopta una clave de idempotencia común? ¿Debe ser opcional o requerida, y para cuáles de los ocho `POST`?
2. ¿Cuál es el ámbito de unicidad de la clave y cuál es su periodo de retención? ¿Qué identidad autenticada permite delimitarla?
3. ¿Qué respuesta se entrega a una repetición concurrente aún en curso, y qué se conserva para replay (estado, cabeceras, cuerpo o referencia)?
4. ¿Qué política se aplica tras expiración, caída antes/después del efecto y una clave reutilizada con otra huella?
5. ¿Se aprueba `confirmarVenta` como único origen de salidas por venta y cuándo ocurre la confirmación comercial?
6. ¿Qué tipos/orígenes se permiten en el endpoint genérico: entradas, ajustes u otros? ¿Las recepciones requieren un caso de uso separado?
7. ¿Qué operación y permisos se requieren para anular una venta o revertir un movimiento? ¿Debe ser un movimiento compensatorio y cuál es su relación obligatoria con el original?
8. ¿Cómo se evita doble contabilización si se repite la confirmación con otra clave o si existe un movimiento previo?
9. ¿Qué reglas de saldo, stock insuficiente, concurrencia y valoración se aprueban? Estas decisiones no se presuponen en esta revisión.
10. Para el flujo SUNAT, ¿qué identidad documental y procedimiento de consulta/reconciliación se usarán ante un timeout externo, una vez decidido el SEE?

## 8. Verificación y límites

La revisión es documental. Se comprobó el alcance de operaciones OpenAPI y las referencias a DTO/entidades. No se implementó código, no se instalaron dependencias ni se ejecutaron validadores OpenAPI, pruebas de aplicación o pruebas de integración externa. Las pruebas enumeradas son propuestas. No se afirma cumplimiento SUNAT ni aceptación de comprobantes.

La recomendación no cambia RF/RNF, no aprueba contratos, no decide stock negativo, costos, persistencia, tecnología ni arquitectura. `SPEC-002`, OpenAPI, DTOs y demás decisiones permanecen en `DRAFT`/`PROPOSED` hasta autorización expresa.
