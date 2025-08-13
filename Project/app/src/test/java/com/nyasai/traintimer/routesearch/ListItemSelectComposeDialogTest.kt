package com.nyasai.traintimer.routesearch

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * ListItemSelectComposeDialogのテスト
 */
class ListItemSelectComposeDialogTest {

    private lateinit var viewModel: ListItemSelectViewModel

    @BeforeEach
    fun setUp() {
        viewModel = ListItemSelectViewModel()
    }

    @Test
    fun `アイテムリストの更新が正常に動作すること`() {
        // Given
        val testItems = listOf("新宿駅", "渋谷駅", "東京駅")

        // When
        viewModel.updateItems(testItems)

        // Then
        assertEquals(testItems, viewModel.itemsState)
        assertArrayEquals(testItems.toTypedArray(), viewModel.getItems())
    }

    @Test
    fun `アイテム選択が正常に動作すること`() {
        // Given
        val testItems = listOf("新宿駅", "渋谷駅", "東京駅")
        val selectedItem = "渋谷駅"
        var callbackItem: String? = null

        viewModel.updateItems(testItems)
        viewModel.onSelectItem = { item -> callbackItem = item }

        // When
        viewModel.updateSelectedItem(selectedItem)

        // Then
        assertEquals(selectedItem, viewModel.selectedItemState)
        assertEquals(selectedItem, viewModel.selectItem)
        assertEquals(selectedItem, callbackItem)
    }

    @Test
    fun `データクリア時にすべてのデータが空になること`() {
        // Given
        viewModel.updateItems(listOf("新宿駅", "渋谷駅"))
        viewModel.updateSelectedItem("新宿駅")

        // When
        viewModel.clearUIData()

        // Then
        assertTrue(viewModel.itemsState.isEmpty())
        assertTrue(viewModel.getItems().isEmpty())
        assertEquals("", viewModel.selectedItemState)
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