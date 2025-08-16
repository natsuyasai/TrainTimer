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
 * FilterItemSelectDialog Composeコンポーネントのテスト
 */
@RunWith(AndroidJUnit4::class)
class FilterItemSelectDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `ダイアログが正常に表示されること`() {
        // Given
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false),
            FilterInfo(3L, 100L, "特急 - 横浜", true)
        )
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialog(
                    isVisible = true,
                    filterItems = testItems,
                    onItemToggle = { },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { onDismissCalled = true }
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
        
        composeTestRule
            .onNodeWithText("特急 - 横浜")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("OK")
            .assertIsDisplayed()
        
        composeTestRule
            .onNodeWithText("Cancel")
            .assertIsDisplayed()
    }

    @Test
    fun `チェックボックスの状態が正しく表示されること`() {
        // Given
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false)
        )

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialog(
                    isVisible = true,
                    filterItems = testItems,
                    onItemToggle = { },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // Then - チェックボックスの状態を確認
        composeTestRule
            .onNodeWithText("普通 - 新宿")
            .assertIsOn()
        
        composeTestRule
            .onNodeWithText("快速 - 池袋")
            .assertIsOff()
    }

    @Test
    fun `チェックボックスのクリックが正常に動作すること`() {
        // Given
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false)
        )
        var toggledIndex = -1

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialog(
                    isVisible = true,
                    filterItems = testItems,
                    onItemToggle = { index -> toggledIndex = index },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // 最初のチェックボックスをクリック
        composeTestRule
            .onNodeWithText("普通 - 新宿")
            .performClick()

        // Then
        assert(toggledIndex == 0)
    }

    @Test
    fun `OKボタンが正常に動作すること`() {
        // Given
        var onPositiveClickCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialog(
                    isVisible = true,
                    filterItems = emptyList(),
                    onItemToggle = { },
                    onPositiveClick = { onPositiveClickCalled = true },
                    onNegativeClick = { },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("OK")
            .performClick()

        // Then
        assert(onPositiveClickCalled)
        assert(onDismissCalled)
    }

    @Test
    fun `Cancelボタンが正常に動作すること`() {
        // Given
        var onNegativeClickCalled = false
        var onDismissCalled = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialog(
                    isVisible = true,
                    filterItems = emptyList(),
                    onItemToggle = { },
                    onPositiveClick = { },
                    onNegativeClick = { onNegativeClickCalled = true },
                    onDismiss = { onDismissCalled = true }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Cancel")
            .performClick()

        // Then
        assert(onNegativeClickCalled)
        assert(onDismissCalled)
    }

    @Test
    fun `空のリストの場合適切なメッセージが表示されること`() {
        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialog(
                    isVisible = true,
                    filterItems = emptyList(),
                    onItemToggle = { },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("表示対象がありません")
            .assertIsDisplayed()
    }

    @Test
    fun `ダイアログが非表示の時は何も表示されないこと`() {
        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                FilterItemSelectDialog(
                    isVisible = false,
                    filterItems = listOf(
                        FilterInfo(1L, 100L, "普通 - 新宿", true)
                    ),
                    onItemToggle = { },
                    onPositiveClick = { },
                    onNegativeClick = { },
                    onDismiss = { }
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("表示対象を選択してください")
            .assertDoesNotExist()
    }
}