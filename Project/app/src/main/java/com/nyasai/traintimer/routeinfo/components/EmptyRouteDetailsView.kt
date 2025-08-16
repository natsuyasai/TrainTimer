package com.nyasai.traintimer.routeinfo.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import com.nyasai.traintimer.R

/**
 * 空データ時の表示Composable
 */
@Composable
fun EmptyRouteDetailsView(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "路線詳細データがありません",
                color = colorResource(id = R.color.textGray),
                textAlign = TextAlign.Center
            )
        }
    }
}