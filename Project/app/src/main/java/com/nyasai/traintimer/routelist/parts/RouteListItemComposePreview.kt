package com.nyasai.traintimer.routelist.parts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.nyasai.traintimer.database.RouteListItem
import com.nyasai.traintimer.ui.theme.TrainTimerTheme

/**
 * RouteListItemComposeの改善されたプレビュー関数群
 * 複数のデータパターンとテーマをテスト
 */

/**
 * プレビュー用のRouteListItemデータプロバイダ
 */
class RouteListItemPreviewParameterProvider : PreviewParameterProvider<RouteListItem> {
    override val values = sequenceOf(
        // 標準的な路線データ
        RouteListItem().apply {
            dataId = 1L
            routeName = "JR山手線"
            stationName = "新宿駅"
            destination = "池袋・上野方面"
            displayColor = Color.Green.toArgb()
            sortIndex = 1L
        },
        
        // 私鉄データ
        RouteListItem().apply {
            dataId = 2L
            routeName = "東急田園都市線"
            stationName = "渋谷駅"
            destination = "中央林間方面"
            displayColor = Color.Blue.toArgb()
            sortIndex = 2L
        },
        
        // 地下鉄データ
        RouteListItem().apply {
            dataId = 3L
            routeName = "東京メトロ丸ノ内線"
            stationName = "東京駅"
            destination = "池袋方面"
            displayColor = Color.Red.toArgb()
            sortIndex = 3L
        },
        
        // 長い名前のテスト
        RouteListItem().apply {
            dataId = 4L
            routeName = "京浜急行電鉄本線"
            stationName = "京急蒲田駅"
            destination = "羽田空港・三崎口方面"
            displayColor = Color.Magenta.toArgb()
            sortIndex = 4L
        },
        
        // 色なしデータ
        RouteListItem().apply {
            dataId = 5L
            routeName = "JR中央線"
            stationName = "立川駅"
            destination = "新宿・東京方面"
            displayColor = null
            sortIndex = 5L
        },
        
        // 空データのテスト
        RouteListItem().apply {
            dataId = 6L
            routeName = ""
            stationName = ""
            destination = ""
            displayColor = null
            sortIndex = 6L
        },
        
        // 一部空データのテスト
        RouteListItem().apply {
            dataId = 7L
            routeName = "JR総武線"
            stationName = ""
            destination = "千葉方面"
            displayColor = Color.Yellow.toArgb()
            sortIndex = 7L
        }
    )
}

/**
 * 基本のプレビュー（ライトテーマ）
 */
@Preview(
    name = "Light Theme - Standard",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun RouteListItemComposePreviewLight() {
    TrainTimerTheme {
        Surface {
            RouteListItemCompose(
                routeListItem = RouteListItem().apply {
                    routeName = "JR山手線"
                    stationName = "新宿駅"
                    destination = "池袋・上野方面"
                    displayColor = Color.Green.toArgb()
                }
            )
        }
    }
}

/**
 * ダークテーマのプレビュー
 */
@Preview(
    name = "Dark Theme - Standard",
    showBackground = true,
    backgroundColor = 0xFF121212
)
@Composable
private fun RouteListItemComposePreviewDark() {
    TrainTimerTheme(darkTheme = true) {
        Surface {
            RouteListItemCompose(
                routeListItem = RouteListItem().apply {
                    routeName = "JR山手線"
                    stationName = "新宿駅"
                    destination = "池袋・上野方面"
                    displayColor = Color.Green.toArgb()
                }
            )
        }
    }
}

/**
 * 複数データパターンのプレビュー
 */
@Preview(
    name = "Multiple Data Patterns",
    showBackground = true,
    heightDp = 600
)
@Composable
private fun RouteListItemComposeMultiplePreview(
    @PreviewParameter(RouteListItemPreviewParameterProvider::class) 
    routeListItem: RouteListItem
) {
    TrainTimerTheme {
        Surface {
            RouteListItemCompose(routeListItem = routeListItem)
        }
    }
}

/**
 * 全データパターンの一覧表示プレビュー
 */
@Preview(
    name = "All Data Patterns",
    showBackground = true,
    heightDp = 800
)
@Composable
private fun RouteListItemComposeAllPatternsPreview() {
    val provider = RouteListItemPreviewParameterProvider()
    
    TrainTimerTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                provider.values.forEach { routeListItem ->
                    Box(modifier = Modifier.padding(vertical = 4.dp)) {
                        RouteListItemCompose(routeListItem = routeListItem)
                    }
                }
            }
        }
    }
}

/**
 * エラー状態のプレビュー
 */
@Preview(
    name = "Error States",
    showBackground = true,
    heightDp = 400
)
@Composable
private fun RouteListItemComposeErrorStatesPreview() {
    TrainTimerTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                // 空値のテスト
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    RouteListItemCompose(
                        routeListItem = RouteListItem().apply {
                            routeName = ""
                            stationName = ""
                            destination = ""
                            displayColor = null
                        }
                    )
                }
                
                // 極端に長い文字列のテスト
                Box(modifier = Modifier.padding(vertical = 4.dp)) {
                    RouteListItemCompose(
                        routeListItem = RouteListItem().apply {
                            routeName = "非常に長い路線名がここに表示されて文字が切れるかどうかをテストしています"
                            stationName = "超長い駅名テスト用データ"
                            destination = "とても長い行先表示のテストデータがここにあります"
                            displayColor = Color.Cyan.toArgb()
                        }
                    )
                }
            }
        }
    }
}

/**
 * 異なるデバイスサイズでのプレビュー
 */
@Preview(
    name = "Small Device",
    showBackground = true,
    widthDp = 320,
    heightDp = 120
)
@Composable
private fun RouteListItemComposeSmallDevicePreview() {
    TrainTimerTheme {
        Surface {
            RouteListItemCompose(
                routeListItem = RouteListItem().apply {
                    routeName = "東急田園都市線"
                    stationName = "渋谷駅"
                    destination = "中央林間方面"
                    displayColor = Color.Blue.toArgb()
                }
            )
        }
    }
}

@Preview(
    name = "Large Device",
    showBackground = true,
    widthDp = 600,
    heightDp = 150
)
@Composable
private fun RouteListItemComposeLargeDevicePreview() {
    TrainTimerTheme {
        Surface {
            RouteListItemCompose(
                routeListItem = RouteListItem().apply {
                    routeName = "東急田園都市線"
                    stationName = "渋谷駅"
                    destination = "中央林間方面"
                    displayColor = Color.Blue.toArgb()
                }
            )
        }
    }
}