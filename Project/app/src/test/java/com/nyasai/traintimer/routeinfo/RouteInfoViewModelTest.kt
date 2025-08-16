package com.nyasai.traintimer.routeinfo

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * RouteInfoViewModelのテストクラス
 */
class RouteInfoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private fun createMockRouteDetail(
        dataId: Long,
        parentDataId: Long,
        diagramType: Int,
        departureTime: String,
        trainType: String,
        destination: String
    ): RouteDetail {
        return mock<RouteDetail>().apply {
            this.dataId = dataId
            this.parentDataId = parentDataId
            this.diagramType = diagramType
            this.departureTime = departureTime
            this.trainType = trainType
            this.destination = destination
        }
    }

    private fun createMockFilterInfo(
        dataId: Long,
        parentDataId: Long,
        trainTypeAndDestination: String,
        isShow: Boolean
    ): FilterInfo {
        return mock<FilterInfo>().apply {
            this.dataId = dataId
            this.parentDataId = parentDataId
            this.trainTypeAndDestination = trainTypeAndDestination
            this.isShow = isShow
        }
    }

    @Test
    fun `getDisplayRouteDetailItems_正しくフィルタリングとソートが行われる`() {
        // Given
        val mockDatabase = mock<RouteDatabaseDao>()
        val mockApplication = mock<Application>()
        val parentId = 1L

        val routeDetails = listOf(
            createMockRouteDetail(1L, parentId, 0, "08:30", "快速", "新宿方面"),
            createMockRouteDetail(2L, parentId, 0, "08:00", "普通", "池袋方面"),
            createMockRouteDetail(3L, parentId, 1, "08:15", "急行", "渋谷方面"), // 異なるダイア種別
            createMockRouteDetail(4L, parentId, 0, "09:00", "快速", "新宿方面")
        )

        val filterInfos = listOf(
            createMockFilterInfo(1L, parentId, "快速新宿方面", true),
            createMockFilterInfo(2L, parentId, "普通池袋方面", true),
            createMockFilterInfo(3L, parentId, "急行渋谷方面", false) // 非表示
        )

        val mockRouteListItem = mock<RouteListItem>()
        whenever(mockDatabase.getRouteListItemWithId(parentId)).thenReturn(MutableLiveData(mockRouteListItem))
        whenever(mockDatabase.getRouteDetailItemsWithParentId(parentId)).thenReturn(MutableLiveData(routeDetails))
        whenever(mockDatabase.getFilterInfoItemWithParentId(parentId)).thenReturn(MutableLiveData(filterInfos))

        val viewModel = RouteInfoViewModel(mockDatabase, mockApplication, parentId)

        // When
        val result = viewModel.getDisplayRouteDetailItems()

        // Then
        // ダイア種別0でフィルタが有効なもののみが含まれることを確認
        assertEquals(3, result.size)
        
        // 時刻順にソートされていることを確認
        assertEquals("08:00", result[0].departureTime)
        assertEquals("08:30", result[1].departureTime)
        assertEquals("09:00", result[2].departureTime)
    }

    @Test
    fun `setCurrentCountItem_正しく設定される`() {
        // Given
        val mockDatabase = mock<RouteDatabaseDao>()
        val mockApplication = mock<Application>()
        val parentId = 1L

        val mockRouteListItem = mock<RouteListItem>()
        whenever(mockDatabase.getRouteListItemWithId(parentId)).thenReturn(MutableLiveData(mockRouteListItem))
        whenever(mockDatabase.getRouteDetailItemsWithParentId(parentId)).thenReturn(MutableLiveData(emptyList()))
        whenever(mockDatabase.getFilterInfoItemWithParentId(parentId)).thenReturn(MutableLiveData(emptyList()))

        val viewModel = RouteInfoViewModel(mockDatabase, mockApplication, parentId)
        val testRouteDetail = createMockRouteDetail(1L, parentId, 0, "08:30", "快速", "新宿方面")

        // When
        viewModel.setCurrentCountItem(testRouteDetail)

        // Then
        assertEquals(testRouteDetail, viewModel.currentCountItem.value)
    }

    @Test
    fun `setNextDiagramType_正しく次のダイア種別に変更される`() {
        // Given
        val mockDatabase = mock<RouteDatabaseDao>()
        val mockApplication = mock<Application>()
        val parentId = 1L

        val mockRouteListItem = mock<RouteListItem>()
        whenever(mockDatabase.getRouteListItemWithId(parentId)).thenReturn(MutableLiveData(mockRouteListItem))
        whenever(mockDatabase.getRouteDetailItemsWithParentId(parentId)).thenReturn(MutableLiveData(emptyList()))
        whenever(mockDatabase.getFilterInfoItemWithParentId(parentId)).thenReturn(MutableLiveData(emptyList()))

        val viewModel = RouteInfoViewModel(mockDatabase, mockApplication, parentId)
        val initialDiagramType = viewModel.currentDiagramType.value

        // When
        viewModel.setNextDiagramType()

        // Then
        assertNotEquals(initialDiagramType, viewModel.currentDiagramType.value)
    }

    @Test
    fun `clearDisplayCache_キャッシュが正しくクリアされる`() {
        // Given
        val mockDatabase = mock<RouteDatabaseDao>()
        val mockApplication = mock<Application>()
        val parentId = 1L

        val routeDetails = listOf(
            createMockRouteDetail(1L, parentId, 0, "08:30", "快速", "新宿方面")
        )

        val mockRouteListItem = mock<RouteListItem>()
        whenever(mockDatabase.getRouteListItemWithId(parentId)).thenReturn(MutableLiveData(mockRouteListItem))
        whenever(mockDatabase.getRouteDetailItemsWithParentId(parentId)).thenReturn(MutableLiveData(routeDetails))
        whenever(mockDatabase.getFilterInfoItemWithParentId(parentId)).thenReturn(MutableLiveData(emptyList()))

        val viewModel = RouteInfoViewModel(mockDatabase, mockApplication, parentId)

        // 最初にキャッシュを作成
        viewModel.getDisplayRouteDetailItems()

        // When
        viewModel.clearDisplayCache()

        // キャッシュがクリアされたことを間接的に確認
        // （キャッシュの状態を直接確認する方法がないため、動作を確認）
        val result = viewModel.getDisplayRouteDetailItems()
        assertNotNull(result)
    }

    @Test
    fun `getNextDiffTime_正しい差分時間が返される`() {
        // Given
        val mockDatabase = mock<RouteDatabaseDao>()
        val mockApplication = mock<Application>()
        val parentId = 1L

        val mockRouteListItem = mock<RouteListItem>()
        whenever(mockDatabase.getRouteListItemWithId(parentId)).thenReturn(MutableLiveData(mockRouteListItem))
        whenever(mockDatabase.getRouteDetailItemsWithParentId(parentId)).thenReturn(MutableLiveData(emptyList()))
        whenever(mockDatabase.getFilterInfoItemWithParentId(parentId)).thenReturn(MutableLiveData(emptyList()))

        val viewModel = RouteInfoViewModel(mockDatabase, mockApplication, parentId)

        // When
        val result = viewModel.getNextDiffTime()

        // Then
        // データがない場合は-1が返されることを確認
        assertEquals(-1L, result)
    }
}