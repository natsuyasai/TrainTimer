package com.nyasai.traintimer.routeinfo

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RouteInfoTitleCompose UIテスト
 */
@RunWith(AndroidJUnit4::class)
class RouteInfoTitleComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `路線情報タイトルが正しく表示されること`() {
        // Given
        val testItem = RouteListItem().apply {
            routeName = "JR山手線"
            stationName = "新宿駅"
            destination = "渋谷・品川方面"
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteInfoTitleCompose(
                    routeListItem = testItem,
                    currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Weekday,
                    {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("JR山手線")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("新宿駅")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("渋谷・品川方面")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("[平日]")
            .assertIsDisplayed()
    }

    @Test
    fun `土曜日のダイヤ種別が正しく表示されること`() {
        // Given
        val testItem = RouteListItem().apply {
            routeName = "JR山手線"
            stationName = "新宿駅"
            destination = "渋谷・品川方面"
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteInfoTitleCompose(
                    routeListItem = testItem,
                    currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Saturday,
                    {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("[土曜]")
            .assertIsDisplayed()
    }

    @Test
    fun `日曜祝日のダイヤ種別が正しく表示されること`() {
        // Given
        val testItem = RouteListItem().apply {
            routeName = "JR山手線"
            stationName = "新宿駅"
            destination = "渋谷・品川方面"
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteInfoTitleCompose(
                    routeListItem = testItem,
                    currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Holiday,
                    {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("[日曜・祝日]")
            .assertIsDisplayed()
    }

    @Test
    fun `タイトルクリックが動作すること`() {
        // Given
        val testItem = RouteListItem().apply {
            routeName = "JR山手線"
            stationName = "新宿駅"
            destination = "渋谷・品川方面"
        }
        var clickCallbackCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteInfoTitleCompose(
                    routeListItem = testItem,
                    currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Weekday,
                    onTitleClick = { clickCallbackCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("JR山手線")
            .performClick()

        // Then
        assert(clickCallbackCalled)
    }

    @Test
    fun `null値でもエラーが発生しないこと`() {
        // When & Then
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteInfoTitleCompose(
                    routeListItem = RouteListItem(),
                    currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Weekday,
                    {}
                )
            }
        }

        // コンポーネントが正常に描画されることを確認
        composeTestRule
            .onRoot()
            .assertExists()
        
        composeTestRule
            .onNodeWithText("[平日]")
            .assertIsDisplayed()
    }
}