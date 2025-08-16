package com.nyasai.traintimer.commonparts

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nyasai.traintimer.ui.theme.TrainTimerTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.os.Looper

/**
 * CommonLoadingCompose UIテスト
 */
@RunWith(AndroidJUnit4::class)
class CommonLoadingComposeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: CommonLoadingViewModel

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        
        // メインスレッドでViewModelを初期化
        composeTestRule.runOnUiThread {
            viewModel = CommonLoadingViewModel()
        }
    }

    @Test
    fun `ローディングが非表示の時は何も表示されないこと`() {
        composeTestRule.setContent {
            TrainTimerTheme {
                CommonLoadingCompose(viewModel = viewModel)
            }
        }

        // ローディングコンテンツが存在しないことを確認
        composeTestRule
            .onNodeWithText("読み込み中")
            .assertDoesNotExist()
    }

    @Test
    fun `ローディングが表示されること`() {
        // Given
        composeTestRule.runOnUiThread {
            viewModel.showLoading("読み込み中")
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                CommonLoadingCompose(viewModel = viewModel)
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("読み込み中")
            .assertIsDisplayed()
    }

    @Test
    fun `進捗表示が正しく動作すること`() {
        // Given
        composeTestRule.runOnUiThread {
            viewModel.showLoading("データ処理中")
            viewModel.incrementMaxCountFromBackgroundTask(10)
            viewModel.incrementCurrentCountFromBackgroundTask(5)
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                CommonLoadingCompose(viewModel = viewModel)
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
        // Given
        composeTestRule.runOnUiThread {
            viewModel.showLoading("読み込み中")
        }

        // When
        composeTestRule.setContent {
            TrainTimerTheme {
                CommonLoadingCompose(viewModel = viewModel)
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