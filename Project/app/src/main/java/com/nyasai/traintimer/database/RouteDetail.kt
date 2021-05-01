package com.nyasai.traintimer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 時刻表詳細ページ用データ
 */
@Entity(
    tableName = "route_details_table",
    foreignKeys = [
        ForeignKey(
            entity = RouteListItem::class,
            parentColumns = arrayOf("dataId"),
            childColumns = arrayOf("parent_row_id"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class RouteDetail(

    // データID(連番)
    @PrimaryKey(autoGenerate = true)
    var dataId: Long = 0L,

    // 親データID
    @ColumnInfo(name = "parent_row_id", index = true)
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

    )
