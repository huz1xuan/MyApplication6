package com.example.myapplication.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import kotlin.math.*
import kotlin.random.Random

private const val PI_F = PI.toFloat()

// 可爱风背景（2048）
@Composable
fun CuteBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val bubbleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 渐变背景
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFF3E0),
                        Color(0xFFFFE0B2),
                        Color(0xFFFFCC80)
                    )
                )
            )

            // 可爱的泡泡
            val bubbles = List(20) {
                Triple(
                    Random.nextFloat() * size.width,
                    Random.nextFloat() * size.height,
                    Random.nextFloat() * 30f + 10f
                )
            }

            bubbles.forEach { (x, y, radius) ->
                drawCircle(
                    color = Color(0xFFFFB74D).copy(alpha = 0.3f),
                    radius = radius * bubbleScale,
                    center = Offset(x, y)
                )
            }

            // 装饰性曲线
            for (i in 0..5) {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, size.height * (0.2f + i * 0.15f))
                        cubicTo(
                            size.width * 0.3f, size.height * (0.1f + i * 0.15f),
                            size.width * 0.7f, size.height * (0.3f + i * 0.15f),
                            size.width, size.height * (0.2f + i * 0.15f)
                        )
                    },
                    color = Color(0xFFFFB74D).copy(alpha = 0.1f),
                    style = Stroke(width = 20f)
                )
            }
        }
        content()
    }
}

// 贪吃蛇的简约背景
@Composable
fun CyberpunkBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 渐变背景
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E)
                    )
                )
            )

            // 简单的网格
            val gridSize = 100f
            for (x in -1..(size.width / gridSize).toInt() + 1) {
                for (y in -1..(size.height / gridSize).toInt() + 1) {
                    drawLine(
                        color = Color(0xFF304675).copy(alpha = 0.1f),
                        start = Offset(x * gridSize, 0f),
                        end = Offset(x * gridSize, size.height),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color(0xFF304675).copy(alpha = 0.1f),
                        start = Offset(0f, y * gridSize),
                        end = Offset(size.width, y * gridSize),
                        strokeWidth = 1f
                    )
                }
            }

            // 柔和的光晕
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF304675).copy(alpha = 0.05f),
                        Color.Transparent
                    )
                ),
                radius = size.width * 0.8f,
                center = Offset(size.width * 0.5f, size.height * 0.5f)
            )
        }
        content()
    }
}

// 清新自然风背景（记忆翻牌）
@Composable
fun NatureBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val leafRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing)
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 渐变背景
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E9),
                        Color(0xFFC8E6C9),
                        Color(0xFFA5D6A7)
                    )
                )
            )

            // 飘落的叶子
            repeat(15) { i ->
                val x = cos(i * PI_F / 7.5f) * size.width * 0.4f + size.width * 0.5f
                val y = sin(i * PI_F / 7.5f) * size.height * 0.4f + size.height * 0.5f
                
                rotate(degrees = leafRotation + i * 24f, pivot = Offset(x, y)) {
                    drawPath(
                        path = Path().apply {
                            moveTo(x, y - 20f)
                            cubicTo(
                                x + 20f, y - 15f,
                                x + 20f, y + 15f,
                                x, y + 20f
                            )
                            cubicTo(
                                x - 20f, y + 15f,
                                x - 20f, y - 15f,
                                x, y - 20f
                            )
                        },
                        color = Color(0xFF81C784).copy(alpha = 0.3f)
                    )
                }
            }

            // 装饰性曲线
            for (i in 0..3) {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, size.height * (0.3f + i * 0.2f))
                        cubicTo(
                            size.width * 0.3f, size.height * (0.2f + i * 0.2f),
                            size.width * 0.7f, size.height * (0.4f + i * 0.2f),
                            size.width, size.height * (0.3f + i * 0.2f)
                        )
                    },
                    color = Color(0xFF81C784).copy(alpha = 0.2f),
                    style = Stroke(width = 30f)
                )
            }
        }
        content()
    }
}

