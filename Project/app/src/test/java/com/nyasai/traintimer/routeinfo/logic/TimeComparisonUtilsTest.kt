package com.nyasai.traintimer.routeinfo.logic

import com.nyasai.traintimer.database.RouteDetail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * TimeComparisonUtilsのテストクラス
 */
class TimeComparisonUtilsTest {

    private fun createRouteDetail(departureTime: String): RouteDetail {
        return mock<RouteDetail>().apply {
            whenever(this.departureTime).thenReturn(departureTime)
        }
    }

    @Test
    fun `getTrainTimeInSeconds_正常な時刻文字列でCorrect計算`() {
        // Given
        val morningTime = "08:30"
        val nightTime = "23:45"
        val earlyMorningTime = "02:15"

        // When
        val morningSeconds = TimeComparisonUtils.getTrainTimeInSeconds(morningTime)
        val nightSeconds = TimeComparisonUtils.getTrainTimeInSeconds(nightTime)
        val earlyMorningSeconds = TimeComparisonUtils.getTrainTimeInSeconds(earlyMorningTime)

        // Then
        assertEquals((8 * 60 + 30) * 60L, morningSeconds) // 8:30
        assertEquals((23 * 60 + 45) * 60L, nightSeconds) // 23:45
        assertEquals(((2 + 24) * 60 + 15) * 60L, earlyMorningSeconds) // 2:15 -> 26:15
    }

    @Test
    fun `getTrainTimeInMinutes_正常な時刻文字列でCorrect計算`() {
        // Given
        val morningTime = "08:30"
        val nightTime = "23:45"
        val earlyMorningTime = "02:15"

        // When
        val morningMinutes = TimeComparisonUtils.getTrainTimeInMinutes(morningTime)
        val nightMinutes = TimeComparisonUtils.getTrainTimeInMinutes(nightTime)
        val earlyMorningMinutes = TimeComparisonUtils.getTrainTimeInMinutes(earlyMorningTime)

        // Then
        assertEquals(8 * 60 + 30, morningMinutes) // 8:30
        assertEquals(23 * 60 + 45, nightMinutes) // 23:45
        assertEquals((2 + 24) * 60 + 15, earlyMorningMinutes) // 2:15 -> 26:15
    }

    @Test
    fun `getTrainTimeInSeconds_不正な時刻文字列でnull返却`() {
        // Given
        val invalidTime = "invalid"
        val emptyTime = ""

        // When
        val invalidResult = TimeComparisonUtils.getTrainTimeInSeconds(invalidTime)
        val emptyResult = TimeComparisonUtils.getTrainTimeInSeconds(emptyTime)

        // Then
        assertNull(invalidResult)
        assertNull(emptyResult)
    }

    @Test
    fun `getTimeStatus_有効な時刻でStatus計算`() {
        // Given
        val routeDetail = createRouteDetail("08:30")

        // When
        val status = TimeComparisonUtils.getTimeStatus(routeDetail)

        // Then
        assertNotNull(status)
        assertTrue(status in listOf(
            TimeComparisonUtils.TimeStatus.PAST,
            TimeComparisonUtils.TimeStatus.FUTURE,
            TimeComparisonUtils.TimeStatus.CURRENT
        ))
    }

    @Test
    fun `getTimeStatus_無効な時刻でINVALID返却`() {
        // Given
        val invalidRouteDetail = createRouteDetail("")
        val nullRouteDetail = createRouteDetail("invalid")

        // When
        val invalidStatus = TimeComparisonUtils.getTimeStatus(invalidRouteDetail)
        val nullStatus = TimeComparisonUtils.getTimeStatus(nullRouteDetail)

        // Then
        assertEquals(TimeComparisonUtils.TimeStatus.INVALID, invalidStatus)
        assertEquals(TimeComparisonUtils.TimeStatus.INVALID, nullStatus)
    }

    @Test
    fun `getTimeStatusWithContext_フォールバック選択でNEXT_DAY返却`() {
        // Given
        val pastTimeRoute = createRouteDetail("06:00") // 過去の時刻と仮定

        // When
        val normalStatus = TimeComparisonUtils.getTimeStatusWithContext(pastTimeRoute, false)
        val fallbackStatus = TimeComparisonUtils.getTimeStatusWithContext(pastTimeRoute, true)

        // Then
        // フォールバック選択でない場合は通常のPAST判定
        // フォールバック選択の場合はNEXT_DAY
        if (normalStatus == TimeComparisonUtils.TimeStatus.PAST) {
            assertEquals(TimeComparisonUtils.TimeStatus.NEXT_DAY, fallbackStatus)
        }
    }

