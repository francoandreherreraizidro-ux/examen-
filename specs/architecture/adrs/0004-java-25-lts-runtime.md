# ADR-0004: Java 25 LTS Runtime Baseline

---

## Metadata

| Attribute | Details |
|---|---|
| ADR Number | `ADR-0004` |
| Status | `ACCEPTED` |
| Deciders | Project owner |
| Date | 2026-11-04 |
| Related Specs | [SPEC-004](../../features/004-java-25-lts-runtime.md) |
| Superseded By | N/A |

## 1. Context and problem statement

The Maven project currently targets Java 21 through `maven.compiler.release`. The project owner requested an upgrade to the latest Java LTS release. The existing architecture review identifies Java 25 as an LTS alternative, but the implemented [SPEC-003](../../features/003-generador-invoice-ubl21.md) records Java 21 as the approved baseline for the original implementation.

This ADR proposed a Java 25 baseline for the repository. The project owner explicitly approved this ADR and SPEC-004 on 2026-11-04.

## 2. Decision drivers

- Meet the requested Java LTS baseline.
- Keep the existing Maven project and UBL generator behavior.
- Avoid unrelated dependency, framework, or architectural changes.
- Make the required JDK explicit and verifiable during builds and tests.

## 3. Considered options

1. Keep Java 21, the current approved and implemented baseline.
2. Upgrade the compiler and test runtime baseline to Java 25 LTS.

## 4. Decision

Adopt Java 25 LTS as the project's compiler and test runtime baseline by setting Maven's compiler release to 25 and running the full test suite on a Java 25 JDK.

## 5. Consequences and follow-up

### Expected benefits

- The project uses the requested latest LTS baseline.
- Java 25 APIs and bytecode are available to the project.

### Trade-offs

- Java 21 will no longer be a supported compile/runtime baseline for this project after implementation.
- Developers and CI environments must provide a Java 25 JDK.
- The project must verify the complete existing test suite on Java 25.

### Follow-up actions

- [x] Approve SPEC-004 and this ADR on 2026-11-04.
- [x] Update Maven compiler release to 25.
- [x] Update Java-version documentation.
- [x] Run `mvn clean test` on Java 25 and record the JDK/Maven versions and result in SPEC-004.
- [x] Update SPEC-004 and its task checklist after verification.

## 6. Implementation record

Implemented on 2026-11-04. The project compiles with `maven.compiler.release=25`; a fresh-copy `mvn clean test` using Eclipse Temurin `25.0.5+8` and Apache Maven `3.9.9` passed all 9 tests.
