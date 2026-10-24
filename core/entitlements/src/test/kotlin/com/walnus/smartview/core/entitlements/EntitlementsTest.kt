package com.walnus.smartview.core.entitlements

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val NOW: Long = 1_760_000_000_000L
private const val DAY: Long = Durations.DAY_MILLIS

private fun snapshot(
    plan: EntitlementPlan,
    status: SubscriptionStatus,
    source: VerificationSource = VerificationSource.SERVER_VERIFIED,
    verifiedAt: Long = NOW,
    expiresAt: Long? = NOW + 30 * DAY,
): EntitlementSnapshot = EntitlementSnapshot(
    plan = plan,
    status = status,
    source = source,
    verifiedAtEpochMillis = verifiedAt,
    expiresAtEpochMillis = expiresAt,
)

class FeatureKeyTest {

    @Test
    fun acceptsLowerSnakeCaseKeys() {
        assertEquals("ai_assistance", FeatureKey("ai_assistance").value)
        assertEquals("paint_estimation", FeatureKeys.PAINT_ESTIMATION.value)
    }

    @Test
    fun rejectsMalformedKeys() {
        assertFailsWith<IllegalArgumentException> { FeatureKey("AI Assistance") }
        assertFailsWith<IllegalArgumentException> { FeatureKey("Advanced_Visualization") }
        assertFailsWith<IllegalArgumentException> { FeatureKey("a") }
    }
}

class PlanCatalogTest {

    @Test
    fun freeTierIncludesCorePaintingEstimation() {
        assertTrue(PlanCatalog.FREE.allows(FeatureKeys.PAINT_ESTIMATION))
        assertFalse(PlanCatalog.FREE.allows(FeatureKeys.AI_ASSISTANCE))
    }

    @Test
    fun businessTierIsUnlimitedAndIncludesEveryDeclaredFeature() {
        assertNull(PlanCatalog.BUSINESS.projectLimit)
        assertTrue(PlanCatalog.BUSINESS.allows(FeatureKeys.TEAM_PROJECTS))
        assertTrue(PlanCatalog.BUSINESS.allows(FeatureKeys.SUPPLIER_DISCOVERY))
    }

    @Test
    fun planLimitsAreOrderedByTier() {
        val free = PlanCatalog.FREE.projectLimit!!
        val trial = PlanCatalog.TRIAL.projectLimit!!
        val premium = PlanCatalog.PREMIUM.projectLimit!!
        assertTrue(free < trial)
        assertTrue(trial < premium)
    }

    @Test
    fun catalogExposesEveryTier() {
        val catalog = PlanCatalog.default()
        PlanTier.entries.forEach { tier ->
            assertTrue(catalog.containsKey(tier), "Missing plan for $tier")
        }
    }
}

class EntitlementEvaluatorTest {

    private val evaluator = EntitlementEvaluator(clock = ManualClock(NOW))

    @Test
    fun withoutSnapshotTheFreeTierAppliesAndSurveyingStillWorks() {
        val evaluation = evaluator.evaluate(null)
        assertEquals(PlanTier.FREE, evaluation.effectivePlan.tier)
        assertEquals(TrustLevel.FREE_TIER, evaluation.trustLevel)
        assertFalse(evaluation.shouldRefresh)

        assertIs<AccessDecision.Allowed>(evaluator.check(FeatureKeys.PAINT_ESTIMATION, null))
        assertIs<AccessDecision.UpgradeRequired>(evaluator.check(FeatureKeys.AI_ASSISTANCE, null))
    }

    @Test
    fun serverVerifiedPremiumGrantsItsFeatures() {
        val premium = snapshot(PlanCatalog.PREMIUM, SubscriptionStatus.ACTIVE)
        val evaluation = evaluator.evaluate(premium)
        assertEquals(PlanTier.PREMIUM, evaluation.effectivePlan.tier)
        assertEquals(TrustLevel.SERVER_VERIFIED, evaluation.trustLevel)
        assertFalse(evaluation.shouldRefresh)
        assertIs<AccessDecision.Allowed>(evaluator.check(FeatureKeys.AI_ASSISTANCE, premium))
    }

    @Test
    fun deviceOnlySnapshotNeverGrantsPaidFeatures() {
        val forged = snapshot(
            plan = PlanCatalog.BUSINESS,
            status = SubscriptionStatus.ACTIVE,
            source = VerificationSource.DEVICE_ONLY,
        )
        val evaluation = evaluator.evaluate(forged)
        assertEquals(PlanTier.FREE, evaluation.effectivePlan.tier)
        assertEquals(TrustLevel.DEVICE_ONLY, evaluation.trustLevel)
        assertTrue(evaluation.shouldRefresh)

        assertIs<AccessDecision.VerificationRequired>(
            evaluator.check(FeatureKeys.TEAM_PROJECTS, forged),
        )
        // Free capability still works so field work is not blocked.
        assertIs<AccessDecision.Allowed>(evaluator.check(FeatureKeys.PAINT_ESTIMATION, forged))
    }

    @Test
    fun expiredButInsideGraceStillAllowsPaidCapability() {
        val lapsed = snapshot(
            plan = PlanCatalog.PREMIUM,
            status = SubscriptionStatus.EXPIRED,
            verifiedAt = NOW - 40 * DAY,
            expiresAt = NOW - 3 * DAY,
        )
        // EXPIRED status is not access-granting, so the plan falls back even inside grace.
        assertEquals(PlanTier.FREE, evaluator.evaluate(lapsed).effectivePlan.tier)

        // ACTIVE status past expiry (for example a lapse discovered while offline) uses grace.
        val staleActive = snapshot(
            plan = PlanCatalog.PREMIUM,
            status = SubscriptionStatus.ACTIVE,
            expiresAt = NOW - 3 * DAY,
        )
        val evaluation = evaluator.evaluate(staleActive)
        assertEquals(PlanTier.PREMIUM, evaluation.effectivePlan.tier)
        assertEquals(TrustLevel.SERVER_GRACE, evaluation.trustLevel)
        assertTrue(evaluation.shouldRefresh)
        assertIs<AccessDecision.Allowed>(evaluator.check(FeatureKeys.ENHANCED_REPORTS, staleActive))
    }

