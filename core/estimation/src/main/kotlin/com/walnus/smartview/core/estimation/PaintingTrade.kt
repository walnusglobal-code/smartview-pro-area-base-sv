package com.walnus.smartview.core.estimation

import com.walnus.smartview.core.entitlements.FeatureKeys
import com.walnus.smartview.core.trade.TradeCapability
import com.walnus.smartview.core.trade.TradeIds

/**
 * Painting, the first implemented trade.
 *
 * The shell can list this capability and route to the painting workflow without knowing
 * anything about coverage rates or pack sizes.
 */
object PaintingTrade : TradeCapability {
    override val id: String = TradeIds.PAINTING
    override val displayName: String = "Painting"
    override val requiredFeature = FeatureKeys.PAINT_ESTIMATION
    override val summary: String = "Estimate emulsion, primer and pack quantities from measured surfaces"
}
