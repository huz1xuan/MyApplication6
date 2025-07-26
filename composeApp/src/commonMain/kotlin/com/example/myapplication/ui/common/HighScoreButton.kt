package com.example.myapplication.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HighScoreButton(
    score: Int,
    themeColor: Color,
    modifier: Modifier = Modifier,
    showAbove: Boolean = false
) {
    var showScore by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // 如果需要在上方显示分数
        if (showScore && showAbove) {
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.9f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "🏆 最高分: $score",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        IconButton(
            onClick = { showScore = !showScore },
            modifier = Modifier
                .size(40.dp)
                .background(themeColor.copy(alpha = 0.8f), CircleShape)
        ) {
            Text(
                text = "🏆",
                fontSize = 20.sp,
                color = Color.White
            )
        }

        // 如果需要在下方显示分数（默认）
        if (showScore && !showAbove) {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.9f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "🏆 最高分: $score",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
} 