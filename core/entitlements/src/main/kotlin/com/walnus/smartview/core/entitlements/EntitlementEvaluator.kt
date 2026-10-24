package com.walnus.smartview.core.entitlements

/**
 * Turns the last known [EntitlementSnapshot] into an access decision.
 *
 * Rules, in order:
 * 1. No snapshot → free tier. Surveying keeps working offline without an account.
 * 2. A device-only snapshot never grants paid capability, even if it claims Premium.
 * 3. A snapshot that appears to come from the future (beyond the clock-skew tolerance)
 *    is treated as untrusted.
 * 4. A server-verified snapshot grants its plan until it expires.
 * 5. After expiry, cached server verdicts keep working for a bounded grace window so a
 *    lapse on a site with no signal does not stop a surveyor mid-job.
 * 6. After the grace window the effective plan falls back to free and a refresh is
 *    requested. Local project data is never deleted by this class.
 *
 * This class makes decisions only. It performs no I/O, so it is safe to call from any
 * dispatcher and deterministic under test.
 */
class EntitlementEvaluator(
    private val clock: Clock = SystemClock,
    private val policy: OfflineEntitlementPolicy = OfflineEntitlementPolicy.DEFAULT,
    private val catalog: Map<PlanTier, EntitlementPlan> = PlanCatalog.default(),
) {

    /** Plan applied when nothing better is trusted. */
    val freePlan: EntitlementPlan = catalog[PlanTier.FREE] ?: PlanCatalog.FREE

    /** Resolves the effective plan and how much the snapshot can be trusted. */
    fun evaluate(snapshot: EntitlementSnapshot?): EntitlementEvaluation {
        val now = clock.nowEpochMillis()

        if (snapshot == null) {
            return EntitlementEvaluation(
                effectivePlan = freePlan,
                trustLevel = TrustLevel.FREE_TIER,
                shouldRefresh = false,
                reason = "no-entitlement-snapshot",
            )
        }

        if (snapshot.verifiedAtEpochMillis - now > policy.clockSkewToleranceMillis) {
            return untrusted(TrustLevel.STALE, "snapshot-timestamp-in-future")
        }

        if (snapshot.source == VerificationSource.DEVICE_ONLY) {
            return untrusted(TrustLevel.DEVICE_ONLY, "device-only-snapshot")
        }

        if (!snapshot.status.isAccessGranting) {
            return EntitlementEvaluation(
                effectivePlan = freePlan,
                trustLevel = TrustLevel.SERVER_VERIFIED,
                shouldRefresh = snapshot.status == SubscriptionStatus.UNKNOWN,
                reason = "status-${snapshot.status.name.lowercase()}",
            )
        }

        val expiry = snapshot.expiresAtEpochMillis
        if (expiry == null) {
            // Only a perpetual paid record may have no expiry; a trial without an end is
            // treated as a bad record rather than an unlimited free pass.
            if (snapshot.status == SubscriptionStatus.ACTIVE) {
                return EntitlementEvaluation(
                    effectivePlan = planFor(snapshot),
                    trustLevel = TrustLevel.SERVER_VERIFIED,
                    shouldRefresh = false,
                    reason = "server-verified-open-ended",
                )
            }
            return EntitlementEvaluation(
                effectivePlan = freePlan,
                trustLevel = TrustLevel.STALE,
                shouldRefresh = true,
                reason = "status-${snapshot.status.name.lowercase()}-without-expiry",
            )
        }

        return when {
            now <= expiry -> EntitlementEvaluation(
                effectivePlan = planFor(snapshot),
                trustLevel = TrustLevel.SERVER_VERIFIED,
                shouldRefresh = false,
                reason = "server-verified",
            )

            now - expiry <= policy.graceAfterExpiryMillis -> EntitlementEvaluation(
                effectivePlan = planFor(snapshot),
                trustLevel = TrustLevel.SERVER_GRACE,
                shouldRefresh = true,
                reason = "offline-grace",
            )

            else -> EntitlementEvaluation(
                effectivePlan = freePlan,
                trustLevel = TrustLevel.STALE,
                shouldRefresh = true,
                reason = "offline-grace-exhausted",
            )
        }
    }

    /** Decides whether one capability may be used right now. */
    fun check(feature: FeatureKey, snapshot: EntitlementSnapshot?): AccessDecision {
        val evaluation = evaluate(snapshot)
        val plan = evaluation.effectivePlan

        if (plan.allows(feature)) {
            return AccessDecision.Allowed(
                feature = feature,
                plan = plan.tier,
                trustLevel = evaluation.trustLevel,
            )
        }

        return when (evaluation.trustLevel) {
            TrustLevel.DEVICE_ONLY, TrustLevel.STALE -> AccessDecision.VerificationRequired(
                feature = feature,
                reason = evaluation.reason,
            )

            else -> AccessDecision.UpgradeRequired(feature = feature, currentPlan = plan.tier)
        }
    }

    /**
     * Decides whether another project may be created.
     *
     * Project creation is a free-tier capability, so it keeps working offline; only the
     * count allowance can block it.
     */
    fun checkProjectCreation(existingProjectCount: Int, snapshot: EntitlementSnapshot?): ProjectAccess {
        require(existingProjectCount >= 0) { "existingProjectCount must not be negative" }
        val plan = evaluate(snapshot).effectivePlan
        val limit = plan.projectLimit
            ?: return ProjectAccess.Allowed(plan = plan.tier, remaining = null)

        return if (existingProjectCount < limit) {
            ProjectAccess.Allowed(plan = plan.tier, remaining = limit - existingProjectCount)
        } else {
            ProjectAccess.LimitReached(limit = limit, plan = plan.tier)
        }
    }

    private fun planFor(snapshot: EntitlementSnapshot): EntitlementPlan =
        catalog[snapshot.plan.tier] ?: snapshot.plan

    private fun untrusted(trustLevel: TrustLevel, reason: String): EntitlementEvaluation =
        EntitlementEvaluation(
            effectivePlan = freePlan,
            trustLevel = trustLevel,
            shouldRefresh = true,
            reason = reason,
        )
}
