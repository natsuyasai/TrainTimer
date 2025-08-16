package com.nyasai.traintimer.routelist.parts

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * RouteListItemEditViewModelのテスト
 */
class RouteListItemEditViewModelTest {

    private lateinit var viewModel: RouteListItemEditViewModel

    @BeforeEach
    fun setUp() {
        viewModel = RouteListItemEditViewModel()
    }

    @Test
    fun `初期状態ではUpdateが選択されていること`() {
        // Then
        Assertions.assertEquals(
            RouteListItemEditViewModel.EditType.Update,
            viewModel.selectedEditType
        )
    }

    @Test
    fun `編集種別の更新が正常に動作すること`() {
        // When
        viewModel.updateEditType(RouteListItemEditViewModel.EditType.Delete)

        // Then
        Assertions.assertEquals(
            RouteListItemEditViewModel.EditType.Delete,
            viewModel.selectedEditType
        )
    }

    @Test
    fun `データIDの設定が正常に動作すること`() {
        // Given
        val testDataId = 123L

        // When
        viewModel.setTargetDataId(testDataId)

        // Then
        Assertions.assertEquals(testDataId, viewModel.targetDataId)
    }

    @Test
    fun `Positiveボタンコールバックが正常に動作すること`() {
        // Given
        var callbackEditType: RouteListItemEditViewModel.EditType? = null
        var callbackDataId: Long? = null
        val testDataId = 456L

        viewModel.onClickPositiveButtonCallback = { editType, dataId ->
            callbackEditType = editType
            callbackDataId = dataId
        }
        viewModel.updateEditType(RouteListItemEditViewModel.EditType.Delete)
        viewModel.setTargetDataId(testDataId)

        // When
        viewModel.onPositiveButtonClick()

        // Then
        Assertions.assertEquals(RouteListItemEditViewModel.EditType.Delete, callbackEditType)
        Assertions.assertEquals(testDataId, callbackDataId)
    }

    @Test
    fun `Negativeボタンコールバックが正常に動作すること`() {
        // Given
        var callbackEditType: RouteListItemEditViewModel.EditType? = null
        var callbackDataId: Long? = null
        val testDataId = 789L

        viewModel.onClickNegativeButtonCallback = { editType, dataId ->
            callbackEditType = editType
            callbackDataId = dataId
        }
        viewModel.updateEditType(RouteListItemEditViewModel.EditType.Update)
        viewModel.setTargetDataId(testDataId)

        // When
        viewModel.onNegativeButtonClick()

        // Then
        Assertions.assertEquals(RouteListItemEditViewModel.EditType.Update, callbackEditType)
        Assertions.assertEquals(testDataId, callbackDataId)
    }

    @Test
    fun `データクリア時に初期状態に戻ること`() {
        // Given
        viewModel.updateEditType(RouteListItemEditViewModel.EditType.Delete)
        viewModel.setTargetDataId(999L)

        // When
        viewModel.clearUIData()

        // Then
        Assertions.assertEquals(
            RouteListItemEditViewModel.EditType.Update,
            viewModel.selectedEditType
        )
        Assertions.assertNull(viewModel.targetDataId)
    }

    @Test
    fun `コールバックが設定されていない場合でもエラーが発生しないこと`() {
        // When & Then (例外が発生しないことを確認)
        Assertions.assertDoesNotThrow {
            viewModel.onPositiveButtonClick()
            viewModel.onNegativeButtonClick()
        }
    }
}