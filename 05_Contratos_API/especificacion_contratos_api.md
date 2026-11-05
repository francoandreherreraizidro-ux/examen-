# Contrato API — Sistema comercial

| Metadato | Detalle |
|---|---|
| Contract ID | `API-SISTEMA-COMERCIAL` |
| Estado | `DRAFT` — propuesta para revisión, no aprobada para implementación |
| Versión | `0.1.0` |
| Base de rutas propuesta | `/api/v1` |
| Contrato OpenAPI | [sistema-comercial.openapi.yaml](../specs/api/sistema-comercial.openapi.yaml) |
| Especificación de DTOs | [dtos_schemas.md](../03_DTOs_Schemas/dtos_schemas.md) |
| Fuente principal | [análisis de requisitos](../01_Analisis_Requisitos/analisis_requisitos.md) |
| Modelo conceptual | [modelo de dominio](../02_Modelo_Dominio/modelo_dominio.md) |

> **Estado Spec-First:** `SPEC-002` sigue en `DRAFT`. Este contrato es una propuesta para revisar; no autoriza crear controladores o servicios y no afirma cumplimiento normativo ni integración productiva con SUNAT.

## 1. Alcance y fuentes

El contrato cubre de forma preliminar las capacidades de gestión de productos, almacenes, terceros, movimientos de inventario y ventas, además de la solicitud y consulta del estado de comprobantes. Se conservan los códigos del análisis vigente: `RF-PRO-001`, `RF-INV-001` a `RF-INV-002`, `RF-COM-001`, `RF-VEN-001` a `RF-VEN-002`, `RF-CPE-001`, `RF-SUN-001`, `RF-MIG-001`, `RF-VAL-001` y `RF-TRZ-001`.

Las rutas para comprobantes son propuestas por la asociación inferida entre venta y documento (`RF-VEN-002`). Los identificadores de otros documentos/versiones no se trasladan como requisitos.

El contrato OpenAPI es el detalle normativo de rutas, parámetros, cuerpos y respuestas; los DTOs se referencian desde [dto-contracts.schema.json](../03_DTOs_Schemas/dto-contracts.schema.json). La especificación OpenAPI 3.1 permite referencias externas; confirmar su resolución en la herramienta de documentación/validación que se elija.

## 2. Convenciones de API propuestas

| Tema | Contrato preliminar | Pendiente |
|---|---|---|
| Protocolo y formato | HTTP con JSON; respuestas de error `application/problem+json`. | Confirmar consumo REST/HTTP como interfaz deseada. |
| Métodos de escritura | `POST` crea recursos o ejecuta transiciones; `PATCH` actualiza parcialmente atributos descriptivos; no se ofrece `PUT` ni borrado físico. | Confirmar qué recursos pueden editarse/desactivarse y si deben exponerse estas operaciones. |
| Versionado | Prefijo `/api/v1`. | Política de cambios incompatibles y versionado. |
| Nombres de rutas | Recursos en español y plural. | Aprobación de nomenclatura y compatibilidad con interfaces consumidoras. |
| Identificadores | UUID en rutas y DTOs actuales. | Confirmar estrategia de identidad común. |
| Paginación | `pagina` (desde 1) y `tamanoPagina` (1–100, default 20). | Límites, orden estable, conteo total y filtros. |
| Respuesta de listas | `{ elementos, paginacion }`. | Aprobar sobrecarga/costo del campo `totalElementos`. |
| Errores | Perfil de Problem Details con `code` e `invalidParams` opcionales. | Confirmar catálogo de errores y correlación de solicitudes. |
| Autenticación | OpenAPI no exige un esquema todavía. | Decidir proveedor, mecanismo, identidad, permisos y protección de datos. |
| Idempotencia | `Idempotency-Key` obligatorio en escrituras del borrador; clave y contenido iguales reproducen el resultado anterior, contenido distinto da `409`. | Propuesta; aprobar obligatoriedad, alcance, vigencia y almacenamiento/replay atómico. La clave no va dentro de los DTOs. |
| Integración SUNAT | Solo se modela consulta del estado local y resultado disponible. | Modalidad, protocolo, autenticación, ambiente y contrato externo requieren fuentes oficiales y decisión empresarial. |

No se fijan URLs de entorno. La API de negocio no expone directamente operaciones SOAP de SUNAT ni convierte el nombre de una operación externa en endpoint público.

