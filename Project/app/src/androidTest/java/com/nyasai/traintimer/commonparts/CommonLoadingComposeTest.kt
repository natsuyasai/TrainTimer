package com.nyasai.traintimer.commonparts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * CommonLoadingCompose UIテスト（State Hoistingパターン版）
 */
@RunWith(AndroidJUnit4::class)
class CommonLoadingComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ローディングが非表示の時は何も表示されないこと`() {
        composeTestRule.setContent {
            TrainTimerTheme {
                CommonLoadingCompose(
                    loadingState = LoadingState(isVisible = false)
                )
            }
        }

        // ローディングコンテンツが存在しないことを確認
        composeTestRule
            .onNodeWithText("読み込み中")
            .assertDoesNotExist()
    }

    @Test
    fun `ローディングが表示されること`() {
        composeTestRule.setContent {
            TrainTimerTheme {
                CommonLoadingCompose(
                    loadingState = LoadingState(
                        isVisible = true,
                        loadingText = "読み込み中"
                    )
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("読み込み中")
            .assertIsDisplayed()
    }

    @Test
    fun `進捗表示が正しく動作すること`() {
        composeTestRule.setContent {
            TrainTimerTheme {
                CommonLoadingCompose(
                    loadingState = LoadingState(
                        isVisible = true,
                        loadingText = "データ処理中",
                        currentCount = 5,
                        maxCount = 10
                    )
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("データ処理中")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("5/10")
            .assertIsDisplayed()
    }

    @Test
    fun `maxCountが0の時は進捗が表示されないこと`() {
        composeTestRule.setContent {
            TrainTimerTheme {
                CommonLoadingCompose(
                    loadingState = LoadingState(
                        isVisible = true,
                        loadingText = "読み込み中",
                        currentCount = 0,
                        maxCount = 0
                    )
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("読み込み中")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("0/0")
            .assertDoesNotExist()
    }
}