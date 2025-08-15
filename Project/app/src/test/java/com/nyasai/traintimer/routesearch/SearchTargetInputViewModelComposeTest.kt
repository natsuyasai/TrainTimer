package com.nyasai.traintimer.routesearch

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * SearchTargetInputViewModelのテスト (Compose対応版)
 */
class SearchTargetInputViewModelComposeTest {

    private lateinit var viewModel: SearchTargetInputViewModel

    @BeforeEach
    fun setUp() {
        viewModel = SearchTargetInputViewModel()
    }

    @Test
    fun `駅名の更新が正常に動作すること`() {
        // Given
        val testStationName = "新宿駅"

        // When
        viewModel.updateStationName(testStationName)

        // Then
        assertEquals(testStationName, viewModel.stationNameState)
    }

    @Test
    fun `データクリア時に駅名が空になること`() {
        // Given
        viewModel.updateStationName("新宿駅")

        // When
        viewModel.clearUIData()

        // Then
        assertEquals("", viewModel.stationNameState)
    }

    @Test
    fun `コールバック設定が正常に動作すること`() {
        // Given
        var positiveCallbackCalled = false
        var negativeCallbackCalled = false

        viewModel.onClickPositiveButtonCallback = { positiveCallbackCalled = true }
        viewModel.onClickNegativeButtonCallback = { negativeCallbackCalled = true }

        // When
        viewModel.onClickPositiveButtonCallback?.invoke()
        viewModel.onClickNegativeButtonCallback?.invoke()

        // Then
        assertTrue(positiveCallbackCalled)
        assertTrue(negativeCallbackCalled)
    }
}