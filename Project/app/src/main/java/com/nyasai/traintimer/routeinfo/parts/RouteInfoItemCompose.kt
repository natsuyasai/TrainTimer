package com.nyasai.traintimer.routeinfo.parts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
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
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onItemClick: ((RouteDetail) -> Unit)? = null
) {
    val isPastTime = isPastTime(routeDetail)
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 0.dp)
            .let { if (isPastTime) it.alpha(0.5f) else it }
            .let { 
                if (onItemClick != null) {
                    it.clickable { onItemClick(routeDetail) }
                } else {
                    it
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                colorResource(id = R.color.colorSortBackground)
            } else {
                colorResource(id = R.color.colorNormalBackground)
            }
        ),
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
                fontSize = 18.sp,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.2f)
            )
            
            // 種別（40%）
            Text(
                text = routeDetail.trainType ?: "",
                color = colorResource(id = R.color.textRed),
                fontSize = 18.sp,
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
                fontSize = 18.sp,
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
private fun getTimeTextColor(routeDetail: RouteDetail): Color {
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
 * 深夜0時～3時は24時～27時として扱う
 */
private fun getTimeStatus(routeDetail: RouteDetail): TimeStatus {
    return try {
        val departureTime = routeDetail.departureTime
        if (departureTime.isEmpty()) {
            TimeStatus.INVALID
        } else {
            val now = LocalTime.now()
            val trainTime = LocalTime.parse(departureTime)
            
            // 分単位で時刻を計算（深夜0時～3時59分は24時～27時59分として扱う）
            val nowMinutes = if (now.hour < 4) {
                (now.hour + 24) * 60 + now.minute
            } else {
                now.hour * 60 + now.minute
            }
            
            val trainMinutes = if (trainTime.hour < 4) {
                (trainTime.hour + 24) * 60 + trainTime.minute
            } else {
                trainTime.hour * 60 + trainTime.minute
            }
            
            when {
                trainMinutes < nowMinutes -> TimeStatus.PAST
                trainMinutes > nowMinutes -> TimeStatus.FUTURE
                else -> TimeStatus.CURRENT
            }
        }
    } catch (e: DateTimeParseException) {
        TimeStatus.INVALID
    } catch (e: Exception) {
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
    
    Column {
        RouteInfoItemCompose(
            routeDetail = sampleRouteDetail,
            isSelected = false
        )
        RouteInfoItemCompose(
            routeDetail = sampleRouteDetail,
            isSelected = true
        )
    }
}