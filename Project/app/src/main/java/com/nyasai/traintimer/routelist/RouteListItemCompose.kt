package com.nyasai.traintimer.routelist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    OutlinedCard (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.colorNormalBackground)
        ),
        border = BorderStroke(width = 1.dp, color = Color.Black),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 路線名
            Text(
                text = routeListItem.routeName ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 駅名（メイン表示）
            Text(
                text = routeListItem.stationName ?: "",
                color = colorResource(id = R.color.textColor),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
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

@Preview(showBackground = true)
@Composable
private fun RouteListItemComposePreview() {
    val sampleRoute = RouteListItem().apply {
        routeName = "JR山手線"
        stationName = "新宿駅"
        destination = "池袋・上野方面"
    }
    
    RouteListItemCompose(
        routeListItem = sampleRoute
    )
}