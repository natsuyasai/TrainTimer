package com.nyasai.traintimer

import android.util.Log
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteListItem
import org.junit.After
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

    // endregiuon DB準備，片付け


    // region テスト

    @Test
    @Throws(Exception::class)
    fun insertRouteListItem() {
        val item = RouteListItem()
        item.routeName = "JR 宝塚線"
        item.stationName = "草野駅"
        item.destination = "大阪方面"
        routeDao.insertRouteListItem(item)
        val dbItems = routeDao.getAllRouteListItemsSync()
        if(dbItems != null){
            Log.d("DBTest", dbItems.toString())
        }
        else{
            Log.d("DBTest", "null")
        }
        routeDao.clearAllRouteListItem()
        //assertNull(dbItem)
    }

    // endregion
}