## 3. Operaciones propuestas

`Propuesta` significa que el análisis pide la capacidad general, pero no especifica ese método HTTP, sus filtros o su respuesta completa. La ruta equivalente aparece en el OpenAPI y usa su `operationId`.

| Método y ruta | Operación | RF | Entrada / salida principal | Estado |
|---|---|---|---|---|
| `GET /api/v1/productos` | `listarProductos` | `RF-PRO-001` | filtros/paginación → `ProductosPage` | Propuesta |
| `POST /api/v1/productos` | `crearProducto` | `RF-PRO-001` | `CrearProductoDto` → `ProductoDto` | Propuesta |
| `GET /api/v1/productos/{productoId}` | `obtenerProducto` | `RF-PRO-001` | UUID → `ProductoDto` | Propuesta |
| `PATCH /api/v1/productos/{productoId}` | `actualizarProducto` | `RF-PRO-001` | `ActualizarProductoDto` → `ProductoDto` | Propuesta |
| `GET /api/v1/almacenes` | `listarAlmacenes` | `RF-INV-001` | paginación → `AlmacenesPage` | Propuesta |
| `POST /api/v1/almacenes` | `crearAlmacen` | `RF-INV-001` | `CrearAlmacenDto` → `AlmacenDto` | Propuesta; operación de escritura marcada como tal en OpenAPI |
| `GET /api/v1/almacenes/{almacenId}` | `obtenerAlmacen` | `RF-INV-001` | UUID → `AlmacenDto` | Propuesta |
| `PATCH /api/v1/almacenes/{almacenId}` | `actualizarAlmacen` | `RF-INV-001` | `ActualizarAlmacenDto` → `AlmacenDto` | Propuesta |
| `GET /api/v1/inventario/movimientos` | `listarMovimientosInventario` | `RF-INV-002`, `RF-TRZ-001` | filtros/paginación → `MovimientosPage` | Propuesta |
| `POST /api/v1/inventario/movimientos` | `registrarMovimientoInventario` | `RF-INV-002`, `RF-VAL-001` | `RegistrarMovimientoInventarioDto` → `MovimientoInventarioDto` | Propuesta; tipos/orígenes pendientes para evitar duplicar ventas |
| `GET /api/v1/inventario/movimientos/{movimientoId}` | `obtenerMovimientoInventario` | `RF-INV-002`, `RF-TRZ-001` | UUID → `MovimientoInventarioDto` | Propuesta |
| `GET /api/v1/clientes` | `listarClientes` | `RF-COM-001` | filtros/paginación → `TercerosPage` | Propuesta |
| `POST /api/v1/clientes` | `crearCliente` | `RF-COM-001` | `CrearClienteDto` → `TerceroDto` | Propuesta |
| `GET /api/v1/clientes/{clienteId}` | `obtenerCliente` | `RF-COM-001` | UUID → `TerceroDto` | Propuesta |
| `PATCH /api/v1/clientes/{clienteId}` | `actualizarCliente` | `RF-COM-001` | `ActualizarTerceroDto` → `TerceroDto` | Propuesta |
| `GET /api/v1/proveedores` | `listarProveedores` | `RF-COM-001` | filtros/paginación → `TercerosPage` | Propuesta |
| `POST /api/v1/proveedores` | `crearProveedor` | `RF-COM-001` | `CrearProveedorDto` → `TerceroDto` | Propuesta |
| `GET /api/v1/proveedores/{proveedorId}` | `obtenerProveedor` | `RF-COM-001` | UUID → `TerceroDto` | Propuesta |
| `PATCH /api/v1/proveedores/{proveedorId}` | `actualizarProveedor` | `RF-COM-001` | `ActualizarTerceroDto` → `TerceroDto` | Propuesta |
| `GET /api/v1/ventas` | `listarVentas` | `RF-VEN-001` | filtros/paginación → `VentasPage` | Propuesta |
| `POST /api/v1/ventas` | `crearVenta` | `RF-VEN-001`, `RF-VAL-001` | `CrearVentaDto` → `VentaDto` | Propuesta |
| `GET /api/v1/ventas/{ventaId}` | `obtenerVenta` | `RF-VEN-001`, `RF-TRZ-001` | UUID → `VentaDto` | Propuesta |
| `POST /api/v1/ventas/{ventaId}/confirmar` | `confirmarVenta` | `RF-VEN-001`, `RF-VEN-002`, `RF-INV-002`, `RF-TRZ-001` | `ConfirmarVentaDto` → `VentaDto` | Propuesta arquitectónica; `RF-VEN-002` no confirma esta transición |
| `POST /api/v1/comprobantes` | `solicitarEmisionComprobante` | `RF-CPE-001`, `RF-SUN-001`, `RF-VEN-002` | `SolicitarEmisionComprobanteDto` → `202 SolicitudEmisionAceptada` | Propuesta; `RF-VEN-002` es inferido |
| `GET /api/v1/comprobantes/{comprobanteId}` | `obtenerComprobante` | `RF-CPE-001`, `RF-SUN-001`, `RF-TRZ-001` | UUID → estado local y resultado de integración opcional | Propuesta |

