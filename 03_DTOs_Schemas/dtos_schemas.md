# Contratos de datos — DTOs y JSON Schema

| Campo | Valor |
|---|---|
| Fase | Definición preliminar de contratos de datos |
| Estado | `DRAFT` — requiere revisión y aprobación; no habilita implementación |
| Fuente principal | [Análisis de requisitos](../01_Analisis_Requisitos/analisis_requisitos.md) |
| Modelo relacionado | [Modelo de dominio](../02_Modelo_Dominio/modelo_dominio.md) |
| Esquema estructural | [dto-contracts.schema.json](./dto-contracts.schema.json) |
| Alcance | DTOs conceptuales y validación estructural; no se definen endpoints, tablas SQL, controladores ni lógica de aplicación |

## 1. Propósito y límites

Este documento define una primera propuesta de mensajes de datos para los conceptos del modelo de dominio vigente. Es un contrato por revisar, no un contrato aprobado ni una implementación. Se priorizan las entidades de núcleo y se dejan fuera del alcance los detalles no establecidos, tales como precios mayoristas, crédito, lotes, tipos tributarios, series, certificados, XML/CPE detallado, catálogos SUNAT y el formato de importación legado.

Los nombres de propiedades usan `lowerCamelCase` en español, alineados con el modelo conceptual. Los identificadores de entidades se transportan como UUID en esta propuesta; confirmar la estrategia de identidad antes de aprobar el contrato.

### Fuentes y certezas

- Los códigos funcionales siguen el análisis vigente: `RF-PRO-001`, `RF-INV-001`, `RF-INV-002`, `RF-COM-001`, `RF-VEN-001`, `RF-VEN-002`, `RF-CPE-001`, `RF-SUN-001`, `RF-MIG-001`, `RF-VAL-001` y `RF-TRZ-001`.
- Los contratos vinculados con venta a comprobante (`RF-VEN-002`) son inferidos, no un requisito de flujo ya aprobado.
- El JSON Schema realiza validaciones de forma y tipo. Las reglas dependientes de base de datos, negocio o normativa requieren validación semántica posterior y no se simulan como validaciones de esquema.
- El esquema no valida que un UUID exista, que las líneas sumen correctamente, que haya stock o que SUNAT acepte un documento.

## 2. Convenciones del contrato

| Concepto | Representación propuesta | Estado |
|---|---|---|
| Identificadores | String UUID | Propuesta que debe armonizarse con OpenAPI y el modelo de identidad final. |
| Fecha/hora | RFC 3339 con zona horaria, formato `date-time` | Propuesta; acordar fuente horaria y política de normalización. La validación efectiva de `format` depende de la configuración del validador. |
| Cantidad e importe | JSON `number` | Propuesta básica. JSON Schema no garantiza precisión decimal exacta; acordar escala/representación antes de cerrar contratos financieros. |
| Campos opcionales | Se omiten cuando no se conocen/no aplican; no se usa `null` salvo que el contrato lo indique | Convención propuesta. |
| Campos desconocidos | `additionalProperties: false` en DTOs definidos | Rechazo estructural para evitar propiedades no contratadas; revisar compatibilidad/versionado antes de aprobar. |
| Estado de negocio | No se acepta desde comandos de creación salvo que se acuerde explícitamente | Evita que el cliente imponga el estado interno. |
| Actualización parcial | `PATCH` con `application/merge-patch+json`; propiedad ausente no cambia, `null` elimina solo campos opcionales donde se indica | Propuesta para atributos descriptivos; no habilita alterar historia ni datos de identidad. |
| Idempotencia | Encabezado HTTP opcional `Idempotency-Key` propuesto en ocho comandos `POST`; excluido de los cuatro `PATCH` descriptivos | Propuesta pendiente de aprobación; obligatoriedad, operaciones, ámbito, retención, replay y concurrencia por decidir. No forma parte de los DTOs JSON; reintentos HTTP y SUNAT son distintos. |
| Mensaje de error | Perfil Problem Details propuesto en el contrato OpenAPI | Semántica HTTP y catálogo final pendientes de aprobación. |

