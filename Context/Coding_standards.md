WALNUS SMARTVIEW PRO

CODING STANDARDS

Version: 1.0
Date: 24 October 2026
Owner: Walnus Global Ventures
Status: Development Standard

---

1. Purpose

These standards govern all code written, modified, reviewed, or committed to Walnus SmartView Pro.

Every implementation must prioritize:

- Accuracy and reliability.
- Maintainability and modularity.
- Security and privacy.
- Performance across high-end and low-spec Android devices.
- Offline resilience where required.
- Consistent user experience.
- Verifiable builds and documented progress.

Agents and developers must inspect the existing repository before modifying it. Do not replace working code or restructure the application without evidence that the change is necessary.

2. Technology and Repository Rules

1. Follow the technology stack, language versions, dependency versions, and Android configuration already established in the repository unless an approved change is required.
2. Use Kotlin and the existing Android architecture conventions where applicable.
3. Use Gradle through the project's existing wrapper. Do not introduce a different build system.
4. Reuse established libraries and components before adding dependencies.
5. Never upgrade multiple major dependencies without a documented compatibility reason.
6. Never commit API keys, passwords, signing credentials, private tokens, or other secrets.
7. Do not commit generated build output, temporary files, or local machine configuration unless explicitly required by the repository.
8. Preserve existing functionality unless the task explicitly requires changing it.

Repository inspection is mandatory before implementation. Confirm the current branch, working-tree status, relevant files, recent commits, build configuration, and existing tests.

3. Architecture and Modularity

- Keep responsibilities separated between UI, domain logic, data access, and external services.
- Keep business rules out of UI components.
- Use repositories or established data-access abstractions rather than accessing databases and network services directly from screens.
- Keep trade-specific functionality independent wherever practical.
- Avoid circular dependencies between features and core components.
- Prefer small, cohesive classes and functions with explicit responsibilities.
- Introduce separate Gradle modules only when they provide a meaningful architectural or maintenance benefit.
- Do not create abstractions, interfaces, or modules without a practical use case.
- Make visual components replaceable without rewriting business logic.

The intended feature progression is painting first, followed by plastering and screeding. New features must not destabilize existing survey, camera, navigation, or persistence functionality.

4. Naming and Formatting

Use standard Kotlin and Android naming conventions.

Element| Convention| Example
Classes and objects| PascalCase| "SurveyRepository"
Functions and properties| camelCase| "calculatePaintQuantity()"
Constants| UPPER_SNAKE_CASE| "MAX_RETRY_COUNT"
Packages| lowercase| "com.walnus.smartview"
Composable UI functions| PascalCase| "SurveySummaryScreen"
Boolean properties| Clear true/false meaning| "isSurveyComplete"

Additional rules:

- Use descriptive names that communicate intent.
- Avoid unexplained abbreviations, single-letter names outside conventional short scopes, and vague names such as "data", "manager", or "helper" when a precise name is possible.
- Keep formatting consistent with the existing repository and automated formatter.
- Remove unused imports, dead code, debug statements, and obsolete comments.
- Do not reformat unrelated files merely to produce a cleaner diff.

5. Functions, Classes, and Error Handling

- Keep functions focused on one responsibility.
- Avoid duplicated business logic.
- Validate external input at the appropriate boundary.
- Handle expected failures explicitly.
- Do not silently swallow exceptions.
- Do not use empty catch blocks.
- Do not expose internal stack traces, credentials, or sensitive user data in the UI or logs.
- Use structured logging consistent with the project.
- Use appropriate coroutine dispatchers for database, file, and network operations.
- Avoid blocking the main thread.
- Use clear loading, empty, success, and error states where applicable.
- Make retry behavior bounded and appropriate to the operation.

Use comments to explain non-obvious decisions and constraints, not to narrate code that is already clear.

6. Data and Offline-First Requirements

The survey data hierarchy is:

Project → Survey → Measurement

Where Room is the established local persistence layer:

- Use the existing entity, DAO, database, and migration conventions.
- Preserve data integrity and relationships.
- Add schema migrations for database changes; never casually destroy a user's local database to resolve a migration failure.
- Keep database access off the main thread.
- Make writes and updates predictable and testable.
- Distinguish measured facts from estimates and suggestions.

Every relevant measurement must preserve its provenance:

- "MEASURED"
- "INFERRED"
- "ESTIMATED"
- "USER_CONFIRMED"
- "AI_SUGGESTED"

Never present an AI suggestion or estimate as a physically verified measurement. Maintain units, timestamps, and other required measurement metadata consistently.

Offline behavior must be explicitly defined for each feature. A network failure must not silently erase saved survey work.

7. Android Compatibility and Performance

SmartView Pro must support capable devices while remaining usable on lower-specification Android phones.

