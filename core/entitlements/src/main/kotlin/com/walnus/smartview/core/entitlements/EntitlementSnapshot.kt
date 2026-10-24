package com.walnus.smartview.core.entitlements

/**
 * How an entitlement snapshot was established.
 *
 * A locally edited flag is never proof of payment; only [SERVER_VERIFIED] snapshots can
 * grant paid capabilities, and only for a bounded offline period.
 */
enum class VerificationSource {
    /** Returned by the trusted backend after purchase or subscription verification. */
    SERVER_VERIFIED,

    /** Present on the device only (cache written by an older build, manual edit, preview). */
    DEVICE_ONLY,
}

/** Subscription lifecycle state reported with a snapshot. */
enum class SubscriptionStatus(val label: String) {
    /** No subscription record for this account. */
    NONE("No subscription"),

    /** Trial period running. */
    TRIALING("Trial"),

    /** Paid subscription active. */
    ACTIVE("Active"),

    /** Payment failed; provider grace period. */
    GRACE_PERIOD("Grace period"),

    /** Cancelled by the user but still inside the paid period. */
    CANCELLED("Cancelled"),

    /** Lapsed, ended or otherwise expired. */
    EXPIRED("Expired"),

    /** Access withdrawn by the provider (refund, chargeback, abuse). */
    REVOKED("Revoked"),

    /** Backend could not be reached and no cached state is trustworthy. */
    UNKNOWN("Unknown"),
    ;

    /** True when this status may still grant capability, subject to expiry. */
    val isAccessGranting: Boolean
        get() = this == TRIALING || this == ACTIVE || this == GRACE_PERIOD || this == CANCELLED
}

/**
 * The last known access state for an account.
 *
 * [expiresAtEpochMillis] is when the plan itself lapses (trial end, paid-period end).
 * Null means the plan does not expire on its own, which is only meaningful when the
 * snapshot is [VerificationSource.SERVER_VERIFIED] and the status is [SubscriptionStatus.ACTIVE].
 */
data class EntitlementSnapshot(
    val plan: EntitlementPlan,
    val status: SubscriptionStatus,
    val source: VerificationSource,
    /** When the backend (or device) produced this snapshot. */
    val verifiedAtEpochMillis: Long,
    /** When the plan lapses, or null when it does not lapse on its own. */
    val expiresAtEpochMillis: Long?,
    /** Opaque account reference; never a credential. */
    val accountRef: String? = null,
) {
    init {
        require(verifiedAtEpochMillis >= 0) { "verifiedAtEpochMillis must not be negative" }
        require(expiresAtEpochMillis == null || expiresAtEpochMillis >= 0) {
            "expiresAtEpochMillis must not be negative"
        }
    }
}
