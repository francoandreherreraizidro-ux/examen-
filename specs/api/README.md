# API Specifications & Contracts

This directory contains contracts, schema definitions, and endpoint documentation that govern client-server or service-to-service communication.

---

## 📁 Recommended Organization

- **Contract Markdown Specs**: Use [`../templates/api-contract.template.md`](../templates/api-contract.template.md) for Markdown-based contracts.
- **OpenAPI / Swagger**: Place `openapi.yaml` or domain-specific OpenAPI files here (e.g., `auth.openapi.yaml`).
- **GraphQL Schemas**: Place `.graphql` schema files or type definitions here.
- **JSON Schemas / Proto files**: Place request/response schema validators here.

---

## 🔒 Contract-First Rule

Before adding or updating API route handlers in `src/`, the contract file in this directory must be updated and approved. This ensures that frontend and backend developers (or multiple agents) can work against fixed contracts without breaking changes.
