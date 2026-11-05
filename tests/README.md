# Test Suites (`tests/`)

In Spec-Driven Development, tests are the executable expression of the **Acceptance Criteria** documented in `specs/features/`.

---

## 📁 Directory Structure

```text
tests/
├── unit/           # Unit tests testing domain logic & isolated validation rules (Section 3 & 4)
├── integration/    # Integration & contract tests testing APIs & DB interactions (Section 7)
└── e2e/            # End-to-end acceptance tests verifying full user stories (Section 2 & 7)
```

---

## 🧪 Traceability and execution

The Invoice generator tests are derived from SPEC-003 acceptance criteria. Unit tests parse generated XML and inspect namespaces, elements, attributes and values. The integration test additionally validates against the local official UBL 2.1 Invoice schema tree.

Run all unit and structural tests from the repository root with Maven:

```powershell
mvn test
```

See [the implementation guide](../docs/implementacion_generador_invoice_ubl21.md) for the Java/Maven toolchain and exact coverage.
