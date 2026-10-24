WALNUS SMARTVIEW PRO

ARCHITECTURE.md

Version: 2.0
Last Updated: 24 October 2026
Owner: Walnus Global Ventures
Architecture: Modular Android Application with Commercial Entitlements
Status: Active Development — Verify Existing Implementation Before Changes

---

1. Architectural Objective

SmartView Pro must be developed as a modular, maintainable and commercially deployable Android application.

The architecture must support:

- Reliable site surveys and camera capture.
- Offline-first project and measurement persistence.
- Painting as the primary trade workflow.
- Independent plastering and screeding modules.
- Reusable UI components and replaceable design systems.
- AI-assisted estimation and future visualisation capabilities.
- Free trials, paid subscriptions, premium features and enterprise licensing.
- Secure account and entitlement verification.
- Low-spec Android devices and progressive enhancement on capable devices.

Core rule: A new feature must not require unrelated application features to be rewritten.

The architecture should remain as simple as possible while supporting these requirements. Do not introduce unnecessary frameworks, services or modules.

2. Architecture Principles

1. Modularity: Separate responsibilities behind clear interfaces.
2. Separation of concerns: Keep UI, business logic, persistence and external integrations distinct where justified.
3. Offline-first operation: Core survey capture must not depend on continuous connectivity.
4. Data integrity: Preserve survey history, relationships and measurement provenance.
5. Security: Treat the backend as authoritative for protected commercial access.
6. Testability: Business rules and calculations must be independently testable.
7. Replaceability: External providers and visual design implementations must be replaceable without rewriting unrelated features.
8. Incremental migration: Improve the existing repository without discarding validated work.
9. Evidence-based delivery: Every development stage requires build and test evidence.

---

3. Target System Structure

The following is the target logical architecture. Inspect the existing repository before creating or moving files.

SmartView Pro Android Application
│
├── Presentation Layer
│   ├── Shared Design System
│   ├── Navigation
│   ├── Projects UI
│   ├── Survey and Camera UI
│   ├── Painting UI
│   ├── Quotations UI
│   ├── Account and Subscription UI
│   └── Future Trade UIs
│
├── Domain Layer
│   ├── Project and Survey Rules
│   ├── Measurement and Estimation
│   ├── Trade-Specific Calculations
│   ├── Quotation Rules
│   └── Entitlement and Access Decisions
│
├── Data Layer
│   ├── Room Database
│   ├── Repository Implementations
│   ├── Offline State
│   └── Synchronisation Abstractions
│
├── Integration Layer
│   ├── Camera and Device Capabilities
│   ├── Remote APIs and AI Services
│   ├── Identity Provider
│   └── Billing Provider Adapter
│
└── Trusted Backend — When Implemented
    ├── Account and Organisation Services
    ├── Subscription and Entitlement Records
    ├── Purchase Verification
    ├── Provider Webhook Processing
    ├── Protected API Authorisation
    └── Optional Synchronisation and AI Services

The backend is a proposed architectural responsibility, not a claim that these services already exist.

4. Module Boundaries

Create modules only when the existing code and requirements justify them. Begin with logical package boundaries where sufficient, then extract independent Gradle modules incrementally.

Module| Responsibility
"core:model"| Shared domain models, IDs and value types
"core:database"| Room entities, DAOs and migrations
"core:designsystem"| Reusable UI components and visual tokens
"core:navigation"| Navigation contracts
"core:network"| Remote API and synchronisation abstractions
"core:entitlements"| Shared access-control contracts and entitlement state
"feature:projects"| Project management
"feature:survey"| Survey creation, camera capture and survey state
"feature:painting"| Painting workflow and calculations
"feature:plastering"| Plastering workflow
"feature:screeding"| Screeding workflow
"feature:quotations"| Quotation and report generation
"feature:account"| Account settings and account state
"feature:billing"| Billing UI and payment-provider integration

These are target boundaries, not an instruction to create every module immediately.

Dependency Rules

- Feature modules may depend on shared core contracts.
- Trade modules must not depend on each other's private implementation.
- UI components must not contain core business calculations.
- Domain rules must not depend directly on payment SDKs.
- Database implementations must be accessed through suitable repository contracts.
- Billing-provider types must not leak throughout the application.
- Avoid circular dependencies.
- Do not create empty modules solely to match this document.

