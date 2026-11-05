# Modelo de dominio — Sistema comercial

| Campo | Valor |
|---|---|
| Fase | Modelo conceptual de dominio |
| Estado | Propuesta para revisión; no aprobada |
| Fuente principal | [Análisis de requisitos](../01_Analisis_Requisitos/analisis_requisitos.md) |
| Referencia secundaria | [Revisión de requisitos](../01_Analisis_Requisitos/revision_requisitos.md), con identificadores de una versión anterior |
| Alcance | Modelo conceptual; no define tablas, SQL, DTOs, controladores, servicios de aplicación ni endpoints |

## 1. Criterios y fuente de requisitos

Se conservan los códigos presentes en la versión vigente del análisis: `RF-PRO-001`, `RF-INV-001` a `RF-INV-002`, `RF-COM-001`, `RF-VEN-001` a `RF-VEN-002`, `RF-CPE-001`, `RF-SUN-001`, `RF-MIG-001`, `RF-VAL-001`, `RF-TRZ-001` y `RNF-TEC-001`, `RNF-ARQ-001`, `RNF-SDD-001`, `RNF-DOC-001`, `RNF-SEG-001`, `RNF-REL-001`, `RNF-PER-001`, `RNF-MIG-001`.

La revisión secundaria usa códigos que no existen en ese análisis vigente (por ejemplo, `RF-CAT-001`, `RF-ALM-003` y `RN-05`) y contiene afirmaciones normativas que el análisis actual dejó pendientes de verificación. Por tanto, se consideran preguntas y antecedentes, no requisitos aprobados ni reglas para este modelo. No se renumeran ni se trasladan esos códigos.

### Certeza del modelo

- **Núcleo propuesto**: entidad necesaria para representar directamente una capacidad del análisis vigente.
- **Condicional**: útil solo si se confirma una regla o proceso que todavía no está definido.
- **Extensión opcional**: alternativa de diseño no exigida; se excluye del núcleo hasta su aprobación.
- Los tipos son conceptuales, independientes de un lenguaje y de un esquema PostgreSQL. `Identificador` denota una identidad de dominio; las asociaciones expresan referencias. No se enumeran claves foráneas como atributos.

## 2. Selección de entidades candidatas

| Entidad candidata | Decisión | Justificación y requisito | Pendiente / cautela |
|---|---|---|---|
| `Producto` | Núcleo propuesto | Representa los artículos cuya gestión está solicitada por `RF-PRO-001` y que aparecen en los movimientos y ventas. | Atributos comerciales, unidades, códigos y estados se deben acordar. |
| `Categoria` | Extensión opcional | Podría organizar productos, pero el análisis no requiere categorías ni jerarquías. | Incorporar solo si se confirma clasificación o navegación por categoría. |
| `Proveedor` | Núcleo propuesto | La gestión de proveedores está expresamente incluida en `RF-COM-001`. | No se conocen atributos, identificadores o reglas de unicidad. |
| `Cliente` | Núcleo propuesto | La gestión de clientes y ventas figura en `RF-COM-001` y `RF-VEN-001`. | No toda venta tiene que identificar cliente hasta confirmar la política comercial. |
| `Lote` | Condicional | No figura como requisito vigente; podría apoyar trazabilidad por lote o vencimiento. | Decidir si se manejan lotes, para qué productos y cómo se distribuye un lote entre almacenes. |
| `Recepcion` / `DetalleRecepcion` | Extensión opcional | El análisis exige registrar ingresos (`RF-INV-002`), pero no define un proceso de recepción de compras ni su documento. | Puede resolverse inicialmente con `MovimientoInventario` y su referencia de origen. Crear recepción solo si se confirma el flujo de abastecimiento. |
| `MovimientoInventario` | Núcleo propuesto | Entidad directa para el historial de ingresos, salidas y movimientos solicitado por `RF-INV-002`. | Immutabilidad, valorización, ajustes, reservas y política de stock son decisiones pendientes. |
| `Almacen` | Núcleo propuesto | Necesario para representar la gestión de almacén de `RF-INV-001`; el número de almacenes no está definido. | No asumir multialmacén físico hasta confirmarlo; el modelo admite más de uno. |
| `Venta` | Núcleo propuesto | Representa la gestión de ventas solicitada por `RF-VEN-001`; relación con comprobantes es una inferencia registrada en `RF-VEN-002`. | Estados, pagos, devoluciones, despacho y canales están por acordar. |
| `DetalleVenta` | Núcleo propuesto | Desglosa artículos e importes de una venta; es una composición necesaria para caracterizar una transacción comercial. | Campos de precio, descuento e impuestos no se fijan hasta acordar reglas y contratos. |
| `Usuario` | Extensión opcional | Puede atribuir operaciones y apoyar trazabilidad; los usuarios humanos y los datos de autoría no están definidos. | `RF-TRZ-001` pide trazabilidad, pero no especifica auditoría por usuario ni perfiles. |
| `ComprobanteElectronico` | Núcleo propuesto | Necesario para representar emisión electrónica (`RF-CPE-001`), integración SUNAT (`RF-SUN-001`) y formato considerado por `RNF-DOC-001`. | No se presupone que todo comprobante esté aceptado inmediatamente ni se modela como clase cada XML/servicio SUNAT. |

