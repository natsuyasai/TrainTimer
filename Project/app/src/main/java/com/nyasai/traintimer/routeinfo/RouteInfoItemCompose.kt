package com.nyasai.traintimer.routeinfo

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
import com.nyasai.traintimer.database.RouteDetail
import java.time.LocalTime

/**
 * 路線詳細情報アイテムのComposeコンポーネント
 */
@Composable
fun RouteInfoItemCompose(
    routeDetail: RouteDetail,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 1.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.colorNormalBackground)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 時刻
            Text(
                text = routeDetail.departureTime ?: "",
                color = colorResource(id = getComposeTimeTextColor(routeDetail.departureTime ?: "")),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxHeight()
                    .wrapContentHeight()
            )

            // 種別
            Text(
                text = routeDetail.trainType ?: "",
                color = colorResource(id = getComposeTimeTextColor(routeDetail.departureTime ?: "", R.color.textRed)),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .wrapContentHeight()
            )

            // 方面
            Text(
                text = routeDetail.destination ?: "",
                color = colorResource(id = getComposeTimeTextColor(routeDetail.departureTime ?: "")),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .wrapContentHeight()
            )
        }
    }
}

/**
 * 時刻情報テキストカラー取得（Compose用）
 */
private fun getComposeTimeTextColor(departureTimeStr: String, defaultColor: Int = R.color.textColor): Int {
    return try {
        val departureTime = LocalTime.parse(departureTimeStr)
        val now = LocalTime.now()
        // 0～3時以外は現在時刻未満を無効に設定
        if (departureTime.hour !in 0..3 && departureTime < now) {
            R.color.textGray
        } else if (departureTime.hour in 0..3) {
            // 0～3時なら日付変更前と後で比較方法変更(日付は変わっているが終電がまだの場合をケア)
            if (now.hour in 0..3) {
                if (departureTime < now) {
                    R.color.textGray
                } else {
                    defaultColor
                }
            } else {
                defaultColor
            }
        } else {
            defaultColor
        }
    } catch (e: Exception) {
        defaultColor
    }
}