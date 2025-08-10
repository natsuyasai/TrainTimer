package com.nyasai.traintimer.commonparts

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

/**
 * 表示状態更新用
 */
@BindingAdapter("android:visibility")
fun setVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

/**
 * LiveData<Boolean>用の表示状態更新
 */
@BindingAdapter("android:visibility")
fun setVisibilityLiveData(view: View, visible: LiveData<Boolean>?) {
    visible?.value?.let { isVisible ->
        view.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}