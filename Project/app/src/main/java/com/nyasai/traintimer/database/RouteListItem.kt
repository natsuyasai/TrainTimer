package com.nyasai.traintimer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_list_item_table")
data class RouteListItem(

    // データID(連番)
    @PrimaryKey(autoGenerate = true)
    var dataId: Long = 0L,

    // 路線名
    @ColumnInfo(name = "route_name")
    var routeName: String = "",

    // 駅名
    @ColumnInfo(name = "station_name")
    var stationName: String = "",

    // 方面
    @ColumnInfo(name = "direction")
    var direction: String = ""

)