package com.walnus.smartview.core.model

/** Lifecycle of a project record. */
enum class ProjectStatus(val label: String) {
    DRAFT("Draft"),
    SURVEYING("Surveying"),
    QUOTED("Quoted"),
    ARCHIVED("Archived"),
}

/**
 * A construction or finishing job. The root of the Project → Survey → Surface hierarchy.
 *
 * [currencyCode] is an ISO 4217 code that the user can change; no prices are hard-coded
 * in the domain layer.
 */
data class Project(
    val id: ProjectId,
    val name: String,
    val clientName: String? = null,
    val siteAddress: String? = null,
    val status: ProjectStatus = ProjectStatus.DRAFT,
    val currencyCode: String = DEFAULT_CURRENCY_CODE,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
) {
    init {
        require(name.isNotBlank()) { "Project name must not be blank" }
        require(createdAtEpochMillis >= 0) { "createdAtEpochMillis must not be negative" }
        require(updatedAtEpochMillis >= createdAtEpochMillis) {
            "updatedAtEpochMillis must not precede createdAtEpochMillis"
        }
        require(CURRENCY_CODE_REGEX.matches(currencyCode)) {
            "currencyCode must be an ISO 4217 alpha-3 code"
        }
    }

    companion object {
        /**
         * Default currency for the target market (Nigeria).
         * This is a locale default, not a commercial pricing decision.
         */
        const val DEFAULT_CURRENCY_CODE: String = "NGN"

        private val CURRENCY_CODE_REGEX = Regex("^[A-Z]{3}$")
    }
}

/** Lifecycle of a survey attached to a project. */
enum class SurveyStatus(val label: String) {
    DRAFT("Draft"),
    IN_PROGRESS("In progress"),
    COMPLETE("Complete"),
}

/**
 * A survey of one project's scope for one trade.
 *
 * [tradeId] refers to a trade capability identifier (for example "painting"); the model
 * layer deliberately does not depend on the trade modules.
 */
data class Survey(
    val id: SurveyId,
    val projectId: ProjectId,
    val tradeId: String,
    val status: SurveyStatus = SurveyStatus.DRAFT,
    val startedAtEpochMillis: Long,
    val completedAtEpochMillis: Long? = null,
    val surfaces: List<Surface> = emptyList(),
) {
    init {
        require(tradeId.isNotBlank()) { "tradeId must not be blank" }
        require(startedAtEpochMillis >= 0) { "startedAtEpochMillis must not be negative" }
        require(completedAtEpochMillis == null || completedAtEpochMillis >= startedAtEpochMillis) {
            "completedAtEpochMillis must not precede startedAtEpochMillis"
        }
        require(completedAtEpochMillis == null || status == SurveyStatus.COMPLETE) {
            "A survey can only carry a completion time when its status is COMPLETE"
        }
        require(surfaces.map { it.id }.toSet().size == surfaces.size) {
            "A survey cannot contain two surfaces with the same id"
        }
    }

    /** Surfaces that have usable dimensions. */
    val measurableSurfaces: List<Surface> get() = surfaces.filter { it.isMeasurable }
}