## 3. Entidades del modelo

### 3.1 `Producto` — núcleo propuesto

- **Responsabilidad:** representar un artículo que la empresa administra y puede relacionar con movimientos y líneas de venta.
- **Identificador primario:** `id: Identificador`.
- **Atributos conceptuales:** `codigoInterno: Texto` (si existe), `nombre: Texto`, `descripcion: Texto opcional`, `unidadComercial: Código opcional`, `estado: EstadoProducto`.
- **Relaciones:** cero o una `Categoria` si se adopta esa clasificación; cero o más `MovimientoInventario`; cero o más `DetalleVenta`; cero o más `Lote` solo si se habilita el control por lote.
- **Invariantes:** identidad estable; los datos mínimos y unicidad de códigos se definen en el contrato de catálogo. Un producto desactivado no debería borrarse si su historial debe conservarse, como propuesta derivada de trazabilidad y pendiente de confirmar.
- **Requisitos:** `RF-PRO-001`, `RF-INV-002`, `RF-VEN-001`, `RF-TRZ-001`.

### 3.2 `Almacen` — núcleo propuesto

- **Responsabilidad:** representar el ámbito físico/lógico donde se contabilizan movimientos de existencias.
- **Identificador primario:** `id: Identificador`.
- **Atributos conceptuales:** `codigo: Texto`, `nombre: Texto`, `estado: EstadoAlmacen`; dirección/ubicación puede ser `Texto opcional` si se requiere.
- **Relaciones:** un almacén participa en cero o más `MovimientoInventario`. La cardinalidad exacta depende de confirmar si se administrará uno o más almacenes.
- **Invariantes:** su identificador no se reutiliza para otro almacén si hay historial asociado (propuesta de conservación).
- **Requisitos:** `RF-INV-001`, `RF-INV-002`.

### 3.3 `MovimientoInventario` — núcleo propuesto

- **Responsabilidad:** registrar un evento de ingreso, salida u otro movimiento que afecte el historial de existencias.
- **Identificador primario:** `id: Identificador`.
- **Atributos conceptuales:** `tipo: TipoMovimiento`, `cantidad: Cantidad`, `unidad: Código`, `fechaHoraEfectiva: FechaHora`, `fechaHoraRegistro: FechaHora`, `referenciaOrigen: Referencia opcional`, `observacion: Texto opcional`.
- **Relaciones:** se asocia a exactamente un `Producto` y un `Almacen` en el modelo propuesto; puede asociarse a cero o un `Lote`, `Venta` y `Usuario` según el origen, las reglas de lote y la atribución que se confirmen. Un movimiento no exige una entidad `Recepcion`.
- **Invariantes:** la cantidad debe ser válida y la operación debe identificar su sentido (entrada/salida) mediante `tipo`, no por valores ambiguos. La convención de signo/unidad, valoración, correcciones compensatorias y prevención de saldo negativo son decisiones pendientes. Conservar el registro histórico es expreso; prohibir modificaciones físicas y corregir solo por compensación es una propuesta por aprobar.
- **Requisitos:** `RF-INV-002`, `RF-TRZ-001`, `RF-MIG-001`; `RF-VAL-001` para validar datos asociados.

