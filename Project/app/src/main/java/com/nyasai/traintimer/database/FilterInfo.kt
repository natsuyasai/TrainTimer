package com.nyasai.traintimer.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * フィルタ情報データ
 */
@Entity(tableName = "filter_info_table")
data class FilterInfo (

    // データID(連番)
    @PrimaryKey(autoGenerate = true)
    var dataId: Long = 0L,

    // 親データID
    @ColumnInfo(name = "parent_row_id")
    var parentDataId: Long = 0L,

    // 列車種別(普通，快速，etc...)
    @ColumnInfo(name = "train_type")
    var trainType: String = "",

    // 表示するか
    @ColumnInfo(name = "is_show")
    var isShow: Boolean = true,
)