El contrato no incluye `DELETE` físico, transferencias, ajustes, reservas, búsquedas externas de identidad, descarga de XML/CDR ni carga ETL. Los 25 métodos propuestos están distribuidos en 15 rutas; el conteo incluye métodos propuestos y no implica aprobación funcional.

## 4. Servicios lógicos candidatos

Estos nombres describen agrupaciones funcionales para la arquitectura MVC/hexagonal; no son clases, puertos ni servicios de aplicación aprobados.

| Servicio lógico candidato | Operaciones que podría respaldar | Requisito |
|---|---|---|
| Gestión de catálogo | Productos y sus consultas | `RF-PRO-001` |
| Gestión de almacén e inventario | Almacenes, movimientos y efecto de inventario al confirmar venta | `RF-INV-001`, `RF-INV-002`, vínculo comercial inferido |
| Gestión de terceros | Clientes y proveedores | `RF-COM-001` |
| Gestión comercial | Registro y consulta de ventas | `RF-VEN-001`; vínculo con comprobante inferido por `RF-VEN-002` |
| Emisión e integración de comprobantes | Solicitud y consulta del estado | `RF-CPE-001`, `RF-SUN-001` |
| Migración histórica | Sin operación pública definida todavía | `RF-MIG-001` |

El límite entre servicio de aplicación, adaptador MVC y dominio se concretará en la fase de arquitectura, no mediante este contrato.

## 5. Decisiones de diseño: propuesta y estado

| Tema | Hecho confirmado por requisitos/documentos fuente | Propuesta para revisión | Pendiente de aprobación |
|---|---|---|---|
| HTTP y actualización | Se solicitan gestión/registro y conservación histórica; no se fijan verbos ni reglas de actualización. | `GET` consulta, `POST` crea/ejecuta transición, `PATCH` modifica solo datos descriptivos; sin borrado físico ni `PUT` general. | Aceptar rutas de actualización, campos editables y política de desactivación lógica. |
| Venta e inventario | Hay requisitos para registrar ventas y movimientos (`RF-VEN-001`, `RF-INV-002`); no establecen cuándo se afectan ni política de stock. | Venta se registra inicialmente sin movimiento y se confirma explícitamente; confirmación y sus movimientos son atómicos. | Stock insuficiente, concurrencia, confirmación parcial, varios almacenes, cancelación/reverso y momento comercial de confirmación. |
| Autenticación/autorización | Seguridad es `RNF-SEG-001` propuesta; el análisis no aprueba usuarios ni roles. `ACT-P02`–`ACT-P05` son perfiles propuestos. | IdP OIDC/OAuth2 con tokens JWT o mecanismo equivalente; mínimo privilegio y autorización por capacidad. Perfiles de administrador, almacén, vendedor/cajero y responsable contable son solo candidatos. | IdP, sesión/token, claims/scopes, aprovisionamiento, políticas, separación de funciones y matriz de permisos. No se declaran roles como confirmados ni se hace obligatorio `bearerAuth` todavía. |
| Idempotencia | No hay garantía técnica aprobada expresamente. | Encabezado `Idempotency-Key` en todas las escrituras; mismo contenido devuelve resultado previo, contenido distinto responde `409`. | Ámbito, expiración, protección concurrente, retención y semántica para solicitudes en curso. Reintento SUNAT es independiente del reintento API. |
| Migración histórica | `RF-MIG-001` exige migrar históricos, sin fuente/formato/proceso definido. | Proceso administrativo ETL/CLI fuera de la API pública, con staging, IDs de origen, validación, reporte de errores, conciliación y aprobación previa a activar registros. | Fuentes, mapeos, volumen, periodo, tratamiento de duplicados, transformación, autorizador y estrategia de reversa. |
| Modalidad SUNAT | `RF-SUN-001` expresa integración; no especifica canal ni protocolo. | Adaptador/puerto desacoplado detrás del límite de aplicación; contrato API permanece neutral. | Elegir SEE directo u OSE/proveedor luego de requerimientos empresariales y verificación oficial vigente; definir autenticación, ambientes, estados y manejo de respuesta. |

