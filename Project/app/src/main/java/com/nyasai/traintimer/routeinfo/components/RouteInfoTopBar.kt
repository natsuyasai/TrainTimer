package com.nyasai.traintimer.routeinfo.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

/**
 * 路線詳細画面のトップバーComposable
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteInfoTopBar(
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    TopAppBar(
        title = { Text("路線詳細") },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
            }
        },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Default.FilterAlt, contentDescription = "フィルタ")
            }
        }
    )
}