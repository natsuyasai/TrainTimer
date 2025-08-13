package com.nyasai.traintimer.routelist

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * RouteListItemDeleteConfirmViewModelのテスト
 */
class RouteListItemDeleteConfirmViewModelTest {

    private lateinit var viewModel: RouteListItemDeleteConfirmViewModel

    @BeforeEach
    fun setUp() {
        viewModel = RouteListItemDeleteConfirmViewModel()
    }

    @Test
    fun `初期状態ではデータIDがnullであること`() {
        // Then
        assertNull(viewModel.targetDataId)
    }

    @Test
    fun `データIDの設定が正常に動作すること`() {
        // Given
        val testDataId = 123L

        // When
        viewModel.setTargetDataId(testDataId)

        // Then
        assertEquals(testDataId, viewModel.targetDataId)
    }

    @Test
    fun `Positiveボタンコールバックが正常に動作すること`() {
        // Given
        var callbackDataId: Long? = null
        val testDataId = 456L

        viewModel.onClickPositiveButtonCallback = { dataId ->
            callbackDataId = dataId
        }
        viewModel.setTargetDataId(testDataId)

        // When
        viewModel.onPositiveButtonClick()

        // Then
        assertEquals(testDataId, callbackDataId)
    }

    @Test
    fun `Negativeボタンコールバックが正常に動作すること`() {
        // Given
        var callbackDataId: Long? = null
        val testDataId = 789L

        viewModel.onClickNegativeButtonCallback = { dataId ->
            callbackDataId = dataId
        }
        viewModel.setTargetDataId(testDataId)

        // When
        viewModel.onNegativeButtonClick()

        // Then
        assertEquals(testDataId, callbackDataId)
    }

    @Test
    fun `データクリア時にnullに戻ること`() {
        // Given
        viewModel.setTargetDataId(999L)

        // When
        viewModel.clearUIData()

        // Then
        assertNull(viewModel.targetDataId)
    }

    @Test
    fun `コールバックが設定されていない場合でもエラーが発生しないこと`() {
        // When & Then (例外が発生しないことを確認)
        assertDoesNotThrow {
            viewModel.onPositiveButtonClick()
            viewModel.onNegativeButtonClick()
        }
    }

    @Test
    fun `nullのデータIDを設定できること`() {
        // Given
        viewModel.setTargetDataId(123L)

        // When
        viewModel.setTargetDataId(null)

        // Then
        assertNull(viewModel.targetDataId)
    }
}