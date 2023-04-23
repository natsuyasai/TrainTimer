package com.nyasai.traintimer.datamigration

class DataMigrationDefine {

    companion object {
        // データバージョン
        private const val DATA_VERSION = 1
        const val DATA_VERSION_INFO = "DataVersion,${DATA_VERSION}"

        // 路線一覧情報開始位置
        const val ROUTE_LIST_DATA_START_WORD = "RouteListDataStart"
        const val ROUTE_DETAIL_DATA_START_WORD = "RouteDetailDataStart"
        const val FILTER_INFO_DATA_START_WORD = "FilterInfoDataStart"

        const val MIME_TYPE = "application/octet-stream"

        const val DELIMITER = "\t"
    }
}