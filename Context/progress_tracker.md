PROGRESS_TRACKER.md — WALNUS SMARTVIEW PRO

Project: Walnus SmartView Pro
Repository: https://github.com/walnusglobal-code/smartview-pro-area-base-sv
Document purpose: Track SV status, verified work, build evidence, blockers and next actions.
Last updated: 24 October 2026 (SV-001)

------------------------------------------------------------------
1. CURRENT STATE AT A GLANCE
------------------------------------------------------------------

| Item | State |
|---|---|
| Repository baseline | Documentation-only at commit 417dbb6 (no application code) |
| Android application module | NOT CREATED — planned for SV-002 |
| Verified code | 5 pure-Kotlin core modules, 78 unit tests passing on the JVM |
| APK build | BLOCKED in the authoring environment — no Android SDK, no Gradle distribution, no Google Maven/Maven Central access |
| APK artifact | None. No successful build is claimed anywhere in this repository. |
| Working branch | arena/1453e021-smartview-pro-area-base-sv |

Verified evidence (see `docs/build-evidence/`):

- `sv-001-core-verification.log` — Kotlin compilation of all core modules and 78/78 unit tests passing.
- `sv-001-apk-build-attempt.log` — exact diagnostic output of the blocked APK build attempt.

------------------------------------------------------------------
2. ENVIRONMENT CONSTRAINT (READ FIRST)
------------------------------------------------------------------

The environment used for SV-001 can reach only `github.com`, `codeload.github.com`,
`api.github.com`, `registry.npmjs.org`, `pypi.org` and `files.pythonhosted.org`.

Consequences:

- Google Maven and Maven Central are unreachable, so the Android Gradle Plugin,
  AndroidX, Compose and Room cannot be resolved. `./gradlew build` cannot run here.
- `services.gradle.org` is unreachable, so the wrapper cannot download a distribution,
  even though `gradle/wrapper/gradle-wrapper.jar` and `gradle.properties` are committed.
- No Android SDK, `aapt2`, `d8`/R8, `zipalign` or `apksigner` is installed, and no
  desktop-JVM dexer could be obtained from the reachable hosts.

What *is* possible, and was done: compile and unit-test the Android-free core modules
with a real Kotlin/JVM toolchain (`tools/verify-core.sh`).

The build blockage is an environment limitation, not a code defect. It must be cleared
by building in an environment with Android SDK + Maven/Google Maven access before any
APK-dependent claim is made.

------------------------------------------------------------------
3. SV STATUS BOARD
------------------------------------------------------------------

| SV | Objective | Status | Evidence |
|---|---|---|---|
| SV-001 | Modular foundation: build skeleton, shared domain model, provenance rules, paint estimation, quotation maths, entitlement gating, verification tooling | PARTIAL — domain COMPLETE and verified; APK build BLOCKED | 78/78 tests passing; APK attempt log |
| SV-002 | Android app shell (`:app`), Compose design system with the four themes, project/survey list wired to the core | NOT STARTED | — |
| SV-003 | Offline persistence (project/survey/measurement store) with migration discipline | NOT STARTED | — |
| SV-004 | Camera capture and surface entry workflow | NOT STARTED | — |
| SV-005 | Quotation/report export and sharing | NOT STARTED | — |
| SV-006 | Plastering and screeding trades on the shared contract | NOT STARTED | — |
| SV-007 | Entitlement backend contract and billing adapter (provider undecided) | NOT STARTED | — |

Status vocabulary: NOT STARTED, IN PROGRESS, BLOCKED, COMPLETE, PARTIAL.

------------------------------------------------------------------
4. SV-001 — COMPLETED WORK
------------------------------------------------------------------

Build and project skeleton

- Gradle Kotlin-DSL settings, root build file, `gradle.properties`, version catalogue,
  wrapper properties and wrapper jar, `.gitignore` covering build output, secrets and
  local SDK configuration.
- Five JVM modules: `:core:model`, `:core:entitlements`, `:core:trade`,
  `:core:estimation`, `:core:quotation`.

Domain model (`:core:model`)

- `Provenance` (MEASURED, USER_CONFIRMED, INFERRED, ESTIMATED, AI_SUGGESTED) with an
  explicit strength ordering and `weakestOf` combination rule.
- `Length`/`LengthUnit` with exact SI and imperial factors.
- `Measurement` — inferred/AI values must reference a source measurement.
- `Surface`/`SurfaceExtent` — rectangular, direct-area and unknown extents; openings are
  measurable but never counted as paintable area.
- `Project`, `Survey` with lifecycle and consistency validation.

Estimation (`:core:estimation`)

- `SurfaceAreaCalculator` — gross area, opening deductions, capping, provenance
  downgrade, ignored-surface counting.
- `PaintCalculator` — litres per coat, coats, wastage, retail pack breakdown (largest
  first, always rounded up), optional primer, explicit assumption list.
