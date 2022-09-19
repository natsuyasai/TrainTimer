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
import com.nyasai.traintimer.datamigration.DataExport
import com.nyasai.traintimer.util.FragmentUtil
import kotlinx.coroutines.*
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

    private lateinit var _exportLauncher: ActivityResultLauncher<Intent>
    private lateinit var _dataExport: DataExport


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _dataExport = DataExport()
        _exportLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
                if (result?.resultCode == Activity.RESULT_OK) {
                    result.data?.let { data: Intent ->
                        val uri: Uri = data.data ?: return@let
                        val application = requireNotNull(this.activity).application
                        val routeDatabaseDao = RouteDatabase.getInstance(application).routeDatabaseDao
                        try {
                            launch (coroutineContext) {
                            context?.contentResolver?.openOutputStream(uri).use { outputStream ->
                                    outputStream?.let { _dataExport.export(it, routeDatabaseDao) }
                                }
                            }
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Data Export Failed!!!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
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
            Toast.makeText(context, "リストア：未実装", Toast.LENGTH_SHORT).show()
            true
        }

        // バックアップ押下
        findPreference<Preference>("backup")?.setOnPreferenceClickListener {
            Log.d("Debug", "バックアップ押下")
            Toast.makeText(context, "バックアップ：未実装", Toast.LENGTH_SHORT).show()
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
}