# Application Source Code (`src/`)

This directory contains the production implementation of the application.

---

## ⚠️ Spec-First Golden Rule

**Do not write code in this folder unless an approved specification exists in `specs/features/`.**

Every class, module, or service should trace directly to a requirement ID (e.g., `FR-01`, `FR-02`) or an architectural decision (e.g., `ADR-0001`).

---

## Java implementation

The approved UBL Invoice generator is implemented as a Java 21-compatible module. Its domain remains independent of the XML adapter:

```text
src/main/java/com/comercial/
├── domain/billing/ # Invoice and immutable value records
├── adapters/ubl/   # StAX UBL Invoice serializer
└── examples/       # Synthetic XML example entry point
```

See [SPEC-003](../specs/features/003-generador-invoice-ubl21.md) and [the implementation guide](../docs/implementacion_generador_invoice_ubl21.md).
