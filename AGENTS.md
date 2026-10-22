# Agent Guidelines: Spec-First Development

This repository enforces **Spec-Driven Development (SDD)**. As an AI assistant or autonomous agent working in this codebase, you must adhere strictly to these rules.

---

## 🛑 Golden Rule: NEVER Implement Code Without An Approved Spec

Before writing or editing code in `src/`, verify the following:

1. **Check for an active spec**: Look in `specs/features/` for a relevant specification matching the user's request.
2. **If no spec exists**:
   - Inform the user that a spec must be authored first.
   - Offer to generate a draft spec using `specs/templates/feature-spec.template.md`.
   - Wait for user feedback or approval before touching `src/`.
3. **If a spec is in `DRAFT` or `REVIEW`**:
   - Direct work toward refining the spec, creating ADRs (`specs/architecture/adrs/`), or defining API contracts (`specs/api/`).
   - Do not jump into implementation until the spec is in `APPROVED` status.

---

## 📋 The Spec-First Agent Workflow

When receiving a task or feature request:

```text
Step 1: Check / Create Spec (specs/features/XXX-feature.md)
   │
Step 2: Check Architecture / ADR (specs/architecture/adrs/)
   │
Step 3: Define / Update Contract (specs/api/)
   │
Step 4: Create Task Checklist (specs/tasks/XXX-tasks.md)
   │
Step 5: Write Tests First (tests/unit/, tests/integration/, tests/e2e/)
   │
Step 6: Implement Minimal Code in src/ to pass tests
   │
Step 7: Verify against Acceptance Criteria in the Spec
```

---

## 📌 Specification Status Values

Every specification must track its lifecycle status in frontmatter/metadata:
- `DRAFT`: Under initial creation; open to major changes.
- `REVIEW`: Complete draft undergoing review and technical validation.
- `APPROVED`: Frozen for implementation; contracts & acceptance criteria are locked.
- `IN_PROGRESS`: Implementation and automated tests are underway.
- `IMPLEMENTED`: Code complete, tested, and verified against all acceptance criteria.
- `RETIRED`: Deprecated or replaced by another spec.
