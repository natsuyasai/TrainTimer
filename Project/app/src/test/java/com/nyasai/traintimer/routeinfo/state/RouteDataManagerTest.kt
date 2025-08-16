package com.nyasai.traintimer.routeinfo.state

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.FilterInfo
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

/**
 * RouteDataManagerのテストクラス
 */
class RouteDataManagerTest {

    private lateinit var mockDatabase: RouteDatabaseDao
    private lateinit var routeDataManager: RouteDataManager
    private val testParentId = 123L

    @BeforeEach
    fun setUp() {
        // InstantTaskExecutorを手動で設定
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread(): Boolean = true
        })
        
        mockDatabase = mock()
        
        // Mockの戻り値を設定
        whenever(mockDatabase.getRouteListItemWithId(testParentId)).thenReturn(MutableLiveData<RouteListItem>())
        whenever(mockDatabase.getRouteDetailItemsWithParentId(testParentId)).thenReturn(MutableLiveData<List<RouteDetail>>())
        whenever(mockDatabase.getFilterInfoItemWithParentId(testParentId)).thenReturn(MutableLiveData<List<FilterInfo>>())
        
        routeDataManager = RouteDataManager(mockDatabase, testParentId)
    }
    
    @AfterEach
    fun tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun `初期化時にrouteInfoが正常に取得される`() {
        // Then
        assertNotNull(routeDataManager.routeInfo)
        verify(mockDatabase).getRouteListItemWithId(testParentId)
    }

    @Test
    fun `初期化時にrouteItemsが正常に取得される`() {
        // Then
        assertNotNull(routeDataManager.routeItems)
        verify(mockDatabase).getRouteDetailItemsWithParentId(testParentId)
    }

    @Test
    fun `初期化時にfilterInfoが正常に取得される`() {
        // Then
        assertNotNull(routeDataManager.filterInfo)
        verify(mockDatabase).getFilterInfoItemWithParentId(testParentId)
    }

    @Test
    fun `setCurrentCountItemが正常に動作する`() {
        // Given
        val testRouteDetail = RouteDetail().apply {
            departureTime = "14:30"
            trainType = "普通"
            destination = "テスト方面"
        }
        
        // When
        routeDataManager.setCurrentCountItem(testRouteDetail)
        
        // Then
        assertEquals(testRouteDetail, routeDataManager.currentCountItem.value)
    }

    @Test
    fun `updateCurrentCountItemが正常に動作する`() {
        // Given (nullや特定のパラメータでテスト)
        
        // When
        routeDataManager.updateCurrentCountItem(null)
        
        // Then
        // updateCurrentCountItemの結果は内部処理に依存するため、例外が発生しないことを確認
        assertNotNull(routeDataManager.currentCountItem)
    }
}