### 3.4 `Proveedor` — núcleo propuesto

- **Responsabilidad:** representar un proveedor administrado por la empresa.
- **Identificador primario:** `id: Identificador`.
- **Atributos conceptuales:** `nombreORazonSocial: Texto`, `identificadorComercial: Texto opcional`, `datosContacto: DatosContacto opcionales`, `estado: EstadoTercero`.
- **Relaciones:** cero o más `Recepcion` solo si se aprueba esa extensión. El modelo no requiere una compra ni recepción para que exista el proveedor.
- **Invariantes:** reglas de identificación, unicidad y validación se mantienen abiertas; no se presupone RUC obligatorio ni consulta a padrones.
- **Requisitos:** `RF-COM-001`, `RF-MIG-001`, `RF-VAL-001`.

### 3.5 `Cliente` — núcleo propuesto

- **Responsabilidad:** representar un cliente comercial administrado por la empresa.
- **Identificador primario:** `id: Identificador`.
- **Atributos conceptuales:** `nombreORazonSocial: Texto`, `identificadorComercial: Texto opcional`, `datosContacto: DatosContacto opcionales`, `estado: EstadoTercero`.
- **Relaciones:** cero o más `Venta`; una venta puede tener cero o un cliente identificado mientras la política de ventas anónimas/mostrador no esté definida.
- **Invariantes:** formato, obligatoriedad y unicidad de identificadores se definirán según reglas acordadas y requisitos oficiales que correspondan; no se incorporan umbrales tributarios no confirmados.
- **Requisitos:** `RF-COM-001`, `RF-VEN-001`, `RF-VAL-001`, `RF-MIG-001`.

### 3.6 `Venta` — núcleo propuesto

- **Responsabilidad:** representar la transacción comercial y relacionarla con sus líneas, cliente si se identifica, movimientos asociados y comprobantes que se emitan.
- **Identificador primario:** `id: Identificador`.
- **Atributos conceptuales:** `fechaHora: FechaHora`, `estado: EstadoVenta`, `moneda: CódigoMoneda opcional`, `total: Importe opcional`, `referenciaOrigen: Referencia opcional`.
- **Relaciones:** cero o un `Cliente`; una o más `DetalleVenta` para una venta confirmada (un borrador podría no tener líneas); cero o más `MovimientoInventario`; cero o más `ComprobanteElectronico` asociados. No se fija almacén único para toda la venta.
- **Invariantes:** una venta confirmada debe tener líneas y sus importes deben ser consistentes según las reglas comerciales que se aprueben. No se obliga a crear un comprobante en el mismo instante, ni se define que la venta solo sea válida cuando SUNAT lo acepte.
- **Requisitos:** `RF-VEN-001`, `RF-VEN-002`, `RF-TRZ-001`, `RF-VAL-001`.

### 3.7 `DetalleVenta` — núcleo propuesto

- **Responsabilidad:** expresar una línea comercial de una venta y conservar la referencia al producto vendido.
- **Identificador primario:** `id: Identificador`; `numeroLinea: EnteroPositivo` identifica el orden dentro de la venta.
- **Atributos conceptuales:** `cantidad: Cantidad`, `precioUnitario: Importe opcional`, `descuento: Importe opcional`, `importeLinea: Importe opcional`, `descripcionAlMomento: Texto opcional`.
- **Relaciones:** pertenece a exactamente una `Venta` y refiere exactamente un `Producto`.
- **Invariantes:** cantidad positiva al confirmar la línea; importes no negativos si se usan; consistencia de totales sujeta a reglas de redondeo, moneda, impuestos y descuento aún no definidas. La descripción/precio como instantánea es una propuesta para preservar historia.
- **Requisitos:** `RF-VEN-001`, `RF-VEN-002`, `RF-VAL-001`, `RF-TRZ-001`.