5. Project and Survey Data Architecture

Maintain the established hierarchy where applicable:

Project → Survey → Measurement

Project

Represents a construction or finishing job.

Potential fields include:

- Project identifier.
- Name and description.
- Customer or organisation reference, where supported.
- Location information, where necessary and consented to.
- Creation and modification timestamps.
- Project status.

Survey

Represents a survey conducted for a project.

Potential fields include:

- Survey identifier.
- Project identifier.
- Survey type.
- Capture date.
- Relevant room or surface references.
- Survey status.

Measurement

Represents a recorded dimension, quantity or estimate.

Where appropriate, retain:

- Measurement identifier.
- Survey reference.
- Measurement type and unit.
- Value and associated assumptions.
- Provenance.
- Timestamp.
- Source information.
- Confidence or uncertainty where applicable.
- User correction or confirmation state.

Use existing model definitions where they already meet these requirements. Do not introduce duplicate entities without a migration plan.

Measurement Provenance

Preserve the established provenance categories:

- "MEASURED"
- "INFERRED"
- "ESTIMATED"
- "USER_CONFIRMED"
- "AI_SUGGESTED"

A calculated quantity must not silently become a measured physical value.

Persistence Requirements

- Room provides local persistence for supported offline workflows.
- Schema changes require explicit migrations.
- Migration tests must protect existing project data.
- Failed writes must not produce false success messages.
- Data synchronisation must define conflict-resolution rules before implementation.
- Subscription expiry must not delete user-created project data.

6. Modular Trade Architecture

All trade modules should reuse shared project, survey, estimation infrastructure and reporting contracts.

Painting

Painting is the first priority.

Its module owns:

- Painting-specific inputs.
- Coverage and coat assumptions.
- Paint quantity calculations.
- Painting labour and material costing.
- Painting-specific validation and reporting data.

Calculations must be deterministic where possible and covered by unit tests.

Plastering

The plastering module owns its own inputs, calculations, assumptions and validation.

It may reuse common measurement and quotation infrastructure but must not rely on painting-specific internals.

Screeding

The screeding module follows the same rule: independent trade logic, shared platform contracts.

Integration Contract

A trade module should expose only the functionality required by the application shell and reporting system.

Conceptually, a trade capability may declare:

- A stable capability identifier.
- Its supported workflow.
- Required inputs.
- Its calculation or estimation operations.
- Its quotation data.
- Its entitlement requirements.

Implement this contract using patterns compatible with the existing repository. Do not build a complex plug-in framework unless dynamic module loading or independently distributed features become an actual requirement.

7. Subscription and Entitlement Architecture

Commercial access must be part of the architecture from the beginning.

The application must support configurable Trial, Subscription and Premium plans, with a future path to enterprise licensing.

7.1 Core Concepts

Plan: A commercial offering with defined capabilities and limits.

Entitlement: Permission to use a particular feature or resource.

Subscription status: The current commercial state, such as trialing, active, past due, cancelled, expired or revoked.

Entitlement snapshot: The latest verified account-access state, including its validity metadata.

Entitlement repository: The shared interface through which feature modules request access decisions.

Billing provider adapter: An abstraction for provider-specific purchase, restore and subscription-management operations.

Use names and data structures consistent with the actual implementation language and conventions.

7.2 Proposed Access Flow

User signs in
     |
     v
Application requests entitlements
     |
     v
Trusted backend validates account and subscription
     |
     v
Backend returns verified plan and capabilities
     |
     v
Shared entitlement interface exposes access decisions
     |
     +----> Painting module
     +----> Survey and reporting modules
     +----> Premium visualisation
     +----> Business/team capabilities
     |
     v
Protected backend operations validate access independently

The diagram describes intended behaviour, not existing deployed infrastructure.

7.3 Trial Plan

The system must support:

- Trial eligibility.
- Start and expiry timestamps.
- Configurable trial duration.
- Trial-specific feature permissions.
- Usage limits.
- Expiry handling.
- Upgrade options.

