WALNUS SMARTVIEW PRO

PROJECT_OVERVIEW.md

Version: 2.0
Last Updated: 24 October 2026
Product Owner: Walnus Global Ventures
Product Type: Commercial Android Application — SaaS-Enabled Construction Technology
Development Methodology: Modular, Incremental, Test-Driven and AI-Assisted
Repository: https://github.com/shawnthedon/Walnus-SmartView-Pro
Status: Active Development

---

1. Project Vision

Walnus SmartView Pro is a commercial construction-survey and finishing intelligence application designed to help painters, contractors, property developers and enterprise organisations capture site information, assess surfaces, visualise finishing options, estimate material requirements and produce professional quotations.

The application combines mobile surveying, structured project records, painting calculations, AI-assisted workflows and commercial subscription management.

The product must be reliable on affordable Android devices while supporting advanced capabilities on higher-performance devices.

Core objective: Build a dependable, commercially licensable application that can expand through independently developed modules without requiring the entire application to be rewritten.

2. Product Principles

Every development decision must support:

1. Accuracy: Clearly distinguish measured facts from estimates and AI suggestions.
2. Reliability: Preserve project information and protect existing functionality.
3. Efficiency: Minimise unnecessary steps, processing overhead and development complexity.
4. Connectivity: Support offline work and synchronise safely when connectivity returns.
5. Modularity: Allow features to be developed, tested, replaced and expanded independently.
6. Commercial readiness: Support trials, subscriptions, premium features and business licensing.
7. Device inclusivity: Provide practical functionality on lower-specification devices and progressive enhancements on capable devices.
8. Maintainability: Keep code, architecture, documentation and testing aligned throughout development.

---

3. Target Customers

Individual professionals

- Professional painters.
- Independent contractors.
- Small finishing businesses.

Commercial customers

- Painting contractors and construction companies.
- Property developers and facility managers.
- Medium-sized and large organisations.
- Enterprise customers requiring team access and project controls.

The application should accommodate different customer types through configurable entitlements and licensing arrangements, rather than maintaining separate applications for each market.

---

4. Core Application Modules

Features must be organised around clear responsibilities and shared interfaces.

4.1 Core Platform

Responsible for shared functionality:

- Project identification and organisation.
- Common domain models.
- Local database and migrations.
- Navigation and shared design components.
- Connectivity and synchronisation interfaces.
- Error handling and logging.
- Account and entitlement interfaces.

4.2 SmartView Survey

The foundation for site-survey operations:

- Create and manage projects and surveys.
- Capture site photographs using the device camera.
- Record room and surface information.
- Persist survey information locally.
- Preserve measurement units, assumptions and provenance.
- Support offline-first operation.

Maintain the established hierarchy where applicable:

Project → Survey → Measurement

Measurement provenance must use the appropriate category:

- "MEASURED"
- "INFERRED"
- "ESTIMATED"
- "USER_CONFIRMED"
- "AI_SUGGESTED"

Never present an estimate as a verified physical measurement.

4.3 Painting Module — First Priority

Painting is the first trade-specific module.

Planned capabilities:

- Surface assessment and preparation records.
- Paint quantity estimation.
- Coats, coverage rates and material assumptions.
- Colour-selection and visualisation workflows.
- Labour and material costing.
- Painting quotations and reports.

All calculations must be independently testable. Required inputs, assumptions and limitations must be visible to the user.

4.4 Plastering Module

A separate trade module for plastering-related surveys, material estimation, costing and reporting.

It should reuse common project and survey infrastructure without depending on the internal implementation of the painting module.

4.5 Screeding Module

A separate module for screeding measurements, material quantities, costing and reporting.

It must follow the same modular integration principles.

4.6 Quotations and Reporting

A reusable reporting capability supporting:

- Project and customer information.
- Survey summaries.
- Material quantities and assumptions.
- Cost breakdowns.
- Professional quotation presentation.
- Supported export and sharing workflows.

Trade-specific modules supply their relevant data through defined interfaces.

4.7 AI Assistance

AI capabilities may support:

- Estimation assistance.
- Material and supplier research.
- Price comparisons.
- Workflow guidance.
- Report preparation.
- Future image and 3D analysis.

AI-generated results must be labelled appropriately, validated before use and accompanied by source information where applicable.

External supplier prices must not be presented as guaranteed current prices without verification.

AI functionality must not prevent core offline survey operations from working.

4.8 Advanced Visualisation and AR

Future capabilities include:

