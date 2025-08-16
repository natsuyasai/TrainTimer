package com.nyasai.traintimer.routelist.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

/**
 * 路線一覧画面のトップバーComposable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteListTopBar(
    isEditMode: Boolean,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(
                if (isEditMode) "路線一覧 (編集モード)" else "路線一覧"
            ) 
        },
        actions = {
            // 路線追加ボタン
            IconButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "路線追加")
            }
            
            // 編集ボタン
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "編集")
            }
            
            // 設定ボタン
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "設定")
            }
        }
    )
}