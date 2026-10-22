# ADR-0003: Selección de PostgreSQL y Modelo de Persistencia para Kardex Permanente

---

## 📋 Metadata

| Attribute | Details |
|---|---|
| **ADR Number** | ADR-0003 |
| **Status** | `ACCEPTED` <!-- Options: PROPOSED | ACCEPTED | REJECTED | DEPRECATED | SUPERSEDED --> |
| **Deciders** | Senior Software Architect & Evaluador Docente de Arquitectura |
| **Date** | 2026-10-22 |
| **Related Specs** | [SPEC-002](../../features/002-sistema-comercial-facturacion.md), [03 DTOs y Schemas](../../../03_DTOs_Schemas/dtos_schemas.md) |
| **Superseded By** | N/A |

---

## 1. Context and Problem Statement

El sistema debe gestionar operaciones de venta de alta concurrencia en puntos de venta retail junto con pedidos mayoristas voluminosos, garantizando en todo momento:
1. **La invariante de Kardex no negativo (`RN-05`)**: Prohibición estricta de saldos menores a cero.
2. **Cálculo exacto del Costo Promedio Ponderado**: Recálculo continuo en moneda nacional (PEN) con alta precisión matemática.
3. **Inmutabilidad de comprobantes y asientos de Kardex**: Cumplimiento de la Resolución de Superintendencia N° 234-2006/SUNAT para libros contables y tributarios.
4. **Almacenamiento híbrido estructurado y semiestructurado**: Manejo de tablas relacionales estrictas combinadas con estructuras flexibles para cuotas de crédito (RS 193-2020) y cargas útiles de eventos Outbox.

---

## 2. Decision Drivers (Key Requirements)

- **Transaccionalidad ACID Robusta**: Garantizar que la deducción de inventario, el registro de la venta y el encolado del evento Outbox ocurran en una única transacción atómica sin inconsistencias.
- **Control de Concurrencia y Bloqueo de Filas**: Capacidad de ejecutar bloqueos pesimistas controlados (`SELECT ... FOR UPDATE`) sobre los registros de existencias físicas para evitar sobreventas concurrentes entre terminales POS.
- **Tipos de Datos Numéricos de Alta Precisión**: Soporte para aritmética de punto fijo sin errores de coma flotante (`NUMERIC(16,6)` para costos y `NUMERIC(14,2)` para importes fiscales).
- **Tipos Semiestructurados Nativos**: Soporte eficiente para campos `JSONB` indexables mediante índices GIN.

---

## 3. Considered Options

1. **Opción 1: Base de Datos NoSQL Documental (ej. MongoDB)**.
2. **Opción 2: Base de Datos Relacional MySQL / MariaDB (InnoDB)**.
3. **Opción 3: Base de Datos Relacional PostgreSQL (versión 15 o superior)**.

---

## 4. Decision & Rationale

Elegimos la **Opción 3: PostgreSQL 15+ como motor de persistencia relacional principal**.

### Fundamentación Técnica:
1. **Garantía Transaccional ACID y Concurrencia**:
   * PostgreSQL implementa un motor MVCC (*Multi-Version Concurrency Control*) de alto rendimiento.
   * Permite aislar transacciones bajo niveles `Read Committed` o `Serializable`, ejecutando bloqueos atómicos sobre filas específicas de la tabla `inventory_warehouse_stock`, lo que previene que dos cajeros vendan la última unidad simultáneamente.
2. **Restricciones de Integridad en el Motor de Base de Datos**:
   * Permite definir restricciones `CHECK` nativas (ej. `CONSTRAINT chk_stock_non_negative CHECK (physical_quantity >= 0)`), actuando como una barrera de defensa en profundidad que complementa las invariantes del dominio DDD.
3. **Potencia de JSONB para Casos de Uso Tributarios**:
   * La tabla de eventos Outbox (`billing_outbox_events`) y el desglose de cuotas de crédito (`payment_terms_details`) se almacenan como `JSONB`, permitiendo evolucionar esquemas de comprobantes sin alterar tablas relacionales rígidas y facilitando consultas indexadas.
4. **Tipos Binarios Nativos para Artefactos Fiscales**:
   * Soporte para el tipo `BYTEA` para la custodia inmutable de los archivos binarios XML firmados y los ZIP de respuesta oficial de SUNAT (CDR).

---

## 5. Pros and Cons of the Options

### Opción 1: NoSQL Documental (MongoDB)
- **Bueno**, para esquemas flexibles y documentos anidados.
- **Malo**, porque carece de transaccionalidad multi-documento nativa robusta para cuadres contables de Kardex y dificulta garantizar la invariante de no saldo negativo ante alta concurrencia.

### Opción 2: MySQL / InnoDB
- **Bueno**, ampliamente extendido en sistemas web tradicionales.
- **Malo**, menor madurez en el soporte de operadores JSON avanzados, tipos de datos geométricos o extensiones de auditoría en comparación con PostgreSQL.

### Opción 3: PostgreSQL 15+ (Seleccionada)
- **Bueno**, máxima robustez transaccional y cumplimiento estricto de estándares SQL.
- **Bueno**, tipos `NUMERIC` arbitrarios que eliminan cualquier riesgo de descuadre de centavos en la liquidación del 18% de IGV o en el prorrateo de costos promedio.
- **Bueno**, excelente integración con el patrón *Transactional Outbox* mediante `SKIP LOCKED` para workers asíncronos concurrentes.
- **Malo**, requiere mayor rigor en el diseño de esquemas e índices relacionales en comparación con almacenes clave-valor.

---

## 6. Consequences & Follow-up Actions

### Positive Consequences
- Consistencia matemática garantizada en el Kardex y en los balances de existencias.
- Las tablas del sistema quedan listas para soportar auditorías fiscales de SUNAT con integridad referencial completa.

### Negative Consequences / Trade-offs
- Se requiere gestionar migraciones de esquema SQL formales para cualquier cambio en la estructura de datos.

### Action Items
- [x] Documentar sentencias DDL completas con índices y restricciones en [03_DTOs_Schemas/dtos_schemas.md](../../../03_DTOs_Schemas/dtos_schemas.md).
- [ ] Definir los scripts de migración de base de datos en `infrastructure/adapters/out/persistence/schema/` cuando se autorice la fase de implementación.
