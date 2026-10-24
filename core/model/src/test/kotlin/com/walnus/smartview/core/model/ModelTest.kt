package com.walnus.smartview.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProvenanceTest {

    @Test
    fun measuredAndUserConfirmedArePhysicallyVerified() {
        assertTrue(Provenance.MEASURED.isPhysicallyVerified)
        assertTrue(Provenance.USER_CONFIRMED.isPhysicallyVerified)
    }

    @Test
    fun inferredEstimatedAndAiAreNotPhysicallyVerified() {
        assertFalse(Provenance.INFERRED.isPhysicallyVerified)
        assertFalse(Provenance.ESTIMATED.isPhysicallyVerified)
        assertFalse(Provenance.AI_SUGGESTED.isPhysicallyVerified)
    }

    @Test
    fun strengthOrderingMatchesDeclarationOrder() {
        assertTrue(Provenance.MEASURED.strength > Provenance.USER_CONFIRMED.strength)
        assertTrue(Provenance.USER_CONFIRMED.strength > Provenance.INFERRED.strength)
        assertTrue(Provenance.INFERRED.strength > Provenance.ESTIMATED.strength)
        assertTrue(Provenance.ESTIMATED.strength > Provenance.AI_SUGGESTED.strength)
    }

    @Test
    fun weakestOfReturnsLeastTrustworthyValue() {
        assertEquals(
            Provenance.AI_SUGGESTED,
            Provenance.weakestOf(Provenance.MEASURED, Provenance.AI_SUGGESTED, Provenance.ESTIMATED),
        )
        assertEquals(
            Provenance.ESTIMATED,
            Provenance.weakestOf(listOf(Provenance.MEASURED, Provenance.ESTIMATED)),
        )
        assertEquals(Provenance.MEASURED, Provenance.weakestOf(Provenance.MEASURED))
    }

    @Test
    fun weakestOfRejectsEmptyInput() {
        assertFailsWith<IllegalArgumentException> { Provenance.weakestOf(emptyList()) }
    }

    @Test
    fun provenanceSurvivesValueMapping() {
        val measured = ProvValue(2.0, Provenance.MEASURED)
        val area = measured.map { it * 3.0 }
        assertEquals(6.0, area.value)
        assertEquals(Provenance.MEASURED, area.provenance)
    }
}

class LengthTest {

    @Test
    fun convertsMetricAndImperialUnitsExactly() {
        assertEquals(2.5, Length(250.0, LengthUnit.CENTIMETRE).metres, 1e-12)
        assertEquals(0.9, Length(900.0, LengthUnit.MILLIMETRE).metres, 1e-12)
        assertEquals(0.3048, Length(1.0, LengthUnit.FOOT).metres, 1e-12)
        assertEquals(0.0254, Length(1.0, LengthUnit.INCH).metres, 1e-12)
    }

    @Test
    fun convertsBetweenUnitsWithoutDrift() {
        val feet = Length(12.0, LengthUnit.FOOT)
        val inches = feet.convertedTo(LengthUnit.INCH)
        assertEquals(144.0, inches.value, 1e-9)
        assertEquals(feet.metres, inches.metres, 1e-12)
    }

    @Test
    fun rejectsInvalidLengths() {
        assertFailsWith<IllegalArgumentException> { Length(0.0, LengthUnit.METRE) }
        assertFailsWith<IllegalArgumentException> { Length(-1.0, LengthUnit.METRE) }
        assertFailsWith<IllegalArgumentException> { Length(Double.NaN, LengthUnit.METRE) }
    }

    @Test
    fun formatsWholeNumbersWithoutDecimalTail() {
        assertEquals("3 m", Length(3.0, LengthUnit.METRE).toString())
        assertEquals("2.5 cm", Length(2.5, LengthUnit.CENTIMETRE).toString())
    }
}

class MeasurementTest {

    private val base = MeasurementId("m-1")

    @Test
    fun measuredValueNeedsNoSource() {
        val measurement = Measurement(
            id = MeasurementId.new(),
            length = Length(3.2, LengthUnit.METRE),
            provenance = Provenance.MEASURED,
            recordedAtEpochMillis = 1_700_000_000_000,
        )
        assertEquals(3.2, measurement.metres, 1e-12)
        assertNull(measurement.derivedFrom)
    }

    @Test
    fun inferredValueMustReferenceItsSource() {
        assertFailsWith<IllegalArgumentException> {
            Measurement(
                id = base,
                length = Length(3.2, LengthUnit.METRE),
                provenance = Provenance.INFERRED,
                recordedAtEpochMillis = 1_700_000_000_000,
            )
        }

        val inferred = Measurement(
            id = base,
            length = Length(3.2, LengthUnit.METRE),
            provenance = Provenance.INFERRED,
            recordedAtEpochMillis = 1_700_000_000_000,
            derivedFrom = MeasurementId("m-source"),
        )
        assertEquals(MeasurementId("m-source"), inferred.derivedFrom)
    }

    @Test
    fun rejectsNegativeTimestamp() {
        assertFailsWith<IllegalArgumentException> {
            Measurement(
                id = base,
                length = Length(1.0, LengthUnit.METRE),
                provenance = Provenance.MEASURED,
                recordedAtEpochMillis = -1,
            )
        }
    }
}

class SurfaceTest {

    @Test
    fun rectangularSurfaceComputesAreaInSquareMetres() {
        val wall = Surface.rectangular(
            label = "North wall",
            kind = SurfaceKind.WALL,
            width = Length(4.0, LengthUnit.METRE),
            height = Length(3.0, LengthUnit.METRE),
            provenance = Provenance.MEASURED,
        )
        assertEquals(12.0, wall.areaSquareMetres!!, 1e-12)
        assertTrue(wall.countsTowardsArea)
        assertFalse(wall.isOpening)
    }

