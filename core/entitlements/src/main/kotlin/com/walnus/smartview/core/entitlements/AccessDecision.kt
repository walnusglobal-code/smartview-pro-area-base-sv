package com.walnus.smartview.core.entitlements

/** How far a cached server verdict may be trusted while offline. */
data class OfflineEntitlementPolicy(
    /**
     * Extra time after a plan lapses during which cached server-verified access is still
     * honoured, so a lapse on a site with no signal does not lock a working surveyor out.
     */
    val graceAfterExpiryMillis: Long = 7 * Durations.DAY_MILLIS,
    /** Tolerance for a device clock that is slightly ahead of the backend. */
    val clockSkewToleranceMillis: Long = 10 * Durations.MINUTE_MILLIS,
) {
    init {
        require(graceAfterExpiryMillis >= 0) { "graceAfterExpiryMillis must not be negative" }
        require(clockSkewToleranceMillis >= 0) { "clockSkewToleranceMillis must not be negative" }
    }

    companion object {
        val DEFAULT: OfflineEntitlementPolicy = OfflineEntitlementPolicy()
    }
}

/** How much confidence the evaluation has in the snapshot it used. */
enum class TrustLevel {
    /** Server-verified snapshot, still inside its validity window. */
    SERVER_VERIFIED,

    /** Server-verified snapshot, past expiry but inside the offline grace window. */
    SERVER_GRACE,

    /** Server-verified snapshot, but too old to keep granting paid capability. */
    STALE,

    /** Only a device-asserted snapshot was available; paid capability is withheld. */
    DEVICE_ONLY,

    /** No snapshot at all; the free tier applies. */
    FREE_TIER,
}

/**
 * The resolved access state after applying policy to a snapshot.
 *
 * [effectivePlan] is what gating should use — it may be the free plan even when the
 * snapshot claimed something larger.
 */
data class EntitlementEvaluation(
    val effectivePlan: EntitlementPlan,
    val trustLevel: TrustLevel,
    /** True when the app should refresh entitlements from the backend when connectivity allows. */
    val shouldRefresh: Boolean,
    /** Short machine-readable explanation, useful for diagnostics and support. */
    val reason: String,
)

/** Outcome of asking whether one capability may be used. */
sealed interface AccessDecision {
    /** The capability may be used. [plan] is the plan that granted it. */
    data class Allowed(
        val feature: FeatureKey,
        val plan: PlanTier,
        val trustLevel: TrustLevel,
    ) : AccessDecision

    /** The capability exists but the current plan does not include it. */
    data class UpgradeRequired(
        val feature: FeatureKey,
        val currentPlan: PlanTier,
    ) : AccessDecision

    /** Access cannot be decided until the backend is reachable. Never deletes local data. */
    data class VerificationRequired(
        val feature: FeatureKey,
        val reason: String,
    ) : AccessDecision

    /** The plan's project allowance is used up. */
    data class ProjectLimitReached(
        val limit: Int,
        val currentPlan: PlanTier,
    ) : AccessDecision
}

/** Convenience for UI/use-case code that only needs a yes/no. */
val AccessDecision.isAllowed: Boolean get() = this is AccessDecision.Allowed

/** Outcome of asking whether a new project may be created. */
sealed interface ProjectAccess {
    /** Creation allowed. [remaining] is null when the plan is unlimited. */
    data class Allowed(
        val plan: PlanTier,
        val remaining: Int?,
    ) : ProjectAccess

    /** The plan's allowance is exhausted. Existing projects are never deleted. */
    data class LimitReached(
        val limit: Int,
        val plan: PlanTier,
    ) : ProjectAccess
}
