package com.nyasai.traintimer.commonparts

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * 表示状態更新用
 */
@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}