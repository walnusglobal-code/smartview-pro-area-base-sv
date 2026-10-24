package com.walnus.smartview.core.entitlements

/** Commercial tier. Contains no pricing information. */
enum class PlanTier(val label: String) {
    FREE("Free"),
    TRIAL("Trial"),
    PREMIUM("Premium"),
    BUSINESS("Business"),
}

/**
 * A plan definition: which capabilities it unlocks and how many projects it allows.
 *
 * Plan definitions are data so they can be supplied by a trusted backend later without
 * changing feature code. [projectLimit] of null means unlimited.
 */
data class EntitlementPlan(
    val tier: PlanTier,
    val features: Set<FeatureKey>,
    val projectLimit: Int?,
) {
    init {
        require(projectLimit == null || projectLimit >= 0) { "projectLimit must be >= 0 or null" }
    }

    fun allows(feature: FeatureKey): Boolean = features.contains(feature)
}

/**
 * Provisional, configurable plan definitions used until a trusted backend supplies them.
 *
 * These limits are engineering defaults for gating tests and are explicitly **not**
 * approved commercial terms. No prices, currencies or billing periods appear here.
 */
object PlanCatalog {

    /** Every gated capability currently declared by the application. */
    private val ALL_FEATURES: Set<FeatureKey> = setOf(
        FeatureKeys.PAINT_ESTIMATION,
        FeatureKeys.ADVANCED_VISUALISATION,
        FeatureKeys.AI_ASSISTANCE,
        FeatureKeys.ENHANCED_REPORTS,
        FeatureKeys.TEAM_PROJECTS,
        FeatureKeys.SUPPLIER_DISCOVERY,
    )

    val FREE: EntitlementPlan = EntitlementPlan(
        tier = PlanTier.FREE,
        features = setOf(FeatureKeys.PAINT_ESTIMATION),
        projectLimit = 3,
    )

    val TRIAL: EntitlementPlan = EntitlementPlan(
        tier = PlanTier.TRIAL,
        features = setOf(
            FeatureKeys.PAINT_ESTIMATION,
            FeatureKeys.ADVANCED_VISUALISATION,
            FeatureKeys.AI_ASSISTANCE,
            FeatureKeys.ENHANCED_REPORTS,
        ),
        projectLimit = 10,
    )

    val PREMIUM: EntitlementPlan = EntitlementPlan(
        tier = PlanTier.PREMIUM,
        features = setOf(
            FeatureKeys.PAINT_ESTIMATION,
            FeatureKeys.ADVANCED_VISUALISATION,
            FeatureKeys.AI_ASSISTANCE,
            FeatureKeys.ENHANCED_REPORTS,
            FeatureKeys.SUPPLIER_DISCOVERY,
        ),
        projectLimit = 50,
    )

    val BUSINESS: EntitlementPlan = EntitlementPlan(
        tier = PlanTier.BUSINESS,
        features = ALL_FEATURES,
        projectLimit = null,
    )

    /** Plans keyed by tier. */
    fun default(): Map<PlanTier, EntitlementPlan> = mapOf(
        PlanTier.FREE to FREE,
        PlanTier.TRIAL to TRIAL,
        PlanTier.PREMIUM to PREMIUM,
        PlanTier.BUSINESS to BUSINESS,
    )
}
