package com.nyasai.traintimer.setting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.database.RouteDatabaseDao
import com.nyasai.traintimer.datamigration.DataExport
import com.nyasai.traintimer.datamigration.DataImport
import com.nyasai.traintimer.util.FragmentUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * 設定画面フラグメント
 */
class PreferenceFragment : PreferenceFragmentCompat(), CoroutineScope {

    private companion object {
        // アプリケーション情報ダイアログ
        const val AppInfoDialogTag = "AppInfoDialog"
    }

    // 本フラグメント用job
    private val _job = Job()

    // 本スコープ用のコンテキスト
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + _job

    // TODO ViewModel作成
    private lateinit var _routeDatabaseDao: RouteDatabaseDao
    private lateinit var _dataExport: DataExport
    private lateinit var _exportLauncher: ActivityResultLauncher<Intent>
    private lateinit var _dataInport: DataImport
    private lateinit var _importLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = requireNotNull(this.activity).application
        _routeDatabaseDao =
            RouteDatabase.getInstance(application).routeDatabaseDao
        _dataExport = DataExport()
        setExportDirectorySelectedEvent()
        _dataInport = DataImport(_routeDatabaseDao)
        setImportDirectorySelectedEvent()
    }

    /**
     * onCreatePreferences
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_fragment, rootKey)

        // アプリケーション情報設定押下
        findPreference<Preference>("app_info")?.setOnPreferenceClickListener {
            Log.d("Debug", "アプリケーション情報押下")
            showAppInfoDialog()
            true
        }

        // リストア押下
        findPreference<Preference>("restore")?.setOnPreferenceClickListener {
            Log.d("Debug", "リストア押下")
            //Toast.makeText(context, "リストア：未実装", Toast.LENGTH_SHORT).show()
            _dataInport.launchFileSelector(_importLauncher)
            true
        }

        // バックアップ押下
        findPreference<Preference>("backup")?.setOnPreferenceClickListener {
            Log.d("Debug", "バックアップ押下")
            _dataExport.launchFolderSelector(_exportLauncher)

            true
        }
    }

    /**
     * アプリケーション情報ダイアログ表示
     */
    private fun showAppInfoDialog() {
        // 前回分削除
        FragmentUtil.deletePrevDialog(AppInfoDialogTag, parentFragmentManager)

        // ダイアログ表示
        val dialog = AppInfoDialogFragment()
        dialog.showNow(parentFragmentManager, AppInfoDialogTag)
    }

    // region エクスポート処理(

    /**
     * ファイル出力先選択インテントの選択後イベント登録
     */
    private fun setExportDirectorySelectedEvent() {
        _exportLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
                if (result?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                result.data?.let { data: Intent ->
                    val uri: Uri = data.data ?: return@let
                    exportCore(uri)
                }
            }
    }

    /**
     * エクスポートメイン処理
     */
    private fun exportCore(uri: Uri) {
        try {
            launch {
                val allRouteLists = _routeDatabaseDao.getAllRouteListItemsSync()
                val allRouteDetailItems = _routeDatabaseDao.getAllRouteDetailItemsSync()
                val allFilterInfoItems = _routeDatabaseDao.getAllFilterInfoItemSync()
                context?.contentResolver?.openOutputStream(uri)
                    .use { outputStream ->
                        outputStream?.let {
                            _dataExport.export(
                                it,
                                allRouteLists,
                                allRouteDetailItems,
                                allFilterInfoItems
                            )
                        }
                    }
            }
            launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Data Export Complete!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Data Export Failed!!!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // endregion エクスポート処理

    // region インポート処理

    /**
     * ファイル選択インテントの選択後イベント登録
     */
    private fun setImportDirectorySelectedEvent() {
        _importLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
                if (result?.resultCode != Activity.RESULT_OK) {
                    return@registerForActivityResult
                }
                result.data?.let { data: Intent ->
                    val uri: Uri = data.data ?: return@let
                    importCore(uri)
                }
            }
    }

    /**
     * インポートメイン処理
     */
    private fun importCore(uri: Uri) {
        try {
            launch {
                context?.contentResolver?.openInputStream(uri)
                    .use { inputStream ->
                        inputStream?.let {
                            _dataInport.import(it)
                        }
                    }
            }
            launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Data Import Complete!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Data Export Failed!!!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // endregion インポート処理
}