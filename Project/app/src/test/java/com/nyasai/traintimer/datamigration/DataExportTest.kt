@file:Suppress("NonAsciiCharacters", "EmptyMethod")

package com.nyasai.traintimer.datamigration

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import io.mockk.mockk
import io.mockk.verify
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
        val activityResultLauncherMock = mockk<ActivityResultLauncher<Intent>>(relaxed = true)

        target.launchFolderSelector(activityResultLauncherMock)

        verify(exactly = 1) { activityResultLauncherMock.launch(any()) }
    }

    @Test
    fun `エクスポート処理実行 出力データなし`() {
        val target = DataExportTesting()
        val outputStreamMock = mockk<OutputStream>(relaxed = true)

        target.export(outputStreamMock, emptyList(), emptyList(), emptyList())
        verify(exactly = 2) { outputStreamMock.writeLine() }
        verify(exactly = 1) { outputStreamMock.writeLine("DataVersion,1") }
        verify(exactly = 1) { outputStreamMock.writeLine("RouteListDataStart") }
        verify(exactly = 1) { outputStreamMock.writeLine("RouteDetailDataStart") }
        verify(exactly = 1) { outputStreamMock.writeLine("FilterInfoDataStart") }
    }

    internal class DataExportTesting : DataExport() {
        override fun getIntent(filename: String): Intent {
            return mockk(relaxed = true)
        }
    }
}