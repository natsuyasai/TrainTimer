package com.nyasai.traintimer.routeinfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nyasai.traintimer.R
import com.nyasai.traintimer.routeinfo.CountdownState

/**
 * カウントダウン表示のComposable
 */
@Composable
fun CountdownDisplay(
    countdownState: CountdownState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.background(colorResource(id = R.color.colorNormalBackground))
    ) {
        // 次の時刻情報
        Text(
            text = countdownState.nextTimeInfo,
            color = colorResource(id = R.color.textColor),
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(start = 10.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // カウントダウン
        Text(
            text = countdownState.countdownText,
            color = colorResource(id = R.color.textColor),
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier
                .weight(0.7f)
                .fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(end = 10.dp)
        )
    }
}