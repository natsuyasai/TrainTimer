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
            outputStream.writeLine(DataMigrationDefine.DATA_VERSION_INFO)
            outputStream.writeLine(DataMigrationDefine.ROUTE_LIST_DATA_START_WORD)
            for (item in allRouteLists) {
                outputStream.writeLine("${item.dataId}${DELIMITER}${item.routeName}${DELIMITER}${item.stationName}${DELIMITER}${item.destination}${DELIMITER}${item.sortIndex}")
            }
            outputStream.writeLine()
            outputStream.writeLine(DataMigrationDefine.ROUTE_DETAIL_DATA_START_WORD)
            for (item in allRouteDetailItems) {
                outputStream.writeLine("${item.dataId}${DELIMITER}${item.parentDataId}${DELIMITER}${item.diagramType}${DELIMITER}${item.departureTime}${DELIMITER}${item.trainType}${DELIMITER}${item.destination}")
            }
            outputStream.writeLine()
            outputStream.writeLine(DataMigrationDefine.FILTER_INFO_DATA_START_WORD)
            for (item in allFilterInfoItems) {
                outputStream.writeLine("${item.dataId}${DELIMITER}${item.parentDataId}${DELIMITER}${item.trainTypeAndDestination}${DELIMITER}${item.isShow}")
            }
        } catch (e: Exception) {
            Log.e("Exception", e.toString())
            throw e
        }
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