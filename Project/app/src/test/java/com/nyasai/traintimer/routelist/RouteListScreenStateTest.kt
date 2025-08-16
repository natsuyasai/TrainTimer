package com.nyasai.traintimer.routelist

import com.nyasai.traintimer.database.RouteListItem
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * RouteListScreenStateのテストクラス
 */
class RouteListScreenStateTest {

    private fun createMockRouteListItem(
        dataId: Long,
        routeName: String,
        stationName: String,
        destination: String,
        sortIndex: Long
    ): RouteListItem {
        return mock<RouteListItem>().apply {
            this.dataId = dataId
            this.routeName = routeName
            this.stationName = stationName
            this.destination = destination
            this.sortIndex = sortIndex
        }
    }

    @Test
    fun `DialogState_初期状態で全てのダイアログが非表示`() {
        // Given
        val dialogState = DialogState()

        // Then
        assertFalse(dialogState.showSearchDialog)
        assertFalse(dialogState.showStationSelectDialog)
        assertFalse(dialogState.showDestinationSelectDialog)
        assertFalse(dialogState.showEditDialog)
        assertFalse(dialogState.showDeleteConfirmDialog)
        assertFalse(dialogState.showColorSelectDialog)
        assertNull(dialogState.selectedItem)
    }

    @Test
    fun `SearchState_初期状態で空の状態`() {
        // Given
        val searchState = SearchState()

        // Then
        assertTrue(searchState.stationOptions.isEmpty())
        assertTrue(searchState.destinationOptions.isEmpty())
        assertEquals("", searchState.currentStationName)
        assertEquals("", searchState.searchStationName)
        assertEquals("", searchState.selectedStationItem)
        assertEquals("", searchState.selectedDestinationItem)
    }

    @Test
    fun `DragDropState_初期状態で非ドラッグ状態`() {
        // Given
        val dragDropState = DragDropState()

        // Then
        assertFalse(dragDropState.isDragging)
        assertEquals(0f, dragDropState.draggedDistance)
        assertNull(dragDropState.initialDraggedIndex)
        assertNull(dragDropState.currentDragOverIndex)
    }

    @Test
    fun `RouteListScreenState_dialogActions_showSearchDialog_正しく状態が更新される`() {
        // Given
        val screenState = RouteListScreenState()

        // When
        screenState.dialogActions.showSearchDialog()

        // Then
        assertTrue(screenState.dialogState.showSearchDialog)
    }

    @Test
    fun `RouteListScreenState_dialogActions_hideSearchDialog_正しく状態が更新される`() {
        // Given
        val screenState = RouteListScreenState()
        screenState.dialogActions.showSearchDialog() // まず表示

        // When
        screenState.dialogActions.hideSearchDialog()

        // Then
        assertFalse(screenState.dialogState.showSearchDialog)
    }

    @Test
    fun `RouteListScreenState_dialogActions_showEditDialog_アイテムと状態が正しく設定される`() {
        // Given
        val screenState = RouteListScreenState()
        val testItem = createMockRouteListItem(1L, "山手線", "新宿駅", "池袋方面", 1L)

        // When
        screenState.dialogActions.showEditDialog(testItem)

        // Then
        assertTrue(screenState.dialogState.showEditDialog)
        assertEquals(testItem, screenState.dialogState.selectedItem)
    }

    @Test
    fun `RouteListScreenState_searchActions_updateStationOptions_正しく更新される`() {
        // Given
        val screenState = RouteListScreenState()
        val testOptions = mapOf("新宿駅" to "shinjuku", "池袋駅" to "ikebukuro")

        // When
        screenState.searchActions.updateStationOptions(testOptions)

        // Then
        assertEquals(testOptions, screenState.searchState.stationOptions)
    }

    @Test
    fun `RouteListScreenState_searchActions_clearSearchState_状態がリセットされる`() {
        // Given
        val screenState = RouteListScreenState()
        screenState.searchActions.updateCurrentStationName("新宿駅")
        screenState.searchActions.updateSearchStationName("池袋駅")

        // When
        screenState.searchActions.clearSearchState()

        // Then
        assertEquals("", screenState.searchState.currentStationName)
        assertEquals("", screenState.searchState.searchStationName)
        assertTrue(screenState.searchState.stationOptions.isEmpty())
    }

