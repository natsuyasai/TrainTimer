package com.nyasai.traintimer.commonparts

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Assert
import org.junit.jupiter.api.Test
import androidx.lifecycle.Observer;
import com.nyasai.traintimer.testutil.InstantExecutorExtension
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
internal class CommonLoadingViewModelTest{

    /**
     * ローディング表示_成功_テキスト変更
     */
    @Test
    fun showLoading_success_change_text() {
        val target = CommonLoadingViewModel()
        target.changeText("")

        // オブザーバ設定
        val observer = mock<Observer<String>>()
        target.loadingText.observeForever(observer)

        val testMessage = "Test!!!!"
        target.showLoading(testMessage)

        Assert.assertEquals(target.isVisible(), true)
        verify(observer).onChanged(testMessage)
    }
}