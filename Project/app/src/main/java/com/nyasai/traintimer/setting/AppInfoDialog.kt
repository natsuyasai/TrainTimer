package com.nyasai.traintimer.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R

/**
 * アプリケーション情報表示用ダイアログ (Jetpack Compose版)
 */
@Composable
fun AppInfoDialog(
    isVisible: Boolean,
    appTitle: String,
    twitterInfo: String,
    onClose: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = appTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = twitterInfo,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClose()
                        onDismiss()
                    }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }
}

/**
 * ViewModelと統合されたAppInfoDialog
 */
@Composable
fun AppInfoDialogWithViewModel(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    viewModel: AppInfoViewModel = viewModel()
) {
    AppInfoDialog(
        isVisible = isVisible,
        appTitle = viewModel.appTitle,
        twitterInfo = viewModel.twitterInfo,
        onClose = {
            viewModel.onCloseButtonClick()
        },
        onDismiss = onDismiss
    )
}