## 3. DTOs propuestos

Todos los nombres son identificadores de contrato y no implican endpoints ni casos de uso aprobados.

### 3.1 `CrearProductoDto`

- **Uso:** datos iniciales de un producto. `RF-PRO-001`.
- **Obligatorios propuestos:** `nombre`.
- **Opcionales:** `codigoInterno`, `descripcion`, `unidadComercial`.
- **No incluido:** categoría, código de barras, lote, vencimiento, precio, códigos fiscales y estado; el análisis vigente no los requiere ni define.
- **Salida relacionada:** `ProductoDto`, que agrega `id` y `estado`.
- **Actualización propuesta:** `ActualizarProductoDto` permite cambiar `nombre` y `descripcion`; `null` limpia la descripción. No modifica identidad, código, estado ni historial.

### 3.2 `CrearAlmacenDto` y `AlmacenDto`

- **Uso:** datos mínimos propuestos para crear y consultar un almacén. `RF-INV-001`.
- **Entrada:** `nombre` obligatorio; `codigo` opcional. No acepta estado ni cantidades de stock.
- **Salida:** agrega `id` y `estado`.
- **Actualización propuesta:** `ActualizarAlmacenDto` permite cambiar datos descriptivos, no movimientos o saldos.
- **Pendiente:** decidir si se admite crear almacenes por esta API, atributos de ubicación y reglas de unicidad/cambio de estado.

### 3.3 `RegistrarMovimientoInventarioDto`

- **Uso:** datos de un ingreso, salida u otro movimiento histórico. `RF-INV-002`.
- **Obligatorios propuestos:** `productoId`, `almacenId`, `tipo`, `cantidad`, `unidad`, `fechaHoraEfectiva`.
- **Opcionales:** `referenciaOrigen`, `observacion`.
- **No incluido:** balance, costo, usuario, aprobación, stock resultante o lote. No se debe aceptar un saldo calculado suministrado por el cliente.
- **Validación semántica pendiente:** existencia de producto y almacén, unidad compatible, reglas de signos, política de existencias y relación con venta/recepción.
- **Origen propuesto:** este comando solo registra entradas y ajustes no originados por ventas. La confirmación de venta sería la única que crea sus salidas y los movimientos resultantes deben identificar `ventaId` y, cuando sea posible, `detalleVentaId`. La operación genérica no acepta salidas de venta.
- **Anulaciones/reversiones:** propuesta de registrar un movimiento compensatorio ligado al movimiento original; no editar ni borrar el historial. No hay DTO/endpoint de reversión definido y la política debe aprobarse.
- **Control pendiente:** acordar catálogo de tipos/orígenes y cómo se expresan entradas, ajustes y reversiones. No se presupone política de stock negativo, valoración ni requisito de recepción para una entrada.

### 3.4 `CrearClienteDto` y `CrearProveedorDto`

- **Uso:** datos de alta de terceros administrados por la empresa. `RF-COM-001`.
- **Obligatorios propuestos:** `nombreORazonSocial`.
- **Opcionales:** `identificadorComercial`, `datosContacto`.
- **No incluido:** tipos de documento enumerados, RUC/DNI, estado tributario, crédito, mora o consulta a padrones; deben especificarse y verificarse antes de incorporarlos.
- **Salida relacionada:** `TerceroDto`, que agrega `id` y `estado`.
- **Actualización propuesta:** `ActualizarTerceroDto` permite editar nombre y datos de contacto; `null` elimina el bloque opcional de contacto. Identificadores comerciales no se modifican por este contrato.

### 3.5 `CrearVentaDto`

