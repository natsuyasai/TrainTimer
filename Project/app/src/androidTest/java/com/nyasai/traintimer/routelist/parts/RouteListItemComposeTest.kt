package com.nyasai.traintimer.routelist.parts

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RouteListItemCompose UIテスト
 */
@RunWith(AndroidJUnit4::class)
class RouteListItemComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `路線リストアイテムが正しく表示されること`() {
        // Given
        val testItem = RouteListItem().apply {
            routeName = "JR山手線"
            stationName = "新宿駅"
            destination = "渋谷・品川方面"
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemCompose(routeListItem = testItem)
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
    }
}