    @Test
    fun `RouteListScreenState_dragDropActions_startDrag_正しくドラッグが開始される`() {
        // Given
        val screenState = RouteListScreenState()
        val testIndex = 2

        // When
        screenState.dragDropActions.startDrag(testIndex)

        // Then
        assertTrue(screenState.dragDropState.isDragging)
        assertEquals(testIndex, screenState.dragDropState.initialDraggedIndex)
        assertEquals(testIndex, screenState.dragDropState.currentDragOverIndex)
        assertEquals(0f, screenState.dragDropState.draggedDistance)
    }

    @Test
    fun `RouteListScreenState_dragDropActions_updateDrag_正しく状態が更新される`() {
        // Given
        val screenState = RouteListScreenState()
        screenState.dragDropActions.startDrag(0)
        val testDistance = 50.0f
        val testDragOverIndex = 3

        // When
        screenState.dragDropActions.updateDrag(testDistance, testDragOverIndex)

        // Then
        assertEquals(testDistance, screenState.dragDropState.draggedDistance)
        assertEquals(testDragOverIndex, screenState.dragDropState.currentDragOverIndex)
    }

    @Test
    fun `RouteListScreenState_dragDropActions_resetDragState_状態がリセットされる`() {
        // Given
        val screenState = RouteListScreenState()
        screenState.dragDropActions.startDrag(0)
        screenState.dragDropActions.updateDrag(50.0f, 2)

        // When
        screenState.dragDropActions.resetDragState()

        // Then
        assertFalse(screenState.dragDropState.isDragging)
        assertEquals(0f, screenState.dragDropState.draggedDistance)
        assertNull(screenState.dragDropState.initialDraggedIndex)
        assertNull(screenState.dragDropState.currentDragOverIndex)
    }

    @Test
    fun `RouteListScreenState_incrementColorUpdateTrigger_トリガー値が増加する`() {
        // Given
        val screenState = RouteListScreenState()
        val initialTrigger = screenState.colorUpdateTrigger

        // When
        screenState.incrementColorUpdateTrigger()

        // Then
        assertEquals(initialTrigger + 1, screenState.colorUpdateTrigger)
    }

    @Test
    fun `RouteListScreenState_updateSelectedItem_選択アイテムが更新される`() {
        // Given
        val screenState = RouteListScreenState()
        val testItem = createMockRouteListItem(1L, "山手線", "新宿駅", "池袋方面", 1L)

        // When
        screenState.updateSelectedItem(testItem)

        // Then
        assertEquals(testItem, screenState.dialogState.selectedItem)
    }

    @Test
    fun `RouteListScreenState_updateLocalRouteListItem_正しくアイテムが更新される`() {
        // Given
        val screenState = RouteListScreenState()
        val item1 = createMockRouteListItem(1L, "山手線", "新宿駅", "池袋方面", 1L)
        val item2 = createMockRouteListItem(2L, "中央線", "東京駅", "高尾方面", 2L)
        val originalList = listOf(item1, item2)
        
        screenState.localRouteList = originalList

        val updatedItem1 = createMockRouteListItem(1L, "山手線", "新宿駅", "上野方面", 1L) // destinationを変更

        // When
        screenState.updateLocalRouteListItem(updatedItem1)

        // Then
        assertEquals(2, screenState.localRouteList.size)
        assertEquals(updatedItem1, screenState.localRouteList[0])
        assertEquals(item2, screenState.localRouteList[1])
    }

    @Test
    fun `RouteListScreenState_updateLocalRouteListItem_存在しないアイテムの場合変更されない`() {
        // Given
        val screenState = RouteListScreenState()
        val item1 = createMockRouteListItem(1L, "山手線", "新宿駅", "池袋方面", 1L)
        val originalList = listOf(item1)
        
        screenState.localRouteList = originalList

        val nonExistentItem = createMockRouteListItem(999L, "存在しない路線", "存在しない駅", "存在しない方面", 999L)

        // When
        screenState.updateLocalRouteListItem(nonExistentItem)

        // Then
        assertEquals(1, screenState.localRouteList.size)
        assertEquals(item1, screenState.localRouteList[0])
    }
}