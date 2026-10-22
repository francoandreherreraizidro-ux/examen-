# Contratos de API del sistema comercial

| Metadato | Detalle |
|---|---|
| Entregable | Especificación de endpoints y servicios mediante contratos API |
| OpenAPI | [`specs/api/sistema-comercial.openapi.yaml`](../specs/api/sistema-comercial.openapi.yaml) |
| Especificación funcional base | [`specs/features/002-sistema-comercial-facturacion.md`](../specs/features/002-sistema-comercial-facturacion.md) |
| Versión del contrato | `0.1.0` |
| Estado | `DRAFT - PROPUESTA PARA VALIDACIÓN` |
| Base de rutas | `/api/v1` |
| Host de producción | Pendiente; no se fija una URL de producción |

> **Límite de aprobación:** SPEC-002 continúa en `DRAFT`; el análisis de requisitos también mantiene asuntos abiertos y los documentos de dominio/DTO no están consolidados. Este documento y el OpenAPI son una propuesta para revisión, no una autorización de implementación ni una afirmación de cumplimiento tributario. Las ampliaciones de DTO y operaciones se señalan como propuestas o pendientes.

## 1. Fuentes y criterios de diseño

Se revisaron las instrucciones del repositorio (`AGENTS.md`), el flujo Spec-First descrito en `README.md` y `specs/README.md`, los requisitos, el modelo, los DTOs/Schemas, la arquitectura, los ADRs y el contrato API previo.

Fuentes del dominio disponibles:

- [`01_Analisis_Requisitos/analisis_requisitos.md`](../01_Analisis_Requisitos/analisis_requisitos.md): identificadores RF/RNF/RN, clasificaciones de certeza y preguntas aún abiertas.
- [`02_Modelo_Dominio/modelo_dominio.md`](../02_Modelo_Dominio/modelo_dominio.md): modelo conceptual en español, relaciones e invariantes declaradas.
- [`03_DTOs_Schemas/dtos_schemas.md`](../03_DTOs_Schemas/dtos_schemas.md): DTOs y algunos schemas de entrada/salida.
- [`04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md`](../04_Arquitectura_MVC_Hexagonal/diagramas_arquitectura.md), [`ADR-0002`](../specs/architecture/adrs/0002-arquitectura-hexagonal-mvc.md) y [`ADR-0003`](../specs/architecture/adrs/0003-persistencia-postgresql-kardex.md): responsabilidades MVC/hexagonales, puertos y PostgreSQL.

El contrato usa DTOs de frontera y proyecciones de respuesta, no expone tablas ni entidades de persistencia. Los servicios indicados son puertos/casos de uso candidatos de la arquitectura; sus firmas no están aprobadas.

## 2. Convenciones generales propuestas

- JSON para solicitudes y respuestas; OpenAPI 3.1 y ruta versionada `/api/v1`.
- Respuestas exitosas envueltas en `data`; las listas incluyen `pagination`.
- Errores con `application/problem+json`, siguiendo Problem Details. `code` e `invalidParams` son extensiones de aplicación.
- Autenticación bearer JWT por RNF-SEC-002. Emisor, audiencia, claims, scopes y asociación de roles a cada operación están pendientes; no se inventan roles de API.
- Las operaciones de escritura usan `Idempotency-Key`. El contrato propone detectar reutilización de clave con un payload distinto como `409`; duración de retención y tratamiento definitivo de replay requieren aprobación.
- `409` identifica conflictos de estado/concurrencia; `422` una regla de negocio no satisfecha; `404` un recurso no existente; `400` un payload inválido; `401/403` fallos de autenticación/autorización. No se fijan códigos SUNAT.
- La escritura de venta y sus cambios de inventario debe ser atómica (RF-TRZ-002, RNF-DAT-001). El balance y el Kardex no se aceptan desde el cliente.
- El precio final, descuentos permitidos, impuestos y totales son calculados o validados por el servidor. No se acepta un total enviado por el consumidor.
- `202 Accepted` solo indica que la API recibió y registró una solicitud asíncrona; no implica envío, aceptación ni conformidad de SUNAT/OSE.

## 3. Catálogo de operaciones

Los identificadores, schemas normativos del borrador, parámetros completos, ejemplos y respuestas están en el archivo OpenAPI vinculado arriba. El cuadro resume el propósito y la dependencia de cada operación.

