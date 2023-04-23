package com.nyasai.traintimer.datamigration

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import java.io.InputStream

open class DataImport(private val _routeDatabaseDao: RouteDatabaseDao) {

    enum class DataState {
        INIT,
        ROUTE_LIST_DATA_START,
        ROUTE_DETAIL_DATA_START,
        FILTER_INFO_DATA_START
    }

    private var dataState: DataState = DataState.INIT

    /**
     * アプリケーションデータ出力先選択起動
     */
    fun launchFileSelector(launcher: ActivityResultLauncher<Intent>) {
        try {
            val intent = getIntent()
            launcher.launch(intent)
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    /**
     * データ入力
     */
    fun import(inputStream: InputStream) {
        dataState = DataState.INIT
        try {
            _routeDatabaseDao.clearAllFilterInfo()
            _routeDatabaseDao.clearAllRouteDetailItem()
            _routeDatabaseDao.clearAllRouteListItem()
            inputStream.bufferedReader().use { reader ->
                if (!isEnableVersion(reader.readLine())) {
                    return@use
                }
                reader.forEachLine { line ->
                    setDataState(line)
                    importCore(line)
                }
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            throw e
        } finally {
            dataState = DataState.INIT
        }
    }

    protected open fun getIntent(): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = DataMigrationDefine.MIME_TYPE
        }
        return intent
    }

    /**
     * データ種別保持
     */
    private fun setDataState(line: String) {
        when (line) {
            DataMigrationDefine.ROUTE_LIST_DATA_START_WORD -> {
                dataState = DataState.ROUTE_LIST_DATA_START
            }

            DataMigrationDefine.ROUTE_DETAIL_DATA_START_WORD -> {
                dataState = DataState.ROUTE_DETAIL_DATA_START
            }

            DataMigrationDefine.FILTER_INFO_DATA_START_WORD -> {
                dataState = DataState.FILTER_INFO_DATA_START
            }

            else -> {
            }
        }
    }

    /**
     * 有効なバージョンか
     */
    private fun isEnableVersion(line: String): Boolean {
        return line == DataMigrationDefine.DATA_VERSION_INFO
    }

    /**
     * インポート
     */
    private fun importCore(line: String) {
        when (dataState) {
            DataState.ROUTE_LIST_DATA_START -> {
                importRouteListData(line)
            }

            DataState.ROUTE_DETAIL_DATA_START -> {
                importRouteDetailData(line)
            }

            DataState.FILTER_INFO_DATA_START -> {
                importFilterInfoData(line)
            }

            else -> {
            }
        }
    }

    /**
     * 路線一覧データインポート
     */
    private fun importRouteListData(line: String) {
        val splitData = line.split(DataMigrationDefine.DELIMITER)
        if (splitData.size < RouteListItem.DataSize) {
            return
        }
        val insertData = RouteListItem(
            splitData[0].toLong(),
            splitData[1],
            splitData[2],
            splitData[3],
            splitData[4].toLong()
        )
        _routeDatabaseDao.insertRouteListItem(insertData)
    }

    /**
     * 路線詳細情報データインポート
     */
    private fun importRouteDetailData(line: String) {
        val splitData = line.split(DataMigrationDefine.DELIMITER)
        if (splitData.size < RouteDetail.DataSize) {
            return
        }
        val insertData = RouteDetail(
            splitData[0].toLong(),
            splitData[1].toLong(),
            splitData[2].toInt(),
            splitData[3],
            splitData[4],
            splitData[5]
        )
        _routeDatabaseDao.insertRouteDetailItem(insertData)
    }

    /**
     * フィルター情報インポート
     */
    private fun importFilterInfoData(line: String) {
        val splitData = line.split(DataMigrationDefine.DELIMITER)
        if (splitData.size < FilterInfo.DataSize) {
            return
        }
        val insertData = FilterInfo(
            splitData[0].toLong(),
            splitData[1].toLong(),
            splitData[2],
            splitData[3].toBoolean()
        )
        _routeDatabaseDao.insertFilterInfoItem(insertData)
    }
}