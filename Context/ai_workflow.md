AI_WORKFLOW.md — WALNUS SMARTVIEW PRO

1. Purpose

This document defines the mandatory AI-assisted development workflow for Walnus SmartView Pro. Every AI agent, developer, and automated workflow must follow these instructions throughout the project lifecycle.

The objective is to deliver a reliable, modular, well-documented Android application that is tested on real devices, builds successfully, and remains maintainable as new features are introduced.

Core principles: Accuracy, reliability, efficiency, connectivity, modularity, security, and continuous verification.

2. Project Understanding and Documentation

Before modifying code, every AI agent must:

1. Read "README.md" to understand the project purpose, current implementation, architecture, and development status.
2. Read "PROJECT_OVERVIEW.md", "ARCHITECTURE.md", and "CODING_STANDARDS.md" when available.
3. Review "PROGRESS_TRACKER.md" and the latest completion report.
4. Inspect the relevant source files, dependencies, existing tests, Git branch, and recent commits.
5. Identify existing functionality and known limitations before proposing changes.

If documentation is missing or outdated, update it before proceeding with substantial implementation.

Maintain "README.md" as the evolving project record. Update it as the application develops, including completed features, architectural decisions, setup instructions, known limitations, and verified build procedures.

Never claim a feature exists unless its implementation has been verified.

3. Modular Development Methodology

Develop SmartView Pro in small, independently verifiable modules.

Each module must have:

- A clearly defined purpose and scope.
- Explicit inputs, outputs, dependencies, and acceptance criteria.
- A defined interface with other modules.
- Appropriate error handling and tests.
- Updated documentation.
- A clear completion status.

Implement modules in dependency order. Avoid rewriting stable functionality when an isolated change can achieve the same result.

Keep the user interface, business logic, data storage, networking, AI services, and device-specific functionality appropriately separated.

Design the UI so its visual structure, themes, components, and navigation can be modified without unnecessary changes to core business logic.

Use reusable components and established Android architecture patterns. Avoid unnecessary abstractions, duplicated logic, oversized classes, and tightly coupled features.

Do not begin dependent modules until their prerequisites have been implemented and verified. Independent modules may proceed in parallel when file ownership, interfaces, and integration requirements are clear.

4. Mandatory Development Cycle

Every development stage, identified by its assigned SV number, must follow this sequence:

1. Understand: Read project documentation and inspect the existing implementation.
2. Plan: Define the scope, dependencies, acceptance criteria, risks, and affected files.
3. Implement: Make the smallest coherent set of changes required.
4. Review: Inspect the changes for correctness, security, compatibility, maintainability, and unintended regressions.
5. Test: Run relevant unit tests, integration tests, static analysis, and other available checks.
6. Build: Produce a fresh Android APK from the current source state.
7. Verify: Confirm build success, inspect the generated artifact, and test relevant functionality.
8. Document: Update the README, progress tracker, and other affected documentation.
9. Commit: Commit only changes that satisfy the stage's acceptance criteria and mandatory verification requirements.
10. Report: Produce a completion report documenting the results.

Mandatory rule: Build the APK before every feature-stage commit. Never represent an unsuccessful, untested, or unverified build as successful.

If a build or test fails, diagnose and fix the issue before committing the affected implementation. If the failure cannot be resolved, document the blocker, preserve the diagnostic evidence, and do not mark the stage complete.

Documentation-only commits may be exempt from producing a new APK only when the changes cannot affect source code, dependencies, build configuration, or application behavior. The exemption must be recorded explicitly.

5. SV Stage Completion Requirements

Every SV stage must end with a completion report containing:

- SV stage identifier and objective.
- Summary of changes implemented.
- Files added, modified, or removed.
- Features completed and acceptance criteria verified.
- Tests executed and their actual results.
- APK build result, artifact location, and build identifier where available.
- Known defects, limitations, and outstanding tasks.
- Documentation updated.
- Git branch and commit SHA, if committed.
- Real-device testing status.
- Recommended next stage.

Use explicit statuses: "PASS", "FAIL", "BLOCKED", or "NOT TESTED".

Do not report a feature as device-tested when it has only been compiled or tested in an emulator.

6. Android Build and APK Management

Use the repository's established Gradle configuration and Android SDK requirements.

