# Entregable 3 — DTOs y Schemas

| Metadato | Detalle |
|---|---|
| Proyecto | Examen de Arquitectura de Software |
| Estado | `DRAFT` — pendiente de revisión y aprobación |
| Fuente | [Análisis de requisitos](../01_Analisis_Requisitos/analisis_requisitos.md) y [modelo de dominio](../02_Modelo_Dominio/modelo_dominio.md) |

## Contenido

- [dtos_schemas.md](./dtos_schemas.md): propósito, convenciones, DTOs propuestos, cobertura y decisiones pendientes.
- [dto-contracts.schema.json](./dto-contracts.schema.json): borrador JSON Schema Draft 2020-12 para validación estructural de DTOs.
- [modelo_dominio_contratos.md](./modelo_dominio_contratos.md): mapeo resumido entre conceptos del dominio y los DTOs.

Los contratos son propuestas y no deben usarse como aprobación para implementar. No definen endpoints, controladores, servicios, tablas ni migraciones SQL. El archivo OpenAPI pertenece a la fase de contratos API y debe reconciliarse con estos DTOs antes de aprobarse.

Las decisiones normativas sobre UBL y SUNAT están pendientes de verificación contra fuentes oficiales vigentes; estos documentos no afirman conformidad tributaria.