- Detect camera, AR, memory, storage, and other required capabilities at runtime.
- Provide a documented fallback when a device lacks an optional capability.
- Do not assume ARCore or hardware-accelerated features are available on every device.
- Avoid unnecessary background processing, excessive memory allocation, and large image loads.
- Release camera and other hardware resources when no longer needed.
- Handle permission denial and revoked permissions.
- Preserve user work during configuration changes and recoverable interruptions.
- Test on a representative high-end device and a low-specification device when available.
- Measure performance before introducing complex optimization.

A missing optional capability must not crash the application or unnecessarily block core painting-survey workflows.

8. UI and Design System

- Use shared design tokens and reusable components for colors, typography, spacing, and common controls.
- Keep screens focused on the task the user is performing.
- Maintain consistent navigation, validation, accessibility, and feedback.
- Provide useful empty, loading, success, and failure states.
- Support appropriate screen sizes, text scaling, and touch targets.
- Avoid hardcoding visual values throughout feature logic when a shared design token is appropriate.
- Keep design changes separate from domain logic wherever possible.

The interface must remain modular so that future visual redesigns do not require rewriting survey calculations, data storage, or subscription logic.

9. Security and Privacy

- Apply least-privilege permissions.
- Collect only data needed for a defined feature.
- Explain and request permissions when needed.
- Protect project, location, image, survey, and account data.
- Do not log sensitive data unnecessarily.
- Validate authorization on trusted server-side boundaries.
- Never treat client-side UI restrictions as sufficient authorization.
- Do not embed private service credentials in the APK.
- Review file access, network transport, and data retention when introducing a new data flow.

Security-sensitive changes require focused review and appropriate tests before merging.

10. Trials, Subscriptions, and Entitlements

Subscription functionality must follow the architecture defined in "ARCHITECTURE.md".

- Keep plan definitions and feature-access rules centralized.
- Separate trial, active subscription, premium, enterprise, expired, cancelled, and unavailable states where applicable.
- Do not scatter hardcoded plan checks across UI screens.
- Use a shared entitlement service or repository.
- Treat trusted backend verification as authoritative for paid entitlements.
- Validate purchase events and provider notifications server-side.
- Make purchase-event processing idempotent.
- Define offline entitlement behavior and cache expiry explicitly.
- Never unlock paid features solely because a local preference says "isPremium = true".
- Do not delete a user's saved projects merely because a trial or subscription expires.
- Test purchase cancellation, renewal, expiration, restoration, network failure, and account changes as applicable.

Do not implement a payment provider until the distribution channel, target markets, and applicable payment requirements have been confirmed.

11. AI and External Services

- Keep AI calls behind explicit service interfaces.
- Treat model output as untrusted input.
- Validate AI-generated quantities, prices, recommendations, and structured data before use.
- Distinguish verified supplier data from estimates and AI-generated suggestions.
- Handle timeouts, rate limits, malformed responses, and unavailable services.
- Never allow AI output to bypass access controls or data validation.
- Keep secrets on trusted server infrastructure.
- Make the application usable when optional AI services are unavailable.

AI-assisted development does not remove the requirement for human-readable code, tests, review, or verified builds.

12. Testing Requirements

Tests must be appropriate to the change.

Unit tests

- Business rules and calculations.
- Measurement conversions and quantity estimates.
- Entitlement and subscription-state transitions.
- Input validation and error handling.

Data tests

- Room persistence and relationships.
- Database migrations.
- Repository behavior and failure recovery.

UI and integration tests

- Critical navigation and user workflows.
- Permission handling and camera states.
- Painting-survey creation and persistence.
- Subscription and feature-gating behavior where implemented.

For quantity calculations, test normal cases, boundary values, invalid input, and unit conversions.

Do not claim a test passed unless it was actually executed and its result was checked. If a test cannot run, document the reason.

13. Dependencies and Configuration

- Prefer stable, maintained dependencies compatible with the existing project.
- Add a dependency only when its value justifies its maintenance, security, size, and compatibility costs.
- Record significant dependency additions and upgrades.
- Use the repository's established version-management approach.
- Keep environment-specific configuration out of source code.
- Verify dependency licenses and security implications where relevant.
- Avoid introducing duplicate libraries that solve the same problem.

14. Required Development Workflow

Every assigned development stage ("SV-001", "SV-002", and subsequent stages) must follow this sequence:

1. Inspect: Review repository state, previous work, relevant documentation, and build status.
2. Plan: Define scope, acceptance criteria, affected files, and risks.
3. Implement: Make the smallest coherent change that satisfies the stage.
4. Test: Run relevant automated checks and targeted tests.
5. Build: Produce and verify a debug APK before committing.
6. Inspect: Review the diff for regressions, unrelated changes, secrets, and generated files.
7. Document: Update "README.md" and relevant project documents to reflect actual progress.
8. Commit: Commit only after required checks pass, unless a documented blocker prevents completion.
9. Report: Produce a completion report with evidence.

Do not mark a stage complete because code was generated or a commit was created.

