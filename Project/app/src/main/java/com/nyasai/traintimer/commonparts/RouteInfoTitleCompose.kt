package com.nyasai.traintimer.commonparts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.util.YahooRouteInfoGetter

/**
 * 路線詳細情報のタイトル部分のComposeコンポーネント
 */
@Composable
fun RouteInfoTitleCompose(
    routeListItem: RouteListItem,
    currentDiagramType: YahooRouteInfoGetter.Companion.DiagramType,
    onTitleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clickable { onTitleClick() },
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.colorNormalBackground)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 路線名
            Text(
                text = routeListItem.routeName ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 駅名（メイン表示）
            Text(
                text = routeListItem.stationName ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 方面
            Text(
                text = routeListItem.destination ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // ダイヤ種別（平日/土曜/日曜祝日）
            Text(
                text = getDiagramTypeText(currentDiagramType),
                color = colorResource(id = R.color.textColor),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * ダイヤ種別を文字列に変換
 */
private fun getDiagramTypeText(diagramType: YahooRouteInfoGetter.Companion.DiagramType): String {
    return when (diagramType) {
        YahooRouteInfoGetter.Companion.DiagramType.Weekday -> "平日"
        YahooRouteInfoGetter.Companion.DiagramType.Saturday -> "土曜"
        YahooRouteInfoGetter.Companion.DiagramType.Holiday -> "日曜祝日"
        YahooRouteInfoGetter.Companion.DiagramType.Max -> "未設定"
    }
}

@Preview(showBackground = true)
@Composable
private fun RouteInfoTitleComposePreview() {
    val sampleRoute = RouteListItem().apply {
        routeName = "JR山手線"
        stationName = "新宿駅"
        destination = "池袋・上野方面"
    }
    
    RouteInfoTitleCompose(
        routeListItem = sampleRoute,
        currentDiagramType = YahooRouteInfoGetter.Companion.DiagramType.Weekday,
        onTitleClick = { }
    )
}