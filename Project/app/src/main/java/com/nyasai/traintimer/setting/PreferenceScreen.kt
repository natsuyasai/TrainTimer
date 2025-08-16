package com.nyasai.traintimer.setting

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDatabase
import com.nyasai.traintimer.datamigration.DataExport
import com.nyasai.traintimer.datamigration.DataImport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    
    
    // データベースアクセス
    val database = RouteDatabase.getInstance(context).routeDatabaseDao
    
    // データエクスポート/インポート
    val dataExport = remember { DataExport() }
    val dataImport = remember { DataImport(database) }
    
    // ファイル作成用ランチャー（バックアップ）
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleExportResult(result, scope, context, database, dataExport)
    }
    
    // ファイル選択用ランチャー（リストア）
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleImportResult(result, scope, context, dataImport)
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
                        icon = Icons.Default.Backup,
                        onClick = {
                            try {
                                dataExport.launchFolderSelector(exportLauncher)
                            } catch (e: Exception) {
                                Toast.makeText(context, "バックアップ開始に失敗しました: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
                
                item {
                    PreferenceCard(
                        title = "リストア",
                        description = "データをインポート",
                        icon = Icons.Default.Restore,
                        onClick = {
                            try {
                                dataImport.launchFileSelector(importLauncher)
                            } catch (e: Exception) {
                                Toast.makeText(context, "リストア開始に失敗しました: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }
        }
    }
    
    // アプリ情報ダイアログ
    if (showAppInfoDialog) {
        AppInfoDialog(
            isVisible = showAppInfoDialog,
            appTitle = "時刻表",
            twitterInfo = "Twitter:natsuyasai7",
            onDismiss = { showAppInfoDialog = false },
            onClose = { showAppInfoDialog = false }
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
        )
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

/**
 * エクスポート結果の処理
 */
private fun handleExportResult(
    result: androidx.activity.result.ActivityResult,
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context,
    database: com.nyasai.traintimer.database.RouteDatabaseDao,
    dataExport: DataExport
) {
    if (result.resultCode == android.app.Activity.RESULT_OK) {
        result.data?.data?.let { uri ->
            scope.launch {
                try {
                    performDataExport(context, uri, database, dataExport)
                    Toast.makeText(context, "バックアップが完了しました", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "バックアップに失敗しました: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

/**
 * データエクスポートの実行
 */
private suspend fun performDataExport(
    context: android.content.Context,
    uri: android.net.Uri,
    database: com.nyasai.traintimer.database.RouteDatabaseDao,
    dataExport: DataExport
) {
    withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            val allRouteListItems = database.getAllRouteListItemsSync()
            val allRouteDetailItems = database.getAllRouteDetailItemsSync()
            val allFilterInfoItems = database.getAllFilterInfoItemSync()
            
            dataExport.export(
                outputStream,
                allRouteListItems,
                allRouteDetailItems,
                allFilterInfoItems
            )
        }
    }
}

/**
 * インポート結果の処理
 */
private fun handleImportResult(
    result: androidx.activity.result.ActivityResult,
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context,
    dataImport: DataImport
) {
    if (result.resultCode == android.app.Activity.RESULT_OK) {
        result.data?.data?.let { uri ->
            scope.launch {
                try {
                    performDataImport(context, uri, dataImport)
                    Toast.makeText(context, "リストアが完了しました", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "リストアに失敗しました: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

/**
 * データインポートの実行
 */
private suspend fun performDataImport(
    context: android.content.Context,
    uri: android.net.Uri,
    dataImport: DataImport
) {
    withContext(Dispatchers.IO) {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            dataImport.import(inputStream)
        }
    }
}