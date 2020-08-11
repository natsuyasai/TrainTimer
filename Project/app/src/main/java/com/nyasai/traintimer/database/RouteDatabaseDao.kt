package com.nyasai.traintimer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RouteDatabaseDao {

    @Insert
    fun insert(routeListItem: RouteListItem)

    @Query("SELECT * from route_list_item_table WHERE dataId = :key")
    fun get(key: Long): RouteListItem?
}