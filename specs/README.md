# Specifications Hub

This directory contains all formal specifications governing the system. Specifications are treated with the same rigor and source control standards as production code.

---

## 📂 Subdirectories

- **[`templates/`](./templates/)**: Markdown templates for authoring new specifications.
  - [`feature-spec.template.md`](./templates/feature-spec.template.md) - Standard functional/product specification.
  - [`adr.template.md`](./templates/adr.template.md) - Architectural Decision Record.
  - [`api-contract.template.md`](./templates/api-contract.template.md) - API endpoints & data contract specification.
  - [`task-breakdown.template.md`](./templates/task-breakdown.template.md) - Actionable implementation task checklist.
- **[`features/`](./features/)**: Numbered feature specifications (e.g., `001-user-authentication.md`).
- **[`architecture/adrs/`](./architecture/adrs/)**: Architecture Decision Records documenting key technical choices and trade-offs.
- **[`api/`](./api/)**: API contract specifications, OpenAPI schemas, and payload definitions.
- **[`tasks/`](./tasks/)**: Granular implementation task lists derived directly from approved specifications.

---

## 🚦 Specification Lifecycle

```
[ DRAFT ] ──> [ REVIEW ] ──> [ APPROVED ] ──> [ IN_PROGRESS ] ──> [ IMPLEMENTED ]
                                                                       │
                                                                       └──> [ RETIRED ]
```

| Status | Meaning | Permitted Actions |
|---|---|---|
| `DRAFT` | Initial specification in progress | Edits allowed, not ready for code |
| `REVIEW` | Submitted for team/agent critique | Discussion, review comments, minor revisions |
| `APPROVED` | Consensus reached, contracts locked | Ready for task breakdown & test creation |
| `IN_PROGRESS` | Implementation and testing ongoing | Code in `src/` & `tests/` actively being committed |
| `IMPLEMENTED` | All acceptance criteria satisfied | Verified against spec tests |
| `RETIRED` | Replaced or superseded | Kept for historical reference |

---

## 📝 How to Create a New Specification

1. Copy the appropriate template from `specs/templates/` to the target directory:
   ```bash
   cp specs/templates/feature-spec.template.md specs/features/002-my-new-feature.md
   ```
2. Fill out all sections (leave none blank; use `N/A` with explanation if non-applicable).
3. Set status to `DRAFT`.
4. Submit for review or share with your team / AI assistant.
5. Once marked `APPROVED`, create tasks in `specs/tasks/` and begin test writing in `tests/`.
