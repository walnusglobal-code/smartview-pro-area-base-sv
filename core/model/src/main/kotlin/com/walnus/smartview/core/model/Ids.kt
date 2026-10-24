package com.walnus.smartview.core.model

import java.util.UUID

/**
 * Stable identifier for a [Project].
 *
 * Wrapping the raw string prevents accidental mixing of id types at call sites and
 * keeps persistence (Room, sync payloads) free to serialise a plain string.
 */
@JvmInline
value class ProjectId(val value: String) {
    init {
        require(value.isNotBlank()) { "ProjectId must not be blank" }
    }

    companion object {
        /** Generates a new random identifier. */
        fun new(): ProjectId = ProjectId(UUID.randomUUID().toString())
    }
}

/** Stable identifier for a [Survey]. */
@JvmInline
value class SurveyId(val value: String) {
    init {
        require(value.isNotBlank()) { "SurveyId must not be blank" }
    }

    companion object {
        fun new(): SurveyId = SurveyId(UUID.randomUUID().toString())
    }
}

/** Stable identifier for a [Surface]. */
@JvmInline
value class SurfaceId(val value: String) {
    init {
        require(value.isNotBlank()) { "SurfaceId must not be blank" }
    }

    companion object {
        fun new(): SurfaceId = SurfaceId(UUID.randomUUID().toString())
    }
}

/** Stable identifier for a [Measurement]. */
@JvmInline
value class MeasurementId(val value: String) {
    init {
        require(value.isNotBlank()) { "MeasurementId must not be blank" }
    }

    companion object {
        fun new(): MeasurementId = MeasurementId(UUID.randomUUID().toString())
    }
}
