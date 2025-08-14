package com.nyasai.traintimer.commonparts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import com.nyasai.traintimer.R

/**
 * 共通ローディング画面のComposeコンポーネント
 */
@Composable
fun CommonLoadingCompose(
    viewModel: CommonLoadingViewModel,
    modifier: Modifier = Modifier
) {
    val isVisible by viewModel.isVisible.observeAsState(false)
    val loadingText by viewModel.loadingText.observeAsState("")
    val currentCount by viewModel.currentCount.observeAsState(0)
    val maxCount by viewModel.maxCount.observeAsState(0)

    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.shade)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // メッセージ
                Text(
                    text = loadingText,
                    color = colorResource(id = R.color.textColor),
                    fontSize = 30.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.textGray))
                        .padding(16.dp)
                )

                // 進捗表示
                if (maxCount.compareTo(0) > 0) {
                    Text(
                        text = "$currentCount/$maxCount",
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

/**
 * ViewModelと統合されたローディングコンポーネント
 */
@Composable
fun CommonLoadingWithViewModel(
    viewModel: CommonLoadingViewModel,
    modifier: Modifier = Modifier
) {
    CommonLoadingCompose(
        viewModel = viewModel,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun CommonLoadingComposePreview() {
    // プレビュー用の模擬実装
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.shade)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // メッセージ
            Text(
                text = "データを読み込み中...",
                color = colorResource(id = R.color.textColor),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.textGray))
                    .padding(16.dp)
            )
            
            // 進捗表示
            Text(
                text = "3/10",
                color = colorResource(id = R.color.textColor),
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = R.color.textGray))
                    .padding(16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // プログレスバー
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = colorResource(id = R.color.textColor)
            )
        }
    }
}