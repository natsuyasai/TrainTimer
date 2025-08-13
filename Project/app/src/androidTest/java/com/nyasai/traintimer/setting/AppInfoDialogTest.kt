package com.nyasai.traintimer.setting

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * AppInfoDialog Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class AppInfoDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ダイアログが正常に表示されること`() {
        // Given
        val testAppTitle = "時刻表"
        val testTwitterInfo = "Twitter:natsuyasai7"
        var onCloseCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                AppInfoDialog(
                    isVisible = true,
                    appTitle = testAppTitle,
                    twitterInfo = testTwitterInfo,
                    onClose = { onCloseCalled = true },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("時刻表")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Twitter:natsuyasai7")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("OK")
            .assertIsDisplayed()
    }

    @Test
    fun `OKボタンが正常に動作すること`() {
        // Given
        var onCloseCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                AppInfoDialog(
                    isVisible = true,
                    appTitle = "時刻表",
                    twitterInfo = "Twitter:natsuyasai7",
                    onClose = { onCloseCalled = true },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        // Then
        assert(onCloseCalled)
        assert(onDismissCalled)
    }

    @Test
    fun `ダイアログが非表示の時は何も表示されないこと`() {
        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                AppInfoDialog(
                    isVisible = false,
                    appTitle = "時刻表",
                    twitterInfo = "Twitter:natsuyasai7",
                    onClose = { },
                    onDismiss = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("時刻表")
            .assertDoesNotExist()
    }

    @Test
    fun `カスタムタイトルとTwitter情報が正しく表示されること`() {
        // Given
        val customTitle = "カスタムアプリ"
        val customTwitter = "Twitter:customuser"

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                AppInfoDialog(
                    isVisible = true,
                    appTitle = customTitle,
                    twitterInfo = customTwitter,
                    onClose = { },
                    onDismiss = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(customTitle)
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText(customTwitter)
            .assertIsDisplayed()
    }
}