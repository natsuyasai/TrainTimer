package com.nyasai.traintimer.datamigration

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.nyasai.traintimer.database.RouteDatabaseDao
import io.mockk.InternalPlatformDsl.toArray
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.kotlin.*
import java.io.OutputStream

internal class DataExportTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `フォルダ出力先選択処理起動`() {
        val target = DataExportTesting()
        val activityResultLauncherMock = mock<ActivityResultLauncher<Intent>>()

        target.launchFolderSelector(activityResultLauncherMock)

        verify(activityResultLauncherMock, times(1)).launch(any())
    }

    @Test
    fun `エクスポート処理実行 出力データなし`() {
        val target = DataExportTesting()
        val outputStreamMock = mock<OutputStream>()
        val routeDatabaseDaoMock = mock<RouteDatabaseDao>()

        target.export(outputStreamMock, routeDatabaseDaoMock)

        argumentCaptor<String>().apply {
            verify(outputStreamMock, times(6)).writeLine(capture())
//            val expected = listOf(
//                "${DataMigrationDefine.DATA_VERSION_INFO}\r\n".toByteArray(),
//                "${DataMigrationDefine.ROUTE_LIST_DATA_START_WORD}\r\n".toByteArray(),
//                "\r\n".toByteArray(),
//                "${DataMigrationDefine.ROUTE_DETAIL_DATA_START_WORD}\r\n".toByteArray(),
//                "\r\n".toByteArray(),
//                "${DataMigrationDefine.FILTER_INFO_DATA_START_WORD}\r\n".toByteArray()
//            )
            //Assertions.assertArrayEquals(expected.toArray(), allValues.toArray())
        }
        verify(outputStreamMock, times(2)).writeLine()
    }

    internal class DataExportTesting : DataExport() {
        override fun getIntent(filename: String): Intent {
            return mock<Intent>()
        }
    }
}