Trial eligibility and expiry must be decided by a trusted service when available. Do not rely exclusively on a client-maintained clock or editable local preference.

7.4 Subscription Plan

The system must support:

- Configurable subscription tiers.
- Monthly or annual billing where offered.
- Current status and renewal information.
- Upgrade and downgrade flows.
- Cancellation.
- Failed renewal handling.
- Grace periods where policy permits.
- Subscription restoration.
- Refunds and entitlement revocation.

Commercial prices, billing periods and plan limits must remain configurable. Do not hard-code unapproved pricing into feature modules.

7.5 Premium Plan

Premium features may include:

- Advanced visualisation.
- Higher usage limits.
- Enhanced reports.
- Advanced AI assistance.
- Team and business workflows.

Each feature must have an explicit entitlement key, for example:

advanced_visualization
enhanced_reports
team_projects
ai_assistance

These are illustrative identifiers, not a final commercial feature catalogue.

The application must not assume that every Premium candidate feature is implemented or available at launch.

7.6 Enterprise Licensing

The architecture should permit future organisation-level access, including:

- Organisation accounts.
- Team membership.
- Role-based permissions.
- Organisation-level entitlement assignment.
- Centralised subscription administration.
- Configurable usage limits.

Implement these only when approved requirements justify them.

8. Billing Security and Provider Independence

Trusted Authority

The backend must be authoritative for paid entitlements wherever online verification is available.

The Android application may cache a previously verified entitlement snapshot for a bounded offline period under an explicit policy. It must not grant permanent access based on a local flag such as "isPremium = true".

Protected backend operations must independently authorise requests.

Purchase Verification

The intended lifecycle is:

1. The user selects an available plan.
2. The client initiates the provider-supported purchase flow.
3. The provider processes the purchase.
4. The trusted backend verifies purchase or subscription evidence.
5. Provider events are processed idempotently.
6. The backend updates entitlement records.
7. The client refreshes access state.
8. The UI reflects the verified result.

Never treat an unverified client response as definitive proof of payment.

Security Requirements

- Never embed secret payment keys in the APK.
- Do not store card details directly.
- Protect credentials and tokens.
- Avoid logging payment information or sensitive account data.
- Apply least-privilege authorisation to project and organisation records.
- Validate all protected operations server-side.
- Preserve local user data after trial or subscription expiry.

Provider Adapter

Provider-specific logic must sit behind a replaceable interface.

The selected provider may depend on distribution channel, market, currency support and applicable policies.

Do not commit to a production billing provider until those decisions have been confirmed. Verify current distribution-platform rules before implementation.

9. Feature Gating

Every commercial capability must use the shared entitlement mechanism.

The architecture must distinguish:

- Access permitted.
- Trial access permitted.
- Upgrade required.
- Usage limit reached.
- Subscription action required.
- Offline verification temporarily unavailable.

Feature access must be checked at the domain/use-case layer and, where relevant, by the backend.

UI-only gating is insufficient.

Do not scatter hard-coded plan names throughout the application. Feature modules should ask whether a capability is allowed, not independently interpret the entire subscription lifecycle.

10. Offline Operation and Synchronisation

Core survey capture must remain usable without continuous internet access.

Offline behaviour must define:

- Which data is stored locally.
- Which operations remain available.
- How pending changes are queued.
- How synchronisation failures are reported.
- How conflicts are resolved.
- How entitlement snapshots expire.
- How the application behaves when subscription verification is unavailable.

Do not erase locally stored project data because the user is offline or a subscription has expired.

Avoid synchronisation complexity until the local data model and core workflows are stable.

11. Camera, AR and Visualisation Architecture

Camera capture and baseline survey functionality must not depend on optional AR capabilities.

Use runtime capability detection for:

- Camera availability and permissions.
- Required sensors.
- AR support.
- Available memory and processing resources.
- Network-dependent features.

Provide fallbacks when capabilities are unavailable.

Image-to-3D, video-to-3D and advanced visualisation must be introduced incrementally. Evaluate accuracy, processing cost, memory usage and performance on actual target devices before presenting them as production-ready.

12. UI and Design-System Architecture

Keep shared presentation components separate from feature-specific business rules.

The design system should centralise:

- Colours and typography.
- Spacing and layout tokens.
- Buttons, fields and cards.
- Loading, error and empty states.
- Dialogs and confirmations.
- Access-gate and upgrade components.

Feature screens should own their state and interactions. Shared components should not contain painting calculations, billing rules or other feature-specific business logic.

The design must support future visual redesigns without requiring business-logic rewrites.

13. AI and External Service Integration

AI and supplier services must be accessed through defined integration boundaries.

Requirements:

- Core survey capture must not depend on AI availability.
- AI suggestions must remain distinguishable from verified facts.
- External prices must include source and timestamp when available.
- API secrets must remain on trusted infrastructure.
- Network failures must be handled explicitly.
- Expensive operations must not block the UI.
- AI results must be validated before being used in calculations or quotations.

Do not build a large agent orchestration layer before a concrete, testable use case requires it.

14. Device and Performance Strategy

Support a broad range of Android devices through capability-based implementation.

- Establish the supported minimum SDK from the existing repository and device tests.
- Preserve compatible dependencies unless a justified upgrade is approved.
- Keep heavy work off the main thread.
- Avoid unnecessary image copies and excessive memory use.
- Use progressive enhancement for advanced visuals.
- Provide fallbacks for unsupported hardware.
- Test on a lower-spec device and a capable device when available.

Do not claim device compatibility without evidence.

15. Testing Architecture

Unit Tests

Test:

- Painting and other trade calculations.
- Measurement validation.
- Provenance handling.
- Entitlement decisions.
- Trial and subscription state transitions.
- Usage-limit enforcement.

Persistence Tests

Test:

- Room reads and writes.
- Schema migrations.
- Offline capture.
- Preservation of existing data.

Integration Tests

Test:

- Repository behaviour.
- Backend communication.
- Entitlement refresh.
- Purchase verification interfaces.
- Synchronisation conflicts when sync is implemented.

UI Tests

Test:

- Core project and survey journeys.
- Camera permission states.
- Loading, failure and offline states.
- Trial, subscription and premium access states.
- Expiry and restoration flows.

Security Tests

Verify that:

- Local changes cannot permanently unlock paid capabilities.
- Protected backend operations reject unauthorised requests.
- Secrets are not exposed in source code or APK resources.
- Subscription events are processed safely and idempotently.

16. GitHub and Build Requirements

Before implementation, inspect the current branch, working tree, recent commits and CI status.

For every code-changing commit:

1. Review the proposed diff.
2. Run relevant tests and checks.
3. Build the APK against the exact proposed code.
4. Resolve failures before making a normal feature commit.
5. Record the build result and evidence.
6. Commit focused changes to the intended branch.

After every SV stage, document:

- Implemented changes.
- Modules and files affected.
- Test results.
- APK build evidence.
- Branch and commit SHA.
- Physical-device or emulator verification.
- Known limitations.

A commit does not prove correctness, and a successful build does not prove that the feature behaves correctly.

17. Migration and Change Control

Before significant refactoring:

- Map the current implementation.
- Identify validated functionality.
- Identify dependencies and data-migration risks.
- Define a bounded change.
- Establish regression tests.
- Confirm the rollback strategy.

Do not restructure the entire application to match this document in one operation.

Do not delete user changes, reset the database, rewrite shared Git history or introduce production billing changes without appropriate authorisation.

18. Immediate Architecture Priorities

Execute in this order:

1. Inspect the latest repository and GitHub Actions results.
2. Resolve the current build blocker.
3. Verify existing camera, survey persistence and navigation behaviour.
4. Confirm README and canonical documentation consistency.
5. Identify the smallest useful modular boundary from the actual code.
6. Define plan, entitlement and trial contracts.
7. Confirm backend and distribution requirements.
8. Implement secure entitlement verification.
9. Integrate the selected billing provider behind an adapter.
10. Expand trade modules and advanced visualisation incrementally.

Do not begin payment integration by scattering subscription checks across existing screens.

19. Architecture Definition of Done

A module or architectural change is accepted only when:

- Its responsibility and interface are documented.
- Dependencies follow the agreed boundaries.
- Existing functionality remains operational.
- Relevant tests pass.
- The APK builds successfully.
- Migrations and securi