Before each qualifying commit:

1. Execute the appropriate Gradle build and test tasks.
2. Run static analysis and lint checks where configured.
3. Build the relevant debug or release APK.
4. Confirm that the expected APK exists and is non-empty.
5. Record the build result and artifact path.
6. Preserve diagnostic logs when failures occur.

Use GitHub Actions or another configured cloud build environment when local hardware is insufficient.

Store distributable APKs as GitHub Actions artifacts or appropriate release assets. Avoid committing generated APKs to source control unless the repository explicitly requires that arrangement.

Never invent an artifact URL, build number, test result, or successful workflow status.

7. High-End and Low-Spec Device Compatibility

SmartView Pro must support a broad Android device ecosystem, including devices with limited processing power, memory, storage, camera capabilities, and connectivity.

High-end devices

Where supported, use enhanced capabilities such as:

- Higher-resolution image processing.
- More advanced visualisation.
- Optional accelerated processing.
- Richer animations and responsive visual effects.
- Enhanced AI-assisted functionality.

Low-spec devices

Provide graceful fallbacks, including:

- Reduced image-processing workloads.
- Efficient memory and storage use.
- Simplified animations and visual effects.
- Lower-resolution previews when appropriate.
- Background operations that avoid blocking the UI.
- Clear handling of unsupported hardware and unavailable services.
- Offline access to locally available project and survey data.

Detect capabilities rather than assuming that all devices support the same features.

AR, advanced camera functions, and other hardware-dependent capabilities must be optional enhancements unless explicitly established as essential requirements.

Maintain a consistent core workflow across supported devices. Never allow an unavailable enhancement to disable unrelated essential functionality.

8. Testing and Physical Device Verification

Use a layered testing strategy:

1. Unit tests for business logic and calculations.
2. Integration tests for data persistence, repositories, and service interactions.
3. UI tests for navigation and critical user journeys.
4. Build and static-analysis checks in CI.
5. Emulator testing where appropriate.
6. Physical-device testing for camera access, permissions, storage, performance, and hardware-dependent behavior.

After every SV stage, identify which tests have been completed and which remain outstanding.

When a physical device is unavailable to the agent, report physical testing as "NOT TESTED". Do not fabricate test results.

Prioritize regression testing for previously verified functionality, particularly camera capture, survey creation, offline data persistence, navigation, and quotation-related workflows.

9. SmartView Pro Feature Priorities

Implement features in the approved project order, with professional painting and building-surface surveys as the initial priority.

The planned capability areas include:

- Project and survey management.
- Camera-based site capture.
- Surface identification and measurement workflows as supported by the implementation.
- Painting quantity estimation and quotation generation.
- Colour visualisation and painted-property reporting.
- Plastering and screeding workflows.
- Offline-first data handling.
- AI-assisted material estimation and supplier research.
- Future image-to-3D and video-to-3D capabilities where technically feasible.

Do not treat planned features as completed features. Follow the current project overview and progress tracker when determining which capabilities are authorised for the current stage.

Prioritise accurate calculations, transparent assumptions, reliable data capture, and usable reports over speculative or visually impressive features that are not yet dependable.

10. Data Accuracy and Provenance

Where measurements, quantities, or estimates are generated, preserve their provenance.

Use the following classifications where applicable:

- "MEASURED" — obtained through a verified measurement process.
- "INFERRED" — derived from available evidence.
- "ESTIMATED" — calculated using assumptions or approximate inputs.
- "USER_CONFIRMED" — explicitly reviewed and confirmed by the user.
- "AI_SUGGESTED" — proposed by an AI system and not independently verified.

Clearly distinguish measured values from estimates and AI suggestions.

Do not present AI-generated dimensions, material quantities, supplier prices, or quotations as guaranteed accurate.

Allow users to review and correct important inputs. Document calculation assumptions, units, confidence limitations, and relevant data sources.

11. Offline-First Operation and Connectivity

Essential project and survey operations should work offline wherever the approved architecture supports them.

Use the existing local persistence strategy and established data hierarchy. Preserve data integrity during application restarts and intermittent connectivity.

When connectivity returns, any synchronisation functionality must handle failures, retries, duplicate operations, and conflicting updates safely.

Clearly communicate when AI services, live supplier research, remote pricing, or other network-dependent features are unavailable.

