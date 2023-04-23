package com.nyasai.traintimer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * 路線一覧用データ
 */
@Entity(tableName = "route_list_item_table")
data class RouteListItem @Ignore constructor(

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
    @ColumnInfo(name = "destination")
    var destination: String = "",

    // ソート用インデックス
    @ColumnInfo(name = "sort_index")
    var sortIndex: Long = 0L

) {
    constructor() : this(0, "", "", "", 0L)
    companion object {
        const val DataSize = 5
    }

}