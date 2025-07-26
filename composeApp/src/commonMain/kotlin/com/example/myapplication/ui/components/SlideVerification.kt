package com.example.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// 滑动验证组件（使用标准Slider）
@Composable
fun SlideVerification(
    onVerificationComplete: () -> Unit,
    onDismiss: () -> Unit,
    gameTitle: String
) {
    var sliderValue by remember { mutableStateOf(0f) }
    var isCompleted by remember { mutableStateOf(false) }
    var showWelcome by remember { mutableStateOf(false) }
    
    // 每次显示时重置状态
    LaunchedEffect(Unit) {
        sliderValue = 0f
        isCompleted = false
        showWelcome = false
    }
    
    // 完成动画
    val completionScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    
    // 欢迎文字动画
    val welcomeAlpha by animateFloatAsState(
        targetValue = if (showWelcome) 1f else 0f,
        animationSpec = tween(800)
    )
    
    // 监听滑动完成
    LaunchedEffect(sliderValue) {
        if (sliderValue >= 0.95f && !isCompleted) {
            isCompleted = true
        }
    }
    
    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            delay(600)
            showWelcome = true
            delay(1800)
            onVerificationComplete()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000).copy(alpha = 0.98f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(600.dp)
                .scale(completionScale),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFF1E1E1E))
                    .padding(36.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                // 标题
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🎮 滑动验证",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "将滑块滑动到终点以解锁游戏",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                // 使用标准Slider
                Box(
                    modifier = Modifier
                        .width(480.dp)
                        .height(72.dp)
                        .background(
                            Color(0xFF2A2A2A),
                            RoundedCornerShape(36.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Slider(
                        value = sliderValue,
                        onValueChange = { newValue ->
                            // 只允许向右滑动，不允许回退
                            if (newValue >= sliderValue && !isCompleted) {
                                sliderValue = newValue
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF4CAF50),
                            activeTrackColor = Color(0xFF4CAF50),
                            inactiveTrackColor = Color.Transparent
                        )
                    )
                    
                    // 滑动提示图标
                    if (sliderValue < 0.1f) {
                        Text(
                            text = "👉",
                            fontSize = 24.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.offset(x = 150.dp)
                        )
                    }
                }
                
                // 提示文字
                Text(
                    text = if (isCompleted) "✅ 验证完成！" else "👉 滑动滑块到终点",
                    fontSize = 18.sp,
                    color = if (isCompleted) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.8f),
                    fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Medium
                )
                
                // 欢迎文字
                if (showWelcome) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🎉 欢迎进入",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.alpha(welcomeAlpha)
                        )
                        Text(
                            text = gameTitle,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            modifier = Modifier.alpha(welcomeAlpha)
                        )
                    }
                }
                
                // 取消按钮
                if (!isCompleted) {
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            "取消",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
} 