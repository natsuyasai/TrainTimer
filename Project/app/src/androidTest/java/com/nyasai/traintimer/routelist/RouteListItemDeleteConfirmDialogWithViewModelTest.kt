package com.nyasai.traintimer.routelist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RouteListItemDeleteConfirmDialogWithViewModel Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class RouteListItemDeleteConfirmDialogWithViewModelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ViewModelと統合されたダイアログが正常に表示されること`() {
        // Given
        val viewModel = RouteListItemDeleteConfirmViewModel()
        val testDataId = 123L
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialogWithViewModel(
                    isVisible = true,
                    targetDataId = testDataId,
                    onDismiss = { onDismissCalled = true },
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("削除の確認")
            .assertIsDisplayed()
        
        // ViewModelにデータIDが設定されていることを確認
        assert(viewModel.targetDataId == testDataId)
    }

    @Test
    fun `ViewModelのコールバックが正常に動作すること`() {
        // Given
        val viewModel = RouteListItemDeleteConfirmViewModel()
        val testDataId = 456L
        var positiveCallbackCalled = false
        var negativeCallbackCalled = false
        var callbackDataId: Long? = null

        viewModel.onClickPositiveButtonCallback = { dataId ->
            positiveCallbackCalled = true
            callbackDataId = dataId
        }

        viewModel.onClickNegativeButtonCallback = { dataId ->
            negativeCallbackCalled = true
            callbackDataId = dataId
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialogWithViewModel(
                    isVisible = true,
                    targetDataId = testDataId,
                    onDismiss = { },
                    viewModel = viewModel
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
        assert(viewModel.targetDataId == null) // clearUIData()が呼ばれることを確認

        // Reset for negative button test
        positiveCallbackCalled = false
        negativeCallbackCalled = false
        callbackDataId = null

        viewModel.setTargetDataId(testDataId)

        composeTestRule
            .onNodeWithText("いいえ")
            .performClick()

        assert(negativeCallbackCalled)
        assert(callbackDataId == testDataId)
        assert(viewModel.targetDataId == null) // clearUIData()が呼ばれることを確認
    }

    @Test
    fun `ダイアログを閉じるとViewModelがクリアされること`() {
        // Given
        val viewModel = RouteListItemDeleteConfirmViewModel()
        val testDataId = 789L
        
        viewModel.setTargetDataId(testDataId)

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialogWithViewModel(
                    isVisible = true,
                    targetDataId = testDataId,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNodeWithText("はい")
            .performClick()

        // Then
        assert(viewModel.targetDataId == null)
    }

    @Test
    fun `nullのデータIDでも正常に動作すること`() {
        // Given
        val viewModel = RouteListItemDeleteConfirmViewModel()

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemDeleteConfirmDialogWithViewModel(
                    isVisible = true,
                    targetDataId = null,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("削除の確認")
            .assertIsDisplayed()
        
        assert(viewModel.targetDataId == null)
    }
}