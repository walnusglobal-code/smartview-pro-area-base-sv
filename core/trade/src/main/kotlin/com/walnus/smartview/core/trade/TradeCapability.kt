package com.walnus.smartview.core.trade

import com.walnus.smartview.core.entitlements.FeatureKey

/**
 * A trade the application can run surveys and estimates for.
 *
 * The shell only needs identity and gating information; each trade performs its own
 * calculations behind this contract, so painting, plastering and screeding stay
 * independently replaceable.
 *
 * Deliberately small: this is not a plug-in framework. It exists because the shell
 * must list trades and gate them without knowing trade internals.
 */
interface TradeCapability {

    /** Stable identifier persisted with surveys (for example "painting"). */
    val id: String

    /** Name shown to the user. */
    val displayName: String

    /** Capability required to use this trade, or null when it is free. */
    val requiredFeature: FeatureKey?

    /** One-line description of what the trade produces. */
    val summary: String
}

/** Stable trade identifiers used in persisted survey records. */
object TradeIds {
    const val PAINTING = "painting"

    /** Planned. No implementation exists yet; do not persist surveys against it. */
    const val PLASTERING = "plastering"

    /** Planned. No implementation exists yet; do not persist surveys against it. */
    const val SCREEDING = "screeding"
}
