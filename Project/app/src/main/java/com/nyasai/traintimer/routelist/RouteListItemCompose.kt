package com.nyasai.traintimer.routelist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyasai.traintimer.R
import com.nyasai.traintimer.database.RouteListItem

/**
 * 路線リストアイテムのComposeコンポーネント
 */
@Composable
fun RouteListItemCompose(
    routeListItem: RouteListItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp),
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
                text = routeListItem.routeName ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )

            // 駅名
            Text(
                text = routeListItem.stationName ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            // 方面
            Text(
                text = routeListItem.destination ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}