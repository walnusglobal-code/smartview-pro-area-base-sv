# SV-001 Completion Report — Modular Foundation and Verified Domain Core

**Project:** Walnus SmartView Pro
**Branch:** `arena/1453e021-smartview-pro-area-base-sv`
**Date:** 24 October 2026
**Status:** **PARTIAL** — the domain core is COMPLETE and verified; the APK build is **BLOCKED** by the authoring environment.

---

## 1. SV identifier and objective

**SV-001 — Modular foundation and verified domain core.**

Objective: turn a documentation-only repository into a buildable, modular project whose
calculation and access-control logic is real, tested and independent of any UI, so that
later SVs (app shell, persistence, camera, reporting) can be added without rewrites.

Acceptance criteria agreed before implementation:

| # | Criterion | Result |
|---|---|---|
| A1 | Standard Gradle project structure with a central version catalogue | Met (layout committed; execution blocked — see §6) |
| A2 | Core domain modules contain no Android dependencies | Met |
| A3 | Measurement provenance preserved end-to-end and never upgraded by derivation | Met and tested |
| A4 | Deterministic paint estimation including deductions, wastage and pack rounding | Met and tested |
| A5 | Quotation arithmetic in integer minor units with explicit tax/discount rules | Met and tested |
| A6 | Entitlement decisions isolated from UI, resistant to device-forged premium state | Met and tested |
| A7 | Trade contract that allows plastering/screeding without touching painting internals | Met (`TradeCapability`, `PaintingTrade`) |
| A8 | All added logic covered by executed unit tests | Met — 78/78 passing |
| A9 | Fresh APK build before commit | **Not met — BLOCKED** (no SDK/Gradle/Maven in environment) |
| A10 | README, six core documents and this report updated | Met |

---

## 2. Implemented functionality

### Build skeleton
- Gradle Kotlin DSL: `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`,
  `gradle/libs.versions.toml`, wrapper script (`gradlew`, `gradlew.bat`), wrapper jar and
  properties, `.gitignore` excluding build output, IDE files and signing material.
- Five JVM (Android-free) modules: `:core:model`, `:core:entitlements`, `:core:trade`,
  `:core:estimation`, `:core:quotation`.

### Domain model (`:core:model`)
- `Provenance` with the five mandated categories, an explicit strength ordering and a
  `weakestOf` combination rule used everywhere a value is combined.
- `Length`/`LengthUnit`: exact SI and international-yard conversions (mm, cm, m, in, ft).
- `Measurement`: immutable record with timestamp, note and source reference; inferred and
  AI-suggested values must point at the measurement they came from.
- `Surface` + `SurfaceExtent` (rectangular, direct area, unknown): openings are measured
  so they can be deducted, never counted as paintable area; incomplete surfaces are
  recorded but excluded from calculations.
- `Project` and `Survey` with lifecycle enums and consistency validation (timeline order,
  completion status, duplicate surface ids, ISO-4217 currency).

### Estimation (`:core:estimation`)
- `SurfaceAreaCalculator`: gross area, opening deduction, 100% deduction cap with a flag,
  ignored-surface count, provenance downgrade to the weakest contributor.
- `PaintCalculator`: litres per coat, total litres, wastage, largest-first pack breakdown
  that always rounds up, optional primer, and a human-readable assumption list shipped
  with the numbers.
- Derived quantities are capped at `ESTIMATED` and inherit weaker input provenance
  (`AI_SUGGESTED` in → `AI_SUGGESTED` out). They can never become `MEASURED`.
- `PaintingTrade` exposes painting through the trade contract with the
  `paint_estimation` feature key.

### Quotation (`:core:quotation`)
- `Money` in integer minor units with half-up rounding; `CurrencyCode` validation.
- `QuoteLine` and `Quotation` with subtotal, discount capped at subtotal, proportional
  discount allocation to taxable lines, basis-point tax and total. No prices, rates or
  jurisdiction defaults are hard-coded.

### Entitlements (`:core:entitlements`)
- `FeatureKey`/`FeatureKeys`, `EntitlementPlan`, `PlanCatalog`, `SubscriptionStatus`,
  `VerificationSource`, `EntitlementSnapshot` and `EntitlementEvaluator`.
- Decision rules implemented and tested:
  - no snapshot → free tier; surveying works offline without an account;
  - device-only snapshot claiming Premium → paid features withheld (`VerificationRequired`);
  - snapshot timestamped beyond clock-skew tolerance → untrusted;
  - server-verified plan honoured until expiry;
  - after expiry, bounded offline grace (7 days by default) keeps paid features working;
  - after grace → free plan plus a refresh request, never data deletion;
  - trial with no expiry is not treated as unlimited access;
  - project-count allowances evaluated per tier.
- No billing provider, price or purchase flow is implemented or claimed.

### Tooling
- `tools/verify-core.sh` — compiles each module separately (so illegal cross-module
  dependencies fail the gate), compiles the tests, runs them and writes a log.
- `tools/jvmtest/TestRunner.kt` — reflective runner used only when JUnit is unavailable;
  the test sources use plain `kotlin.test`/JUnit annotations so they run unchanged under
  Gradle.
- `tools/jvmtest/stubs/` — documented compile-time stand-in for `org.junit.Test`,
  excluded from the application and from release builds.

---

## 3. Files created or modified

Created — build and repository:
- `.gitignore`, `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`
- `gradle/libs.versions.toml`, `gradle/wrapper/gradle-wrapper.properties`,
  `gradle/wrapper/gradle-wrapper.jar`, `gradlew`, `gradlew.bat`

