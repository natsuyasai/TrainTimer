package com.nyasai.traintimer.routelist

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteListItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * 新しいRouteListScreenStateのテストクラス
 */
class RouteListScreenStateTest {

    private lateinit var mockDatabase: RouteDatabaseDao
    private lateinit var mockApplication: Application
    private lateinit var screenState: RouteListScreenState

    @BeforeEach
    fun setUp() {
        mockDatabase = mock()
        mockApplication = mock()
        
        // Mockの戻り値を設定
        whenever(mockDatabase.getAllRouteListItems()).thenReturn(MutableLiveData<List<RouteListItem>>())
        
        screenState = RouteListScreenState(mockDatabase, mockApplication)
    }

    @Test
    fun `初期状態でダイアログが全て非表示`() {
        // Then
        assertFalse(screenState.dialogState.showSearchDialog)
        assertFalse(screenState.dialogState.showStationSelectDialog)
        assertFalse(screenState.dialogState.showDestinationSelectDialog)
        assertFalse(screenState.dialogState.showEditDialog)
        assertFalse(screenState.dialogState.showDeleteConfirmDialog)
        assertFalse(screenState.dialogState.showColorSelectDialog)
        assertNull(screenState.dialogState.selectedItem)
    }

    @Test
    fun `初期状態で検索状態が空`() {
        // Then
        assertTrue(screenState.searchState.stationOptions.isEmpty())
        assertTrue(screenState.searchState.destinationOptions.isEmpty())
        assertEquals("", screenState.searchState.currentStationName)
        assertEquals("", screenState.searchState.searchStationName)
        assertEquals("", screenState.searchState.selectedStationItem)
        assertEquals("", screenState.searchState.selectedDestinationItem)
    }

    @Test
    fun `初期状態でドラッグ状態が非アクティブ`() {
        // Then
        assertFalse(screenState.dragDropState.isDragging)
        assertEquals(0f, screenState.dragDropState.draggedDistance)
        assertNull(screenState.dragDropState.initialDraggedIndex)
        assertNull(screenState.dragDropState.currentDragOverIndex)
    }

    @Test
    fun `searchDialog表示と非表示が正常に動作する`() {
        // When - 表示
        screenState.dialogActions.showSearchDialog()
        
        // Then
        assertTrue(screenState.dialogState.showSearchDialog)
        
        // When - 非表示
        screenState.dialogActions.hideSearchDialog()
        
        // Then
        assertFalse(screenState.dialogState.showSearchDialog)
    }

    @Test
    fun `stationSelectDialog表示と非表示が正常に動作する`() {
        // When - 表示
        screenState.dialogActions.showStationSelectDialog()
        
        // Then
        assertTrue(screenState.dialogState.showStationSelectDialog)
        
        // When - 非表示
        screenState.dialogActions.hideStationSelectDialog()
        
        // Then
        assertFalse(screenState.dialogState.showStationSelectDialog)
    }

    @Test
    fun `dragDropActions_startDragが正常に動作する`() {
        // Given
        val testIndex = 3
        
        // When
        screenState.dragDropActions.startDrag(testIndex)
        
        // Then
        assertTrue(screenState.dragDropState.isDragging)
        assertEquals(testIndex, screenState.dragDropState.initialDraggedIndex)
        assertEquals(testIndex, screenState.dragDropState.currentDragOverIndex)
        assertEquals(0f, screenState.dragDropState.draggedDistance)
    }

    @Test
    fun `dragDropActions_updateDragが正常に動作する`() {
        // Given
        val testDistance = 150f
        val testDragOverIndex = 5
        screenState.dragDropActions.startDrag(2)
        
        // When
        screenState.dragDropActions.updateDrag(testDistance, testDragOverIndex)
        
        // Then
        assertEquals(testDistance, screenState.dragDropState.draggedDistance)
        assertEquals(testDragOverIndex, screenState.dragDropState.currentDragOverIndex)
    }

    @Test
    fun `dragDropActions_resetDragStateが正常に動作する`() {
        // Given
        screenState.dragDropActions.startDrag(1)
        screenState.dragDropActions.updateDrag(100f, 3)
        
        // When
        screenState.dragDropActions.resetDragState()
        
        // Then
        assertFalse(screenState.dragDropState.isDragging)
        assertEquals(0f, screenState.dragDropState.draggedDistance)
        assertNull(screenState.dragDropState.initialDraggedIndex)
        assertNull(screenState.dragDropState.currentDragOverIndex)
    }

    @Test
    fun `selectedItemの設定と取得が正常に動作する`() {
        // Given
        val testItem = RouteListItem().apply {
            dataId = 123L
            routeName = "テスト路線"
            stationName = "テスト駅"
        }
        
        // When
        screenState.updateSelectedItem(testItem)
        
        // Then
        assertEquals(testItem, screenState.dialogState.selectedItem)
    }

    @Test
    fun `colorUpdateTriggerのインクリメントが正常に動作する`() {
        // Given
        val initialValue = screenState.colorUpdateTrigger
        
        // When
        screenState.incrementColorUpdateTrigger()
        
        // Then
        assertEquals(initialValue + 1, screenState.colorUpdateTrigger)
    }
}