### 3.8 `ComprobanteElectronico` — núcleo propuesto, separado de transporte SUNAT

- **Responsabilidad:** representar el documento electrónico asociado a la operación comercial y el estado de su ciclo de emisión, sin incorporar al dominio el cliente SOAP, el XML como objeto de integración o la lógica de transporte.
- **Identificador primario:** `id: Identificador`.
- **Atributos conceptuales:** `tipoDocumento: Código`, `identificadorDocumento: Texto opcional` (serie/número u otra identidad definida), `fechaEmision: FechaHora`, `estadoEmision: EstadoEmision`, `referenciaExterna: Texto opcional`, `huellaDocumento: Texto opcional`.
- **Relaciones:** cero o un `Venta` relacionada en cada documento; una venta puede vincularse con cero o más comprobantes. Si se confirma emisión individual originada por una venta, el vínculo será obligatorio para los comprobantes operativos nuevos; el histórico migrado podría no tener la venta íntegra disponible.
- **Invariantes:** la emisión, el envío y la respuesta externa son estados distintos; `creado` no significa `aceptado`. Una clave de idempotencia y una identidad documental única deben impedir duplicar la misma solicitud/documento durante reintentos, pero su alcance y composición son decisiones de contrato. No se prescribe retener XML/CDR dentro de esta entidad: artefactos y respuestas pueden quedar detrás de un puerto de integración con referencias/huellas.
- **Requisitos:** `RF-CPE-001`, `RF-SUN-001`, `RF-TRZ-001`, `RF-VAL-001`, `RNF-DOC-001`, `RNF-REL-001`.

### 3.9 Entidades condicionales y extensiones

#### `Categoria` — extensión opcional

- **Responsabilidad:** clasificar productos.
- **Identificador:** `id: Identificador`; atributos candidatos `nombre: Texto`, `descripcion: Texto opcional`.
- **Relación:** un producto tiene cero o una categoría en la alternativa simple; una categoría clasifica cero o más productos.
- **Invariantes:** unicidad de nombre/código y jerarquía (si existe) deben acordarse.
- **Requisito:** ninguno directo en el análisis vigente; posible refinamiento de `RF-PRO-001`.

#### `Lote` — condicional

- **Responsabilidad:** identificar una partida de producto cuando se requiera rastreo por lote.
- **Identificador:** `id: Identificador`; candidatos: `codigoLote: Texto`, `fechaVencimiento: Fecha opcional`.
- **Relaciones:** cada lote corresponde a un producto; un producto puede tener varios lotes. La ubicación/saldo por lote requiere modelar su interacción con almacenes y movimientos, no añadir un saldo duplicado sin una decisión de inventario.
- **Invariantes:** unicidad de lote, vencimiento y regla de despacho no están especificados.
- **Requisito:** ninguno directo en el análisis vigente; decisión abierta en su sección 8.

#### `Recepcion` y `DetalleRecepcion` — extensión opcional

- **Responsabilidad:** representar una recepción de mercadería y sus líneas si el proceso de abastecimiento se incluye.
- **Identificadores:** cada entidad tendría `id: Identificador`; atributos candidatos: fecha/estado para `Recepcion`; cantidad y costo opcional para `DetalleRecepcion`.
- **Relaciones candidatas:** una recepción contiene una o más líneas al confirmarse; cada línea corresponde a un producto. El proveedor puede ser obligatorio para una recepción de compra, pero no necesariamente para otros ingresos (devolución, migración u otro origen). La recepción identifica un almacén destino si el ingreso se almacena allí.
- **Invariantes:** la confirmación debe producir movimientos de entrada consistentes y no duplicados; costo/moneda y documentos fuente dependen de reglas de compra no definidas. No crear una recepción paralela a `MovimientoInventario` sin aclarar cuál representa el hecho de inventario.
- **Requisitos:** `RF-INV-002` cubre el registro de ingresos, pero no impone este agregado ni el proceso de compras.

#### `Usuario` — extensión opcional