- Assisted surface selection.
- Colour visualisation on captured images.
- Image-to-3D or video-to-3D exploration.
- AR-assisted measurement where supported.

These features must be introduced incrementally after the core survey and quotation workflows are reliable.

The application must detect device capabilities and provide suitable alternatives when AR, sensors, memory or graphics performance are insufficient.

---

5. Modular Development Methodology

Modularity is mandatory. Unnecessary architectural complexity is not.

The objective is to establish clear feature boundaries, not to create the maximum possible number of modules.

5.1 Architectural Boundaries

Inspect the existing repository before changing its structure. Where appropriate, use logical or Gradle module boundaries such as:

Module| Responsibility
"core:model"| Shared domain models and identifiers
"core:database"| Room persistence and schema migrations
"core:designsystem"| Reusable UI components and design tokens
"core:navigation"| Navigation contracts
"core:network"| API and synchronisation abstractions
"core:entitlements"| Shared access and entitlement decisions
"feature:projects"| Project management
"feature:survey"| Survey and camera workflows
"feature:painting"| Painting-specific functionality
"feature:plastering"| Plastering functionality
"feature:screeding"| Screeding functionality
"feature:quotations"| Reports and quotations
"feature:account"| Account management
"feature:billing"| Payment integration and billing screens

These are target boundaries, not instructions to create every module immediately. Start with the smallest useful separation and extract modules as justified by actual code and requirements.

5.2 Mandatory Modularity Rules

- Each feature owns its specialised business rules and UI.
- Shared functionality belongs in appropriate core modules.
- Feature modules must not depend on unrelated feature internals.
- Use defined interfaces for shared data and operations.
- Separate presentation, business logic, persistence and external integrations where complexity warrants it.
- Use shared design tokens so the UI can be redesigned without rewriting business logic.
- Keep payment-provider details behind an adapter.
- Avoid circular dependencies and duplicated business rules.
- Do not introduce microservices or unnecessary frameworks without a demonstrated requirement.

5.3 Incremental Implementation

For every new module or significant feature:

1. Inspect the current implementation.
2. Define its responsibility and acceptance criteria.
3. Identify its dependencies and public interface.
4. Implement the smallest useful increment.
5. Add relevant tests.
6. Build the APK and validate existing functionality.
7. Update documentation and progress records.
8. Commit only after required validation passes.

Existing working functionality must be preserved unless a change is explicitly justified and tested.

---

6. Commercial Model: Trials, Subscriptions and Premium

SmartView Pro is a proprietary commercial product. Business and individual customers must use the application according to the applicable licence and subscription terms.

The commercial model must be designed into the application architecture from the beginning, even if payment processing is introduced later.

6.1 Trial Access

Provide a configurable evaluation period.

The system must support:

- Trial eligibility and start date.
- Configurable duration.
- Trial-specific feature entitlements.
- Usage limits where appropriate.
- Clear trial status and expiry information.
- Upgrade options.
- Defined behaviour after trial expiry.

Trial duration, eligibility and limits must be configurable. Do not invent final commercial terms or hard-code trial rules throughout the application.

6.2 Subscription Access

Paid subscriptions provide access to the capabilities included in the customer's selected plan.

The architecture must support:

- Plan selection.
- Monthly or annual billing where offered.
- Subscription status and renewal information.
- Upgrades and downgrades.
- Cancellation.
- Failed payment and grace-period handling.
- Subscription restoration.
- Refunds and revoked entitlements.
- Account and subscription management.

Plan names, prices, billing periods, limits and regional availability remain configurable product decisions.

6.3 Premium Access

Premium represents an enhanced commercial offering.

Potential capabilities include:

- Advanced visualisation.
- Higher usage allowances.
- Enhanced reporting.
- Additional AI-assisted functionality.
- Team and business workflows.
- Enterprise-oriented project controls.

These are candidate features, not promises that every capability will be available at launch.

Each capability must have a defined entitlement. Premium access must not be scattered across the codebase as unrelated Boolean checks.

6.4 Enterprise Licensing

The architecture should permit future business licensing, including configurable organisation-level entitlements and team access.

Enterprise licensing may require:

- Organisation accounts.
- Multiple users.
- Role-based permissions.
- Centralised billing.
- Configurable usage limits.
- Organisation-level subscription management.

Implement these capabilities only when supported by validated commercial requirements.

6.5 Entitlement Architecture

Create a shared entitlement interface used by relevant application modules.

The system should distinguish:

- Current plan.
- Subscription status.
- Available capabilities.
- Usage allowances.
- Trial expiry.
- Entitlement validity.
- Offline verification status.

