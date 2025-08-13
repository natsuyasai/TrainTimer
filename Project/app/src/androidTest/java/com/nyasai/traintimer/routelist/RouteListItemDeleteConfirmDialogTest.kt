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
    fun `ダイアログが正常に表示されること`() {
        // Given
        var onDismissCalled = false
        var onPositiveClickCalled = false
        var onNegativeClickCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialog(
                    isVisible = true,
                    onPositiveClick = { onPositiveClickCalled = true },
                    onNegativeClick = { onNegativeClickCalled = true },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("削除の確認")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("このアイテムを削除しますか？")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("はい")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("いいえ")
            .assertIsDisplayed()
    }

    @Test
    fun `はいボタンが正常に動作すること`() {
        // Given
        var onPositiveClickCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialog(
                    isVisible = true,
                    onPositiveClick = { onPositiveClickCalled = true },
                    onNegativeClick = { },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("はい")
            .performClick()

        // Then
        assert(onPositiveClickCalled)
        assert(onDismissCalled)
    }

    @Test
    fun `いいえボタンが正常に動作すること`() {
        // Given
        var onNegativeClickCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialog(
                    isVisible = true,
                    onPositiveClick = { },
                    onNegativeClick = { onNegativeClickCalled = true },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("いいえ")
            .performClick()

        // Then
        assert(onNegativeClickCalled)
        assert(onDismissCalled)
    }

    @Test
    fun `ダイアログが非表示の時は何も表示されないこと`() {
        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialog(
                    isVisible = false,
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("削除の確認")
            .assertDoesNotExist()
    }
}