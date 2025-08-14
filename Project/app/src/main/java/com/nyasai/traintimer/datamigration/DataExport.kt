package com.nyasai.traintimer.datamigration

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.nyasai.traintimer.database.FilterInfo
import com.nyasai.traintimer.database.RouteDetail
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.datamigration.DataMigrationDefine.Companion.DELIMITER
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


open class DataExport {

    /**
     * アプリケーションデータ出力先選択起動
     */
    fun launchFolderSelector(launcher: ActivityResultLauncher<Intent>) {
        try {
            val filename = getFileName()
            val intent = getIntent(filename)
            launcher.launch(intent)
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
        }
    }

    /**
     * データ出力
     */
    fun export(
        outputStream: OutputStream,
        allRouteLists: List<RouteListItem>,
        allRouteDetailItems: List<RouteDetail>,
        allFilterInfoItems: List<FilterInfo>
    ) {
        try {
            writeVersionInfo(outputStream)
            writeRouteListData(outputStream, allRouteLists)
            writeRouteDetailData(outputStream, allRouteDetailItems)
            writeFilterInfoData(outputStream, allFilterInfoItems)
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            throw e
        }
    }

    /**
     * バージョン情報を書き込み
     */
    private fun writeVersionInfo(outputStream: OutputStream) {
        outputStream.writeLine(DataMigrationDefine.DATA_VERSION_INFO)
    }

    /**
     * 路線リストデータを書き込み
     */
    private fun writeRouteListData(outputStream: OutputStream, routeLists: List<RouteListItem>) {
        outputStream.writeLine(DataMigrationDefine.ROUTE_LIST_DATA_START_WORD)
        routeLists.forEach { item ->
            outputStream.writeLine(formatRouteListItem(item))
        }
        outputStream.writeLine()
    }

    /**
     * 路線詳細データを書き込み
     */
    private fun writeRouteDetailData(outputStream: OutputStream, routeDetails: List<RouteDetail>) {
        outputStream.writeLine(DataMigrationDefine.ROUTE_DETAIL_DATA_START_WORD)
        routeDetails.forEach { item ->
            outputStream.writeLine(formatRouteDetailItem(item))
        }
        outputStream.writeLine()
    }

    /**
     * フィルタ情報データを書き込み
     */
    private fun writeFilterInfoData(outputStream: OutputStream, filterInfos: List<FilterInfo>) {
        outputStream.writeLine(DataMigrationDefine.FILTER_INFO_DATA_START_WORD)
        filterInfos.forEach { item ->
            outputStream.writeLine(formatFilterInfoItem(item))
        }
    }

    /**
     * 路線リストアイテムのフォーマット
     */
    private fun formatRouteListItem(item: RouteListItem): String {
        return "${item.dataId}${DELIMITER}${item.routeName}${DELIMITER}${item.stationName}${DELIMITER}${item.destination}${DELIMITER}${item.sortIndex}"
    }

    /**
     * 路線詳細アイテムのフォーマット
     */
    private fun formatRouteDetailItem(item: RouteDetail): String {
        return "${item.dataId}${DELIMITER}${item.parentDataId}${DELIMITER}${item.diagramType}${DELIMITER}${item.departureTime}${DELIMITER}${item.trainType}${DELIMITER}${item.destination}"
    }

    /**
     * フィルタ情報アイテムのフォーマット
     */
    private fun formatFilterInfoItem(item: FilterInfo): String {
        return "${item.dataId}${DELIMITER}${item.parentDataId}${DELIMITER}${item.trainTypeAndDestination}${DELIMITER}${item.isShow}"
    }

    protected open fun getIntent(filename: String): Intent {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = DataMigrationDefine.MIME_TYPE
            putExtra(Intent.EXTRA_TITLE, filename)
        }
        return intent
    }

    /**
     * ファイル名取得
     */
    private fun getFileName(): String {
        val datetime =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        return "TrainTimerData-${datetime}.dat"
    }
}