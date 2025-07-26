package com.example.myapplication.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.*

// 2048游戏 - 几何数字主题背景
@Composable
fun GeometricGameBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing)
        )
    )
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGeometricBackground(size, rotation, scale)
        }
        content()
    }
}

// 贪吃蛇游戏 - 自然森林主题背景
@Composable
fun NatureForestBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        )
    )
    
    val leafDance by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawForestBackground(size, waveOffset, leafDance)
        }
        content()
    }
}

// 记忆翻牌游戏 - 梦幻卡片主题背景
@Composable
fun DreamyCardBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val cloudDrift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing)
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDreamyBackground(size, sparkle, cloudDrift)
        }
        content()
    }
}

private fun DrawScope.drawGeometricBackground(size: Size, rotation: Float, scale: Float) {
    // 增强的渐变基础背景
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF0D47A1),
                Color(0xFF1565C0),
                Color(0xFF1976D2),
                Color(0xFF1E88E5),
                Color(0xFF2196F3)
            ),
            start = Offset.Zero,
            end = Offset(size.width, size.height)
        )
    )
    
    // 添加径向渐变叠加层
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF3F51B5).copy(alpha = 0.3f),
                Color(0xFF303F9F).copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = Offset(size.width * 0.3f, size.height * 0.2f),
            radius = size.width * 0.8f
        )
    )
    
    // 增强的几何网格
    val gridSize = 50.dp.toPx()
    for (i in 0..(size.width / gridSize + 1).toInt()) {
        for (j in 0..(size.height / gridSize + 1).toInt()) {
            val x = i * gridSize
            val y = j * gridSize
            
            // 交替绘制方块和圆形，增加透明度
            if ((i + j) % 2 == 0) {
                rotate(rotation + i * 10f, Offset(x, y)) {
                    drawRect(
                        color = Color(0xFF7986CB).copy(alpha = 0.25f),
                        topLeft = Offset(x - 20f, y - 20f),
                        size = Size(40f, 40f)
                    )
                }
            } else {
                drawCircle(
                    color = Color(0xFF9FA8DA).copy(alpha = 0.2f),
                    radius = 15f * scale,
                    center = Offset(x, y)
                )
            }
        }
    }
    
    // 增强的数字主题装饰
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    listOf("2", "4", "8", "16", "32", "64").forEachIndexed { index, number ->
        val angle = rotation + index * 60f
        val radius = size.width * 0.35f
        val x = centerX + cos(angle * PI / 180f).toFloat() * radius
        val y = centerY + sin(angle * PI / 180f).toFloat() * radius
        
        rotate(angle, Offset(x, y)) {
            // 数字背景
            drawRect(
                color = Color(0xFFBBDEFB).copy(alpha = 0.15f),
                topLeft = Offset(x - 30f, y - 30f),
                size = Size(60f, 60f)
            )
            // 数字边框
            drawRect(
                color = Color(0xFF90CAF9).copy(alpha = 0.3f),
                topLeft = Offset(x - 28f, y - 28f),
                size = Size(56f, 56f),
                style = Stroke(width = 2f)
            )
        }
    }
    
    // 添加数字网格纹理
    val smallGridSize = 25.dp.toPx()
    for (i in 0..(size.width / smallGridSize + 1).toInt()) {
        for (j in 0..(size.height / smallGridSize + 1).toInt()) {
            val x = i * smallGridSize
            val y = j * smallGridSize
            val alpha = sin(rotation * PI / 180f + i + j).toFloat() * 0.05f + 0.1f
            
            drawRect(
                color = Color(0xFFE3F2FD).copy(alpha = alpha),
                topLeft = Offset(x - 2f, y - 2f),
                size = Size(4f, 4f)
            )
        }
    }
}

