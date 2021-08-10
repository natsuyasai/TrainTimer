package com.nyasai.traintimer.datamigration

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Logger

class DataExport(val context: Context) {
    // ビューコンテキスト
    private val _context: Context = context

    /**
     * アプリケーションデータ出力
     */
    fun exportAppData(inputStream: BufferedInputStream) {
        try{
            val filename = getFileName()
            val file = File(_context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),filename)
            val stream = FileOutputStream(filename)

        }
        catch (e: Exception){
            Log.e("Exception", e.toString())
        }
    }

    /**
     * ファイル名取得
     */
    private fun getFileName(): String {
        val datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd_HH-mm-ss"))
        return "traintimer-data-${datetime}.csv"
    }

}