Como punto de partida para revisar autorización, los actores candidatos podrían tener estas capacidades. No se asignan permisos obligatorios ni se incorporan como roles de seguridad aprobados:

| Actor candidato | Alcance de permisos propuesto | Límites/decisiones pendientes |
|---|---|---|
| Administrador (`ACT-P02`) | Administrar catálogo, almacenes, terceros y, si el alcance lo incluye, usuarios/permisos. | Separación de funciones y acceso a datos fiscales/financieros. |
| Personal de almacén (`ACT-P03`) | Consultar almacenes y movimientos; registrar movimientos no asociados a ventas. | Aprobación de ajustes, recepción, stock insuficiente y autoridad para confirmar ventas. |
| Vendedor/cajero (`ACT-P04`) | Consultar productos/clientes; registrar y consultar ventas. | Si puede confirmar ventas, emitir solicitudes de comprobante, editar terceros o consultar precios/costos. |
| Responsable contable/tributario (`ACT-P05`) | Solicitar y consultar emisión de comprobantes y resultados externos. | Si puede operar ventas, reintentar integraciones y acceder a documentos/datos restringidos. |

### 5.1 Movimiento de inventario

`POST /inventario/movimientos` registra el evento, no un saldo calculado. El consumidor no envía stock físico/disponible, costo promedio ni saldo resultante. El cuerpo exige producto, almacén, tipo, cantidad, unidad y fecha efectiva según el DTO preliminar.

El servidor tendría que comprobar existencia de referencias y reglas semánticas. Aún no están definidos tipos/orígenes, signo, valoración, política de existencias insuficientes, transferencias, momento de contabilización ni corrección del historial. Para evitar doble salida, se propone que los movimientos de venta solo se generen desde `confirmarVenta`, no mediante el endpoint genérico. Esto requiere acordar un catálogo que permita distinguir orígenes antes de aprobar el contrato. Las respuestas `409`/`422` son propuestas, no reglas ya aprobadas.

### 5.2 Venta

`POST /ventas` recibe cliente opcional y líneas mínimas con producto/cantidad y registra un estado inicial sin efecto de inventario. La operación `POST /ventas/{ventaId}/confirmar` es una transición propuesta que acepta asignaciones por línea y almacén. El cuerpo no permite declarar estado, importe total, precios o tributos, porque las reglas de cálculo están pendientes.

La propuesta exige que la confirmación y sus movimientos se persistan todos o ninguno, y que cada detalle quede cubierto exactamente una vez por cantidades asignadas. Es una garantía arquitectónica a aprobar, no un requisito expresamente confirmado. Se debe decidir política de stock insuficiente, concurrencia, posibilidad de varios almacenes, y si existen otros eventos entre registro y confirmación. No está permitido inferir que el stock siempre deba ser no negativo.

### 5.3 Solicitud de comprobante e integración

- `POST /comprobantes` responde `202` como propuesta para indicar que la solicitud quedó registrada localmente. Esto **no significa** que se haya enviado a SUNAT, que haya un CDR o que el documento haya sido aceptado.
- `GET /comprobantes/{comprobanteId}` distingue `estadoEmision` del resultado externo opcional; la forma de esos estados/códigos sigue pendiente.
- No se permite asumir que cada venta genera un comprobante inmediatamente o uno solo.
- No se definen en este contrato XML, firma, certificado, CDR, plazos, códigos SUNAT, servicios SOAP/OSE ni formato de representación impresa. Se deberán especificar tras verificar las fuentes oficiales vigentes y la modalidad elegida.

### 5.4 Idempotencia de solicitudes