If the build fails, report the exact failing step, relevant error, attempted fix, and current status. Do not conceal the failure or claim successful APK generation.

15. Commit Standards

Use concise, descriptive commit messages, preferably following Conventional Commits:

- "feat: add painting survey workflow"
- "fix: resolve camera permission handling"
- "test: cover paint quantity calculations"
- "docs: update architecture and progress"
- "refactor: isolate entitlement evaluation"

Each commit should represent a coherent change.

Before committing:

- Confirm the correct branch.
- Inspect the complete diff.
- Ensure no unrelated user work is overwritten.
- Verify required tests and APK build status.
- Confirm documentation reflects the implementation.

Never use destructive Git commands or rewrite shared history without explicit authorization.

16. Mandatory Stage Completion Report

Every completed SV stage must report:

1. Stage identifier and objective.
2. Features implemented.
3. Files added, changed, or removed.
4. Tests executed and their actual results.
5. APK build status and artifact location, if generated.
6. Commit hash and branch.
7. Known defects, limitations, and unresolved risks.
8. Documentation updated.
9. Acceptance criteria passed or failed.
10. Recommended next action, limited to the next necessary step.

A stage is complete only when its agreed acceptance criteria are satisfied and the evidence is recorded. Otherwise, report it as blocked or partially complete.

17. Prohibited Shortcuts

The following are unacceptable:

- Replacing substantial existing code without inspecting it.
- Disabling tests to make a build pass.
- Hiding compiler errors or suppressing warnings without justification.
- Claiming a build, test, or deployment succeeded without evidence.
- Committing secrets or private credentials.
- Implementing fake subscription verification.
- Presenting estimates as measured facts.
- Removing database data to avoid writing migrations.
- Adding unnecessary dependencies or architectural complexity.
- Marking incomplete work as complete.
- Making unrelated changes that increase review risk.

18. Definition of Done

A change is complete only when:

- The agreed acceptance criteria are met.
- The code follows the established architecture and standards.
- Relevant tests have passed, or blockers are explicitly documented.
- The debug APK has been built and verified before the commit, unless a documented blocker prevents it.
- The diff has been reviewed.
- Documentation is current.
- No known critical regression or unresolved security issue is concealed.
- The commit and stage report provide traceable evidence.

Final rule: Preserve working functionality, implement only what the current stage requires, verify the result, document the evidence, and report the truth.

------------------------------------------------------------------
19. ESTABLISHED CONVENTIONS (SV-001)
------------------------------------------------------------------

These conventions are now in force because code exists that follows them. New code must
match them, and deviations must be documented as an ADR in `ARCHITECTURE.md`.

19.1 Layout

- Package root: `com.walnus.smartview`.
- Module layout: `core/<module>/src/main/kotlin/...` and `core/<module>/src/test/kotlin/...`.
- Android-free modules are Kotlin/JVM libraries (`kotlin("jvm")`), so they run under unit
  tests without a device.
- Android modules (from SV-002) live under `app/` (and later `feature/*`, `core/database`
  and so on) and may depend on core modules, never the reverse.

19.2 Domain rules

- Any value a surveyor relies on carries `Provenance`; combine trust with
  `Provenance.weakestOf`, never with an ad-hoc comparison.
- Derived quantities must be capped at `ESTIMATED`; they must never be labelled
  `MEASURED`. If inputs include `AI_SUGGESTED`, that weakness propagates.
- Constructors validate their arguments with `require(...)` and a message that tells the
  caller what to fix. Domain objects are immutable `data class`es.
- Money is `Money` (integer minor units) — never `Double`. Percentages that are really
  rates are stored in basis points.
- No prices, tax rates, coverage figures or plan limits may appear in UI or feature code;
  they come from `PaintSpecification`, the user, or `PlanCatalog`.
- Time is injected through `Clock` wherever expiry or ordering matters; `SystemClock` is
  the production implementation, `ManualClock` is for tests and previews.

19.3 Tests

- Tests live beside the module they verify, in `src/test/kotlin`, and use `kotlin.test`
  assertions with JUnit-compatible annotations.
- Test names describe behaviour: `subtractsDoorsAndWindowsFromWalls`, not `testArea`.
- Every validation rule and every branch of a calculation gets at least one test.
- Deterministic results are asserted twice with the same input (see
  `sameInputsAlwaysProduceSameEstimate`).
- Tests must run without Android, network or clock access.

19.4 Tooling

- `tools/verify-core.sh` is the gate for core changes; run it before committing domain
  work and attach the log to `docs/build-evidence/`.
- `tools/jvmtest/` contains environment workarounds only. Never reference it from module
  source sets, and never ship it.
- Build evidence logs are text, small and committed. APKs are not committed.

19.5 Commits

Conventional commit prefixes used so far: `feat:`, `docs:`, `test:`, `build:`, `chore:`.
A commit that changes domain behaviour must state which gate was run and whether it
passed, or that it was blocked and why.