- **Responsabilidad:** identificar a un actor interno responsable de una acción, si se confirma atribución por usuario.
- **Identificador:** `id: Identificador`; atributos candidatos `nombreVisible: Texto`, `estado: EstadoUsuario`.
- **Relaciones:** cero o más operaciones (movimientos, ventas, recepciones), con autoría opcional hasta acordar la política.
- **Invariantes:** autenticación, roles, permisos e inmutabilidad de auditoría no se derivan de una entidad `Usuario`; deben definirse aparte.
- **Requisitos:** posible soporte a `RF-TRZ-001`; `RNF-SEG-001` es una propuesta para definir controles, no exige esta entidad.

## 4. Relaciones UML y multiplicidades

Las multiplicidades describen la propuesta conceptual persistente, no reglas ya aprobadas. `0..1` indica que la asociación puede no existir; las condiciones de confirmación se señalan expresamente.

| Origen | Destino | Relación y multiplicidad | Obligatoria / opcional | Justificación y supuesto |
|---|---|---|---|---|
| `Categoria` | `Producto` | Una categoría clasifica `0..*` productos; un producto pertenece a `0..1` categoría. | Opcional | Clasificación no exigida por el análisis. Si el responsable la requiere y define obligatoriedad, ajustar cardinalidad. |
| `Proveedor` | `Recepcion` | Un proveedor puede asociarse a `0..*` recepciones; una recepción puede asociarse a `0..1` proveedor en el modelo genérico. | Opcional | `RF-COM-001` gestiona proveedores, pero no define recepciones. Si solo se modelan recepciones de compra, el proveedor será `1` por recepción. |
| `Recepcion` | `DetalleRecepcion` | Una recepción confirmada contiene `1..*` detalles; cada detalle pertenece a `1` recepción. | Obligatoria al confirmar; borrador pendiente | Evita recepción sin productos contabilizados. Recepción es extensión, no núcleo expreso. |
| `DetalleRecepcion` | `Producto` | Cada detalle refiere a `1` producto; cada producto puede aparecer en `0..*` detalles. | Obligatoria en detalle | Necesario para determinar qué artículo ingresó. Solo aplica si se adopta la entidad recepción. |
| `Producto` | `Lote` | Un producto puede tener `0..*` lotes; cada lote corresponde a `1` producto. | Condicional | No se requiere control de lotes en requisitos vigentes. |
| `Producto` | `MovimientoInventario` | Un producto tiene `0..*` movimientos; cada movimiento afecta `1` producto. | Obligatoria por movimiento | La cantidad por artículo debe poder reconstruirse desde el historial (`RF-INV-002`). |
| `Almacen` | `MovimientoInventario` | Un almacén tiene `0..*` movimientos; cada movimiento se refiere a `1` almacén. | Obligatoria en el modelo propuesto | Relaciona el movimiento con el ámbito donde cambia stock (`RF-INV-001`). Si se registran movimientos globales o transferencias como un evento único, modelar origen/destino será una decisión posterior. |
| `Cliente` | `Venta` | Un cliente puede tener `0..*` ventas; una venta refiere a `0..1` cliente identificado. | Opcional para una venta | El análisis no define si se admiten ventas anónimas ni la obligatoriedad de cliente. |
| `Venta` | `DetalleVenta` | Una venta confirmada contiene `1..*` detalles; cada detalle pertenece a `1` venta. | Obligatoria al confirmar | Una venta confirmada sin producto/importe no constituye una operación comercial completa. Los borradores vacíos dependen del ciclo de venta futuro. |
| `DetalleVenta` | `Producto` | Cada detalle representa `1` producto; cada producto puede aparecer en `0..*` detalles. | Obligatoria por detalle | Permite identificar el bien comercializado (`RF-VEN-001`). |
| `Venta` | `ComprobanteElectronico` | Una venta puede estar asociada a `0..*` comprobantes; un comprobante puede asociarse a `0..1` venta en el modelo de transición/histórico. | Opcional | Separa operación comercial de estado de emisión. La cantidad de comprobantes por venta y la carga de histórico deben confirmarse. Un `0..1` estado aceptado no implica que siempre exista aceptación inmediata. |
| `Venta` | `MovimientoInventario` | Una venta puede generar `0..*` movimientos; cada movimiento puede tener `0..1` venta como origen. | Opcional | La afectación de stock puede ocurrir en otro momento o no estar definida aún; otros movimientos no provienen de ventas. |
| `Lote` | `MovimientoInventario` | Un lote puede aparecer en `0..*` movimientos; un movimiento se asocia a `0..1` lote. | Condicional | Solo si el control por lote se confirma y el movimiento corresponde a producto trazado por lote. |
| `Usuario` | `MovimientoInventario` / `Venta` / `Recepcion` | Un usuario puede registrar `0..*` operaciones; cada operación puede tener `0..1` usuario atribuido. | Opcional | La trazabilidad está solicitada, pero auditoría por usuario no está definida. |

