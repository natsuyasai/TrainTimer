package com.nyasai.traintimer

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nyasai.traintimer.database.*
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

    /**
     * 路線詳細アイテム追加
     */
    @Test
    @Throws(Exception::class)
    fun insertRouteDetailItems() {
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
        val items = listOf(item)

        routeDao.insertRouteDetailItems(items)
        val dbItems = routeDao.getAllRouteDetailItemsSync()
        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(dbItems[0].departureTime, item.departureTime)
        Assert.assertEquals(dbItems[0].destination, item.destination)
        Assert.assertEquals(dbItems[0].diagramType, item.diagramType)
        Assert.assertEquals(dbItems[0].parentDataId, item.parentDataId)
        Assert.assertEquals(dbItems[0].trainType, item.trainType)
    }

    /**
     * 路線詳細アイテム更新
     */
    @Test
    @Throws(Exception::class)
    fun updateRouteDetailItem() {
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
        val items = routeDao.getAllRouteDetailItemsSync()

        item.departureTime = "11:11"
        item.destination = ""
        item.diagramType = 2
        item.trainType = ""
        item.dataId = items[0].dataId
        routeDao.updateRouteDetailItem(item)
        val dbItems = routeDao.getAllRouteDetailItemsSync()
        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(dbItems[0].departureTime, item.departureTime)
        Assert.assertEquals(dbItems[0].destination, item.destination)
        Assert.assertEquals(dbItems[0].diagramType, item.diagramType)
        Assert.assertEquals(dbItems[0].parentDataId, item.parentDataId)
        Assert.assertEquals(dbItems[0].trainType, item.trainType)
    }

    /**
     * 路線詳細アイテム更新
     */
    @Test
    @Throws(Exception::class)
    fun updateRouteDetailItems() {
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
        val items = routeDao.getAllRouteDetailItemsSync()

        item.departureTime = "11:11"
        item.destination = ""
        item.diagramType = 2
        item.trainType = ""
        item.dataId = items[0].dataId
        routeDao.updateRouteDetailItems(listOf(item))
        val dbItems = routeDao.getAllRouteDetailItemsSync()
        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(dbItems[0].departureTime, item.departureTime)
        Assert.assertEquals(dbItems[0].destination, item.destination)
        Assert.assertEquals(dbItems[0].diagramType, item.diagramType)
        Assert.assertEquals(dbItems[0].parentDataId, item.parentDataId)
        Assert.assertEquals(dbItems[0].trainType, item.trainType)
    }

    /**
     * 路線詳細アイテム削除
     */
    @Test
    @Throws(Exception::class)
    fun deleteRouteDetailItem() {
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
        var dbItems = routeDao.getAllRouteDetailItemsSync()

        routeDao.deleteRouteDetailItem(dbItems[0].dataId)

        dbItems = routeDao.getAllRouteDetailItemsSync()
        Assert.assertEquals(dbItems.size, 0)

        // 片付け
        routeDao.clearAllRouteListItem()
    }

    /**
     * 路線詳細アイテム削除
     */
    @Test
    @Throws(Exception::class)
    fun deleteRouteDetailItemWithParentId() {
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

        routeDao.deleteRouteDetailItemWithParentId(parents[0].dataId)

        val dbItems = routeDao.getAllRouteDetailItemsSync()
        Assert.assertEquals(dbItems.size, 0)

        // 片付け
        routeDao.clearAllRouteListItem()
    }

    /**
     * 路線詳細アイテム全クリア
     */
    @Test
    @Throws(Exception::class)
    fun clearAllRouteDetailItem() {
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

        routeDao.clearAllRouteDetailItem()

        val dbItems = routeDao.getAllRouteDetailItemsSync()
        Assert.assertEquals(dbItems.size, 0)

        // 片付け
        routeDao.clearAllRouteListItem()
    }


    /**
     * 路線詳細アイテム取得
     */
    @Test
    @Throws(Exception::class)
    fun getRouteDetailItemWithId() {
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
        val items = routeDao.getAllRouteDetailItemsSync()

        val dbItems = routeDao.getRouteDetailItemWithId(items[0].dataId)

        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertNotNull(dbItems)
        Assert.assertEquals(dbItems?.departureTime, item.departureTime)
        Assert.assertEquals(dbItems?.destination, item.destination)
        Assert.assertEquals(dbItems?.diagramType, item.diagramType)
        Assert.assertEquals(dbItems?.parentDataId, item.parentDataId)
        Assert.assertEquals(dbItems?.trainType, item.trainType)
    }

    /**
     * 路線詳細アイテム取得
     */
    @Test
    @Throws(Exception::class)
    fun getRouteDetailItemsWithParentIdSync() {
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

        val dbItems = routeDao.getRouteDetailItemsWithParentIdSync(parents[0].dataId)

        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(dbItems[0].departureTime, item.departureTime)
        Assert.assertEquals(dbItems[0].destination, item.destination)
        Assert.assertEquals(dbItems[0].diagramType, item.diagramType)
        Assert.assertEquals(dbItems[0].parentDataId, item.parentDataId)
        Assert.assertEquals(dbItems[0].trainType, item.trainType)
    }

    /**
     * 路線詳細アイテム一覧取得
     */
    @Test
    @Throws(Exception::class)
    fun getCurrentDiagramRouteDetailItemsWithParentIdSync() {
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

        val dbItems = routeDao.getCurrentDiagramRouteDetailItemsWithParentIdSync(parents[0].dataId, 1)

        // 片付け
        routeDao.clearAllRouteListItem()
        Assert.assertEquals(dbItems[0].departureTime, item.departureTime)
        Assert.assertEquals(dbItems[0].destination, item.destination)
        Assert.assertEquals(dbItems[0].diagramType, item.diagramType)
        Assert.assertEquals(dbItems[0].parentDataId, item.parentDataId)
        Assert.assertEquals(dbItems[0].trainType, item.trainType)
    }

    /**
     * 路線詳細アイテム一覧取得
     */
    @Test
    @Throws(Exception::class)
    fun getAllRouteDetailItemsSync() {
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

    // region フィルタ情報操作

    @Test
    @Throws(Exception::class)
    fun insertFilterInfoItem(){
        // 親データ設定
        val parent = RouteListItem()
        parent.routeName = "JR"
        parent.stationName = "Hoge駅"
        parent.destination = "Fuga方面"
        routeDao.insertRouteListItem(parent)
        val parents = routeDao.getAllRouteListItemsSync()

        val item = FilterInfo()
        item.parentDataId = parents[0].dataId
        item.isShow = true
        item.trainTypeAndDestination = "普通"
        routeDao.insertFilterInfoItem(item)

        val dbItems = routeDao.getFilterInfoItemWithParentIdSync(parents[0].dataId)

        // 片付け
        routeDao.deleteFilterInfoItemWithParentId(parents[0].dataId)
        Assert.assertEquals(dbItems[0].parentDataId, item.parentDataId)
        Assert.assertEquals(dbItems[0].isShow, item.isShow)
        Assert.assertEquals(dbItems[0].trainTypeAndDestination, item.trainTypeAndDestination)
    }

    @Test
    @Throws(Exception::class)
    fun insertFilterInfoItems(){
        // 親データ設定
        val parent = RouteListItem()
        parent.routeName = "JR"
        parent.stationName = "Hoge駅"
        parent.destination = "Fuga方面"
        routeDao.insertRouteListItem(parent)
        val parents = routeDao.getAllRouteListItemsSync()

        val items = mutableListOf<FilterInfo>()
        for (i in 1..10){
            val item = FilterInfo()
            item.parentDataId = parents[0].dataId
            item.isShow = true
            item.trainTypeAndDestination = i.toString()
            items.add(item)
        }
        routeDao.insertFilterInfoItems(items)

        val dbItems = routeDao.getFilterInfoItemWithParentIdSync(parents[0].dataId)

        // 片付け
        routeDao.deleteFilterInfoItemWithParentId(parents[0].dataId)
        Assert.assertEquals(dbItems[0].parentDataId, items[0].parentDataId)
        Assert.assertEquals(dbItems[0].isShow, items[0].isShow)
        Assert.assertEquals(dbItems[0].trainTypeAndDestination, items[0].trainTypeAndDestination)
        Assert.assertEquals(dbItems[9].parentDataId, items[9].parentDataId)
        Assert.assertEquals(dbItems[9].isShow, items[9].isShow)
        Assert.assertEquals(dbItems[9].trainTypeAndDestination, items[9].trainTypeAndDestination)
    }

    // endregion フィルタ情報操作
}