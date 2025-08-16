package com.nyasai.traintimer.routeinfo.parts

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RouteInfoItemCompose UIテスト
 */
@RunWith(AndroidJUnit4::class)
class RouteInfoItemComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `路線詳細アイテムが正しく表示されること`() {
        // Given
        val testDetail = RouteDetail().apply {
            departureTime = "12:34"
            trainType = "快速"
            destination = "新宿"
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteInfoItemCompose(routeDetail = testDetail)
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("12:34")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("快速")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("新宿")
            .assertIsDisplayed()
    }

    @Test
    fun `空データでもエラーが発生しないこと`() {
        // Given
        val emptyDetail = RouteDetail()

        // When & Then
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteInfoItemCompose(routeDetail = emptyDetail)
            }
        }

        // コンポーネントが正常に描画されることを確認
        composeTestRule
            .onRoot()
            .assertExists()
    }

    @Test
    fun `異なる時刻でも正常に表示されること`() {
        // Given
        val futureTimeDetail = RouteDetail().apply {
            departureTime = "23:59"
            trainType = "普通"
            destination = "池袋"
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteInfoItemCompose(routeDetail = futureTimeDetail)
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("23:59")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("普通")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("池袋")
            .assertIsDisplayed()
    }
}