| ID / operación | Propósito y RF | DTO de entrada/salida y entidades principales | Puerto / componente responsable |
|---|---|---|---|
| `listarCategorias` — `GET /api/v1/catalog/categories` | Consultar categorías. RF-CAT-001. | Proyección `CategoriaResumenDto` propuesta; `Categoria`. | `CatalogQueryPort` propuesto; controlador MVC de catálogo. |
| `crearProducto` — `POST /api/v1/catalog/products` | Registrar catálogo y precios. RF-CAT-001/002/003/004. | `CrearProductoComandoDto`, `ProductoResumenDto`; `Producto`, `Categoria`, `EscalaPrecio`. | `ProductCatalogCommandPort` propuesto; controlador de catálogo. |
| `listarProductos` — `GET /api/v1/catalog/products` | Filtrar y paginar el catálogo. RF-CAT-001. | `ProductoResumenDto`; `Producto`, `EscalaPrecio`. | `ProductCatalogQueryPort` propuesto. |
| `obtenerProducto` — `GET /api/v1/catalog/products/{productId}` | Consultar producto. RF-CAT-001. | Proyección de producto; `Producto`, `Categoria`, `EscalaPrecio`. | `ProductCatalogQueryPort` propuesto. |
| `actualizarProducto` — `PATCH /api/v1/catalog/products/{productId}` | Actualizar campos permitidos; excluir SKU/historial. RF-CAT-001/002. **Propuesta.** | `ActualizarProductoComandoDto` propuesto; `Producto`. | `ProductCatalogCommandPort` propuesto. |
| `registrarCambioEscalaPrecio` — `POST /api/v1/catalog/price-tiers` | Registrar vigencia de precio con motivo/auditoría. RF-CAT-002, RF-TRZ-003. **Propuesta.** | Entrada: `ActualizarEscalaPrecioComandoDto`; salida `EscalaPrecioResponse` propuesta. `Producto`, `EscalaPrecio`, `Usuario`. | `ProductCatalogCommandPort` propuesto. |
| `listarAlmacenes` — `GET /api/v1/inventory/warehouses` | Consultar almacenes. RF-ALM-001. | `AlmacenResumenDto` propuesto; `Almacen`. | `WarehouseQueryPort` propuesto. |
| `consultarExistencias` — `GET /api/v1/inventory/stocks` | Consultar saldo físico, reservado y disponible. RF-ALM-001/002. | `StockDisponibilidadDto`; `StockAlmacen`, `Producto`, `Almacen`. | `StockQueryPort` propuesto. |
| `registrarRecepcion` — `POST /api/v1/inventory/receipts` | Registrar entrada y afectar stock/Kardex atómicamente. RF-ALM-003/002, RF-TRZ-002. | `RegistrarEntradaMercaderiaComandoDto`; `RecepcionMercaderia`, `DetalleRecepcion`, `MovimientoInventario`, `StockAlmacen`, `Lote`. | `StockManagementServicePort`; controlador MVC de inventario. |
| `listarRecepciones` — `GET /api/v1/inventory/receipts` | Consultar recepciones por filtros. RF-ALM-003. | `RecepcionResumenDto` propuesto; `RecepcionMercaderia`. | `StockQueryPort` propuesto. |
| `obtenerRecepcion` — `GET /api/v1/inventory/receipts/{receiptId}` | Consultar recepción y líneas. RF-ALM-003, RF-TRZ-001. | Proyección de recepción propuesta; `RecepcionMercaderia`, `DetalleRecepcion`, `MovimientoInventario`. | `StockQueryPort` propuesto. |
| `listarMovimientosInventario` — `GET /api/v1/inventory/movements` | Consultar Kardex/movimientos, sin alterar el historial. RF-ALM-002/004, RF-TRZ-001. | Proyección de movimiento propuesta; `MovimientoInventario`, `Producto`, `Almacen`, `Lote`. | `StockQueryPort` propuesto. |
| `listarLotes` — `GET /api/v1/inventory/lots` | Consultar lotes/vencimientos si se confirma este alcance. RF-CAT-004. **Pendiente de confirmación.** | Proyección de lote propuesta; `Lote`, `Producto`, `Almacen`. | `LotQueryPort` propuesto. |
| `registrarTransferencia` — `POST /api/v1/inventory/transfers` | Iniciar traslado. RF-ALM-005/002. **Propuesta; falta contrato de recepción/discrepancias.** | `TransferirStockInteralmacenComandoDto`; `Almacen`, `StockAlmacen`, `MovimientoInventario`, `Lote`. | `StockManagementServicePort`; adaptador MVC de inventario. |
| `registrarAjusteInventario` — `POST /api/v1/inventory/adjustments` | Registrar ajuste justificado y autorizado. RF-ALM-006/002, RF-TRZ-003. **Propuesta.** | `RegistrarAjusteInventarioComandoDto`; `StockAlmacen`, `MovimientoInventario`, `Usuario`. | `StockManagementServicePort`; adaptador MVC de inventario. |
| `registrarCliente` — `POST /api/v1/directory/customers` | Registrar cliente y perfil comercial. RF-DIR-001/003. | `RegistrarClienteComandoDto`; `Cliente`. | `CustomerDirectoryCommandPort` propuesto. |
| `listarClientes` — `GET /api/v1/directory/customers` | Buscar clientes con filtros/paginación. RF-DIR-001. | `ClienteResumenDto` propuesto; `Cliente`. | `CustomerDirectoryQueryPort` propuesto. |
| `obtenerCliente` — `GET /api/v1/directory/customers/{customerId}` | Consultar cliente. RF-DIR-001. | Proyección de cliente propuesta; `Cliente`. | `CustomerDirectoryQueryPort` propuesto. |
| `actualizarCliente` — `PATCH /api/v1/directory/customers/{customerId}` | Actualizar campos permitidos. RF-DIR-001/003. **Propuesta.** | `ActualizarClienteComandoDto` propuesto; `Cliente`. | `CustomerDirectoryCommandPort` propuesto. |
| `registrarProveedor` — `POST /api/v1/directory/suppliers` | Registrar proveedor. RF-DIR-002. **Propuesta porque no hay DTO aprobado.** | `RegistrarProveedorComandoDto` propuesto; `Proveedor`. | `SupplierDirectoryCommandPort` propuesto. |
| `listarProveedores` — `GET /api/v1/directory/suppliers` | Buscar proveedores. RF-DIR-002. | `ProveedorResumenDto` propuesto; `Proveedor`. | `SupplierDirectoryQueryPort` propuesto. |
| `obtenerProveedor` — `GET /api/v1/directory/suppliers/{supplierId}` | Consultar proveedor. RF-DIR-002. | Proyección de proveedor propuesta; `Proveedor`. | `SupplierDirectoryQueryPort` propuesto. |
| `registrarVentaRetail` — `POST /api/v1/sales/pos` | Venta minorista, detalle y stock atómicos. RF-VEN-001/003, RF-ALM-004, RF-TRZ-002, RNF-DAT-001. | `CrearVentaRetailPosComandoDto`, `VentaRetailResultadoDto`; `Venta`, `DetalleVenta`, `Cliente`, `Producto`, `StockAlmacen`, `MovimientoInventario`. | `SaleOrderServicePort`; controlador POS MVC. |
| `registrarPedidoMayorista` — `POST /api/v1/sales/wholesale/orders` | Crear pedido y líneas; no confirma ni reserva por sí solo. RF-VEN-002. | `CrearPedidoMayoristaComandoDto`; `Venta`, `DetalleVenta`, `Cliente`. | `SaleOrderServicePort`; controlador mayorista MVC. |
| `confirmarPedidoMayorista` — `POST /api/v1/sales/wholesale/orders/{saleId}/confirm` | Confirmar después de validar crédito y stock, aplicando la reserva atómicamente. RF-VEN-002, RF-DIR-003, RF-ALM-004, RF-TRZ-002. **Propuesta.** | Resultado propuesto; `Venta`, `Cliente`, `StockAlmacen`, `CuotaCredito`. | `SaleOrderServicePort`; controlador mayorista MVC. |
| `listarVentas` — `GET /api/v1/sales` | Listar ventas/pedidos por filtros. RF-VEN-001/002. | `VentaResumenDto` propuesto; `Venta`, `Cliente`. | `SaleOrderQueryPort` propuesto. |
| `obtenerVenta` — `GET /api/v1/sales/{saleId}` | Consultar venta y referencias de trazabilidad. RF-VEN-001/002, RF-TRZ-001. | `VentaDetalleDto` propuesto; `Venta`, `DetalleVenta`, `Cliente`, `MovimientoInventario`, `ComprobanteElectronico`. | `SaleOrderQueryPort` propuesto. |
| `listarDetallesVenta` — `GET /api/v1/sales/{saleId}/details` | Consultar líneas de venta. RF-VEN-001/002, RF-TRZ-001. | `DetalleVentaDto` propuesto; `Venta`, `DetalleVenta`, `Producto`, `Lote`. | `SaleOrderQueryPort` propuesto. |
| `solicitarEmisionComprobante` — `POST /api/v1/billing/documents` | Registrar solicitud de emisión asociada a una venta. RF-CPE-001/002/003, RF-SUN-001/002/004/005. | `EmitirComprobantePagoComandoDto`; respuesta de solicitud propuesta; `Venta`, `ComprobanteElectronico`, `DetalleComprobante`, `CuotaCredito`. | `ElectronicInvoicingServicePort`; controlador de facturación MVC y Outbox. |
| `obtenerComprobante` — `GET /api/v1/billing/documents/{documentId}` | Consultar estado local y resultado externo disponible. RF-SUN-002/004, RF-TRZ-001. | `SunatCdrRespuestaDto` opcional y proyección de estado propuesta; `ComprobanteElectronico`. | `ElectronicInvoicingQueryPort` propuesto. |
| `solicitarReintentoComprobante` — `POST /api/v1/billing/documents/{documentId}/retry` | Reintentar procesamiento existente sin duplicar documento. RF-SUN-004/005, RNF-REL-002. **Propuesta.** | Resultado de reintento propuesto; `ComprobanteElectronico`. | `ElectronicInvoicingServicePort`; worker/Outbox secundario. |
| `iniciarCargaSaldosIniciales` — `POST /api/v1/migration/jobs/stock-balances` | Encolar carga de apertura. RF-MIG-002, RF-TRZ-001. | `IngestarSaldosLegadosComandoDto`, `TrabajoMigracionEstadoDto`; `MovimientoInventario`, `StockAlmacen`. | `DataMigrationServicePort`; adaptador de migración. |
| `obtenerEstadoTrabajoMigracion` — `GET /api/v1/migration/jobs/{jobId}` | Consultar estado, errores y conciliación. RF-MIG-001/002/003. | `TrabajoMigracionEstadoDto`; `MigrationBatch` (modelo táctico pendiente de conciliación con modelo español). | `DataMigrationServicePort`; adaptador de migración. |

