package com.nyasai.traintimer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_details_table")
data class RouteDetails (

    // データID(連番)
    @PrimaryKey(autoGenerate = true)
    var dataId: Long = 0L,

    // 親データID
    @ColumnInfo(name = "parent_row_id")
    var parentDataId: Long = 0L,

    // ダイヤ種別(平日:0，土:1，日祝日:2)
    @ColumnInfo(name = "diagram_type")
    var diagramType: Int = 0,

    // 発車時刻
    @ColumnInfo(name = "departure_time")
    var departureTime: String = "",

    // 列車種別(普通，快速，etc...)
    @ColumnInfo(name = "train_type")
    var trainType: String = "",

    // 行先
    @ColumnInfo(name = "destination")
    var destination: String = "",

    // データ取得本URL
    @ColumnInfo(name = "src_url")
    var srcUrl: String = ""

)