private fun DrawScope.drawForestBackground(size: Size, waveOffset: Float, leafDance: Float) {
    // 森林渐变背景
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1B5E20),
                Color(0xFF2E7D32),
                Color(0xFF388E3C),
                Color(0xFF43A047)
            )
        )
    )
    
    // 山峦轮廓
    val mountainPath = Path()
    mountainPath.moveTo(0f, size.height * 0.7f)
    for (x in 0..size.width.toInt() step 10) {
        val y = size.height * 0.7f + sin(x * 0.01f + waveOffset) * 50f
        mountainPath.lineTo(x.toFloat(), y)
    }
    mountainPath.lineTo(size.width, size.height)
    mountainPath.lineTo(0f, size.height)
    mountainPath.close()
    
    drawPath(
        path = mountainPath,
        color = Color(0xFF1B5E20).copy(alpha = 0.3f)
    )
    
    // 树木剪影
    repeat(8) { i ->
        val x = size.width * (i + 1) / 9f
        val treeHeight = 80f + sin(waveOffset + i) * 20f
        
        // 树干
        drawRect(
            color = Color(0xFF2E2E2E).copy(alpha = 0.4f),
            topLeft = Offset(x - 3f, size.height - treeHeight),
            size = Size(6f, treeHeight * 0.6f)
        )
        
        // 树冠
        drawCircle(
            color = Color(0xFF1B5E20).copy(alpha = 0.3f),
            radius = 25f + leafDance * 2f,
            center = Offset(x, size.height - treeHeight + 10f)
        )
    }
    
    // 草地纹理
    for (x in 0..size.width.toInt() step 20) {
        val grassHeight = 10f + sin(x * 0.1f + waveOffset) * 3f
        drawLine(
            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
            start = Offset(x.toFloat(), size.height - 5f),
            end = Offset(x.toFloat(), size.height - grassHeight),
            strokeWidth = 2.dp.toPx()
        )
    }
}

private fun DrawScope.drawDreamyBackground(size: Size, sparkle: Float, cloudDrift: Float) {
    // 梦幻渐变背景
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFE1BEE7),
                Color(0xFFCE93D8),
                Color(0xFFBA68C8),
                Color(0xFF9C27B0)
            ),
            center = Offset(size.width * 0.3f, size.height * 0.2f),
            radius = size.width * 0.8f
        )
    )
    
    // 云朵
    val cloudColors = listOf(
        Color(0xFFFFFFFF).copy(alpha = 0.1f),
        Color(0xFFF3E5F5).copy(alpha = 0.08f),
        Color(0xFFE8EAF6).copy(alpha = 0.06f)
    )
    
    cloudColors.forEachIndexed { index, color ->
        val offset = cloudDrift + index * 30f
        repeat(5) { i ->
            val x = (offset + i * size.width / 4f) % (size.width + 100f) - 50f
            val y = size.height * (0.2f + index * 0.15f + sin(sparkle * PI + i).toFloat() * 0.05f)
            
            // 绘制云朵（多个重叠的圆形）
            repeat(3) { j ->
                drawCircle(
                    color = color,
                    radius = 40f + j * 15f,
                    center = Offset(x + j * 20f, y)
                )
            }
        }
    }
    
    // 魔法光点
    repeat(20) { i ->
        val angle = sparkle * 2 * PI + i * 0.3f
        val radius = 30f + sin(sparkle * PI + i).toFloat() * 10f
        val x = size.width * 0.5f + cos(angle).toFloat() * (size.width * 0.3f)
        val y = size.height * 0.5f + sin(angle).toFloat() * (size.height * 0.3f)
        
        val alpha = (sin(sparkle * PI + i).toFloat() + 1f) / 2f * 0.3f
        
        drawCircle(
            color = Color(0xFFFFD700).copy(alpha = alpha),
            radius = radius * 0.3f,
            center = Offset(x, y)
        )
    }
    
    // 卡片装饰
    val cardSize = 30f
    repeat(6) { i ->
        val x = size.width * (i + 1) / 7f
        val y = size.height * 0.8f + sin(sparkle * PI + i).toFloat() * 20f
        val rotation = sparkle * 90f + i * 15f
        
        rotate(rotation, Offset(x, y)) {
            drawRoundRect(
                color = Color(0xFFFFFFFF).copy(alpha = 0.1f),
                topLeft = Offset(x - cardSize/2, y - cardSize/2),
                size = Size(cardSize, cardSize),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f)
            )
        }
    }
} 