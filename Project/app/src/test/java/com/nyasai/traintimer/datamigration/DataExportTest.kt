@file:Suppress("NonAsciiCharacters", "EmptyMethod")

package com.nyasai.traintimer.datamigration

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.nyasai.traintimer.database.RouteDatabaseDao
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.io.OutputStream

internal class DataExportTest {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `フォルダ出力先選択処理 起動`() {
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
        verify(outputStreamMock, times(2)).writeLine()
        verify(outputStreamMock, times(1)).writeLine("DataVersion,1")
        verify(outputStreamMock, times(1)).writeLine("RouteListDataStart")
        verify(outputStreamMock, times(1)).writeLine("RouteDetailDataStart")
        verify(outputStreamMock, times(1)).writeLine("FilterInfoDataStart")
    }

    internal class DataExportTesting : DataExport() {
        override fun getIntent(filename: String): Intent {
            return mock()
        }
    }
}