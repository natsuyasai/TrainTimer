package com.nyasai.traintimer.routeinfo.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.routeinfo.CountdownState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * CountdownDisplayのテストクラス
 */
@RunWith(AndroidJUnit4::class)
class CountdownDisplayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun countdownDisplay_正常なカウントダウン状態で正しく表示される() {
        // Given
        val countdownState = CountdownState(
            countdownText = "05:42",
            nextTimeInfo = "次: 08:30 快速",
            isActive = true
        )

        // When
        composeTestRule.setContent {
            CountdownDisplay(countdownState = countdownState)
        }

        // Then
        composeTestRule.onNodeWithText("05:42").assertIsDisplayed()
        composeTestRule.onNodeWithText("次: 08:30 快速").assertIsDisplayed()
    }

    @Test
    fun countdownDisplay_デフォルト状態で正しく表示される() {
        // Given
        val countdownState = CountdownState()

        // When
        composeTestRule.setContent {
            CountdownDisplay(countdownState = countdownState)
        }

        // Then
        composeTestRule.onNodeWithText("--:--").assertIsDisplayed()
    }

    @Test
    fun countdownDisplay_長いテキストでも正しく表示される() {
        // Given
        val countdownState = CountdownState(
            countdownText = "59:59",
            nextTimeInfo = "次: 23:59 特急新宿行き",
            isActive = true
        )

        // When
        composeTestRule.setContent {
            CountdownDisplay(countdownState = countdownState)
        }

        // Then
        composeTestRule.onNodeWithText("59:59").assertIsDisplayed()
        composeTestRule.onNodeWithText("次: 23:59 特急新宿行き").assertIsDisplayed()
    }
}