Never imply that live information has been retrieved when the device is offline or a remote request has failed.

12. Security and Privacy

Follow Android security best practices.

- Request only necessary permissions.
- Protect sensitive local and remote data.
- Never hardcode API keys, passwords, or production secrets.
- Keep credentials in appropriate secret-management systems.
- Validate inputs and handle untrusted external data safely.
- Use secure network communication.
- Avoid logging credentials, sensitive survey information, or unnecessary personal data.
- Review dependency changes for compatibility and security risks.

Any AI integration must use approved service interfaces and secure credential handling. Never expose privileged service credentials in the Android application.

13. Subscription and Premium Architecture

Implement subscriptions and premium features as modular capabilities rather than embedding payment assumptions throughout the application.

The architecture must support:

- A clearly defined free tier.
- Premium features and subscription entitlements.
- Optional feature limits based on the approved product specification.
- Subscription-status checks.
- Graceful handling of expired, cancelled, pending, or unavailable subscriptions.
- Secure verification of purchases and entitlements.
- A modular billing integration that can evolve independently of core survey functionality.

Keep subscription logic separate from the core painting, surveying, and project-management logic.

Do not invent prices, subscription durations, or premium entitlements. Obtain these from the approved product specification.

Do not block access to a user's locally stored project data merely because a subscription cannot be verified. Apply entitlement restrictions according to the approved policy while preserving data access and integrity.

Use the applicable official Android billing requirements and supported payment architecture. Do not trust client-side subscription flags as the sole authority for premium access.

If subscription functionality is not part of the current SV stage, document the architecture requirements without introducing unfinished billing behavior.

14. AI Agent Responsibilities

AI agents must operate within the approved scope and maintain traceable changes.

Before implementation:

- Read the latest documentation.
- Inspect the active branch and repository state.
- Identify existing implementations before creating replacements.
- Check dependencies and relevant tests.
- Avoid overwriting another agent's work.

During implementation:

- Keep changes focused.
- Follow coding standards.
- Add or update tests.
- Document important architectural decisions.
- Preserve working functionality.

After implementation:

- Review the complete diff.
- Run the required verification steps.
- Build the APK.
- Update documentation.
- Produce the SV completion report.
- Commit only when the applicable requirements are satisfied.

If multiple agents work concurrently, assign clear module ownership and integration responsibilities. Resolve conflicts deliberately and verify the integrated application before committing.

Agents must not claim to have executed commands, accessed devices, inspected files, or completed external actions that they did not actually perform.

15. GitHub and Version Control

Use meaningful, traceable commits and the repository's established branching strategy.

Recommended commit format:

"feat(sv-XXX): describe feature"

"fix(sv-XXX): describe correction"

"test(sv-XXX): describe verification"

"docs(sv-XXX): describe documentation update"

Before committing:

- Review the working tree.
- Inspect the staged diff.
- Exclude secrets, temporary files, unrelated changes, and generated artifacts that do not belong in source control.
- Verify the required build and tests.
- Confirm that documentation reflects the actual implementation.

After committing, record the commit SHA and ensure the intended changes are present.

Use GitHub Actions for repeatable builds and automated checks where configured. A successful local build does not establish that a separate cloud workflow has succeeded.

Never rewrite shared Git history or discard another agent's work without explicit authorisation.

16. Continuous Documentation

Keep the following project documents aligned with the implementation:

- "README.md" — project introduction, setup, current status, and verified capabilities.
- "PROJECT_OVERVIEW.md" — product goals, scope, priorities, and acceptance criteria.
- "AI_WORKFLOW.md" — mandatory agent and development procedures.
- "ARCHITECTURE.md" — application structure, module boundaries, data flow, and technical decisions.
- "CODING_STANDARDS.md" — coding conventions, security rules, and quality requirements.
- "PROGRESS_TRACKER.md" — completed, active, blocked, and planned SV stages.
- SV completion reports — implementation, build, testing, and release evidence.

Use the exact filenames already established in the repository. Do not create duplicate documents merely because a naming convention differs.

Update relevant documents in the same development stage as the implementation. Record significant design decisions and explain any deviation from the approved architecture.

17. Failure Recovery and Blocker Management

When a build, test, integration, or implementation fails:

1. Capture the actual error and relevant logs.
2. Identify the root cause where possible.
3. Determine whether the issue is caused by code, configuration, dependencies, infrastructure, permissions, or an external service.
4. Apply the smallest safe correction.
5. Repeat the affected tests and build.
6. Check for regressions.
7. Document unresolved issues and required decisions.

If blocked by unavailable credentials, quota limits, missing hardware, external outages, or insufficient permissions, continue with independent authorised tasks where possible.

Do not bypass acceptance criteria, disable meaningful tests to force a pass, conceal failures, or commit broken implementation as completed work.

18. Definition of Done

An SV stage is complete only when all applicable requirements are satisfied:

- [ ] Project documentation reviewed.
- [ ] Scope and acceptance criteria met.
- [ ] Implementation follows the modular architecture.
- [ ] Required tests pass.
- [ ] Static analysis and relevant quality checks completed.
- [ ] Fresh APK built successfully.
- [ ] APK artifact verified and location recorded.
- [ ] Relevant physical-device testing completed or explicitly marked outstanding.
- [ ] Regression risks assessed.
- [ ] README and progress tracker updated.
- [ ] Completion report produced.
- [ ] Git changes reviewed and commit SHA recorded, if committed.
- [ ] Outstanding defects and limitations disclosed.

A stage must remain "BLOCKED" or "IN PROGRESS" when mandatory acceptance criteria have not been met.

19. Final Operating Directive

Build SmartView Pro incrementally, verify every meaningful change, protect existing functionality, and document the evidence.

Do not optimise for the number of files changed, the speed of code generation, or the appearance of progress. Optimise for working software, dependable measurements, maintainable architecture, broad Android compatibility, secure data handling, and a clear audit trail.

Every agent must leave the repository in a state that the next agent can understand, verify, and continue without reconstructing undocumented decisions.

The final objective is a production-oriented SmartView Pro application whose code, documentation, APK artifacts, and reported status agree with one another.

------------------------------------------------------------------
20. VERIFICATION GATES IN THIS REPOSITORY (SV-001)
------------------------------------------------------------------

20.1 Gate ladder

Run the highest-numbered gate the environment supports, and report the highest gate
reached — never a higher one.

| Gate | Command | Requires |
|---|---|---|
| G1 Core compile + unit tests | `KOTLIN_HOME=<kotlin> ./tools/verify-core.sh` | JDK 17+ and a Kotlin compiler |
| G2 Gradle unit tests | `./gradlew test` | Gradle distribution + Maven Central |
| G3 Android build | `./gradlew :app:assembleDebug` | Android SDK + Google Maven (from SV-002) |
| G4 Device verification | install the APK on a physical device | APK + device |

20.2 Evidence rules

- Every gate run must leave a log in `docs/build-evidence/` named `sv-<n>-<gate>.log`.
- The completion report must state the highest gate reached and the exact failure of the
  first gate that was not reached.
- A blocked gate is reported as BLOCKED with the diagnostic output; it is never silently
  skipped, and never described as passing.
- Do not invent APK paths, hashes, device results or CI results.

20.3 Environment constraint currently in force

The SV-001 environment could reach only GitHub, the npm registry and PyPI. Google Maven,
Maven Central, `services.gradle.org` and the Android SDK were unavailable, so G2-G4 could
not run. G1 was executed fully: all five core modules compiled and 78 tests passed.

If a future environment has broader access, run G2 and G3 immediately and record the
results before starting new feature work — an unbuilt `:app` module must not accumulate
scope on top of an unverified foundation.

20.4 Test tooling note

Tests are ordinary `kotlin.test`/JUnit tests under `src/test/kotlin`. In restricted
environments `tools/jvmtest/TestRunner.kt` executes them reflectively and
`tools/jvmtest/stubs/` supplies a compile-time `org.junit.Test` annotation. The stub is
never shipped and must be deleted if the project ever drops JUnit 4 in favour of a
framework whose annotation cannot be stubbed the same way (for example JUnit 5) —
in that case the Gradle build is the only valid gate.

20.5 Documentation obligations retained

Section 5 and the completion-report template in `docs/reports/` remain mandatory. The
first report, `docs/reports/SV-001-completion-report.md`, is written as the reference
example: it records criteria, evidence, the blocked build, limitations and the next SV.
