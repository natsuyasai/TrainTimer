package com.nyasai.traintimer.routeinfo

import com.nyasai.traintimer.database.FilterInfo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * FilterItemSelectViewModelのテスト
 */
class FilterItemSelectViewModelTest {

    private lateinit var viewModel: FilterItemSelectViewModel

    @BeforeEach
    fun setUp() {
        viewModel = FilterItemSelectViewModel()
    }

    @Test
    fun `初期状態では空のリストであること`() {
        // Then
        assertTrue(viewModel.filterItemsState.isEmpty())
        assertTrue(viewModel.filterItemList.isEmpty())
    }

    @Test
    fun `フィルタアイテムの更新が正常に動作すること`() {
        // Given
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false),
            FilterInfo(3L, 100L, "特急 - 横浜", true)
        )

        // When
        viewModel.updateFilterItems(testItems)

        // Then
        assertEquals(3, viewModel.filterItemsState.size)
        assertEquals(3, viewModel.filterItemList.size)
        assertEquals("普通 - 新宿", viewModel.filterItemsState[0].trainTypeAndDestination)
        assertEquals("快速 - 池袋", viewModel.filterItemsState[1].trainTypeAndDestination)
        assertEquals("特急 - 横浜", viewModel.filterItemsState[2].trainTypeAndDestination)
        
        // 既存リストとの同期確認
        assertEquals(testItems[0].isShow, viewModel.filterItemList[0].isShow)
        assertEquals(testItems[1].isShow, viewModel.filterItemList[1].isShow)
        assertEquals(testItems[2].isShow, viewModel.filterItemList[2].isShow)
    }

    @Test
    fun `アイテムの表示状態の切り替えが正常に動作すること`() {
        // Given
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false)
        )
        viewModel.updateFilterItems(testItems)

        // When
        viewModel.toggleItemVisibility(0) // true -> false
        viewModel.toggleItemVisibility(1) // false -> true

        // Then
        assertFalse(viewModel.filterItemsState[0].isShow)
        assertTrue(viewModel.filterItemsState[1].isShow)
        
        // 既存リストとの同期確認
        assertFalse(viewModel.filterItemList[0].isShow)
        assertTrue(viewModel.filterItemList[1].isShow)
    }

    @Test
    fun `範囲外のインデックスでの切り替えでエラーが発生しないこと`() {
        // Given
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true)
        )
        viewModel.updateFilterItems(testItems)

        // When & Then (例外が発生しないことを確認)
        assertDoesNotThrow {
            viewModel.toggleItemVisibility(-1)
            viewModel.toggleItemVisibility(1)
            viewModel.toggleItemVisibility(100)
        }
        
        // 元のデータが変更されていないことを確認
        assertTrue(viewModel.filterItemsState[0].isShow)
    }

    @Test
    fun `Positiveボタンコールバックが正常に動作すること`() {
        // Given
        var callbackCalled = false
        viewModel.onClickPositiveButtonCallback = { callbackCalled = true }

        // When
        viewModel.onPositiveButtonClick()

        // Then
        assertTrue(callbackCalled)
    }

    @Test
    fun `Negativeボタンコールバックが正常に動作すること`() {
        // Given
        var callbackCalled = false
        viewModel.onClickNegativeButtonCallback = { callbackCalled = true }

        // When
        viewModel.onNegativeButtonClick()

        // Then
        assertTrue(callbackCalled)
    }

    @Test
    fun `データクリア時に初期状態に戻ること`() {
        // Given
        val testItems = listOf(
            FilterInfo(1L, 100L, "普通 - 新宿", true),
            FilterInfo(2L, 100L, "快速 - 池袋", false)
        )
        viewModel.updateFilterItems(testItems)

        // When
        viewModel.clearUIData()

        // Then
        assertTrue(viewModel.filterItemsState.isEmpty())
        assertTrue(viewModel.filterItemList.isEmpty())
    }

    @Test
    fun `コールバックが設定されていない場合でもエラーが発生しないこと`() {
        // When & Then (例外が発生しないことを確認)
        assertDoesNotThrow {
            viewModel.onPositiveButtonClick()
            viewModel.onNegativeButtonClick()
        }
    }
}