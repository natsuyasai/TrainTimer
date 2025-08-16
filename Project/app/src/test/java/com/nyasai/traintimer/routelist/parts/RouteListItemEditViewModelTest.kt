package com.nyasai.traintimer.routelist.parts

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * RouteListItemEditStateHolderのテスト
 */
class RouteListItemEditStateTest {

    private lateinit var stateHolder: RouteListItemEditStateHolder

    @BeforeEach
    fun setUp() {
        stateHolder = RouteListItemEditStateHolder()
    }

    @Test
    fun `初期状態ではUpdateが選択されていること`() {
        // Then
        Assertions.assertEquals(
            EditType.Update,
            stateHolder.state.selectedEditType
        )
    }

    @Test
    fun `編集種別の更新が正常に動作すること`() {
        // When
        stateHolder.actions.updateEditType(EditType.Delete)

        // Then
        Assertions.assertEquals(
            EditType.Delete,
            stateHolder.state.selectedEditType
        )
    }

    @Test
    fun `データIDの設定が正常に動作すること`() {
        // Given
        val testDataId = 123L

        // When
        stateHolder.actions.setTargetDataId(testDataId)

        // Then
        Assertions.assertEquals(testDataId, stateHolder.state.targetDataId)
    }

    @Test
    fun `複数の編集種別変更が正常に動作すること`() {
        // Given & When & Then
        stateHolder.actions.updateEditType(EditType.Delete)
        Assertions.assertEquals(EditType.Delete, stateHolder.state.selectedEditType)
        
        stateHolder.actions.updateEditType(EditType.SetColor)
        Assertions.assertEquals(EditType.SetColor, stateHolder.state.selectedEditType)
        
        stateHolder.actions.updateEditType(EditType.Update)
        Assertions.assertEquals(EditType.Update, stateHolder.state.selectedEditType)
    }

    @Test
    fun `状態の独立性が保たれること`() {
        // Given
        val testDataId = 999L
        
        // When
        stateHolder.actions.updateEditType(EditType.SetColor)
        stateHolder.actions.setTargetDataId(testDataId)
        
        // Then
        Assertions.assertEquals(EditType.SetColor, stateHolder.state.selectedEditType)
        Assertions.assertEquals(testDataId, stateHolder.state.targetDataId)
    }

    @Test
    fun `データクリア時に初期状態に戻ること`() {
        // Given
        stateHolder.actions.updateEditType(EditType.Delete)
        stateHolder.actions.setTargetDataId(999L)

        // When
        stateHolder.actions.clearUIData()

        // Then
        Assertions.assertEquals(
            EditType.Update,
            stateHolder.state.selectedEditType
        )
        Assertions.assertNull(stateHolder.state.targetDataId)
    }

    @Test
    fun `初期状態でtargetDataIdがnullであること`() {
        // Then
        Assertions.assertNull(stateHolder.state.targetDataId)
    }
}