    @Test
    fun afterGraceWindowPaidCapabilityRequiresVerification() {
        val tooOld = snapshot(
            plan = PlanCatalog.PREMIUM,
            status = SubscriptionStatus.ACTIVE,
            expiresAt = NOW - 30 * DAY,
        )
        val evaluation = evaluator.evaluate(tooOld)
        assertEquals(PlanTier.FREE, evaluation.effectivePlan.tier)
        assertEquals(TrustLevel.STALE, evaluation.trustLevel)
        assertTrue(evaluation.shouldRefresh)

        assertIs<AccessDecision.VerificationRequired>(
            evaluator.check(FeatureKeys.ADVANCED_VISUALISATION, tooOld),
        )
        assertIs<AccessDecision.Allowed>(evaluator.check(FeatureKeys.PAINT_ESTIMATION, tooOld))
    }

    @Test
    fun trialWithoutExpiryIsNotTreatedAsUnlimitedAccess() {
        val badTrial = snapshot(PlanCatalog.TRIAL, SubscriptionStatus.TRIALING, expiresAt = null)
        val evaluation = evaluator.evaluate(badTrial)
        assertEquals(PlanTier.FREE, evaluation.effectivePlan.tier)
        assertEquals(TrustLevel.STALE, evaluation.trustLevel)
        assertTrue(evaluation.shouldRefresh)
    }

    @Test
    fun snapshotFromTheFutureIsTreatedAsUntrusted() {
        val fromFuture = snapshot(
            plan = PlanCatalog.PREMIUM,
            status = SubscriptionStatus.ACTIVE,
            verifiedAt = NOW + Durations.DAY_MILLIS,
        )
        val evaluation = evaluator.evaluate(fromFuture)
        assertEquals(PlanTier.FREE, evaluation.effectivePlan.tier)
        assertEquals(TrustLevel.STALE, evaluation.trustLevel)
        assertIs<AccessDecision.VerificationRequired>(
            evaluator.check(FeatureKeys.AI_ASSISTANCE, fromFuture),
        )
    }

    @Test
    fun revokedAccessFallsBackToFree() {
        val revoked = snapshot(PlanCatalog.PREMIUM, SubscriptionStatus.REVOKED)
        val evaluation = evaluator.evaluate(revoked)
        assertEquals(PlanTier.FREE, evaluation.effectivePlan.tier)
        assertEquals(TrustLevel.SERVER_VERIFIED, evaluation.trustLevel)
        assertIs<AccessDecision.UpgradeRequired>(evaluator.check(FeatureKeys.AI_ASSISTANCE, revoked))
    }

    @Test
    fun cancelledSubscriptionKeepsAccessUntilItExpires() {
        val cancelled = snapshot(
            plan = PlanCatalog.PREMIUM,
            status = SubscriptionStatus.CANCELLED,
            expiresAt = NOW + 5 * DAY,
        )
        assertIs<AccessDecision.Allowed>(evaluator.check(FeatureKeys.AI_ASSISTANCE, cancelled))
    }

    @Test
    fun projectCreationFollowsPlanLimits() {
        val freeLimit = PlanCatalog.FREE.projectLimit!!
        assertIs<ProjectAccess.Allowed>(evaluator.checkProjectCreation(freeLimit - 1, null))
        assertIs<ProjectAccess.LimitReached>(evaluator.checkProjectCreation(freeLimit, null))

        val premium = snapshot(PlanCatalog.PREMIUM, SubscriptionStatus.ACTIVE)
        val premiumLimit = PlanCatalog.PREMIUM.projectLimit!!
        val reached = evaluator.checkProjectCreation(premiumLimit, premium)
        assertIs<ProjectAccess.LimitReached>(reached)
        assertEquals(premiumLimit, reached.limit)
    }

    @Test
    fun businessPlanAllowsUnlimitedProjects() {
        val business = snapshot(PlanCatalog.BUSINESS, SubscriptionStatus.ACTIVE)
        val access = evaluator.checkProjectCreation(10_000, business)
        assertIs<ProjectAccess.Allowed>(access)
        assertNull(access.remaining)
    }

    @Test
    fun graceWindowIsBoundedByPolicy() {
        val strict = EntitlementEvaluator(
            clock = ManualClock(NOW),
            policy = OfflineEntitlementPolicy(graceAfterExpiryMillis = 0),
        )
        val justExpired = snapshot(
            plan = PlanCatalog.PREMIUM,
            status = SubscriptionStatus.ACTIVE,
            expiresAt = NOW - 1,
        )
        assertIs<AccessDecision.VerificationRequired>(
            strict.check(FeatureKeys.AI_ASSISTANCE, justExpired),
        )
    }

    @Test
    fun evaluationDoesNotDependOnWhenItIsCalledForTheSameClockTime() {
        val paid = snapshot(PlanCatalog.PREMIUM, SubscriptionStatus.ACTIVE)
        val first = evaluator.evaluate(paid)
        val second = evaluator.evaluate(paid)
        assertEquals(first, second)
    }
}