- **Uso:** representación de una solicitud de registro de venta. `RF-VEN-001`; el formato concreto es una inferencia de diseño.
- **Obligatorios propuestos:** `lineas`, con al menos una línea; cada línea requiere `productoId` y `cantidad`.
- **Opcionales:** `clienteId`, `referenciaOrigen`; una venta sin cliente identificado permanece permitida hasta que se decida lo contrario.
- **No incluido:** estado, usuario, almacén, medio de pago, moneda, precio, descuento, impuesto, total ni datos de comprobante.
- **Validación semántica pendiente:** reglas comerciales, precio vigente, importe, existencia y momento de afectación al inventario.
- **Confirmación propuesta:** `ConfirmarVentaDto` asigna cada línea a uno o más almacenes mediante `{ detalleVentaId, almacenId, cantidad }`. El servidor debe validar cobertura exacta de cada línea, referencias y ausencia de asignaciones duplicadas. La forma de resolver falta de stock y la posibilidad de asignar varias ubicaciones quedan pendientes.
- **Consistencia propuesta:** `POST /ventas` registra la venta sin afectar stock; `POST /ventas/{ventaId}/confirmar` aplica todos los movimientos en una única transacción o no confirma ninguno. Es una propuesta arquitectónica, no una regla aprobada.

### 3.6 `SolicitarEmisionComprobanteDto`

- **Uso:** solicitud conceptual para emitir un documento asociado a una venta. `RF-CPE-001`, `RF-SUN-001`; la asociación es inferida por `RF-VEN-002`.
- **Obligatorios propuestos:** `ventaId`, `tipoDocumento`.
- **Idempotencia:** se propone mediante el encabezado HTTP opcional `Idempotency-Key`, no repetido en el DTO. La operación, obligatoriedad, ámbito, huella, retención, concurrencia y replay siguen pendientes; la emisión remota requiere reconciliación separada.
- **No incluido:** campos UBL, datos tributarios, XML firmado, certificados, CDR, serie/correlativo o estado de aceptación; esos contratos requieren definición normativa y de integración.
- **Pendiente:** decidir alcance de la clave, retención y respuesta ante reintento con contenido idéntico o distinto.

### 3.7 `ComprobanteDto` y `ResultadoIntegracionDto`

- **Uso:** separar el estado del documento del resultado de la comunicación externa. `RF-CPE-001`, `RF-SUN-001`, `RF-TRZ-001`.
- **Campos conceptuales de `ComprobanteDto`:** `id`, `tipoDocumento`, `estadoEmision`, `fechaEmision`, `ventaId` opcional.
- **Campos conceptuales de `ResultadoIntegracionDto`:** `estadoIntegracion`, `fechaHoraRespuesta`, `referenciaExterna`, `codigoRespuesta` y `descripcion` opcionales.
- **Regla de interpretación:** la creación/emisión local no equivale a envío ni a aceptación por SUNAT. Las enumeraciones de estado, códigos externos y documentos de respuesta se definirán tras verificar el contrato oficial aplicable.

### 3.8 Migración — contrato pendiente

`RF-MIG-001` requiere migración histórica, pero no identifica origen, formatos, entidades, granularidad ni periodo. No se fija todavía un `ImportarDatosLegadosDto` universal ni se incorpora un `payload` sin tipado. Antes de aprobar un DTO de migración se necesita un inventario de fuentes y un mapeo validado por tipo de dato.

## 4. Estructura del JSON Schema

El archivo [dto-contracts.schema.json](./dto-contracts.schema.json) utiliza JSON Schema Draft 2020-12 y define `$defs` para los DTOs de esta etapa. No valida contratos de endpoints ni persistencia.

### Reglas cubiertas estructuralmente

- Tipos de objeto, propiedad obligatoria, propiedades no reconocidas y longitudes mínimas.
- UUID y fecha/hora RFC 3339 mediante formatos declarados; su aplicación depende de que el validador verifique `format`.
- Cantidades positivas donde el modelo las requiere para líneas/movimientos.
- `lineas` con al menos un elemento en el DTO de creación de venta.

### Reglas fuera del alcance del esquema

- Existencia de referencias y unicidad dentro del sistema.
- Cantidades y unidades válidas para el producto.
- Coherencia de la venta, precios, impuestos, moneda, descuentos y redondeo.
- Stock suficiente, reservas, costo de inventario y reglas de transferencia.
- Identidad del comprador, perfil tributario, validez UBL o aceptación de SUNAT.
- Idempotencia efectiva, autorización, conciliación y trazabilidad histórica.

