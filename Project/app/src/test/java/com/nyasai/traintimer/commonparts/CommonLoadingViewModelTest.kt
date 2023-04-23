package com.nyasai.traintimer.commonparts

import androidx.lifecycle.Observer
import com.nyasai.traintimer.testutil.InstantExecutorExtension
import com.nyasai.traintimer.testutil.TestObserver
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verify
import org.mockito.kotlin.mock

@ExtendWith(InstantExecutorExtension::class)
internal class CommonLoadingViewModelTest {

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

        Assertions.assertEquals(target.isVisible.value, true)
        verify(observer).onChanged(testMessage)
    }

    /**
     * ローディング表示_成功_テキスト未変更
     */
    @Test
    fun showLoading_success_no_change_text() {
        val target = CommonLoadingViewModel()
        target.changeText("")

        // オブザーバ設定
        val observer = mock<Observer<String>>()
        target.loadingText.observeForever(observer)

        target.showLoading()

        Assertions.assertEquals(target.isVisible.value, true)
        verify(observer).onChanged("読み込み中")
    }

    /**
     * ローディング終了_成功
     */
    @Test
    fun closeLoading_success() {
        val target = CommonLoadingViewModel()

        // オブザーバ設定
        val maxCountObserver = TestObserver<Int>()
        target.maxCount.observeForever(maxCountObserver)
        val currentCountObserver = TestObserver<Int>()
        target.maxCount.observeForever(currentCountObserver)

        target.updateMaxCountFromBackgroundTask(100)
        target.incrementCurrentCountFromBackgroundTask(1)

        target.closeLoading()

        maxCountObserver.await()
        currentCountObserver.await()

        Assertions.assertEquals(target.isVisible.value, false)
        Assertions.assertEquals(maxCountObserver.get(), 0)
        Assertions.assertEquals(currentCountObserver.get(), 0)
    }

    /**
     * 最大件数更新_成功
     */
    @Test
    fun updateMaxCountFromBackgroundTask_success() {
        val target = CommonLoadingViewModel()

        // オブザーバ設定
        val maxCountObserver = TestObserver<Int>()
        target.maxCount.observeForever(maxCountObserver)

        target.updateMaxCountFromBackgroundTask(100)

        maxCountObserver.await()

        Assertions.assertEquals(maxCountObserver.get(), 100)
    }

    /**
     * 最大件数インクリメント_成功
     */
    @Test
    fun incrementMaxCountFromBackgroundTask_success() {
        val target = CommonLoadingViewModel()

        // オブザーバ設定
        val maxCountObserver = TestObserver<Int>()
        target.maxCount.observeForever(maxCountObserver)

        target.incrementMaxCountFromBackgroundTask(1)

        maxCountObserver.await()

        Assertions.assertEquals(maxCountObserver.get(), 1)
    }


}