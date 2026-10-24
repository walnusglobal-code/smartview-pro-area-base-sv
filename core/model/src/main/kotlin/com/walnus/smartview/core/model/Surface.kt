package com.walnus.smartview.core.model

/**
 * The kind of building element a survey line describes.
 *
 * [isOpening] elements (doors, windows) are measured so they can be *deducted* from the
 * paintable area, not painted.
 */
enum class SurfaceKind(
    val label: String,
    val isOpening: Boolean,
) {
    WALL("Wall", false),
    CEILING("Ceiling", false),
    TRIM("Trim / skirting", false),
    DOOR("Door", true),
    WINDOW("Window", true),
    OTHER("Other", false),
    ;

    /** True when this element should count towards paintable area. */
    val contributesArea: Boolean get() = !isOpening
}

/**
 * How a surface's area is known.
 *
 * Keeping this sealed prevents the "area is null but everyone assumes it isn't" bug
 * class and makes incomplete survey lines explicit in the UI.
 */
sealed interface SurfaceExtent {

    /** Area in square metres, or null when the surface is not yet fully measured. */
    val areaSquareMetres: Double?

    /** Width and height captured on site (the normal path for walls and openings). */
    data class Rectangular(val width: Length, val height: Length) : SurfaceExtent {
        override val areaSquareMetres: Double = width.metres * height.metres
    }

    /** A single area value taken directly from a drawing or a laser area reading. */
    data class DirectArea(val squareMetres: Double) : SurfaceExtent {
        init {
            require(squareMetres.isFinite() && squareMetres >= 0.0) {
                "Direct area must be a finite, non-negative value"
            }
        }

        override val areaSquareMetres: Double = squareMetres
    }

    /** Dimensions not captured yet. Never contributes silently to an estimate. */
    data object Unknown : SurfaceExtent {
        override val areaSquareMetres: Double? = null
    }
}

/**
 * One measured building element in a survey.
 *
 * The surface carries the provenance of its own dimensions; derived quantities
 * (see the estimation modules) downgrade trust rather than inheriting it.
 */
data class Surface(
    val id: SurfaceId,
    val label: String,
    val kind: SurfaceKind,
    val extent: SurfaceExtent,
    val provenance: Provenance,
    /** False when the surveyor wants to record but exclude the element (for example, a wall being tiled). */
    val includeInEstimate: Boolean = true,
    val note: String? = null,
) {
    init {
        require(label.isNotBlank()) { "Surface label must not be blank" }
    }

    /** Area in square metres, or null when dimensions are still missing. */
    val areaSquareMetres: Double? get() = extent.areaSquareMetres

    /** True when the surface has enough data to be used in a calculation. */
    val isMeasurable: Boolean get() = areaSquareMetres != null

    /** True when this surface is an opening to be deducted from a paintable area. */
    val isOpening: Boolean get() = kind.isOpening

    /**
     * True when this surface is included in an area calculation.
     *
     * Requires usable dimensions: a wall awaiting measurement is recorded but must not
     * be counted as zero-area paintable surface.
     */
    val countsTowardsArea: Boolean
        get() = includeInEstimate && kind.contributesArea && isMeasurable

    companion object {
        /** Convenience factory for a rectangular element measured on site. */
        fun rectangular(
            label: String,
            kind: SurfaceKind,
            width: Length,
            height: Length,
            provenance: Provenance,
            includeInEstimate: Boolean = true,
            id: SurfaceId = SurfaceId.new(),
        ): Surface = Surface(
            id = id,
            label = label,
            kind = kind,
            extent = SurfaceExtent.Rectangular(width, height),
            provenance = provenance,
            includeInEstimate = includeInEstimate,
        )
    }
}
