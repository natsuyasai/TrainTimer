package com.nyasai.traintimer.setting

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.nyasai.traintimer.R
import com.nyasai.traintimer.util.FragmentUtil

/**
 * 設定画面フラグメント
 */
class PreferenceFragment : PreferenceFragmentCompat() {

    private companion object {
        // アプリケーション情報ダイアログ
        const val AppInfoDialogTag = "AppInfoDialog"
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
        val dialog = AppInfoDialogFlagment()
        dialog.showNow(parentFragmentManager, AppInfoDialogTag)
    }
}