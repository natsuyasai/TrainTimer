package com.nyasai.traintimer.routeinfo.parts

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * FilterItemSelectDialogWithViewModel Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class FilterItemSelectDialogWithViewModelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ViewModelと統合されたダイアログが正常に表示されること`() {
        // Given
        val viewModel = FilterItemSelectViewModel()
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false)
        )
        viewModel.updateFilterItems(testItems)
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { onDismissCalled = true },
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("表示対象を選択してください")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("普通 - 新宿")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("快速 - 池袋")
            .assertIsDisplayed()
    }

    @Test
    fun `ViewModelを通じたチェックボックスの切り替えが正常に動作すること`() {
        // Given
        val viewModel = FilterItemSelectViewModel()
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false)
        )
        viewModel.updateFilterItems(testItems)

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // 最初のチェックボックスをクリック（true -> false）
        composeTestRule
            .onNodeWithText("普通 - 新宿")
            .performClick()

        // Then
        assert(!viewModel.filterItemsState[0].isShow) // ViewModelの状態が更新されていることを確認
        assert(!viewModel.filterItemList[0].isShow) // 既存リストとの同期も確認
    }

    @Test
    fun `ViewModelのコールバックが正常に動作すること`() {
        // Given
        val viewModel = FilterItemSelectViewModel()
        var positiveCallbackCalled = false
        var negativeCallbackCalled = false

        viewModel.onClickPositiveButtonCallback = { positiveCallbackCalled = true }
        viewModel.onClickNegativeButtonCallback = { negativeCallbackCalled = true }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // OKボタンをクリック
        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        // Then
        assert(positiveCallbackCalled)

        // Reset for negative button test
        positiveCallbackCalled = false

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        assert(negativeCallbackCalled)
    }

    @Test
    fun `複数のアイテムの状態変更が正常に動作すること`() {
        // Given
        val viewModel = FilterItemSelectViewModel()
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false),
            FilterInfo(3L, 100L, "特急 - 横浜", true)
        )
        viewModel.updateFilterItems(testItems)

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // 複数のチェックボックスを操作
        composeTestRule
            .onNodeWithText("普通 - 新宿")
            .performClick() // true -> false

        composeTestRule
            .onNodeWithText("快速 - 池袋")
            .performClick() // false -> true

        // Then
        assert(!viewModel.filterItemsState[0].isShow)
        assert(viewModel.filterItemsState[1].isShow)
        assert(viewModel.filterItemsState[2].isShow) // 変更されていない
        
        // 既存リストとの同期確認
        assert(!viewModel.filterItemList[0].isShow)
        assert(viewModel.filterItemList[1].isShow)
        assert(viewModel.filterItemList[2].isShow)
    }

    @Test
    fun `空のViewModelでも正常に表示されること`() {
        // Given
        val viewModel = FilterItemSelectViewModel()

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialogWithViewModel(
                    isVisible = true,
                    onDismiss = { },
                    viewModel = viewModel
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("表示対象を選択してください")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("表示対象がありません")
            .assertIsDisplayed()
    }
}