// 优雅设置页面背景
@Composable
fun ElegantSettingsBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    // 慢速浮动动画
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing) // 更慢的动画
        )
    )
    
    // 柔和的缩放动画
    val gentleScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = FastOutSlowInEasing), // 更慢的脉冲
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // 渐变旋转
    val gradientRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing) // 非常慢的旋转
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 更浅的渐变背景
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA), // 几乎白色
                        Color(0xFFF1F3F4), // 浅灰白
                        Color(0xFFE8EAED), // 淡灰
                        Color(0xFFDEE1E6)  // 稍深的淡灰
                    ),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height)
                )
            )
            
            // 旋转的径向渐变叠加
            rotate(degrees = gradientRotation, pivot = Offset(size.width * 0.5f, size.height * 0.3f)) {
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF4285F4).copy(alpha = 0.03f), // 非常淡的蓝色
                            Color(0xFF34A853).copy(alpha = 0.02f), // 非常淡的绿色
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.3f, size.height * 0.2f),
                        radius = size.width * 0.8f
                    )
                )
            }

            // 慢速浮动的装饰圆圈
            repeat(8) { i ->
                val angle = floatingOffset + i * PI.toFloat() / 4f
                val baseRadius = size.width * 0.35f
                val x = cos(angle) * baseRadius + size.width * 0.5f
                val y = sin(angle * 0.7f) * baseRadius * 0.6f + size.height * 0.5f
                
                val circleAlpha = (sin(floatingOffset + i) + 1f) / 2f * 0.06f // 非常淡的透明度
                val circleRadius = (20f + i * 5f) * gentleScale
                
                drawCircle(
                    color = Color(0xFF5F6368).copy(alpha = circleAlpha),
                    radius = circleRadius,
                    center = Offset(x, y)
                )
            }
            
            // 精美的几何装饰
            repeat(12) { i ->
                val angle = i * 30f + gradientRotation * 0.1f
                val distance = size.width * 0.4f
                val x = cos(angle * PI / 180f).toFloat() * distance + size.width * 0.5f
                val y = sin(angle * PI / 180f).toFloat() * distance * 0.8f + size.height * 0.5f
                
                // 小的装饰菱形
                rotate(degrees = angle + floatingOffset * 10f, pivot = Offset(x, y)) {
                    val diamondSize = 8f * gentleScale
                    drawPath(
                        path = Path().apply {
                            moveTo(x, y - diamondSize)
                            lineTo(x + diamondSize, y)
                            lineTo(x, y + diamondSize)
                            lineTo(x - diamondSize, y)
                            close()
                        },
                        color = Color(0xFF9AA0A6).copy(alpha = 0.08f)
                    )
                }
            }
            
            // 柔和的波浪装饰
            repeat(5) { i ->
                val waveY = size.height * (0.2f + i * 0.15f) + sin(floatingOffset + i) * 20f
                drawPath(
                    path = Path().apply {
                        moveTo(0f, waveY)
                        var x = 0f
                        while (x <= size.width) {
                            val y = waveY + sin((x / 200f + floatingOffset + i)) * 15f
                            lineTo(x, y)
                            x += 10f
                        }
                    },
                    color = Color(0xFF4285F4).copy(alpha = 0.04f),
                    style = Stroke(width = 3f)
                )
            }
            
            // 边缘渐变
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF).copy(alpha = 0.7f),
                        Color.Transparent,
                        Color.Transparent,
                        Color(0xFF000000).copy(alpha = 0.02f)
                    )
                )
            )
        }
        content()
    }
}

// 炸裂风背景（节奏游戏）
@Composable
fun ExplosiveBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 渐变背景
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF311B92),
                        Color(0xFF4527A0),
                        Color(0xFF1A237E)
                    )
                )
            )

            // 能量脉冲
            repeat(8) { i ->
                val angle = i * PI_F / 4f
                val x = cos(angle) * size.width * 0.4f + size.width * 0.5f
                val y = sin(angle) * size.height * 0.4f + size.height * 0.5f
                
                scale(pulseScale, pivot = Offset(x, y)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFE040FB).copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        radius = 50f,
                        center = Offset(x, y)
                    )
                }
            }

            // 动态线条
            repeat(20) {
                val startX = Random.nextFloat() * size.width
                val startY = Random.nextFloat() * size.height
                val endX = startX + Random.nextFloat() * 100f - 50f
                val endY = startY + Random.nextFloat() * 100f - 50f
                
                drawLine(
                    color = Color(0xFFE040FB).copy(alpha = 0.2f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 3f
                )
            }

            // 光晕效果
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE040FB).copy(alpha = 0.1f),
                        Color.Transparent
                    )
                ),
                radius = size.width * 0.8f * pulseScale,
                center = Offset(size.width / 2f, size.height / 2f)
            )
        }
        content()
    }
} 