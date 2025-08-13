package com.nyasai.traintimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * メインアクティビティ
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // フラグメントコンテナにウィンドウインセットを適切に設定
        val fragmentContainer = findViewById<androidx.fragment.app.FragmentContainerView>(R.id.main_fragment)
        ViewCompat.setOnApplyWindowInsetsListener(fragmentContainer) { view, insets ->
            // システムバーとディスプレイカットアウトのインセットを取得
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val displayCutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout())
            
            // 各方向で最大値を使用してパディングを設定
            view.setPadding(
                maxOf(systemBars.left, displayCutout.left),
                maxOf(systemBars.top, displayCutout.top),
                maxOf(systemBars.right, displayCutout.right),
                systemBars.bottom
            )
            
            // インセットを子ビューに伝播させる
            insets
        }
    }
}