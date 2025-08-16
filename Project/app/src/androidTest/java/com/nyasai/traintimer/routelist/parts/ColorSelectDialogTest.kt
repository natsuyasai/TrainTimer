package com.nyasai.traintimer.routelist.parts

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ColorSelectDialogのUIテスト
 */
@RunWith(AndroidJUnit4::class)
class ColorSelectDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun colorSelectDialog_表示されること() {
        // Given
        var selectedColor: Int? = null
        var dismissed = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                ColorSelectDialog(
                    isVisible = true,
                    currentColor = Color.Red.toArgb(),
                    onColorSelected = { selectedColor = it },
                    onDismiss = { dismissed = true }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("表示色を選択").assertIsDisplayed()
        composeTestRule.onNodeWithText("色なし（デフォルト）").assertIsDisplayed()
        composeTestRule.onNodeWithText("キャンセル").assertIsDisplayed()
        composeTestRule.onNodeWithText("適用").assertIsDisplayed()
    }

    @Test
    fun colorSelectDialog_非表示時は何も表示されないこと() {
        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                ColorSelectDialog(
                    isVisible = false,
                    currentColor = null,
                    onColorSelected = { },
                    onDismiss = { }
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("表示色を選択").assertDoesNotExist()
    }

    @Test
    fun colorSelectDialog_キャンセルボタンでdismissされること() {
        // Given
        var dismissed = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                ColorSelectDialog(
                    isVisible = true,
                    currentColor = null,
                    onColorSelected = { },
                    onDismiss = { dismissed = true }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("キャンセル").performClick()

        // Then
        assert(dismissed)
    }

    @Test
    fun colorSelectDialog_適用ボタンで選択色が返されること() {
        // Given
        var selectedColor: Int? = null
        var dismissed = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                ColorSelectDialog(
                    isVisible = true,
                    currentColor = Color.Blue.toArgb(),
                    onColorSelected = { selectedColor = it },
                    onDismiss = { dismissed = true }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("適用").performClick()

        // Then
        assert(selectedColor == Color.Blue.toArgb())
        assert(dismissed)
    }

    @Test
    fun colorSelectDialog_色なしオプションをクリックできること() {
        // Given
        var selectedColor: Int? = Color.Red.toArgb()
        var dismissed = false

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                ColorSelectDialog(
                    isVisible = true,
                    currentColor = Color.Red.toArgb(),
                    onColorSelected = { selectedColor = it },
                    onDismiss = { dismissed = true }
                )
            }
        }

        // When
        composeTestRule.onNodeWithText("色なし（デフォルト）").performClick()
        composeTestRule.onNodeWithText("適用").performClick()

        // Then
        assert(selectedColor == null)
        assert(dismissed)
    }
}