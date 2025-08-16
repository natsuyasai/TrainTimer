package com.nyasai.traintimer.routelist.logic

import com.nyasai.traintimer.commonparts.LoadingState
import com.nyasai.traintimer.commonparts.loadingState
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.routelist.RouteListScreenState
import com.nyasai.traintimer.routelist.parts.EditType
import com.nyasai.traintimer.util.WakeLockManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

/**
 * EditModeManagerのテストクラス
 */
@ExperimentalCoroutinesApi
class EditModeManagerTest {

    private lateinit var mockScreenState: RouteListScreenState
    private lateinit var mockWakeLockManager: WakeLockManager
    private lateinit var editModeManager: EditModeManager
    private lateinit var testScope: TestScope

    @BeforeEach
    fun setUp() {
        mockScreenState = mock()
        mockWakeLockManager = mock()
        editModeManager = EditModeManager(mockScreenState, mockWakeLockManager)
        testScope = TestScope(UnconfinedTestDispatcher())
    }

    @Test
    fun `handleEditModeToggleが正常に呼び出される`() {
        // When
        editModeManager.handleEditModeToggle()
        
        // Then
        verify(mockScreenState).switchEditMode()
    }

    @Test
    fun `handleColorUpdateが正常に呼び出される`() {
        // Given
        val testItem = RouteListItem().apply { dataId = 123L }
        val testColor = 0xFF0000
        
        // When
        editModeManager.handleColorUpdate(testItem, testColor)
        
        // Then
        assertEquals(testColor, testItem.displayColor)
        verify(mockScreenState).updateRouteListItemColor(123L, testColor)
    }

    @Test
    fun `handleEditDialogPositiveClick_Update時に更新処理が呼び出される`() {
        // Given
        val testItem = RouteListItem().apply { dataId = 456L }
        var showDeleteDialogCalled = false
        var showColorDialogCalled = false
        var hideEditDialogCalled = false
        
        whenever(runBlocking { mockScreenState.updateRouteInfo(any(), any(), any()) }).thenReturn(true)
        
        // When
        editModeManager.handleEditDialogPositiveClick(
            EditType.Update,
            testScope,
            LoadingState(),
            testItem,
            { showDeleteDialogCalled = true },
            { showColorDialogCalled = true },
            { hideEditDialogCalled = true }
        )
        
        // Then
        assertFalse(showDeleteDialogCalled)
        assertFalse(showColorDialogCalled)
        assertTrue(hideEditDialogCalled)
    }

    @Test
    fun `handleEditDialogPositiveClick_SetColor時に色選択ダイアログが表示される`() {
        // Given
        val testItem = RouteListItem().apply { dataId = 789L }
        var showDeleteDialogCalled = false
        var showColorDialogCalled = false
        var hideEditDialogCalled = false
        
        // When
        editModeManager.handleEditDialogPositiveClick(
            EditType.SetColor,
            testScope,
            LoadingState(),
            testItem,
            { showDeleteDialogCalled = true },
            { showColorDialogCalled = true },
            { hideEditDialogCalled = true }
        )
        
        // Then
        assertFalse(showDeleteDialogCalled)
        assertTrue(showColorDialogCalled)
        assertTrue(hideEditDialogCalled)
    }

    @Test
    fun `handleEditDialogPositiveClick_Delete時に削除確認ダイアログが表示される`() {
        // Given
        val testItem = RouteListItem().apply { dataId = 321L }
        var showDeleteDialogCalled = false
        var showColorDialogCalled = false
        var hideEditDialogCalled = false
        
        // When
        editModeManager.handleEditDialogPositiveClick(
            EditType.Delete,
            testScope,
            LoadingState(),
            testItem,
            { showDeleteDialogCalled = true },
            { showColorDialogCalled = true },
            { hideEditDialogCalled = true }
        )
        
        // Then
        assertTrue(showDeleteDialogCalled)
        assertFalse(showColorDialogCalled)
        assertTrue(hideEditDialogCalled)
    }

    @Test
    fun `handleEditDialogPositiveClick_None時は何もしない`() {
        // Given
        val testItem = RouteListItem().apply { dataId = 999L }
        var showDeleteDialogCalled = false
        var showColorDialogCalled = false
        var hideEditDialogCalled = false
        
        // When
        editModeManager.handleEditDialogPositiveClick(
            EditType.None,
            testScope,
            LoadingState(),
            testItem,
            { showDeleteDialogCalled = true },
            { showColorDialogCalled = true },
            { hideEditDialogCalled = true }
        )
        
        // Then
        assertFalse(showDeleteDialogCalled)
        assertFalse(showColorDialogCalled)
        assertTrue(hideEditDialogCalled)
    }
}