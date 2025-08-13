package com.nyasai.traintimer.setting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.datamigration.DataExport
import com.nyasai.traintimer.datamigration.DataImport
import kotlinx.coroutines.launch

/**
 * 設定画面のComposeスクリーン
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // ダイアログの状態
    var showAppInfoDialog by remember { mutableStateOf(false) }
    
    // ViewModelインスタンス
    val appInfoViewModel: AppInfoViewModel = viewModel()
    
    // データエクスポート/インポート
    val dataExport = remember { DataExport() }
    val dataImport = remember { 
        // TODO: RouteDatabase実装が必要
        // DataImport(routeDatabaseDao)
        null
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("設定") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                        }
                    }
                )
            }
        ) { paddingValues ->
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    PreferenceCard(
                        title = "アプリケーション情報",
                        description = "アプリの詳細情報を表示",
                        icon = Icons.Default.Info,
                        onClick = { showAppInfoDialog = true }
                    )
                }
                
                item {
                    PreferenceCard(
                        title = "バックアップ",
                        description = "データをエクスポート",
                        icon = Icons.Default.Share,
                        onClick = {
                            scope.launch {
                                try {
                                    // TODO: バックアップ処理の実装
                                    // dataExport.launchFolderSelector(exportLauncher)
                                } catch (e: Exception) {
                                    // エラーハンドリング
                                }
                            }
                        }
                    )
                }
                
                item {
                    PreferenceCard(
                        title = "リストア",
                        description = "データをインポート",
                        icon = Icons.Default.Search,
                        onClick = {
                            scope.launch {
                                try {
                                    // TODO: リストア処理の実装
                                    // dataImport?.launchFileSelector(importLauncher)
                                } catch (e: Exception) {
                                    // エラーハンドリング
                                }
                            }
                        }
                    )
                }
            }
        }
    }
    
    // アプリ情報ダイアログ
    if (showAppInfoDialog) {
        AppInfoDialogWithViewModel(
            isVisible = showAppInfoDialog,
            onDismiss = { showAppInfoDialog = false },
            viewModel = appInfoViewModel.apply {
                onCloseCallback = {
                    showAppInfoDialog = false
                }
            }
        )
    }
}

/**
 * 設定項目のカード
 */
@Composable
private fun PreferenceCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.colorNormalBackground)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorResource(id = R.color.textColor),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = colorResource(id = R.color.textColor),
                    fontSize = 18.sp
                )
                Text(
                    text = description,
                    color = colorResource(id = R.color.textGray),
                    fontSize = 14.sp
                )
            }
        }
    }
}