## 4. Contratos y estados relevantes

### Ventas e inventario

- Las líneas llevan producto y cantidad; los totales son proyecciones calculadas por el servidor. Los precios del DTO POS existente requieren decisión explícita: aplicar lista de precios vigente o validar precio pactado.
- La transacción de venta/stock/Kardex debe ser indivisible. Si no hay stock o existe conflicto concurrente, se rechaza y no se deja una venta parcialmente confirmada. No se permite sobregiro hasta resolver el conflicto entre RF-ALM-004 y RN-05.
- El DTO de venta mayorista actual no incluye el calendario de cuotas que aparece en requisitos y comprobantes; debe reconciliarse.
- El control de lote y la selección de lote al despachar solo aplican tras confirmar el alcance del RF-CAT-004 y resolver RN-PEND-02.

### Emisión tributaria

El contrato separa el resultado de la solicitud local (`202`, solicitud registrada) del ciclo externo. La consulta posterior puede indicar estado local pendiente/enviado y, únicamente si fue recibida, la respuesta externa y sus observaciones. No se define código de rechazo SUNAT ni se declara aceptación por el solo hecho de recibir `202`.

### Idempotencia y concurrencia

El mismo `Idempotency-Key` y payload debe evitar duplicar venta, recepción, movimiento o comprobante. Una clave reutilizada con otro payload debe ser conflicto. La política de retención y si un replay repite exactamente el status/response original están pendientes de aprobación. Para el inventario, la operación de aplicación debe proteger la lectura/cambio de stock bajo control transaccional acorde con RNF-DAT-001.

