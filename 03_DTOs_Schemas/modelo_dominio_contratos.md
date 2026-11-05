# Referencia entre el modelo de dominio y los contratos

| Campo | Valor |
|---|---|
| Estado | `DRAFT` — documento de referencia; no aprobado para implementación |
| Modelo vigente | [Modelo de dominio](../02_Modelo_Dominio/modelo_dominio.md) |
| Contratos vigentes | [DTOs y JSON Schema](./dtos_schemas.md) |
| Esquema estructural | [dto-contracts.schema.json](./dto-contracts.schema.json) |

## Alcance

Este archivo sustituye el borrador anterior que mezclaba contratos con decisiones de DDD, puertos, protocolos externos y DDL de PostgreSQL. Se conserva el nombre esperado por la carpeta, pero su contenido queda limitado a enlazar el modelo conceptual con los contratos preliminares.

Los contratos no están aprobados. No deben tratarse como instrucciones de implementación ni como definición de tablas. La fuente de requisitos es el [análisis vigente](../01_Analisis_Requisitos/analisis_requisitos.md); los códigos de revisiones anteriores no se trasladan automáticamente.

## Mapeo conceptual

| Concepto del dominio | DTO de entrada propuesto | DTO de salida propuesto | Notas |
|---|---|---|---|
| `Producto` | `CrearProductoDto`, `ActualizarProductoDto` | `ProductoDto` | Catálogo mínimo; el PATCH propuesto solo modifica datos descriptivos. |
| `Almacen` | `CrearAlmacenDto`, `ActualizarAlmacenDto` | `AlmacenDto` | Contrato mínimo propuesto; campos y gestión por confirmar. |
| `MovimientoInventario` | `RegistrarMovimientoInventarioDto` | `MovimientoInventarioDto` | Registra evento; no acepta balances ni costos no acordados. Catálogo de tipo/origen pendiente para evitar duplicar salidas de venta. |
| `Cliente` | `CrearClienteDto`, `ActualizarTerceroDto` | `TerceroDto` | Identificadores y validaciones fiscales pendientes. |
| `Proveedor` | `CrearProveedorDto`, `ActualizarTerceroDto` | `TerceroDto` | Datos básicos; condiciones de abastecimiento no forman parte del alcance actual. |
| `Venta` / `DetalleVenta` | `CrearVentaDto`, `ConfirmarVentaDto` | `VentaDto` | La confirmación propone asignar cantidades por detalle y almacén; el flujo y atomicidad siguen sujetos a aprobación. |
| `ComprobanteElectronico` | `SolicitarEmisionComprobanteDto` | `ComprobanteDto` | Emisión local e integración externa tienen estados separados. |
| Integración externa | No se expone el protocolo como comando de dominio | `ResultadoIntegracionDto` | No implica una interfaz SUNAT concreta ni cumplimiento normativo. |
| Migración | Pendiente de fuentes | Pendiente | No se define payload genérico sin conocer los datos de origen. |

## Cambios respecto al borrador sustituido

- Se eliminan del documento de contrato los ejemplos de DDL y definición de tablas; PostgreSQL se abordará en la decisión de persistencia correspondiente, no como migración SQL aquí.
- No se declara ningún modelo `APPROVED`; `SPEC-002` y los contratos relacionados están en `DRAFT`.
- Se retiran como requisitos de este análisis precio dual, crédito, costo promedio, lotes obligatorios, auditoría por usuario y estados/operaciones específicas de SUNAT, pues no forman parte del análisis vigente.
- Se conserva la separación conceptual entre venta y comprobante, sin imponer que toda venta esté vinculada a un documento aceptado.
- Los DTOs de actualización parcial son comandos de API y no nuevas entidades ni atributos persistentes del dominio.

Para definiciones y restricciones completas, consultar [dtos_schemas.md](./dtos_schemas.md). Las decisiones abiertas deben resolverse antes de aprobar DTOs o avanzar a endpoints.
