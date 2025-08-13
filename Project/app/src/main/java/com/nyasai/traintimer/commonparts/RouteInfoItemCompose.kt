package com.nyasai.traintimer.commonparts

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteDetail

/**
 * 路線詳細情報のアイテムのComposeコンポーネント
 */
@Composable
fun RouteInfoItemCompose(
    routeDetail: RouteDetail,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.colorNormalBackground)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 時刻（20%）
            Text(
                text = routeDetail.departureTime ?: "--:--",
                color = getTimeTextColor(routeDetail),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.2f)
            )
            
            // 種別（40%）
            Text(
                text = routeDetail.trainType ?: "",
                color = colorResource(id = R.color.textRed),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.4f)
            )
            
            // 方面（30%）
            Text(
                text = routeDetail.destination ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.3f)
            )
        }
    }
}

/**
 * 時刻の色を取得（元のdata bindingのgetTimeTextColorロジックを移植）
 */
@Composable
private fun getTimeTextColor(routeDetail: RouteDetail): androidx.compose.ui.graphics.Color {
    // 簡略化：現在時刻との比較ロジックは後で実装
    return colorResource(id = R.color.textColor)
}

@Preview(showBackground = true)
@Composable
private fun RouteInfoItemComposePreview() {
    val sampleRouteDetail = RouteDetail().apply {
        departureTime = "08:30"
        trainType = "快速"
        destination = "新宿方面"
    }
    
    RouteInfoItemCompose(
        routeDetail = sampleRouteDetail
    )
}