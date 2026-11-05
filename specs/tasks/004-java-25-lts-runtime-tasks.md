# Tasks: Java 25 LTS Runtime Baseline

**Specification:** [SPEC-004](../features/004-java-25-lts-runtime.md)  
**ADR:** [ADR-0004](../architecture/adrs/0004-java-25-lts-runtime.md)  
**Status:** `COMPLETED`  
**Approval:** SPEC-004 and ADR-0004 approved by the project owner on 2026-11-04.

## Checklist

- [x] Approve SPEC-004 and ADR-0004.
- [x] Confirm Java 25 JDK is available for build and test validation.
- [x] Update `maven.compiler.release` to 25.
- [x] Update directly related Java-version documentation.
- [x] Run `mvn clean test` with Java 25 and record `java -version` and `mvn -version`.
- [x] Review the diff for preserved behavior and absence of unrelated changes.
- [x] Record verification results and close SPEC-004.
