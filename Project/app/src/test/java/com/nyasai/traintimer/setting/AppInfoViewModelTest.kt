package com.nyasai.traintimer.setting

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * AppInfoViewModelのテスト
 */
class AppInfoViewModelTest {

    private lateinit var viewModel: AppInfoViewModel

    @BeforeEach
    fun setUp() {
        viewModel = AppInfoViewModel()
    }

    @Test
    fun `アプリタイトルが正しく設定されていること`() {
        // Then
        assertEquals("時刻表", viewModel.appTitle)
    }

    @Test
    fun `Twitter情報が正しく設定されていること`() {
        // Then
        assertEquals("Twitter:natsuyasai7", viewModel.twitterInfo)
    }

    @Test
    fun `閉じるボタンコールバックが正常に動作すること`() {
        // Given
        var callbackCalled = false
        viewModel.onCloseCallback = { callbackCalled = true }

        // When
        viewModel.onCloseButtonClick()

        // Then
        assertTrue(callbackCalled)
    }

    @Test
    fun `コールバックが設定されていない場合でもエラーが発生しないこと`() {
        // When & Then (例外が発生しないことを確認)
        assertDoesNotThrow {
            viewModel.onCloseButtonClick()
        }
    }
}