The backend must be the authority for paid entitlements whenever online verification is available.

The Android client must not grant permanent premium access based solely on a locally stored flag.

Protected backend operations must independently validate authorisation. Hiding a button in the UI is not sufficient security.

6.6 Billing Security

- Never embed payment-provider secret keys in the APK.
- Verify purchases and subscription events through a trusted backend.
- Process provider events idempotently.
- Keep provider-specific code behind a billing adapter.
- Do not store payment card details directly.
- Protect account and project data with appropriate authorisation.
- Do not erase user-created project data when a subscription expires.

A temporary network failure must not automatically destroy access to locally stored work.

6.7 Offline Entitlements

Document the offline access policy explicitly.

The application may use a previously verified entitlement snapshot for a bounded period where appropriate. It must not allow indefinite access to paid capabilities through local-data modification.

Core survey capture and access to permitted local project data should remain available offline.

6.8 Payment Provider Decision

The production billing provider must not be selected until distribution channels and target markets are confirmed.

Verify the current requirements of the intended distribution channel, including applicable Google Play Billing rules for digital features, and confirm payment-provider availability in the markets being targeted.

---

7. User Interface Methodology

The interface must be modular, responsive and suitable for real construction-site use.

Requirements

- Shared design system and reusable components.
- Feature-owned screens and state.
- Consistent navigation.
- Clear loading, empty, error and offline states.
- Accessible touch targets and text scaling.
- Readable measurement units and cost breakdowns.
- Clear differentiation between measured values and estimates.
- Graceful fallbacks for unsupported features.
- Responsive behaviour on small Android screens.

Advanced visual effects must never compromise performance or field usability.

The interface should allow future visual redesigns through shared components and design tokens rather than requiring changes throughout the application's business logic.

---

8. Technical and Data Standards

The current repository's validated technology stack takes precedence over assumptions in this document.

Retain established Kotlin and Android conventions, Room persistence and existing camera dependencies where they remain appropriate.

Before introducing a dependency upgrade or major architectural change, establish the need, assess compatibility and validate the result.

Data integrity

- Preserve existing project and survey relationships.
- Include proper Room migrations for schema changes.
- Test migrations and offline persistence.
- Do not delete existing data to simplify upgrades.
- Preserve measurement provenance.
- Handle synchronisation conflicts explicitly before introducing sync behaviour.

Performance

- Avoid unnecessary image and bitmap duplication.
- Keep expensive work off the main thread.
- Use runtime capability detection.
- Provide low-spec fallbacks.
- Avoid making core capture dependent on remote AI services.
- Validate advanced image and 3D functionality against real device constraints.

---

9. Mandatory AI Development Workflow

Every AI agent working on SmartView Pro must follow this workflow.

Before development

1. Read this document and all six canonical project documents.
2. Inspect the current branch, working tree, recent commits and repository structure.
3. Read the current README and identify the actual SV stage.
4. Inspect relevant CI results and unresolved failures.
5. Define the smallest testable task.
6. Preserve existing valid work.

During development

- Make focused, incremental changes.
- Follow established architecture and coding conventions.
- Do not implement unrelated features.
- Add tests alongside relevant code changes.
- Update the README when project behaviour, setup or architecture changes.
- Update progress documentation using verified evidence.
- Report blockers rather than silently bypassing them.

Before every commit

Build and validate the APK against the exact proposed code.

Required checks include:

- Relevant automated tests.
- Applicable static checks.
- Successful Android build.
- Review of the Git diff and working tree.
- Confirmation that secrets and unrelated files are not included.

If the required build fails, do not create a normal feature commit. Resolve the failure or report the task as blocked.

Retain build logs and APK artifacts through the configured CI or release process. Do not commit APK binaries by default unless the repository explicitly requires it.

After every SV stage

Produce a completion report containing:

- Stage identifier and objective.
- Implemented changes and files.
- Modules affected.
- Tests and exact outcomes.
- APK build result and evidence.
- Branch and commit SHA.
- Physical-device or emulator verification.
- Known defects and deferred work.
- Documentation updated.
- Status: "COMPLETE", "PARTIAL" or "BLOCKED".

A successful commit does not prove the application works. A successful build does not prove that a feature behaves correctly.

---

10. README and Documentation Management

The repository README is the living entry point for the project.

Maintain it as development progresses. It must explain:

- Product purpose.
- Current implementation status.
- Supported environment and setup.
- Build and test instructions.
- Architecture overview.
- Available features and known limitations.
- Links to the six canonical documents.