## 5. Matriz de contratos y requisitos

| Código vigente | Contrato/objeto relacionado | Cobertura y límite |
|---|---|---|
| `RF-PRO-001` | `CrearProductoDto`, `ProductoDto` | Atributos mínimos propuestos; la estructura del catálogo está por confirmar. |
| `RF-INV-001` | `CrearAlmacenDto`, `AlmacenDto` | Contrato mínimo propuesto para alta/consulta; política de gestión por validar. |
| `RF-INV-002` | `RegistrarMovimientoInventarioDto`, `MovimientoInventarioDto` | Contrato preliminar del evento, no de saldos ni valuación. |
| `RF-COM-001` | `CrearClienteDto`, `CrearProveedorDto`, `TerceroDto` | Datos básicos propuestos; campos de identificación sujetos a confirmación. |
| `RF-VEN-001` | `CrearVentaDto`, `VentaDto` | Venta y líneas mínimas; ciclo y reglas no se fijan. |
| `RF-VEN-002` | `SolicitarEmisionComprobanteDto` | Relación venta-documento inferida; no define emisión obligatoria o sincrónica. |
| `RF-CPE-001` | `SolicitarEmisionComprobanteDto`, `ComprobanteDto` | Solo intención/estado conceptual; UBL fiscal detallado diferido. |
| `RF-SUN-001` | `ResultadoIntegracionDto` | No define SOAP/REST, OSE, autenticación ni operaciones concretas. |
| `RF-MIG-001` | Ningún DTO de carga aprobado | Requiere especificación de fuente y mapeo antes del contrato. |
| `RF-VAL-001` | Validaciones estructurales del esquema y validaciones semánticas pendientes | El esquema no reemplaza reglas de negocio. |
| `RF-TRZ-001` | `referenciaOrigen`, `ventaId`, identificadores y referencias documentales | La granularidad y retención de trazabilidad se deben acordar. |
| `RNF-TEC-001` | Sin efecto sobre DTOs | PostgreSQL se considera en persistencia, no en el contrato de datos externo. |
| `RNF-ARQ-001` | Separación DTO/dominio | Los DTOs no exponen entidades del dominio directamente. |
| `RNF-SDD-001` | Estado `DRAFT` y revisión previa | Este documento debe aprobarse antes de implementar los contratos en código. |
| `RNF-DOC-001` | `ComprobanteDto` y mapeo UBL posterior | No se afirma conformidad; el perfil normativo queda pendiente de verificación. |
| `RNF-SEG-001` | No se define seguridad en DTO | El requisito figura como propuesta; autenticación y autorización pertenecen a la fase correspondiente. |
| `RNF-REL-001` | Estado conceptual de integración | Patrón/outbox, colas y reintentos son diseño arquitectónico pendiente, no campos de dominio. |
| `RNF-PER-001` | Sin efecto directo en DTO | Métricas y límites de rendimiento no están definidos. |
| `RNF-MIG-001` | Contrato de migración pendiente | Es una propuesta y requiere conocer fuente, volumen y ventanas. |

## 6. Decisiones necesarias para aprobar

1. Aprobar convención de identificadores y representación de cantidades/importes.
2. Confirmar nombres y atributos mínimos del catálogo, almacenes, terceros y ventas.
3. Decidir si cliente puede omitirse, cómo se determina el almacén y qué datos contiene una línea de venta.
4. Definir etapas de emisión, estados locales y respuesta de integración sin equiparar emisión a aceptación.
5. Confirmar requisito y semántica de la clave de idempotencia.
6. Definir tipo de comprobante y perfil UBL requerido tras revisar fuentes oficiales vigentes.
7. Proporcionar formatos y ejemplos del sistema legado para especificar la migración.
8. Revisar coherencia con el contrato OpenAPI, todavía en estado `DRAFT`, antes de aprobar cualquier contrato externo.

No se deben iniciar cambios en `src/` hasta la aprobación de la especificación correspondiente. No se incluyen DDL ni migraciones SQL en esta fase.
