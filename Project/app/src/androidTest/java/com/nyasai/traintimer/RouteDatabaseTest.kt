package com.nyasai.traintimer

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class RouteDatabaseTest {

    private lateinit var routeDao: RouteDatabaseDao
    private lateinit var db: RouteDatabase

    // regiuon DB準備，片付け
    @Before
    fun createDB() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, RouteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        routeDao = db.routeDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDB() {
        db.close()
    }

    // endregion DB準備，片付け


    // region 路線リストアイテム操作

    /**
     * 路線アイテム追加
     */
    @Test
    @Throws(Exception::class)
    fun insertRouteListItem() {
        // 引数設定
        val item = RouteListItem()
        item.routeName = "JR"
        item.stationName = "Hoge駅"
        item.destination = "Fuga方面"

        routeDao.insertRouteListItem(item)
        val dbItems = routeDao.getAllRouteListItemsSync()
        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(dbItems[0].routeName, item.routeName)
        Assert.assertEquals(dbItems[0].stationName, item.stationName)
        Assert.assertEquals(dbItems[0].destination, item.destination)
    }

    /**
     * 路線アイテム更新
     */
    @Test
    @Throws(Exception::class)
    fun updateRouteListItem() {
        // 元データ挿入
        val item = RouteListItem()
        item.routeName = "JR"
        item.stationName = "Hoge駅"
        item.destination = "Fuga方面"
        routeDao.insertRouteListItem(item)
        val dbItems = routeDao.getAllRouteListItemsSync()

        // 引数設定
        dbItems[0].routeName = "RJ"
        dbItems[0].stationName = "Fuga駅"

        routeDao.updateRouteListItem(dbItems[0])

        val newItems = routeDao.getAllRouteListItemsSync()
        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(newItems[0].routeName, dbItems[0].routeName)
        Assert.assertEquals(newItems[0].stationName, dbItems[0].stationName)
        Assert.assertEquals(newItems[0].destination, dbItems[0].destination)
    }

    /**
     * 路線アイテム削除
     */
    @Test
    @Throws(Exception::class)
    fun deleteRouteListItem() {
        // 元データ挿入
        val item = RouteListItem()
        item.routeName = "JR"
        item.stationName = "Hoge駅"
        item.destination = "Fuga方面"
        routeDao.insertRouteListItem(item)
        val dbItems = routeDao.getAllRouteListItemsSync()

        routeDao.deleteRouteListItem(dbItems[0].dataId)

        val newItems = routeDao.getAllRouteListItemsSync()
        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(newItems.count(), 0)
    }

    /**
     * 路線アイテム全削除
     */
    @Test
    @Throws(Exception::class)
    fun clearAllRouteListItem() {
        // 元データ挿入
        val item = RouteListItem()
        item.routeName = "JR"
        item.stationName = "Hoge駅"
        item.destination = "Fuga方面"
        routeDao.insertRouteListItem(item)

        routeDao.clearAllRouteListItem()

        val newItems = routeDao.getAllRouteListItemsSync()
        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(newItems.count(), 0)
    }

    /**
     * 路線アイテム取得
     */
    @Test
    @Throws(Exception::class)
    fun getRouteListItemWithId() {
        // 元データ挿入
        val item = RouteListItem()
        item.routeName = "JR"
        item.stationName = "Hoge駅"
        item.destination = "Fuga方面"
        routeDao.insertRouteListItem(item)

        val newItems = routeDao.getAllRouteListItemsSync()

        val getItem = routeDao.getRouteListItemWithIdSync(newItems[0].dataId)

        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(getItem?.routeName, item.routeName)
        Assert.assertEquals(getItem?.stationName, item.stationName)
        Assert.assertEquals(getItem?.destination, item.destination)
    }

    /**
     * 路線アイテム全取得
     */
    @Test
    @Throws(Exception::class)
    fun getAllRouteListItems() {
        // 元データ挿入
        val item = RouteListItem()
        item.routeName = "JR"
        item.stationName = "Hoge駅"
        item.destination = "Fuga方面"
        routeDao.insertRouteListItem(item)

        val newItems = routeDao.getAllRouteListItemsSync()

        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(newItems[0].routeName, item.routeName)
        Assert.assertEquals(newItems[0].stationName, item.stationName)
        Assert.assertEquals(newItems[0].destination, item.destination)
    }

    // endregion 路線リストアイテム操作

    // region 路線情報詳細操作

    /**
     * 路線詳細アイテム追加
     */
    @Test
    @Throws(Exception::class)
    fun insertRouteDetailItem() {
        // 親データ設定
        val parent = RouteListItem()
        parent.routeName = "JR"
        parent.stationName = "Hoge駅"
        parent.destination = "Fuga方面"
        routeDao.insertRouteListItem(parent)
        val parents = routeDao.getAllRouteListItemsSync()

        // 引数設定
        val item = RouteDetail()
        item.departureTime = "00:00"
        item.destination = "Fuga方面"
        item.diagramType = 1
        item.parentDataId = parents[0].dataId
        item.trainType = "普通"

        routeDao.insertRouteDetailItem(item)
        val dbItems = routeDao.getAllRouteDetailItemsSync()
        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(dbItems[0].departureTime, item.departureTime)
        Assert.assertEquals(dbItems[0].destination, item.destination)
        Assert.assertEquals(dbItems[0].diagramType, item.diagramType)
        Assert.assertEquals(dbItems[0].parentDataId, item.parentDataId)
        Assert.assertEquals(dbItems[0].trainType, item.trainType)
    }

    // endregion 路線情報詳細操作

}