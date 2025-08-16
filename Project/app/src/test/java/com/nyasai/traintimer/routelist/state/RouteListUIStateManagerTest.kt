package com.nyasai.traintimer.routelist.state

import com.nyasai.traintimer.database.RouteListItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * RouteListUIStateManagerのテストクラス
 */
class RouteListUIStateManagerTest {

    private lateinit var uiStateManager: RouteListUIStateManager

    @BeforeEach
    fun setUp() {
        uiStateManager = RouteListUIStateManager()
    }

    @Test
    fun `初期状態でドラッグが無効`() {
        // Then
        assertFalse(uiStateManager.isDragging)
    }

    @Test
    fun `ドラッグ開始と終了が正常に動作する`() {
        // When
        uiStateManager.startDragging()
        
        // Then
        assertTrue(uiStateManager.isDragging)
        
        // When
        uiStateManager.stopDragging()
        
        // Then
        assertFalse(uiStateManager.isDragging)
    }

    @Test
    fun `初期状態で検索ダイアログが非表示`() {
        // Then
        assertFalse(uiStateManager.showSearchDialog)
    }

    @Test
    fun `検索ダイアログの表示と非表示が正常に動作する`() {
        // When
        uiStateManager.showSearchDialog()
        
        // Then
        assertTrue(uiStateManager.showSearchDialog)
        
        // When
        uiStateManager.hideSearchDialog()
        
        // Then
        assertFalse(uiStateManager.showSearchDialog)
    }

    @Test
    fun `初期状態でステーションセレクトダイアログが非表示`() {
        // Then
        assertFalse(uiStateManager.showStationSelectDialog)
    }

    @Test
    fun `ステーションセレクトダイアログの表示と非表示が正常に動作する`() {
        // When
        uiStateManager.showStationSelectDialog()
        
        // Then
        assertTrue(uiStateManager.showStationSelectDialog)
        
        // When
        uiStateManager.hideStationSelectDialog()
        
        // Then
        assertFalse(uiStateManager.showStationSelectDialog)
    }

    @Test
    fun `初期状態でローディングが無効`() {
        // Then
        assertFalse(uiStateManager.isLoading)
    }

    @Test
    fun `ローディング開始と終了が正常に動作する`() {
        // When
        uiStateManager.startLoading()
        
        // Then
        assertTrue(uiStateManager.isLoading)
        
        // When
        uiStateManager.stopLoading()
        
        // Then
        assertFalse(uiStateManager.isLoading)
    }

    @Test
    fun `ローカルルートリストの更新が正常に動作する`() {
        // Given
        val testItems = listOf(
            RouteListItem().apply {
                dataId = 1L
                routeName = "テスト路線1"
            },
            RouteListItem().apply {
                dataId = 2L
                routeName = "テスト路線2"
            }
        )
        
        // When
        uiStateManager.updateLocalRouteList(testItems)
        
        // Then
        assertEquals(2, uiStateManager.localRouteList.size)
        assertEquals("テスト路線1", uiStateManager.localRouteList[0].routeName)
        assertEquals("テスト路線2", uiStateManager.localRouteList[1].routeName)
    }

    @Test
    fun `ローカルルートリストのクリアが正常に動作する`() {
        // Given
        val testItems = listOf(
            RouteListItem().apply { dataId = 1L }
        )
        uiStateManager.updateLocalRouteList(testItems)
        
        // When
        uiStateManager.clearLocalRouteList()
        
        // Then
        assertTrue(uiStateManager.localRouteList.isEmpty())
    }
}