# Entregable N° 3: DTOs y Schemas de Validación (`03_DTOs_Schemas/`)

| Metadato | Detalle |
| :--- | :--- |
| **Proyecto** | Examen de Arquitectura de Software |
| **Entregable** | DTOs (Data Transfer Objects), JSON Schemas y Contratos de Persistencia |
| **Estado** | `CONSOLIDADO / PENDIENTE DE FORMALIZACIÓN COMPLETA` |

---

## 📌 Propósito de la Carpeta

Esta carpeta está destinada a albergar la especificación formal de:
1. **DTOs de Entrada y Salida** para los límites de la aplicación (Driving Adapters y Use Cases).
2. **JSON Schemas** con reglas de validación sintáctica y semántica para payloads de ventas, movimientos y comprobantes.
3. **Contratos de Persistencia** (Mapeo relacional en PostgreSQL, tipos nativos, constraints e índices).
4. **Mapeo a UBL 2.1**: Estructuras DTO intermedias hacia las etiquetas XPath de la SUNAT.

---

## 📄 Contenido Actual Preservado

* **`modelo_dominio_contratos.md`**: Documento técnico preliminar que contiene los contratos de datos, DTOs (`CreateSaleOrderCommandDto`, `RegisterStockMovementDto`, `IssueElectronicDocumentRequestDto`, `SunatCdrResponseDto`, `LegacyStockBalanceIngestDto`), esquemas DDL de PostgreSQL y matriz de mapeo a UBL 2.1 generados en la fase inicial. Se ha preservado intacto en esta carpeta para garantizar cero pérdida de información y facilitar su refinamiento formal cuando se autorice la fase correspondiente.
