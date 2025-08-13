package com.nyasai.traintimer.setting

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * AppInfoDialogWithViewModel Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class AppInfoDialogWithViewModelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ViewModelと統合されたダイアログが正常に表示されること`() {
        // Given
        val viewModel = AppInfoViewModel()
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                AppInfoDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { onDismissCalled = true },
                    viewModel = viewModel
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
    }

    @Test
    fun `ViewModelのコールバックが正常に動作すること`() {
        // Given
        val viewModel = AppInfoViewModel()
        var callbackCalled = false

        viewModel.onCloseCallback = { callbackCalled = true }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                AppInfoDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        // Then
        assert(callbackCalled)
    }

    @Test
    fun `ViewModelの値が正しく表示されること`() {
        // Given
        val viewModel = AppInfoViewModel()

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                AppInfoDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(viewModel.appTitle)
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText(viewModel.twitterInfo)
            .assertIsDisplayed()
    }

    @Test
    fun `ダイアログが非表示の時はViewModelと連携していても何も表示されないこと`() {
        // Given
        val viewModel = AppInfoViewModel()

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                AppInfoDialogWithViewModel(
                    isVisible = false,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText(viewModel.appTitle)
            .assertDoesNotExist()
    }
}