## 5. Errores comunes

| HTTP | Uso propuesto | Código de aplicación |
|---|---|---|
| `400` | JSON mal formado o schema inválido | `INVALID_PAYLOAD` |
| `401` | Token faltante o inválido | `UNAUTHORIZED` |
| `403` | Identidad autenticada sin permiso | `FORBIDDEN` |
| `404` | Producto, cliente, proveedor, venta, documento o trabajo inexistente | `NOT_FOUND` |
| `409` | Clave idempotente incompatible, duplicado, transición inválida o conflicto de concurrencia | `CONFLICT` |
| `422` | Regla de negocio, crédito, stock o consistencia incumplida | `BUSINESS_RULE_VIOLATION` |
| `500` | Error inesperado; respuesta no debe exponer detalles internos | `INTERNAL_SERVER_ERROR` |

`ERR_INSUFFICIENT_STOCK` y `ERR_CREDIT_LIMIT_EXCEEDED` aparecen en documentos del dominio como candidatos; sus nombres y mapping definitivo de HTTP deben aprobarse con los requisitos. No se consideran códigos SUNAT.

## 6. Matriz resumida de trazabilidad

La matriz por operación está en §3. No hay operación definida sin RF asociado. El estado “propuesto” significa que el requisito existe, pero faltan DTO, decisión, validación de reglas o aprobación del stakeholder.

