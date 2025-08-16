package com.nyasai.traintimer.routelist.state

import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter
import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*

/**
 * RouteListDataManagerのテストクラス
 */
@ExperimentalCoroutinesApi
class RouteListDataManagerTest {

    private lateinit var mockDatabase: RouteDatabaseDao
    private lateinit var testScope: TestScope
    private lateinit var dataManager: RouteListDataManager

    @BeforeEach
    fun setUp() {
        // InstantTaskExecutorを手動で設定
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            override fun isMainThread(): Boolean = true
        })
        
        mockDatabase = mock()
        testScope = TestScope(UnconfinedTestDispatcher())
        
        // Mockの戻り値を設定
        whenever(mockDatabase.getAllRouteListItems()).thenReturn(MutableLiveData<List<RouteListItem>>())
        
        dataManager = RouteListDataManager(mockDatabase, testScope)
    }
    
    @AfterEach
    fun tearDown() {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun `初期状態で編集モードがfalse`() {
        // Then
        assertFalse(dataManager.isEditMode.value ?: true)
    }

    @Test
    fun `編集モード切り替えが正常に動作する`() {
        // Given
        val initialValue = dataManager.isEditMode.value ?: false
        
        // When
        dataManager.switchEditMode()
        
        // Then
        assertEquals(!initialValue, dataManager.isEditMode.value)
    }

    @Test
    fun `deleteListItemが正常に呼び出される`() {
        // Given
        val testDataId = 123L
        
        // When
        dataManager.deleteListItem(testDataId)
        
        // Then
        verify(mockDatabase).deleteRouteListItem(testDataId)
    }

    @Test
    fun `updateRouteListItemColorが正常に呼び出される`() {
        // Given
        val testDataId = 456L
        val testColor = 0xFF0000
        
        // When
        dataManager.updateRouteListItemColor(testDataId, testColor)
        
        // Then
        verify(mockDatabase).updateRouteListItemColor(testDataId, testColor)
    }

    @Test
    fun `registerRouteListItemが無効なrouteInfoで-1を返す`() {
        // Given - 空のrouteInfoは無効とみなされる
        val mockRouteInfo = listOf<List<YahooRouteInfoGetter.TimeInfo>>()
        val testItem = RouteListItem().apply {
            routeName = "テスト路線"
            stationName = "テスト駅"
            destination = "テスト方面"
        }
        
        // When
        val result = dataManager.registerRouteListItem(mockRouteInfo, testItem)
        
        // Then
        assertEquals(-1L, result)
        // 無効なrouteInfoの場合はinsertRouteListItemは呼ばれない
        verify(mockDatabase, never()).insertRouteListItem(any())
    }

    @Test
    fun `初期化時にrouteListが正常に取得される`() {
        // Then
        assertNotNull(dataManager.routeList)
        verify(mockDatabase).getAllRouteListItems()
    }
}