## 5. Integridad e invariantes

### 5.1 Reglas derivadas del alcance expreso

1. **Identidad y referencias:** todo movimiento debe poder identificarse y consultarse con su producto y, en este modelo, con el almacén al que afecta (`RF-INV-002`, `RF-TRZ-001`). La clave interna y la política de unicidad se definirán en contratos.
2. **Integridad de las líneas:** una línea de venta confirmada se asocia a un producto y a una venta; cada línea de una recepción, si la extensión se aprueba, se asocia a su recepción y producto.
3. **Datos históricos:** deben preservarse datos históricos migrados y su procedencia dentro del alcance que se acuerde (`RF-MIG-001`, `RF-TRZ-001`). No está definido el periodo ni si se migran todas las transacciones.
4. **Validación:** validar las estructuras de datos antes de aceptarlas según reglas acordadas (`RF-VAL-001`). No se inventan reglas tributarias o políticas comerciales.
5. **Comprobantes:** mantener la relación de trazabilidad con la venta cuando el comprobante se origine de ella y registrar el ciclo de emisión/integración sin equiparar emisión con aceptación (`RF-CPE-001`, `RF-SUN-001`).

### 5.2 Propuestas de integridad para revisión

1. **Cantidades:** usar una cantidad positiva en cada línea/movimiento y expresar ingreso/salida mediante el tipo de movimiento; definir precisión y unidades en el contrato.
2. **Precios e importes:** si aplican, evitar importes negativos salvo que una operación explícita los admita; fijar moneda, precisión, descuentos y redondeo antes de validar totales.
3. **Claves únicas:** acordar unicidad para códigos de producto y documentos externos. No hay suficientes datos para afirmar que SKU, RUC, serie/correlativo u otros campos sean obligatorios.
4. **Historial de inventario:** preservar asientos ya registrados; como alternativa de corrección, generar un evento compensatorio en lugar de alterar el historial. Esta inmutabilidad es propuesta: el análisis solo exige registro histórico.
5. **Venta e inventario:** cada salida originada por venta deberá poder rastrearse a esa venta y, de ser posible, a su línea. La confirmación del momento de descuento, reservas y atomicidad transaccional está pendiente.
6. **Saldo disponible:** no se establece una regla de stock no negativo. Debe decidirse si se rechazan salidas sin existencias, se permiten reservas u otra política.
7. **Venta y comprobante:** no exigir comprobante aceptado como condición para guardar una venta. Mantener por separado estado de venta, estado de emisión, transmisión y resultado recibido.
8. **Reintentos:** una misma solicitud de emisión debe tener un mecanismo idempotente para evitar duplicar el documento lógico. La clave y la retención de resultados se definirán en el contrato de emisión; el transporte/reintento no es una entidad comercial por defecto.
9. **Recepciones:** si se aprueban, no confirmar una recepción vacía y evitar que el mismo evento se contabilice más de una vez; el identificador de documento origen y política de duplicidad están por definir.
10. **Lotes:** si se aprueba su control, validar que el lote corresponda al producto del movimiento; las reglas de vencimiento y saldo por almacén quedan pendientes.

## 6. Datos históricos y legado

