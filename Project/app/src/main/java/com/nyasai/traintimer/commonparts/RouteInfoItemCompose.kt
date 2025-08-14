package com.nyasai.traintimer.commonparts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import java.time.LocalTime
import java.time.format.DateTimeParseException

/**
 * 路線詳細情報のアイテムのComposeコンポーネント
 */
@Composable
fun RouteInfoItemCompose(
    routeDetail: RouteDetail,
    modifier: Modifier = Modifier
) {
    val isPastTime = isPastTime(routeDetail)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .let { if (isPastTime) it.alpha(0.5f) else it },
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
 * 時刻の色を取得（現在時刻との比較による）
 */
@Composable
private fun getTimeTextColor(routeDetail: RouteDetail): androidx.compose.ui.graphics.Color {
    val timeStatus = getTimeStatus(routeDetail)
    return when (timeStatus) {
        TimeStatus.PAST -> colorResource(id = R.color.textGray)
        TimeStatus.FUTURE -> colorResource(id = R.color.textColor)
        TimeStatus.CURRENT -> colorResource(id = R.color.textRed)
        TimeStatus.INVALID -> colorResource(id = R.color.textColor)
    }
}

/**
 * 時刻の状態enum
 */
private enum class TimeStatus {
    PAST, FUTURE, CURRENT, INVALID
}

/**
 * 時刻の状態を取得
 */
private fun getTimeStatus(routeDetail: RouteDetail): TimeStatus {
    return try {
        val departureTime = routeDetail.departureTime
        if (departureTime.isNullOrEmpty()) {
            TimeStatus.INVALID
        } else {
            val now = LocalTime.now()
            val trainTime = LocalTime.parse(departureTime)
            
            when {
                trainTime.isBefore(now) -> TimeStatus.PAST
                trainTime.isAfter(now) -> TimeStatus.FUTURE
                else -> TimeStatus.CURRENT
            }
        }
    } catch (e: DateTimeParseException) {
        TimeStatus.INVALID
    }
}

/**
 * 過去の時刻かどうかを判定
 */
private fun isPastTime(routeDetail: RouteDetail): Boolean {
    return getTimeStatus(routeDetail) == TimeStatus.PAST
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