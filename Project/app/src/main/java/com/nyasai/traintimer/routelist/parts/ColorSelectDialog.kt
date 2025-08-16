package com.nyasai.traintimer.routelist.parts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.content.res.Configuration

/**
 * 色選択ダイアログ
 */
@Composable
fun ColorSelectDialog(
    isVisible: Boolean,
    currentColor: Int?,
    onColorSelected: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            ColorSelectDialogContent(
                currentColor = currentColor,
                onColorSelected = onColorSelected,
                onDismiss = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorSelectDialogContent(
    currentColor: Int?,
    onColorSelected: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(currentColor) }
    
    // 予定カラーパレット
    val colorPalette = listOf(
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color.Magenta,
        Color.Cyan,
        Color(0xFF00C853), // Light Green
        Color(0xFF2196F3), // Blue
        Color(0xFF9C27B0), // Purple  
        Color(0xFFFF9800), // Orange
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFE91E63), // Pink
        Color(0xFF009688), // Teal
        Color(0xFF8BC34A), // Light Green
        Color(0xFF3F51B5), // Indigo
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF4CAF50)  // Green
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // タイトル
            Text(
                text = "表示色を選択",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // 「色なし」オプション
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedColor = null }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .border(
                            BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
                            shape = CircleShape
                        )
                        .background(Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedColor == null) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "選択済み",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "色なし（デフォルト）",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness, 
                color = MaterialTheme.colorScheme.outline
            )

            // カラーパレット
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                items(colorPalette) { color ->
                    ColorItem(
                        color = color,
                        isSelected = selectedColor == color.toArgb(),
                        onColorClick = { selectedColor = color.toArgb() }
                    )
                }
            }
            
            // ボタン行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "キャンセル",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Button(
                    onClick = {
                        onColorSelected(selectedColor)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "適用"
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onColorClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onColorClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface),
                        shape = CircleShape
                    )
                } else {
                    Modifier.border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = CircleShape
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            // 選択色に応じてアイコンの色を動的に決定
            // 色の明度を簡易的に計算 (R*0.299 + G*0.587 + B*0.114)
            val brightness = color.red * 0.299f + color.green * 0.587f + color.blue * 0.114f
            val iconColor = if (brightness > 0.5f) Color.Black else Color.White
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "選択済み",
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "ライトテーマ")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "ダークテーマ")
@Composable
private fun ColorSelectDialogPreview() {
    MaterialTheme {
        ColorSelectDialog(
            isVisible = true,
            currentColor = Color.Blue.toArgb(),
            onColorSelected = { },
            onDismiss = { }
        )
    }
}

@Preview(showBackground = true, name = "ライトテーマ")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "ダークテーマ")
@Composable
private fun ColorSelectDialogContentPreview() {
    MaterialTheme {
        ColorSelectDialogContent(
            currentColor = Color.Red.toArgb(),
            onColorSelected = { },
            onDismiss = { }
        )
    }
}

@Preview(showBackground = true, name = "ライトテーマ")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "ダークテーマ")
@Composable
private fun ColorSelectDialogNoSelectionPreview() {
    MaterialTheme {
        ColorSelectDialogContent(
            currentColor = null,
            onColorSelected = { },
            onDismiss = { }
        )
    }
}