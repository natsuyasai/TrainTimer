package com.nyasai.traintimer.routeinfo.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * EmptyRouteDetailsViewのテストクラス
 */
@RunWith(AndroidJUnit4::class)
class EmptyRouteDetailsViewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyRouteDetailsView_正しいメッセージが表示される() {
        // When
        composeTestRule.setContent {
            EmptyRouteDetailsView()
        }

        // Then
        composeTestRule.onNodeWithText("路線詳細データがありません").assertIsDisplayed()
    }
}