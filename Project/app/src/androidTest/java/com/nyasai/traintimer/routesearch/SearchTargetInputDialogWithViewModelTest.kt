package com.nyasai.traintimer.routesearch

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * SearchTargetInputDialogWithViewModel Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class SearchTargetInputDialogWithViewModelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ViewModelと統合されたダイアログが正常に表示されること`() {
        // Given
        val viewModel = SearchTargetInputViewModel()
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                SearchTargetInputDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { onDismissCalled = true },
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("追加したい駅名を入力してください")
            .assertIsDisplayed()
    }

    @Test
    fun `ViewModelを通じた駅名入力が正常に動作すること`() {
        // Given
        val viewModel = SearchTargetInputViewModel()
        val testInput = "東京駅"

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                SearchTargetInputDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput(testInput)

        // Then
        assert(viewModel.stationNameState == testInput)
        assert(viewModel.getStationName() == testInput)
    }

    @Test
    fun `ViewModelのコールバックが正常に動作すること`() {
        // Given
        val viewModel = SearchTargetInputViewModel()
        var positiveCallbackCalled = false
        var negativeCallbackCalled = false

        viewModel.onClickPositiveButtonCallback = { positiveCallbackCalled = true }
        viewModel.onClickNegativeButtonCallback = { negativeCallbackCalled = true }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                SearchTargetInputDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // Positive button test
        composeTestRule
            .onNodeWithText("検索開始")
            .performClick()

        // Then
        assert(positiveCallbackCalled)
        assert(viewModel.stationNameState == "") // clearUIData()が呼ばれることを確認

        // Reset for negative button test
        viewModel.updateStationName("テスト駅")
        negativeCallbackCalled = false

        composeTestRule
            .onNodeWithText("キャンセル")
            .performClick()

        assert(negativeCallbackCalled)
        assert(viewModel.stationNameState == "") // clearUIData()が呼ばれることを確認
    }

    @Test
    fun `ダイアログを閉じるとViewModelがクリアされること`() {
        // Given
        val viewModel = SearchTargetInputViewModel()
        viewModel.updateStationName("テスト駅")

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                SearchTargetInputDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNodeWithText("検索開始")
            .performClick()

        // Then
        assert(viewModel.stationNameState == "")
        assert(viewModel.getStationName() == "")
    }
}