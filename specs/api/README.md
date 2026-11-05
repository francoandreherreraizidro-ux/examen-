# API Specifications & Contracts

This directory contains contracts, schema definitions, and endpoint documentation that govern client-server or service-to-service communication.

---

## Current API Contract

- [`sistema-comercial.openapi.yaml`](./sistema-comercial.openapi.yaml) is the OpenAPI 3.1 **DRAFT** for the commercial-system API. It references the preliminary DTO definitions in [`03_DTOs_Schemas/dto-contracts.schema.json`](../../03_DTOs_Schemas/dto-contracts.schema.json).
- Its endpoint guide, requirement coverage, and open decisions are in [`05_Contratos_API/especificacion_contratos_api.md`](../../05_Contratos_API/especificacion_contratos_api.md).
- The parent feature, `SPEC-002`, remains `DRAFT`. This contract is a proposal for review, not approval for implementation.
- `x-status` and `x-requirements` extensions mark proposals and requirement traceability in the YAML.
- Paths and HTTP operations are proposed. In particular, descriptive `PATCH` operations, `POST /ventas/{ventaId}/confirmar`, and the `Idempotency-Key` header are proposals, not approved behavior. A schema reference does not mean the DTO or API is approved; reconcile them with the current domain model and requirements before approval.
- The generic inventory-movement operation must not be used to duplicate a sale's inventory effect; the approved movement types/origins and atomicity policy remain undecided.
- The migration contract, authentication, full resource lifecycle, inventory rules, and official SUNAT-specific data remain pending.

## 📁 Recommended Organization

- **Contract Markdown Specs**: Use [`../templates/api-contract.template.md`](../templates/api-contract.template.md) for Markdown-based contracts.
- **OpenAPI / Swagger**: Place `openapi.yaml` or domain-specific OpenAPI files here (e.g., `auth.openapi.yaml`).
- **GraphQL Schemas**: Place `.graphql` schema files or type definitions here.
- **JSON Schemas / Proto files**: Place request/response schema validators here.

---

## 🔒 Contract-First Rule

Before adding or updating API route handlers in `src/`, the contract file in this directory must be updated and approved. This ensures that frontend and backend developers (or multiple agents) can work against fixed contracts without breaking changes.
