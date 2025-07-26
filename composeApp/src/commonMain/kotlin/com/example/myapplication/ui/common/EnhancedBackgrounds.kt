package com.example.myapplication.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random

@Composable
fun EnhancedMainMenuBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val particles = remember { createParticleSystem(200) }
    
    // 主要动画值
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing)
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawEnhancedBackground(
                size = size,
                time = time,
                pulseScale = pulseScale,
                rotation = rotation,
                particles = particles
            )
        }
        
        content()
    }
}

private fun DrawScope.drawEnhancedBackground(
    size: Size,
    time: Float,
    pulseScale: Float,
    rotation: Float,
    particles: List<EnhancedParticle>
) {
    // 多层渐变背景
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF0D47A1),
                Color(0xFF1565C0), 
                Color(0xFF1976D2),
                Color(0xFF1A237E)
            ),
            center = Offset(size.width * 0.3f, size.height * 0.2f),
            radius = size.width * 0.8f
        )
    )
    
    // 添加第二层渐变
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFF3F51B5).copy(alpha = 0.3f),
                Color(0xFF9C27B0).copy(alpha = 0.2f),
                Color(0xFFE91E63).copy(alpha = 0.1f)
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        )
    )
    
    // 动态几何图形
    drawGeometricShapes(size, time, rotation)
    
    // 增强的粒子系统
    drawEnhancedParticles(size, time, pulseScale, particles)
    
    // 光晕效果
    drawGlowEffects(size, time, pulseScale)
    
    // 网格图案
    drawNetworkPattern(size, time)
}

private fun DrawScope.drawGeometricShapes(size: Size, time: Float, rotation: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // 旋转的六边形
    rotate(rotation, Offset(centerX, centerY)) {
        val hexRadius = size.width * 0.15f
        val path = createHexagonPath(centerX, centerY, hexRadius)
        drawPath(
            path = path,
            color = Color(0xFF64B5F6).copy(alpha = 0.1f),
            style = Stroke(width = 3.dp.toPx())
        )
    }
    
    // 浮动的圆形
    repeat(3) { i ->
        val angle = time * 360f + i * 120f
        val radius = size.width * 0.3f
        val x = centerX + cos(angle * PI / 180f).toFloat() * radius
        val y = centerY + sin(angle * PI / 180f).toFloat() * radius * 0.5f
        
        drawCircle(
            color = when(i) {
                0 -> Color(0xFFFF9800).copy(alpha = 0.15f)
                1 -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                else -> Color(0xFF9C27B0).copy(alpha = 0.15f)
            },
            radius = 80.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawEnhancedParticles(
    size: Size, 
    time: Float, 
    pulseScale: Float, 
    particles: List<EnhancedParticle>
) {
    particles.forEachIndexed { index, particle ->
        val animatedTime = (time + index * 0.01f) % 1f
        val x = (particle.startX + cos(animatedTime * 2 * PI + particle.phase) * particle.amplitude).toFloat()
        val y = (particle.startY - animatedTime * size.height * particle.speed) % size.height
        
        val alpha = sin(animatedTime * PI).toFloat() * 0.7f
        val currentScale = pulseScale * particle.scale
        
        // 粒子带光晕效果
        drawCircle(
            color = particle.color.copy(alpha = alpha * 0.3f),
            radius = particle.size * currentScale * 2f,
            center = Offset(x, y)
        )
        
        drawCircle(
            color = particle.color.copy(alpha = alpha),
            radius = particle.size * currentScale,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawGlowEffects(size: Size, time: Float, pulseScale: Float) {
    val glowCenters = listOf(
        Offset(size.width * 0.2f, size.height * 0.3f),
        Offset(size.width * 0.8f, size.height * 0.7f),
        Offset(size.width * 0.6f, size.height * 0.2f)
    )
    
    glowCenters.forEachIndexed { index, center ->
        val animatedRadius = (50 + sin(time * 2 * PI + index).toFloat() * 20) * pulseScale
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFFF).copy(alpha = 0.1f),
                    Color(0xFFE3F2FD).copy(alpha = 0.05f),
                    Color.Transparent
                ),
                radius = animatedRadius * 3
            ),
            radius = animatedRadius * 3,
            center = center
        )
    }
}

private fun DrawScope.drawNetworkPattern(size: Size, time: Float) {
    val gridSize = 100.dp.toPx()
    val cols = (size.width / gridSize).toInt() + 1
    val rows = (size.height / gridSize).toInt() + 1
    
    // 绘制连接线
    for (i in 0 until cols) {
        for (j in 0 until rows) {
            val x1 = i * gridSize
            val y1 = j * gridSize + sin(time * 2 * PI + i * 0.5f).toFloat() * 20f
            
            // 连接到右边的点
            if (i < cols - 1) {
                val x2 = (i + 1) * gridSize  
                val y2 = j * gridSize + sin(time * 2 * PI + (i + 1) * 0.5f).toFloat() * 20f
                val alpha = (sin(time * PI + i + j).toFloat() + 1f) / 2f * 0.1f
                
                drawLine(
                    color = Color(0xFF90CAF9).copy(alpha = alpha),
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 1.dp.toPx()
                )
            }
            
            // 连接到下面的点
            if (j < rows - 1) {
                val x2 = i * gridSize
                val y2 = (j + 1) * gridSize + sin(time * 2 * PI + i * 0.5f).toFloat() * 20f
                val alpha = (sin(time * PI + i + j).toFloat() + 1f) / 2f * 0.1f
                
                drawLine(
                    color = Color(0xFF90CAF9).copy(alpha = alpha),
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}

private fun createHexagonPath(centerX: Float, centerY: Float, radius: Float): Path {
    val path = Path()
    for (i in 0..5) {
        val angle = i * 60f * PI / 180f
        val x = centerX + cos(angle).toFloat() * radius
        val y = centerY + sin(angle).toFloat() * radius
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun createParticleSystem(count: Int): List<EnhancedParticle> {
    return List(count) { 
        EnhancedParticle(
            startX = Random.nextFloat() * 1000f,
            startY = Random.nextFloat() * 2000f,
            size = Random.nextFloat() * 8f + 2f,
            speed = Random.nextFloat() * 0.5f + 0.2f,
            amplitude = Random.nextFloat() * 100f + 50f,
            phase = Random.nextFloat() * 2f * PI.toFloat(),
            scale = Random.nextFloat() * 0.5f + 0.5f,
            color = when (Random.nextInt(5)) {
                0 -> Color(0xFFFFD700)
                1 -> Color(0xFF87CEEB)
                2 -> Color(0xFFDDA0DD)
                3 -> Color(0xFF98FB98)
                else -> Color(0xFFFFB6C1)
            }
        )
    }
}

private data class EnhancedParticle(
    val startX: Float,
    val startY: Float,
    val size: Float,
    val speed: Float,
    val amplitude: Float,
    val phase: Float,
    val scale: Float,
    val color: Color
) 