- Derived quantities can never carry `MEASURED` provenance.
- `PaintingTrade` implements the trade contract.

Quotation (`:core:quotation`)

- Integer minor-unit `Money`, ISO-4217 `CurrencyCode`, `QuoteLine`, `Quotation` with
  proportional discount allocation across taxable and non-taxable lines and basis-point
  tax rates. No price, tax rate or currency is hard-coded as a commercial value.

Entitlements (`:core:entitlements`)

- `FeatureKey`/`FeatureKeys`, data-driven `EntitlementPlan` + `PlanCatalog` (no prices),
  `SubscriptionStatus`, `EntitlementSnapshot` with verification source.
- `EntitlementEvaluator` — free tier without an account, device-only snapshots never
  grant paid capability, clock-skew rejection, bounded offline grace, verification
  requests instead of data loss, project-limit decisions.

Trade contract (`:core:trade`)

- `TradeCapability` (id, display name, required feature, summary) and `TradeIds`.
  Deliberately minimal — not a plug-in framework.

Tooling

- `tools/verify-core.sh` — compiles each core module separately (enforcing module
  boundaries), compiles the tests, runs them, writes a log.
- `tools/jvmtest/TestRunner.kt` — reflective runner for `kotlin.test`/JUnit-annotated
  tests when JUnit is unavailable; the tests themselves remain standard JUnit tests.
- `tools/jvmtest/stubs/` — compile-time stand-in for `org.junit.Test` used only in
  restricted environments; never shipped.

------------------------------------------------------------------
5. TEST RESULTS (SV-001)
------------------------------------------------------------------

| Module | Test classes | Tests |
|---|---|---|
| core:model | ProvenanceTest, LengthTest, MeasurementTest, SurfaceTest, ProjectAndSurveyTest | 26 |
| core:entitlements | FeatureKeyTest, PlanCatalogTest, EntitlementEvaluatorTest | 19 |
| core:estimation | SurfaceAreaCalculatorTest, PaintCalculatorTest, PaintingTradeTest | 21 |
| core:quotation | MoneyTest, QuotationTest | 12 |
| **Total** | **13 classes** | **78 — all passing** |

Command: `KOTLIN_HOME=<kotlin-distribution> ./tools/verify-core.sh`
Result: `Tests run: 78, passed: 78, failed: 0`.
Log: `docs/build-evidence/sv-001-core-verification.log`.

Not covered by automated tests (documented gaps):

- Android application module, Compose UI, navigation: not written yet.
- Persistence, migrations, camera, file export: not written yet.
- Device/emulator testing: none performed.

------------------------------------------------------------------
6. BUILD RESULT (SV-001)
------------------------------------------------------------------

APK build: BLOCKED. See `docs/build-evidence/sv-001-apk-build-attempt.log`.

Observed failures:

1. `./gradlew` — wrapper jar is present and loadable, but no Gradle distribution can be
   downloaded (`services.gradle.org` unreachable).
2. No `gradle` binary on PATH; no JDK `javac`; only a JRE was available.
3. AGP/AndroidX resolution impossible: `dl.google.com` and `repo.maven.apache.org`
   return HTTP 000 from this environment.
4. No `d8`/R8 accessible: the npm `d8-termux` package ships a Dalvik-format jar, and R8
   binaries published on GitHub are Git-LFS pointers served from a blocked host.

To clear the blocker, run on a machine with Android Studio or the SDK command line tools:

```bash
./gradlew :core:model:test :core:estimation:test :core:quotation:test :core:entitlements:test
./gradlew :app:assembleDebug   # available from SV-002 onwards
```

Record the output and the APK path in this tracker.

------------------------------------------------------------------
7. KNOWN LIMITATIONS
------------------------------------------------------------------

- No Android application, UI, persistence or APK exists yet. Treat the product as
  pre-alpha: verified calculation core only.
- `PlanCatalog` limits (3/10/50 projects) are engineering defaults for gating tests and
  are not approved commercial terms.
- Paint coverage, wastage and pack sizes are configurable assumptions, not supplier or
  manufacturer data.
- Quotation tax rates are user-supplied basis points; no jurisdiction default is
  assumed.
- `Money` holds minor units; currencies with non-2-decimal minor units are supported by
  the model but not yet exercised by tests.
- No synchronisation, backend or billing provider is implemented, by design and by the
  commercial decisions still outstanding.

------------------------------------------------------------------
8. NEXT ACTIONS
------------------------------------------------------------------

1. Clear the build blocker (Android SDK + Maven access), then run the Gradle test tasks
   and record the result here.
2. SV-002: add `:app` (manifest, `MainActivity`, Compose 2-theme-first design system,
   project list + painting estimator screens wired to `:core:estimation`, entitlement
   gates wired to `:core:entitlements`), then build the first debug APK.
3. SV-003: introduce persistence behind repository interfaces; add migration tests
   before shipping any schema.
4. Keep this file and `README.md` in step with every SV.
