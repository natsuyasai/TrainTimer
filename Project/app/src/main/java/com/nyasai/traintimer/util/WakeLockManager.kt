package com.nyasai.traintimer.util

import android.content.Context
import android.os.PowerManager
import android.util.Log

/**
 * 画面のスリープを防止するWakeLock管理クラス
 */
class WakeLockManager(private val context: Context) {
    
    private var wakeLock: PowerManager.WakeLock? = null
    private val tag = "TrainTimer:WakeLock"
    
    /**
     * WakeLockを取得してスリープを防止
     */
    fun acquireWakeLock() {
        try {
            if (wakeLock?.isHeld != true) {
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                wakeLock = powerManager.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    tag
                )
                wakeLock?.acquire(10 * 60 * 1000L) // 最大10分間
                Log.d(tag, "WakeLock acquired")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to acquire WakeLock", e)
        }
    }
    
    /**
     * WakeLockを解放してスリープを許可
     */
    fun releaseWakeLock() {
        try {
            wakeLock?.takeIf { it.isHeld }?.let {
                it.release()
                wakeLock = null
                Log.d(tag, "WakeLock released")
            }
        } catch (e: Exception) {
            Log.e(tag, "Failed to release WakeLock", e)
        }
    }
    
    /**
     * WakeLockが有効かどうかを確認
     */
    fun isWakeLockHeld(): Boolean {
        return wakeLock?.isHeld == true
    }
}