| Grupo | RF/RNF | DTO(s) existentes | Entidades | Componente arquitectónico |
|---|---|---|---|---|
| Catálogo | RF-CAT-001/002/003/004 | `CrearProductoComandoDto`, `ProductoResumenDto` | `Producto`, `Categoria`, `EscalaPrecio` | Controlador MVC → puerto de catálogo → PostgreSQL adapter |
| Inventario | RF-ALM-001/002/003/004/005/006, RF-TRZ-001/002/003, RNF-DAT-001 | `RegistrarEntradaMercaderiaComandoDto`, `TransferirStockInteralmacenComandoDto`, `RegistrarAjusteInventarioComandoDto`, `StockDisponibilidadDto` | `Almacen`, `StockAlmacen`, `Lote`, `RecepcionMercaderia`, `DetalleRecepcion`, `MovimientoInventario` | Controlador MVC → `StockManagementServicePort` → repositorios PostgreSQL |
| Directorio | RF-DIR-001/002/003 | `RegistrarClienteComandoDto` | `Cliente`, `Proveedor` | Controlador MVC → puertos de directorio → repositorios |
| Ventas | RF-VEN-001/002/003, RF-ALM-004, RF-TRZ-002/003, RNF-DAT-001 | `CrearVentaRetailPosComandoDto`, `CrearPedidoMayoristaComandoDto`, `VentaRetailResultadoDto` | `Venta`, `DetalleVenta`, `CuotaCredito`, `StockAlmacen`, `MovimientoInventario` | Controladores POS/mayorista → `SaleOrderServicePort` → dominio y adaptadores |
| Comprobantes | RF-CPE-001/002/003, RF-SUN-001/002/004/005, RF-TRZ-001, RNF-REL-002 | `EmitirComprobantePagoComandoDto`, `SunatCdrRespuestaDto` | `ComprobanteElectronico`, `DetalleComprobante`, `Venta` | Controlador MVC → `ElectronicInvoicingServicePort`; Outbox worker, signer y gateway como adaptadores |
| Migración | RF-MIG-001/002/003, RF-TRZ-001 | `IngestarSaldosLegadosComandoDto`, `TrabajoMigracionEstadoDto` | `MovimientoInventario`, `StockAlmacen`, `MigrationBatch` | CLI/API de migración → `DataMigrationServicePort` → adaptador PostgreSQL/archivo |

## 7. Informe de validación y decisiones pendientes

### Resultado de comprobaciones

- OpenAPI declara `3.1.0`, versión `0.1.0`, `operationId` únicos y rutas distintas.
- Las operaciones listadas vinculan RF/RNF y proponen DTOs, entidades y puertos responsables mediante `x-requirements`, `x-dtos`, `x-domain-entities` y `x-application-port`.
- La tabla de este documento coincide con las 33 combinaciones método/ruta del OpenAPI; las referencias internas comprobadas resuelven.
- Las respuestas distinguen `201` para creación local, `200` para consulta y `202` para trabajos/solicitudes asíncronos. `202` no afirma aceptación de SUNAT.
- Los contratos públicos son proyecciones DTO, no tablas ni entidades de persistencia.
- Las solicitudes de venta no tienen un campo de total. El schema propuesto excluye el precio por línea recibido por el DTO POS existente y lo marca como discrepancia pendiente: la fuente de precio debe resolverse antes de aprobar.
- El documento previo incluía ejemplos y decisiones que no se conservaron como contratos activos porque contradicen requisitos aún abiertos o afirman hechos no verificados. El mapeo siguiente conserva el propósito funcional y deja explícito qué debe revisarse:

| Contrato anterior | Tratamiento en esta propuesta | Motivo / decisión pendiente |
|---|---|---|
| `POST /api/v1/inventory/movements/inflows` y su saldo con costo promedio | `POST /api/v1/inventory/receipts` | Se conserva el registro de recepción; el método de valorización no se fija mientras siga pendiente en requisitos. |
| Transferencia presentada como completada en una operación | `POST /api/v1/inventory/transfers` | Se modela como inicio propuesto; faltan operación de recepción, discrepancias y ciclo de vida aprobados. |
| `GET /api/v1/inventory/kardex` | `GET /api/v1/inventory/movements` | Se conserva la consulta de movimientos; filtros y proyección se expresan en el OpenAPI actual. |
| Alta mayorista que confirmaba y reservaba stock inmediatamente | Crear pedido y confirmar mediante operaciones separadas | La confirmación y reserva requieren validación de stock/crédito; la segunda operación continúa propuesta. |
| `POST /api/v1/billing/invoices` y `GET .../cdr` con códigos y resultados concretos | Solicitud asíncrona en `/api/v1/billing/documents` y consulta del documento | No se inventan códigos de SUNAT ni se presenta una aceptación tributaria como resultado garantizado. Los códigos y datos de respuesta externa dependen de fuentes y contratos por verificar. |
| Actor/rol fijo para importar saldos y firma de conciliación específica | Trabajo de migración asíncrono con autenticación global | La autorización por rol, la evidencia de conciliación y la procedencia deben definirse; no se inventan claims ni firmas. |

Los ejemplos anteriores tampoco son ejemplos normativos activos: incluyen datos, estados y validaciones tributarias que no se verificaron contra el modelo aprobado ni fuentes oficiales. Los ejemplos que permanezcan en el OpenAPI son ilustrativos.

- El OpenAPI contiene ejemplos en 7 de 33 operaciones; completar ejemplos de solicitud y respuesta por operación es un pendiente antes de declarar satisfecho el nivel de detalle solicitado.
- La comprobación de referencias y unicidad fue estática. No había un parser OpenAPI/YAML disponible en el entorno, por lo que la sintaxis no se validó formalmente con una herramienta.
- No se validaron aquí obligaciones, umbrales, esquemas XSD/Schematron ni códigos SUNAT contra fuentes oficiales. Los identificadores y reglas fiscales que aparecen en requisitos requieren revisión documental independiente.

### Pendientes bloqueantes para aprobar

1. Aprobar/validar SPEC-002 y resolver las preguntas del análisis: método de valorización, alcance de lotes, modalidad de emisión (SEE/OSE), contingencia offline y profundidad de migración.
2. Corregir la contradicción RF-ALM-004/RN-05 y completar RF-SUN-005 en el cuerpo de requisitos; definir el estado real de aprobación del modelo de dominio.
3. Consolidar el modelo en español frente al segundo documento táctico en inglés; confirmar cardinalidades y ciclo de vida de venta, recepción, transferencia, lote y comprobante.
4. Alinear los DTOs de solicitud/respuesta: precio POS, pagos mixtos, cuotas mayoristas, proveedor, consultas, actualizaciones, migración/procedencia y resultado de emisión.
5. Definir RBAC por operación, políticas idempotentes, estados fiscales/locales y responsabilidades exactas del Outbox; resolver si el reintento manual existe.
6. Verificar con enunciado original y fuentes oficiales los campos, validaciones y reglas tributarias antes de aprobar cualquier contrato fiscal. El enunciado original no está en los archivos examinados.

## 8. Recomendación de flujo Spec-First

No implementar controladores ni servicios sobre este borrador. Primero cerrar y aprobar los requisitos; después reconciliar el dominio y DTOs con las decisiones confirmadas; finalmente revisar este OpenAPI y el presente documento como `REVIEW` y `APPROVED`. La aprobación del contrato debe preceder la implementación y sus pruebas de contrato, conforme a `AGENTS.md` y `specs/README.md`.