Created — `core:model`:
- `build.gradle.kts`
- `src/main/kotlin/.../model/{Ids,Provenance,Length,Measurement,Surface,Project}.kt`
- `src/test/kotlin/.../model/ModelTest.kt`

Created — `core:entitlements`:
- `build.gradle.kts`
- `src/main/kotlin/.../entitlements/{FeatureKey,Plan,EntitlementSnapshot,Clock,AccessDecision,EntitlementEvaluator}.kt`
- `src/test/kotlin/.../entitlements/EntitlementsTest.kt`

Created — `core:trade`:
- `build.gradle.kts`
- `src/main/kotlin/.../trade/TradeCapability.kt`

Created — `core:estimation`:
- `build.gradle.kts`
- `src/main/kotlin/.../estimation/{PaintSpecification,PaintableArea,PaintEstimate,PaintCalculator,PaintingTrade}.kt`
- `src/test/kotlin/.../estimation/EstimationTest.kt`

Created — `core:quotation`:
- `build.gradle.kts`
- `src/main/kotlin/.../quotation/{Money,Quotation}.kt`
- `src/test/kotlin/.../quotation/QuotationTest.kt`

Created — tooling and evidence:
- `tools/verify-core.sh`, `tools/jvmtest/TestRunner.kt`, `tools/jvmtest/stubs/org/junit/Test.kt`
- `docs/build-evidence/sv-001-core-verification.log`
- `docs/build-evidence/sv-001-apk-build-attempt.log`
- `docs/reports/SV-001-completion-report.md` (this file)

Modified:
- `README.md` — implementation status, verified/unverified boundaries, setup and roadmap
- `Context/progress_tracker.md` — rewritten for SV-001
- `Context/architecture.md`, `Context/ai_workflow.md`, `Context/Coding_standards.md`,
  `Context/ui_context.md`, `Context/project_overview.md` — implementation status appended

Not created (deliberate): `:app` module, Compose UI, manifest, resources, persistence,
camera integration. `settings.gradle.kts` therefore does not declare `:app` yet.

---

## 4. Tests and checks performed

**Executed (real, reproducible):**

```
$ KOTLIN_HOME=<kotlin-distribution> ./tools/verify-core.sh
...
Tests run: 78, passed: 78, failed: 0
VERIFIED: core modules compiled and all tests passed.
```

- 13 test classes, 78 tests across four test-bearing modules.
- Each module compiled in isolation with only its declared dependencies on the classpath,
  which verifies the intended module boundaries rather than only syntax.
- Log: `docs/build-evidence/sv-001-core-verification.log`.

**Not performed (and not claimed):**
- Gradle execution (`./gradlew build`) — see §6.
- Android Lint / static analysis — requires AGP.
- Compose/UI tests, instrumentation tests, device or emulator tests — no UI or device.
- Migration tests — no persistence yet.

---

## 5. Build result

| Build | Result |
|---|---|
| Kotlin/JVM compilation of all five core modules | **PASS** |
| Unit test execution | **PASS** (78/78) |
| Gradle configuration/build | **BLOCKED** — Gradle distribution unobtainable |
| Android APK (`assembleDebug`) | **BLOCKED** — no Android SDK, AGP, AndroidX or dexer |

Exact evidence: `docs/build-evidence/sv-001-apk-build-attempt.log`, which records the
missing tooling, the failed HTTP reachability checks for `dl.google.com`,
`repo.maven.apache.org` and `services.gradle.org`, and the unavailable dexer.

No APK was produced. This report does not claim one.

---

## 6. APK artifact location

**None.** No APK exists for this SV. The first APK is expected in SV-002, built in an
environment with Android SDK and Google Maven access, and will be recorded with its path,
size and SHA-256 in `Context/progress_tracker.md`.

---

## 7. Commit SHA

Recorded in `Context/progress_tracker.md` after the SV-001 commit on branch
`arena/1453e021-smartview-pro-area-base-sv`.

---

## 8. Known limitations

- The product remains pre-alpha: no UI, no persistence, no camera, no reports.
- `PlanCatalog` limits (3 / 10 / 50 projects) are engineering defaults for gating tests,
  not approved commercial terms.
- Coverage (10 m²/L), wastage (5%) and pack sizes (20/10/4/1 L) are configurable
  assumptions for planning, not manufacturer data.
- Tax rates are user-supplied basis points; no jurisdiction default is assumed.
- `Money` assumes a two-decimal minor unit for display purposes; the arithmetic is
  currency-agnostic but zero/three-decimal currencies are untested.
- `SurfaceAreaCalculator` deducts whole openings; partial or no-deduct policies are not
  yet configurable.
- The JUnit stub is a sandbox convenience; the Gradle build uses the real JUnit artifact
  declared in the version catalogue, which has not been resolved in this environment.

---

## 9. Outstanding blockers

1. **Android build environment** — the SV cannot produce an APK until a machine with the
   Android SDK, Gradle distribution and Google Maven access runs the build. Minimum
   intervention: run `./gradlew build` on such a machine and append the output to
   `docs/build-evidence/`.
2. **Commercial decisions** — plan limits, pricing, billing provider and distribution
   channel remain unapproved; they are intentionally not implemented.

---

## 10. Next recommended SV

**SV-002 — Android application shell and design system.**

Scope: add `:app` (manifest, `MainActivity`, Compose), implement semantic design tokens
and the four selectable themes, a project/survey list, and the painting estimator screen
wired to `:core:estimation` with provenance badges and entitlement gates; then produce
the first debug APK and record its path and hash. This SV *requires* the build blocker to
be cleared, because a UI that has never been compiled must not be reported as working.
