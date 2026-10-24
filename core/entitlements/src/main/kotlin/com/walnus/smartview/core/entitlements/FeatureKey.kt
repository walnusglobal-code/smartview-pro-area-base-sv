package com.walnus.smartview.core.entitlements

/**
 * Identifier for a gated capability.
 *
 * Feature modules ask for a [FeatureKey]; they never interpret plan names, prices or
 * subscription lifecycles themselves.
 */
@JvmInline
value class FeatureKey(val value: String) {
    init {
        require(PATTERN.matches(value)) {
            "FeatureKey must be lower_snake_case, for example \"ai_assistance\""
        }
    }

    override fun toString(): String = value

    private companion object {
        val PATTERN = Regex("^[a-z][a-z0-9_]{1,63}$")
    }
}

/**
 * Feature keys known to the application.
 *
 * These are identifiers, not a commercial catalogue: a key may exist before the feature
 * ships, and nothing here implies a price or that a capability is implemented.
 */
object FeatureKeys {
    /** Core painting estimation. Available on the free tier. */
    val PAINT_ESTIMATION = FeatureKey("paint_estimation")

    /** Colour and finish visualisation. */
    val ADVANCED_VISUALISATION = FeatureKey("advanced_visualization")

    /** AI-assisted recommendations and research. */
    val AI_ASSISTANCE = FeatureKey("ai_assistance")

    /** Extended report layouts and branding. */
    val ENHANCED_REPORTS = FeatureKey("enhanced_reports")

    /** Shared team/organisation projects. */
    val TEAM_PROJECTS = FeatureKey("team_projects")

    /** Supplier and price discovery. */
    val SUPPLIER_DISCOVERY = FeatureKey("supplier_discovery")
}
