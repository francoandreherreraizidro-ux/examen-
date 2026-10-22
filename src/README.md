# Application Source Code (`src/`)

This directory contains the production implementation of the application.

---

## ⚠️ Spec-First Golden Rule

**Do not write code in this folder unless an approved specification exists in `specs/features/`.**

Every class, module, or service should trace directly to a requirement ID (e.g., `FR-01`, `FR-02`) or an architectural decision (e.g., `ADR-0001`).

---

## 🏛️ Suggested Directory Organization

You can adapt this layout based on your chosen language / framework:

```text
src/
├── types/          # Type definitions, DTOs, and interface contracts (matching specs/api/)
├── domain/         # Pure domain entities, business rules, and invariant validations
├── services/       # Application orchestration, use cases, and workflows
├── adapters/       # Primary/Secondary adapters (HTTP controllers, CLI, database, external APIs)
└── index.ts        # Application entrypoint
```