The six canonical documents are:

1. "PROJECT_OVERVIEW.md"
2. "AI_WORKFLOW.md"
3. "UI_CONTEXT.md"
4. "ARCHITECTURE.md"
5. "PROGRESS.md"
6. "CODE_STANDARD.md"

Avoid contradictory specifications. When a decision changes, update the relevant document and the README where appropriate.

---

11. Current Development Priorities

Work in this order unless repository evidence demonstrates a necessary change.

Priority 1 — Stabilise the existing application

- Inspect the latest repository state and CI results.
- Resolve the current Android build pipeline blocker.
- Verify existing camera, survey persistence and navigation functionality.
- Identify incomplete SV-stage acceptance criteria.
- Preserve validated functionality.

Priority 2 — Establish the modular foundation

- Audit the actual code structure.
- Identify the first useful module boundary.
- Separate shared UI, domain logic and persistence where justified.
- Keep the change small enough to validate safely.

Priority 3 — Define commercial entitlements

- Specify trial, subscription and premium capabilities.
- Define configurable plan-to-feature mappings.
- Establish account and entitlement interfaces.
- Document expiry, cancellation and offline behaviour.
- Avoid committing to unapproved prices or payment providers.

Priority 4 — Implement secure subscription infrastructure

- Determine backend requirements.
- Confirm distribution channels and payment-provider rules.
- Implement server-side entitlement verification.
- Integrate billing behind an adapter.
- Test subscription lifecycle and failure cases.

Priority 5 — Expand trade and visualisation features

- Complete the painting workflow.
- Add plastering and screeding as separate capabilities.
- Improve quotations and reports.
- Evaluate advanced visualisation and AR incrementally.
- Validate performance on supported devices.

---

12. Definition of Done

A feature or SV stage is complete only when:

- Its acceptance criteria are satisfied.
- Existing validated functionality remains operational.
- Relevant tests pass, or exceptions are explicitly documented.
- The APK builds successfully against the intended code.
- Device verification is recorded accurately.
- Known limitations are documented.
- The README and progress tracker reflect reality.
- The change is committed to the intended branch.
- The completion report contains verifiable evidence.

A planned capability must never be described as implemented without supporting evidence.

---

13. Commercial and Intellectual Property Position

SmartView Pro is intended to remain proprietary software owned or commercially controlled by Walnus Global Ventures, subject to appropriate ownership agreements and third-party dependency licences.

Do not introduce an open-source licence or grant redistribution rights without explicit approval.

Review all dependency licences before commercial release. Prepare appropriate commercial software terms, privacy documentation and subscription policies before accepting paying customers.

---

14. Immediate Execution Instruction

Start by inspecting the existing Walnus SmartView Pro repository and

------------------------------------------------------------------
15. IMPLEMENTATION STATUS (SV-001)
------------------------------------------------------------------

This is a factual snapshot, not a plan.

Completed and verified

- Modular Gradle foundation with five Android-free core modules.
- Provenance-preserving domain model (projects, surveys, surfaces, measurements).
- Deterministic paint estimation: deductions, coats, wastage, pack rounding, primer,
  assumptions, trust capping.
- Quotation arithmetic in integer minor units with discount allocation and basis-point
  tax.
- Entitlement evaluation with free-tier offline operation, device-forgery resistance,
  clock-skew rejection and bounded offline grace.
- Trade capability contract with painting as its first implementation.
- Verification tooling and evidence logs (`tools/verify-core.sh`,
  `docs/build-evidence/`).

Not started

- Android application module, navigation, screens, themes and components.
- Local persistence, migrations and offline queue.
- Camera capture, measurement entry and surface selection.
- Reports, exports and sharing.
- AI assistance, supplier discovery and colour visualisation.
- Any billing provider, backend entitlement service or synchronisation.
- Any APK. The APK build is blocked in the authoring environment and is reported as
  blocked, not as passing.

Priority mapping for Section 11

- Priority 1 (stabilise the existing application) is re-scoped: there was no application
  to stabilise, only documentation. The equivalent action was to establish a verified
  foundation, which SV-001 did.
- Priority 2 (modular foundation) is complete for the domain layer and continues in
  SV-002 with the app shell and design system.
- Priorities 3-5 remain open, with Priority 3 (commercial definitions) still awaiting
  business decisions rather than code.

Definition of Done consequence: SV-001 is PARTIAL, because the "APK builds successfully"
criterion is unmet. Any completed SV must satisfy that criterion or record a documented
blocker, as this one does.
