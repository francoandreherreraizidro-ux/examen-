# SPEC-004: Upgrade Java Runtime to Java 25 LTS

---

## Metadata

| Field | Value |
|---|---|
| Spec ID | `SPEC-004` |
| Status | `IMPLEMENTED` |
| Author | Copilot |
| Reviewer | Project owner |
| Created | 2026-11-04 |
| Related feature | [SPEC-003](./003-generador-invoice-ubl21.md) |
| Related ADR | [ADR-0004](../architecture/adrs/0004-java-25-lts-runtime.md) |
| API contract | N/A; no API or data contract changes |

## 1. Problem and proposed change

The project currently compiles with Java 21 (`maven.compiler.release=21`), as specified when the UBL Invoice generator was implemented. The project owner has requested an upgrade to the latest Java LTS. Java 25 is the target LTS identified for this proposal.

This specification proposes changing the project's supported Java baseline to Java 25. It is a draft and does not authorize implementation until reviewed and approved.

### Out of scope

- Changing business behavior, UBL output, API contracts, or XML schemas.
- Adding a framework, runtime libraries, or unrelated dependency upgrades.
- Maintaining Java 21 source/API compatibility after the baseline changes.
- Changing deployment infrastructure or provisioning a JDK on external systems.

## 2. User story

- **US-01**: As a project maintainer, I want the project to build and run on Java 25 LTS so that its Java runtime baseline follows the requested current LTS release.

## 3. Requirements

| ID | Requirement | Priority |
|---|---|---|
| `FR-01` | Set the Maven compiler release to Java 25 so production and test code target Java 25 APIs and bytecode. | Must |
| `FR-02` | Build and execute the existing test suite using a Java 25 JDK. | Must |
| `FR-03` | Update the Java-version instructions in the project documentation to identify Java 25 as the required baseline. | Must |
| `NFR-01` | Preserve existing application behavior, including generated UBL XML and validation behavior. | Must |
| `NFR-02` | Do not add or upgrade unrelated dependencies or change application source behavior as part of this runtime-baseline change. | Must |

## 4. Acceptance criteria

### AC-01 — Java 25 compilation baseline

- **Given** a Java 25 JDK and the existing Maven project,
- **When** Maven compiles the project,
- **Then** the compiler uses release 25 and compilation succeeds.

### AC-02 — Test execution on Java 25

- **Given** the project is configured for Java 25,
- **When** the full existing Maven test suite is run with a Java 25 JDK,
- **Then** all existing tests pass, including the UBL schema validation tests.

### AC-03 — Existing behavior preserved

- **Given** the existing unit and integration test coverage,
- **When** the Java baseline upgrade is complete,
- **Then** the tests continue to verify the same generated XML and validation behavior without changes to expected business outputs.

### AC-04 — Documentation consistency

- **Given** the repository's setup and implementation documentation,
- **When** the runtime baseline is upgraded,
- **Then** the documented required Java version and verification command identify Java 25 consistently.

## 5. Verification

- Confirm the effective Maven compiler release is 25.
- Record `java -version` and `mvn -version` from the Java 25 validation environment.
- Run `mvn clean test` with Java 25 and record the result.
- Review the diff to confirm there are no unrelated source, dependency, API, or output changes.

### Verification results

- JDK: Eclipse Temurin `25.0.5+8` LTS.
- Maven: Apache Maven `3.9.9`, running on Java `25.0.5`.
- A fresh project copy completed `mvn clean test`: 9 tests passed, with 0 failures, errors, or skips. Maven compiled all 7 production sources and both test sources with `--release 25`.
- The existing workspace's `mvn clean` could not remove a generated target directory that is a reparse point used by the VS Code Java language server. Running the clean build in a fresh copy avoided changing or terminating that process.
- Direct `javac --release 25` compilation of all 7 production sources also succeeded.

## 6. Approval and history

The project owner explicitly approved this specification and [ADR-0004](../architecture/adrs/0004-java-25-lts-runtime.md) on 2026-11-04. Implementation and verification are complete.

| Date | Version | Status | Note |
|---|---|---|---|
| 2026-11-04 | 0.1 | `DRAFT` | Initial proposal to upgrade the Java baseline from 21 to Java 25 LTS. |
| 2026-11-04 | 1.0 | `APPROVED` | The project owner approved Java 25 LTS as the compiler and test runtime baseline. |
| 2026-11-04 | 1.1 | `IN_PROGRESS` | Implementation and validation started. |
| 2026-11-04 | 1.2 | `IMPLEMENTED` | Maven targets Java 25; documentation updated; Java 25 clean build and all 9 tests passed. |
