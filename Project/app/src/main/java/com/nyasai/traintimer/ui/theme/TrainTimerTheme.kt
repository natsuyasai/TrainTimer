package com.nyasai.traintimer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text

private val DarkColorScheme = darkColorScheme()
private val LightColorScheme = lightColorScheme()

@Composable
fun TrainTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun TrainTimerThemePreview() {
    TrainTimerTheme {
        Text(text = "TrainTimer Theme Light")
    }
}

@Preview(showBackground = true)
@Composable
private fun TrainTimerThemeDarkPreview() {
    TrainTimerTheme(darkTheme = true) {
        Text(text = "TrainTimer Theme Dark")
    }
}