Se propone el encabezado `Idempotency-Key` en cada escritura. Un reintento con la misma clave y el mismo contenido debe recuperar el resultado previamente registrado; la misma clave con contenido diferente se rechaza con `409`. La clave y la respuesta asociada deben almacenarse de manera que no haya una ventana de duplicación ante concurrencia o caída. Quedan pendientes el alcance (por cliente/operación), retención, recuperación de solicitudes en curso y conducta tras vencimiento. Un reintento de envío al proveedor SUNAT es distinto de repetir una llamada API y no debe crear otro comprobante; se requiere identidad documental persistida y estado de integración.

## 6. Errores HTTP propuestos

| HTTP | Uso preliminar | Condición |
|---|---|---|
| `400` | Solicitud mal formada o inválida estructuralmente | El servidor no procesa la operación. |
| `404` | Recurso no encontrado | No confirma reglas de ocultamiento por autorización. |
| `409` | Conflicto de estado, duplicidad o concurrencia | Catálogo de causas pendiente. |
| `422` | Regla semántica no satisfecha | No asignar aquí códigos normativos SUNAT. |
| `500` | Error inesperado | Sin detalles internos sensibles. |

`401`/`403` no están en las respuestas del borrador mientras no se apruebe un esquema de autenticación; deben incorporarse junto con la definición de seguridad. Las respuestas `400`, `404`, `409`, `422` y `500` utilizan `application/problem+json`. La consistencia de esquemas y aplicación real de las reglas todavía no puede verificarse sin implementación.

## 7. Matriz de cobertura y pendientes

| Requisito | Cobertura en API | Pendiente |
|---|---|---|
| `RF-PRO-001` | Crear/listar/consultar producto y actualización descriptiva propuesta. | Atributos mínimos, campos editables y desactivación. |
| `RF-INV-001` | Listar/crear/consultar almacén y actualización descriptiva propuesta. | Atributos, si almacén es único/múltiple, y desactivación. |
| `RF-INV-002` | Registrar/listar/consultar movimientos; confirmación de venta propone su trazabilidad. | Semántica de tipos/orígenes, saldos, valorización, reversos y política de stock. |
| `RF-COM-001` | Crear/listar/consultar clientes y proveedores; actualización descriptiva propuesta. | Datos, identificación, reglas de duplicidad y campos editables. |
| `RF-VEN-001` | Crear/listar/consultar ventas; confirmación explícita de venta propuesta. | Etapas, pagos, precios, impuestos, devoluciones y reglas de stock. |
| `RF-VEN-002` | Solicitar emisión ligada a venta como propuesta. | Confirmar obligatoriedad, cardinalidad y ciclo de comprobantes por venta. |
| `RF-CPE-001` | Solicitar/consultar un comprobante. | Tipos, perfil UBL, datos fiscales y validaciones oficiales. |
| `RF-SUN-001` | Consultar estado/resultados de integración; servicio externo queda detrás de la frontera API. | Modalidad de emisión, autenticación, protocolo, ambientes y fallos. |
| `RF-MIG-001` | Sin endpoint definido. | Faltan fuentes, formatos, datos, periodo y procedimiento de aprobación/conciliación. |
| `RF-VAL-001` | Esquemas estructurales externos más respuestas de error genéricas. | Reglas semánticas por proceso y reporte de errores. |
| `RF-TRZ-001` | Referencias a venta, movimiento y comprobante en recursos consultables. | Campos de auditoría, procedencia de legado, retención y navegación requerida. |

## 8. Aprobación antes de implementación

Antes de cambiar el estado a `REVIEW` o `APPROVED`, se requiere:

1. Acordar qué operaciones de consulta/alta/actualización son necesarias por cada recurso.
2. Cerrar los campos DTO, nombres, formatos, precisión de cantidades/importes, paginación y errores.
3. Decidir el flujo de inventario asociado a venta y criterios de aceptación/concurrencia.
4. Definir autenticación y permisos; seguridad no se considera resuelta por este contrato.
5. Especificar migración tras obtener muestras de la fuente legada.
6. Verificar contratos tributarios contra documentación oficial vigente y confirmar modalidad SUNAT.
7. Validar sintaxis OpenAPI, referencias externas y consistencia con JSON Schema usando herramientas aprobadas del proyecto.
8. Revisar `SPEC-002`, que permanece en `DRAFT`, y aprobar explícitamente las propuestas antes de implementación.

No se deben crear endpoints en código ni implementar adaptadores hasta que el contrato y su especificación madre sean aprobados.
