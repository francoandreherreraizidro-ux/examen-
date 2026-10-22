# Task Breakdowns

This directory houses task checklists for approved specifications.

---

## 🎯 Purpose

In Spec-Driven Development, we do not begin implementation with open-ended or ad-hoc tasks. Instead, an approved specification in `specs/features/` is broken down into structured, verifiable tasks using the [`../templates/task-breakdown.template.md`](../templates/task-breakdown.template.md).

---

## 📋 File Naming Convention

Name task files with the same prefix ID as their corresponding specification:
- `specs/features/001-example-feature.md` -> `specs/tasks/001-example-tasks.md`
- `specs/features/002-order-checkout.md`  -> `specs/tasks/002-order-checkout-tasks.md`

Tasks must explicitly list the tests to write before implementing code in `src/`.
