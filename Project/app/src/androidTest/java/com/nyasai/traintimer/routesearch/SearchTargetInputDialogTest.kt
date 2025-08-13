package com.nyasai.traintimer.routesearch

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * SearchTargetInputDialog Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class SearchTargetInputDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ダイアログが正常に表示されること`() {
        // Given
        var isVisible = true
        var stationName = ""
        var onDismissCalled = false
        var onPositiveClickCalled = false
        var onNegativeClickCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                SearchTargetInputDialog(
                    isVisible = isVisible,
                    stationName = stationName,
                    onStationNameChange = { stationName = it },
                    onPositiveClick = { onPositiveClickCalled = true },
                    onNegativeClick = { onNegativeClickCalled = true },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("追加したい駅名を入力してください")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("検索開始")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("キャンセル")
            .assertIsDisplayed()
        
        composeTestRule
            .onNode(hasSetTextAction())
            .assertIsDisplayed()
    }

    @Test
    fun `テキスト入力が正常に動作すること`() {
        // Given
        var stationName = ""
        val testInput = "新宿駅"

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                SearchTargetInputDialog(
                    isVisible = true,
                    stationName = stationName,
                    onStationNameChange = { stationName = it },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput(testInput)

        // Then
        assert(stationName == testInput)
    }

    @Test
    fun `検索開始ボタンが正常に動作すること`() {
        // Given
        var onPositiveClickCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                SearchTargetInputDialog(
                    isVisible = true,
                    stationName = "",
                    onStationNameChange = { },
                    onPositiveClick = { onPositiveClickCalled = true },
                    onNegativeClick = { },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("検索開始")
            .performClick()

        // Then
        assert(onPositiveClickCalled)
        assert(onDismissCalled)
    }

    @Test
    fun `キャンセルボタンが正常に動作すること`() {
        // Given
        var onNegativeClickCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                SearchTargetInputDialog(
                    isVisible = true,
                    stationName = "",
                    onStationNameChange = { },
                    onPositiveClick = { },
                    onNegativeClick = { onNegativeClickCalled = true },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("キャンセル")
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
                SearchTargetInputDialog(
                    isVisible = false,
                    stationName = "",
                    onStationNameChange = { },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("追加したい駅名を入力してください")
            .assertDoesNotExist()
    }
}