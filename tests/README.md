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

## 🧪 Traceability Requirement

Each test suite should annotate or reference the Spec ID and Requirement ID it verifies:

```typescript
describe('SPEC-001: User Profile Management', () => {
  describe('FR-02: Display Name Validation', () => {
    it('rejects display names shorter than 2 characters', () => {
      // test logic
    });
  });
});
```
