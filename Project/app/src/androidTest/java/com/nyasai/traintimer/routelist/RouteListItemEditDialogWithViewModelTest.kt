package com.nyasai.traintimer.routelist

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RouteListItemEditDialogWithViewModel Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class RouteListItemEditDialogWithViewModelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ViewModelと統合されたダイアログが正常に表示されること`() {
        // Given
        val viewModel = RouteListItemEditViewModel()
        val testDataId = 123L
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemEditDialogWithViewModel(
                    isVisible = true,
                    targetDataId = testDataId,
                    onDismiss = { onDismissCalled = true },
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("編集操作を選択してください")
            .assertIsDisplayed()
        
        // ViewModelにデータIDが設定されていることを確認
        assert(viewModel.targetDataId == testDataId)
    }

    @Test
    fun `ViewModelを通じた編集種別の選択が正常に動作すること`() {
        // Given
        val viewModel = RouteListItemEditViewModel()

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemEditDialogWithViewModel(
                    isVisible = true,
                    targetDataId = null,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // 削除オプションを選択
        composeTestRule
            .onNodeWithText("削除")
            .performClick()

        // Then
        assert(viewModel.selectedEditType == RouteListItemEditViewModel.EditType.Delete)
    }

    @Test
    fun `ViewModelのコールバックが正常に動作すること`() {
        // Given
        val viewModel = RouteListItemEditViewModel()
        val testDataId = 456L
        var positiveCallbackCalled = false
        var negativeCallbackCalled = false
        var callbackEditType: RouteListItemEditViewModel.EditType? = null
        var callbackDataId: Long? = null

        viewModel.onClickPositiveButtonCallback = { editType, dataId ->
            positiveCallbackCalled = true
            callbackEditType = editType
            callbackDataId = dataId
        }

        viewModel.onClickNegativeButtonCallback = { editType, dataId ->
            negativeCallbackCalled = true
            callbackEditType = editType
            callbackDataId = dataId
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemEditDialogWithViewModel(
                    isVisible = true,
                    targetDataId = testDataId,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // 削除を選択してから選択ボタンをクリック
        composeTestRule
            .onNodeWithText("削除")
            .performClick()

        composeTestRule
            .onNodeWithText("選択")
            .performClick()

        // Then
        assert(positiveCallbackCalled)
        assert(callbackEditType == RouteListItemEditViewModel.EditType.Delete)
        assert(callbackDataId == testDataId)
        assert(viewModel.selectedEditType == RouteListItemEditViewModel.EditType.Update) // clearUIData()が呼ばれることを確認

        // Reset for negative button test
        positiveCallbackCalled = false
        callbackEditType = null
        callbackDataId = null

        viewModel.updateEditType(RouteListItemEditViewModel.EditType.Update)
        viewModel.setTargetDataId(testDataId)

        composeTestRule
            .onNodeWithText("キャンセル")
            .performClick()

        assert(negativeCallbackCalled)
        assert(callbackEditType == RouteListItemEditViewModel.EditType.Update)
        assert(callbackDataId == testDataId)
    }

    @Test
    fun `ダイアログを閉じるとViewModelがクリアされること`() {
        // Given
        val viewModel = RouteListItemEditViewModel()
        val testDataId = 789L
        
        viewModel.updateEditType(RouteListItemEditViewModel.EditType.Delete)
        viewModel.setTargetDataId(testDataId)

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemEditDialogWithViewModel(
                    isVisible = true,
                    targetDataId = testDataId,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        composeTestRule
            .onNodeWithText("選択")
            .performClick()

        // Then
        assert(viewModel.selectedEditType == RouteListItemEditViewModel.EditType.Update)
        assert(viewModel.targetDataId == null)
    }
}