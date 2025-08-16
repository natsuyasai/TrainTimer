package com.nyasai.traintimer.routelist.parts

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * RouteListItemEditDialog Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class RouteListItemEditDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ダイアログが正常に表示されること`() {
        // Given
        var selectedEditType = RouteListItemEditViewModel.EditType.Update
        var onDismissCalled = false
        var onPositiveClickCalled = false
        var onNegativeClickCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemEditDialog(
                    isVisible = true,
                    selectedEditType = selectedEditType,
                    onEditTypeChange = { selectedEditType = it },
                    onPositiveClick = { onPositiveClickCalled = true },
                    onNegativeClick = { onNegativeClickCalled = true },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("編集操作を選択してください")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("更新")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("削除")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("選択")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("キャンセル")
            .assertIsDisplayed()
    }

    @Test
    fun `ラジオボタンの選択が正常に動作すること`() {
        // Given
        var selectedEditType = RouteListItemEditViewModel.EditType.Update

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemEditDialog(
                    isVisible = true,
                    selectedEditType = selectedEditType,
                    onEditTypeChange = { selectedEditType = it },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // 削除ラジオボタンをクリック
        composeTestRule
            .onNodeWithText("削除")
            .performClick()

        // Then
        assert(selectedEditType == RouteListItemEditViewModel.EditType.Delete)
    }

    @Test
    fun `選択ボタンが正常に動作すること`() {
        // Given
        var onPositiveClickCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemEditDialog(
                    isVisible = true,
                    selectedEditType = RouteListItemEditViewModel.EditType.Update,
                    onEditTypeChange = { },
                    onPositiveClick = { onPositiveClickCalled = true },
                    onNegativeClick = { },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("選択")
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
                RouteListItemEditDialog(
                    isVisible = true,
                    selectedEditType = RouteListItemEditViewModel.EditType.Update,
                    onEditTypeChange = { },
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
                RouteListItemEditDialog(
                    isVisible = false,
                    selectedEditType = RouteListItemEditViewModel.EditType.Update,
                    onEditTypeChange = { },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("編集操作を選択してください")
            .assertDoesNotExist()
    }

    @Test
    fun `デフォルトで更新が選択されていること`() {
        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                RouteListItemEditDialog(
                    isVisible = true,
                    selectedEditType = RouteListItemEditViewModel.EditType.Update,
                    onEditTypeChange = { },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // Then - 更新のラジオボタンが選択されていることを確認
        composeTestRule
            .onNodeWithText("更新")
            .assertIsSelected()
        
        composeTestRule
            .onNodeWithText("削除")
            .assertIsNotSelected()
    }
}