La migración (`RF-MIG-001`) es un proceso del sistema, no una razón para añadir una entidad genérica de legado al núcleo comercial.

- Conservar la identidad propia del dominio, sin reemplazarla por claves del sistema anterior.
- Asociar al registro migrado una referencia de origen (`sistemaOrigen`, `identificadorOrigen`) mediante metadatos de migración o un registro de correspondencia fuera del agregado comercial. Se trata de una propuesta para soportar `RF-TRZ-001`, sujeta a confirmar granularidad.
- Mantener separado el expediente de carga (lote de migración, errores, conciliación y aprobación) de `Producto`, `Venta` o `MovimientoInventario`.
- Si el histórico se consulta como archivo de solo lectura y no se transforma al modelo operacional, conservarlo en un repositorio/área de archivo aislada. El análisis no determina si se importará al modelo de dominio o se consultará externamente.
- No atribuir automáticamente al usuario actual la autoría de operaciones legadas. La fecha, zona horaria, fuente y fidelidad de cada dato deben conservarse según el mapeo aprobado.
- El periodo, granularidad (maestros, saldos, transacciones), criterio de conciliación y tratamiento de errores requieren decisión del responsable.

## 7. Separación del dominio comercial e integración SUNAT

`ComprobanteElectronico` representa la intención/documento comercial emitido y su estado de ciclo de vida. No se introducen en el núcleo clases de cliente SOAP, ZIP, WSDL, ticket de transporte o formatos de respuesta externos. Esos detalles pertenecen a los contratos y adaptadores de integración posteriores, sin definirlos aquí.

La estructura UBL Invoice 2.1 es un formato externo. Los datos del documento pueden originarse en una venta y conservar una huella o referencia al artefacto, pero no se duplica automáticamente cada nodo UBL como entidad de dominio. El mapeo y la correspondencia normativa se definirán después de verificar especificaciones oficiales.

## 8. Revisión de calidad y cobertura

- **Cohesión:** producto, venta, línea, movimiento y comprobante tienen responsabilidades diferenciadas.
- **Sin duplicación innecesaria:** no se crea `StockAlmacen` como balance persistido paralelo al historial; un saldo puede derivarse de movimientos cuando se defina su semántica. Si se requiere rendimiento, una proyección de lectura sería decisión técnica posterior.
- **Relaciones prudentes:** categorías, lotes, recepciones y usuarios no se tratan como capacidades obligatorias; las relaciones opcionales reflejan incertidumbres del análisis.
- **Trazabilidad:** los requisitos con reflejo en el modelo se listan en [trazabilidad_dominio.csv](./trazabilidad_dominio.csv).
- **Límites:** el modelo no establece políticas fiscales, métodos de costo, stocks negativos, pagos, autorización de usuario ni aceptación SUNAT como reglas confirmadas.

## 9. Decisiones pendientes antes de DTOs/Schemas

1. Confirmar si existen uno o varios almacenes y si un movimiento requiere almacén único, origen/destino o ambos.
2. Definir los estados y momento de confirmación de venta y su relación temporal con salidas de inventario.
3. Confirmar si las ventas pueden ser anónimas y la cardinalidad/ciclo de vida de comprobantes por venta.
4. Decidir si recepción de compras requiere agregado propio, qué tipos de ingreso soporta y cómo se relaciona con proveedor.
5. Confirmar si se requieren categorías, lotes y vencimientos.
6. Definir atributos mínimos, unidades, moneda, precios, impuestos y redondeo sin asumir normas no verificadas.
7. Acordar qué significa trazabilidad, si requiere usuario, eventos, inmutabilidad, retención y referencias de legado.
8. Definir la clave idempotente e identidad única documental a nivel de contrato, y el modelo de estados separado entre emisión y respuesta externa.
9. Aclarar con el docente/propietario cuál documento de análisis es la fuente vigente: la revisión presente en la carpeta usa códigos y requisitos anteriores que no coinciden con el análisis actual.

La fase de modelo de dominio queda como propuesta documentada. No se inicia el diseño de DTOs/Schemas hasta recibir autorización expresa.
