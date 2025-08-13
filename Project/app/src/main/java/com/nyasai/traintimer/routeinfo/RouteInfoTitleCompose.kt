package com.nyasai.traintimer.routeinfo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * 路線情報タイトルのComposeコンポーネント
 */
@Composable
fun RouteInfoTitleCompose(
    routeListItem: RouteListItem?,
    currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType,
    onTitleClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTitleClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.colorNormalBackground)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 路線名
            Text(
                text = routeListItem?.routeName ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // 駅名
            Text(
                text = routeListItem?.stationName ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // 方面
            Text(
                text = routeListItem?.destination ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // ダイヤ種別
            Text(
                text = getDiagramTypeText(currentDiagramType),
                color = colorResource(id = getDiagramTypeColor(currentDiagramType)),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * ダイヤ種別テキスト取得
 */
private fun getDiagramTypeText(diagramType: YahooRouteInfoGetter.Companion.DiagramType): String {
    return when (diagramType) {
        YahooRouteInfoGetter.Companion.DiagramType.Weekday -> "[平日]"
        YahooRouteInfoGetter.Companion.DiagramType.Saturday -> "[土曜]"
        YahooRouteInfoGetter.Companion.DiagramType.Holiday -> "[日曜・祝日]"
        else -> ""
    }
}

/**
 * ダイヤ種別カラー取得
 */
private fun getDiagramTypeColor(diagramType: YahooRouteInfoGetter.Companion.DiagramType): Int {
    return when (diagramType) {
        YahooRouteInfoGetter.Companion.DiagramType.Weekday -> R.color.weekday
        YahooRouteInfoGetter.Companion.DiagramType.Saturday -> R.color.saturday
        YahooRouteInfoGetter.Companion.DiagramType.Holiday -> R.color.sunday
        else -> R.color.weekday
    }
}