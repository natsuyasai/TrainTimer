package com.nyasai.traintimer.routeinfo.state

import com.nyasai.traintimer.database.RouteDetail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * CountdownStateManagerのテストクラス
 */
class CountdownStateManagerTest {

    private lateinit var countdownStateManager: CountdownStateManager

    @BeforeEach
    fun setUp() {
        countdownStateManager = CountdownStateManager()
    }

    @Test
    fun `getNextDiffTime_nullの場合-1が返される`() {
        // When
        val result = countdownStateManager.getNextDiffTime(null)
        
        // Then
        assertEquals(-1L, result)
    }

    @Test
    fun `getNextDiffTime_有効なRouteDetailの場合計算される`() {
        // Given - 現在時刻より確実に未来の時刻を設定
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val futureHour = (currentHour + 2) % 24
        val futureTime = String.format("%02d:30", futureHour)
        
        val routeDetail = RouteDetail().apply {
            departureTime = futureTime
            trainType = "普通"
            destination = "テスト方面"
        }
        
        // When
        val result = countdownStateManager.getNextDiffTime(routeDetail)
        
        // Then
        // 未来の時刻なので正の値が返されることを確認
        assertTrue(result > 0L)
    }

    private fun createRouteDetail(time: String, trainType: String): RouteDetail {
        return RouteDetail().apply {
            departureTime = time
            this.trainType = trainType
            destination = "テスト方面"
        }
    }
}