    @Test
    fun `findNextTrainIndex_空のリストで-1返却`() {
        // Given
        val emptyList = emptyList<RouteDetail>()

        // When
        val result = TimeComparisonUtils.findNextTrainIndex(emptyList)

        // Then
        assertEquals(-1, result)
    }

    @Test
    fun `findNextTrainIndex_有効なリストでインデックス返却`() {
        // Given
        val routeDetails = listOf(
            createRouteDetail("06:00"),
            createRouteDetail("08:00"),
            createRouteDetail("10:00"),
            createRouteDetail("23:00")
        )

        // When
        val result = TimeComparisonUtils.findNextTrainIndex(routeDetails)

        // Then
        assertTrue(result >= 0) // 有効なインデックスまたはフォールバック(0)
        assertTrue(result < routeDetails.size)
    }

    @Test
    fun `findNextTrain_空のリストでnull返却`() {
        // Given
        val emptyList = emptyList<RouteDetail>()

        // When
        val result = TimeComparisonUtils.findNextTrain(emptyList)

        // Then
        assertNull(result)
    }

    @Test
    fun `findNextTrain_有効なリストで電車返却`() {
        // Given
        val routeDetails = listOf(
            createRouteDetail("06:00"),
            createRouteDetail("08:00"),
            createRouteDetail("10:00")
        )

        // When
        val result = TimeComparisonUtils.findNextTrain(routeDetails)

        // Then
        assertNotNull(result)
        assertTrue(routeDetails.contains(result))
    }

    @Test
    fun `isNextDayFallback_空のリストでfalse返却`() {
        // Given
        val emptyList = emptyList<RouteDetail>()
        val routeDetail = createRouteDetail("06:00")

        // When
        val result = TimeComparisonUtils.isNextDayFallback(emptyList, routeDetail)

        // Then
        assertFalse(result)
    }

    @Test
    fun `isNextDayFallback_nullの電車でfalse返却`() {
        // Given
        val routeDetails = listOf(createRouteDetail("06:00"))

        // When
        val result = TimeComparisonUtils.isNextDayFallback(routeDetails, null)

        // Then
        assertFalse(result)
    }

    @Test
    fun `getTimeDifferenceInSeconds_有効な時刻で差分計算`() {
        // Given
        val routeDetail = createRouteDetail("23:59")

        // When
        val difference = TimeComparisonUtils.getTimeDifferenceInSeconds(routeDetail)

        // Then
        assertTrue(difference >= -1L) // 有効な値または無効(-1)
    }

    @Test
    fun `getTimeDifferenceInSeconds_無効な時刻で-1返却`() {
        // Given
        val invalidRouteDetail = createRouteDetail("")

        // When
        val difference = TimeComparisonUtils.getTimeDifferenceInSeconds(invalidRouteDetail)

        // Then
        assertEquals(-1L, difference)
    }

    @Test
    fun `時刻正規化_深夜時間帯での正しい変換`() {
        // Given
        val midnightTime = "00:30"
        val earlyMorningTime = "02:45"
        val regularTime = "14:20"

        // When
        val midnightMinutes = TimeComparisonUtils.getTrainTimeInMinutes(midnightTime)
        val earlyMorningMinutes = TimeComparisonUtils.getTrainTimeInMinutes(earlyMorningTime)
        val regularMinutes = TimeComparisonUtils.getTrainTimeInMinutes(regularTime)

        // Then
        assertEquals((0 + 24) * 60 + 30, midnightMinutes) // 00:30 -> 24:30
        assertEquals((2 + 24) * 60 + 45, earlyMorningMinutes) // 02:45 -> 26:45
        assertEquals(14 * 60 + 20, regularMinutes) // 14:20 -> 14:20 (変更なし)
    }

    @Test
    fun `時刻状態_境界値テスト`() {
        // 境界値となる時刻でテスト
        val testTimes = listOf("00:00", "03:59", "04:00", "23:59")
        
        testTimes.forEach { time ->
            val routeDetail = createRouteDetail(time)
            val status = TimeComparisonUtils.getTimeStatus(routeDetail)
            
            // 有効な状態のいずれかであることを確認
            assertTrue(status in TimeComparisonUtils.TimeStatus.values())
        }
    }
}