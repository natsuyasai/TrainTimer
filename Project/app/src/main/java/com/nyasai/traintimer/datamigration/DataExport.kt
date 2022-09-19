package com.nyasai.traintimer.datamigration

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.nyasai.traintimer.database.RouteDatabaseDao
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DataExport: Fragment() {

    /**
     * アプリケーションデータ出力先選択起動
     */
    fun launchFolderSelector(launcher: ActivityResultLauncher<Intent>/*inputStream: BufferedInputStream*/) {
        try{
            val filename = getFileName()
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/octet-stream"
                putExtra(Intent.EXTRA_TITLE, filename)
            }
            launcher.launch(intent)
        }
        catch (e: Exception){
            Log.e("Exception", e.toString())
        }
    }

    /**
     * データ出力
     */
    fun export(outputStream: OutputStream, routeDatabaseDao: RouteDatabaseDao) {
        try {
            outputStream.write("RouteListDataStart\r\n".toByteArray())
            for (item in routeDatabaseDao.getAllRouteListItemsSync()){
                outputStream.write("${item.dataId},${item.routeName},${item.stationName},${item.destination},${item.sortIndex}\r\n".toByteArray())
            }
            outputStream.write("\r\n".toByteArray())
            outputStream.write("RouteDetailDataStart\r\n".toByteArray())
            for (item in routeDatabaseDao.getAllRouteDetailItemsSync()){
                outputStream.write("${item.dataId},${item.parentDataId},${item.diagramType},${item.departureTime},${item.trainType},${item.destination}\n".toByteArray())
            }
            outputStream.write("\r\n".toByteArray())
            outputStream.write("FilterInfoDataStart\r\n".toByteArray())
            for (item in routeDatabaseDao.getAllFilterInfoItemSync()){
                outputStream.write("${item.dataId},${item.parentDataId},${item.trainTypeAndDestination},${item.isShow}\n".toByteArray())
            }
        }
        catch (e: java.lang.Exception){
            e.printStackTrace()
            throw e
        }
    }

    /**
     * ファイル名取得
     */
    private fun getFileName(): String {
        val datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))
        return "TrainTimerData-${datetime}.dat"
    }

}