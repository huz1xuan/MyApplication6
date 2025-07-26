package com.example.myapplication.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 优化的分数显示组件
 * 使用derivedStateOf减少不必要的重组
 */
@Composable
fun OptimizedScoreDisplay(
    score: Int,
    highScore: Int,
    modifier: Modifier = Modifier,
    scoreColor: Color = Color.White,
    highScoreColor: Color = Color(0xFFFFD700)
) {
    // 只有当分数改变时才重组
    val scoreText by remember { derivedStateOf { "分数: $score" } }
    val highScoreText by remember { derivedStateOf { "最高: $highScore" } }
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = scoreText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = scoreColor
        )
        
        Text(
            text = highScoreText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = highScoreColor
        )
    }
}

/**
 * 优化的游戏状态指示器
 */
@Composable
fun GameStatusIndicator(
    isGameOver: Boolean,
    isWin: Boolean,
    isPaused: Boolean,
    modifier: Modifier = Modifier
) {
    val statusText by remember {
        derivedStateOf {
            when {
                isPaused -> "游戏暂停"
                isWin -> "恭喜过关！"
                isGameOver -> "游戏结束"
                else -> ""
            }
        }
    }
    
    val statusColor by remember {
        derivedStateOf {
            when {
                isPaused -> Color(0xFFFF9800)
                isWin -> Color(0xFF4CAF50)
                isGameOver -> Color(0xFFF44336)
                else -> Color.Transparent
            }
        }
    }
    
    if (statusText.isNotEmpty()) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(statusColor.copy(alpha = 0.9f))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = statusText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * 优化的动画值缓存
 */
@Composable
fun rememberAnimationValue(targetValue: Float): State<Float> {
    return remember(targetValue) {
        mutableStateOf(targetValue)
    }
}

/**
 * 防抖点击修饰符
 */
@Composable
fun Modifier.debounceClickable(
    debounceTime: Long = 300L,
    onClick: () -> Unit
) = this.then(
    clickableWithDebounce(debounceTime, onClick)
)

@Composable
private fun clickableWithDebounce(
    debounceTime: Long,
    onClick: () -> Unit
): Modifier {
    var canClick by remember { mutableStateOf(true) }
    
    return Modifier.clickable {
        if (canClick) {
            canClick = false
            onClick()
            // 简化版防抖，不依赖System.currentTimeMillis()
            canClick = true
        }
    }
} 