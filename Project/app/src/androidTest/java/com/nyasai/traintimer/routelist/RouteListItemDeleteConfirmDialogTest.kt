package com.nyasai.traintimer.routelist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RouteListItemDeleteConfirmDialog Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class RouteListItemDeleteConfirmDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ViewModelと統合されたダイアログが正常に表示されること`() {
        val testDataId = 123L
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialog(
                    isVisible = true,
                    onDismiss = { onDismissCalled = true },
                    onPositiveClick = {},
                    onNegativeClick = {},
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("削除の確認")
            .assertIsDisplayed()
    }

    @Test
    fun `ViewModelのコールバックが正常に動作すること`() {
        // Given
        val testDataId = 456L
        var positiveCallbackCalled = false
        var negativeCallbackCalled = false
        var callbackDataId: Long? = null


        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialog(
                    isVisible = true,
                    onDismiss = { },
                    onPositiveClick = {
                        positiveCallbackCalled = true
                        callbackDataId = testDataId
                    },
                    onNegativeClick = {
                        negativeCallbackCalled = true
                        callbackDataId = testDataId
                    },
                )
            }
        }

        // はいボタンをクリック
        composeTestRule
            .onNodeWithText("はい")
            .performClick()

        // Then
        assert(positiveCallbackCalled)
        assert(callbackDataId == testDataId)

        // Reset for negative button test
        positiveCallbackCalled = false
        negativeCallbackCalled = false
        callbackDataId = null

        composeTestRule
            .onNodeWithText("いいえ")
            .performClick()

        assert(negativeCallbackCalled)
        assert(callbackDataId == testDataId)
    }
}