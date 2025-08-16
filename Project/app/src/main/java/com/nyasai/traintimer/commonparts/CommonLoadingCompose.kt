package com.nyasai.traintimer.commonparts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyasai.traintimer.R
import java.util.concurrent.locks.ReentrantLock

interface LoadingAction {
    fun show(text: String = "読み込み中")
    fun close()

    fun updateMaxCount(count: Int)

    fun incrementMaxCount(count: Int)

    fun incrementCurrentCount(count: Int)
    fun changeText(text: String)
}

/**
 * ローディング状態のデータクラス
 */
@Stable
class LoadingState {
    var isVisible = mutableStateOf(false)
        private set
    var loadingText = mutableStateOf("読み込み中")
        private set
    var currentCount = mutableIntStateOf(0)
        private set
    var maxCount = mutableIntStateOf(0)
        private set

    constructor()
    constructor(isVisible: Boolean, loadingText: String = "読み込み中", currentCount: Int = 0, maxCount: Int = 0) {
        this.isVisible.value = isVisible
        this.loadingText.value = loadingText
        this.currentCount.intValue = currentCount
        this.maxCount.intValue = maxCount
    }

    private val _maxCountLockObj = ReentrantLock()
    private val _currentCountLockObj = ReentrantLock()

    val actions = object : LoadingAction {
        override fun show(text: String) {
            if (text.isNotEmpty()) {
                loadingText.value = text
            }
            isVisible.value = true
        }

        override fun close() {
            isVisible.value = false
            maxCount.intValue = 0
            currentCount.intValue = 0
            loadingText.value = "読み込み中"
        }

        override fun updateMaxCount(count: Int) {
            exclusiveUpdate(_maxCountLockObj) {
                maxCount.intValue = count
            }
        }

        override fun incrementMaxCount(count: Int) {
            exclusiveUpdate(_maxCountLockObj) {
                maxCount.intValue = maxCount.intValue + count
            }
        }

        override fun incrementCurrentCount(count: Int) {
            exclusiveUpdate(_currentCountLockObj) {
                currentCount.intValue = currentCount.intValue + count
            }
        }

        override fun changeText(text: String) {
            loadingText.value = text
        }
    }

    /**
     * 排他更新
     */
    private fun exclusiveUpdate(lockObj: ReentrantLock, func: () -> Unit) {
        lockObj.lock()
        try {
            func()
        } finally {
            lockObj.unlock()
        }
    }
}

@Composable
fun loadingState(): LoadingState {
    return remember { LoadingState() }
}

/**
 * 共通ローディング画面のComposeコンポーネント
 * State Hoistingパターンを使用してViewModelの依存を除去
 */
@Composable
fun CommonLoadingCompose(
    loadingState: LoadingState,
    modifier: Modifier = Modifier
) {
    if (loadingState.isVisible.value) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.shade))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* タッチイベントを吸収して背景操作を防ぐ */ },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // メッセージ
                Text(
                    text = loadingState.loadingText.value,
                    color = colorResource(id = R.color.textColor),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.textGray))
                        .padding(16.dp)
                )

                // 進捗表示
                if (loadingState.maxCount.intValue > 0) {
                    Text(
                        text = "${loadingState.currentCount.intValue}/${loadingState.maxCount.intValue}",
                        color = colorResource(id = R.color.textColor),
                        fontSize = 30.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(id = R.color.textGray))
                            .padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // プログレスバー
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = colorResource(id = R.color.textColor)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommonLoadingComposePreview() {
    CommonLoadingCompose(
        loadingState = LoadingState(
            isVisible = true,
            loadingText = "データを読み込み中...",
            currentCount = 3,
            maxCount = 10
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun CommonLoadingComposePreviewWithoutProgress() {
    CommonLoadingCompose(
        loadingState = LoadingState(
            isVisible = true,
            loadingText = "処理中...",
            currentCount = 0,
            maxCount = 0
        )
    )
}

