package com.nyasai.traintimer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * フィルタ情報データ
 */
@Entity(
    tableName = "filter_info_table",
    foreignKeys = [
        ForeignKey(
            entity = RouteListItem::class,
            parentColumns = arrayOf("dataId"),
            childColumns = arrayOf("parent_row_id"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class FilterInfo @Ignore constructor(

    // データID(連番)
    @PrimaryKey(autoGenerate = true)
    var dataId: Long = 0L,

    // 親データID
    @ColumnInfo(name = "parent_row_id", index = true)
    var parentDataId: Long = 0L,

    // 列車種別(普通，快速，etc...)+行先
    @ColumnInfo(name = "train_type_and_destination")
    var trainTypeAndDestination: String = "",

    // 表示するか
    @ColumnInfo(name = "is_show")
    var isShow: Boolean = true,
) {

    constructor() : this(0, 0L, "", true)

    companion object {
        /**
         *
         */
        fun createFilterKey(trainType: String, destination: String) =
            "$trainType - $destination"
    }
}