    @Test
    fun mixedUnitsProduceCorrectArea() {
        val wall = Surface.rectangular(
            label = "Wall in feet and cm",
            kind = SurfaceKind.WALL,
            width = Length(10.0, LengthUnit.FOOT),
            height = Length(250.0, LengthUnit.CENTIMETRE),
            provenance = Provenance.MEASURED,
        )
        // 3.048 m × 2.5 m
        assertEquals(7.62, wall.areaSquareMetres!!, 1e-9)
    }

    @Test
    fun doorIsAnOpeningAndNotCountedAsPaintableArea() {
        val door = Surface.rectangular(
            label = "Entrance door",
            kind = SurfaceKind.DOOR,
            width = Length(900.0, LengthUnit.MILLIMETRE),
            height = Length(2.1, LengthUnit.METRE),
            provenance = Provenance.MEASURED,
        )
        assertTrue(door.isOpening)
        assertFalse(door.countsTowardsArea)
        assertEquals(1.89, door.areaSquareMetres!!, 1e-9)
    }

    @Test
    fun unknownExtentIsNotMeasurable() {
        val surface = Surface(
            id = SurfaceId.new(),
            label = "Hall wall",
            kind = SurfaceKind.WALL,
            extent = SurfaceExtent.Unknown,
            provenance = Provenance.MEASURED,
        )
        assertNull(surface.areaSquareMetres)
        assertFalse(surface.isMeasurable)
        assertFalse(surface.countsTowardsArea)
    }

    @Test
    fun directAreaRequiresNonNegativeValue() {
        assertFailsWith<IllegalArgumentException> { SurfaceExtent.DirectArea(-1.0) }
        assertEquals(18.5, SurfaceExtent.DirectArea(18.5).areaSquareMetres!!, 1e-12)
    }

    @Test
    fun excludedSurfaceDoesNotCountTowardsArea() {
        val tiled = Surface.rectangular(
            label = "Tiled wall",
            kind = SurfaceKind.WALL,
            width = Length(2.0, LengthUnit.METRE),
            height = Length(2.0, LengthUnit.METRE),
            provenance = Provenance.MEASURED,
            includeInEstimate = false,
        )
        assertFalse(tiled.countsTowardsArea)
    }

    @Test
    fun rejectsBlankLabel() {
        assertFailsWith<IllegalArgumentException> {
            Surface(
                id = SurfaceId.new(),
                label = " ",
                kind = SurfaceKind.WALL,
                extent = SurfaceExtent.Unknown,
                provenance = Provenance.MEASURED,
            )
        }
    }
}

class ProjectAndSurveyTest {

    private fun project(
        createdAt: Long = 1_000L,
        updatedAt: Long = 2_000L,
        currency: String = "NGN",
    ) = Project(
        id = ProjectId.new(),
        name = "Lekki duplex repaint",
        createdAtEpochMillis = createdAt,
        updatedAtEpochMillis = updatedAt,
        currencyCode = currency,
    )

    @Test
    fun projectDefaultsToDraftAndTargetMarketCurrency() {
        val project = project()
        assertEquals(ProjectStatus.DRAFT, project.status)
        assertEquals("NGN", project.currencyCode)
    }

    @Test
    fun projectRejectsInvalidCurrencyAndTimeline() {
        assertFailsWith<IllegalArgumentException> { project(currency = "naira") }
        assertFailsWith<IllegalArgumentException> { project(createdAt = 5_000L, updatedAt = 1_000L) }
    }

    @Test
    fun projectRejectsBlankName() {
        assertFailsWith<IllegalArgumentException> {
            Project(
                id = ProjectId.new(),
                name = "  ",
                createdAtEpochMillis = 0,
                updatedAtEpochMillis = 0,
            )
        }
    }

    @Test
    fun surveyRejectsDuplicateSurfaceIds() {
        val sharedId = SurfaceId.new()
        val wall = Surface.rectangular(
            label = "Wall A",
            kind = SurfaceKind.WALL,
            width = Length(1.0, LengthUnit.METRE),
            height = Length(1.0, LengthUnit.METRE),
            provenance = Provenance.MEASURED,
            id = sharedId,
        )
        assertFailsWith<IllegalArgumentException> {
            Survey(
                id = SurveyId.new(),
                projectId = ProjectId.new(),
                tradeId = "painting",
                startedAtEpochMillis = 1,
                surfaces = listOf(wall, wall),
            )
        }
    }

    @Test
    fun surveyRequiresStatusCompleteWhenCompletionTimeIsSet() {
        assertFailsWith<IllegalArgumentException> {
            Survey(
                id = SurveyId.new(),
                projectId = ProjectId.new(),
                tradeId = "painting",
                status = SurveyStatus.IN_PROGRESS,
                startedAtEpochMillis = 1_000,
                completedAtEpochMillis = 2_000,
            )
        }
    }

    @Test
    fun measurableSurfacesFilterIncompleteLines() {
        val survey = Survey(
            id = SurveyId.new(),
            projectId = ProjectId.new(),
            tradeId = "painting",
            startedAtEpochMillis = 1,
            surfaces = listOf(
                Surface.rectangular(
                    label = "Measured wall",
                    kind = SurfaceKind.WALL,
                    width = Length(2.0, LengthUnit.METRE),
                    height = Length(2.0, LengthUnit.METRE),
                    provenance = Provenance.MEASURED,
                ),
                Surface(
                    id = SurfaceId.new(),
                    label = "Unmeasured wall",
                    kind = SurfaceKind.WALL,
                    extent = SurfaceExtent.Unknown,
                    provenance = Provenance.MEASURED,
                ),
            ),
        )
        assertEquals(1, survey.measurableSurfaces.size)
    }
}
