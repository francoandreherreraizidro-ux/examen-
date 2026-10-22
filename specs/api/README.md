# API Specifications & Contracts

This directory contains contracts, schema definitions, and endpoint documentation that govern client-server or service-to-service communication.

---

## Current API Contract

- [`sistema-comercial.openapi.yaml`](./sistema-comercial.openapi.yaml) is the OpenAPI 3.1 **DRAFT** for the commercial-system API.
- Its companion endpoint guide, traceability matrix, and validation notes are in [`05_Contratos_API/especificacion_contratos_api.md`](../../05_Contratos_API/especificacion_contratos_api.md).
- The parent feature, `SPEC-002`, remains `DRAFT`. This contract is a proposal for review, not approval for implementation.
- `x-status`, `x-pending`, and `x-requirements` extensions mark proposals, unresolved dependencies, and traceability in the YAML.
- Existing DTOs are referenced where possible. A schema identified as proposed is not an approved DTO and must be reconciled with the domain model before contract approval.

## 📁 Recommended Organization

- **Contract Markdown Specs**: Use [`../templates/api-contract.template.md`](../templates/api-contract.template.md) for Markdown-based contracts.
- **OpenAPI / Swagger**: Place `openapi.yaml` or domain-specific OpenAPI files here (e.g., `auth.openapi.yaml`).
- **GraphQL Schemas**: Place `.graphql` schema files or type definitions here.
- **JSON Schemas / Proto files**: Place request/response schema validators here.

---

## 🔒 Contract-First Rule

Before adding or updating API route handlers in `src/`, the contract file in this directory must be updated and approved. This ensures that frontend and backend developers (or multiple agents) can work against fixed contracts without breaking changes.
