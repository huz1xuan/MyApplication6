package com.example.myapplication.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val rotationSpeed: Float,
    val color: Color
)

@Composable
fun AnimatedBackground(
    baseColor: Color,
    particleColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val particles = remember {
        List(30) {
            Particle(
                x = Random.nextFloat() * 1000,
                y = Random.nextFloat() * 2000,
                size = Random.nextFloat() * 15 + 5,
                speed = Random.nextFloat() * 2 + 1,
                rotationSpeed = Random.nextFloat() * 2 - 1,
                color = particleColor.copy(alpha = Random.nextFloat() * 0.3f + 0.1f)
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 绘制渐变背景
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        baseColor,
                        baseColor.copy(alpha = 0.8f),
                        baseColor.copy(alpha = 0.6f)
                    )
                )
            )

            particles.forEach { particle ->
                val y = (particle.y - time * size.height * particle.speed) % size.height
                rotate(
                    degrees = (time * 360 * particle.rotationSpeed) % 360,
                    pivot = Offset(particle.x, y)
                ) {
                    drawCircle(
                        color = particle.color,
                        radius = particle.size,
                        center = Offset(particle.x, y)
                    )

                    // 绘制光晕效果
                    drawCircle(
                        color = particle.color.copy(alpha = 0.3f),
                        radius = particle.size * 1.5f,
                        center = Offset(particle.x, y)
                    )
                }

                // 连接临近的粒子
                particles.forEach { other ->
                    val otherY = (other.y - time * size.height * other.speed) % size.height
                    val distance = kotlin.math.sqrt(
                        (particle.x - other.x) * (particle.x - other.x) +
                        (y - otherY) * (y - otherY)
                    )
                    if (distance < 100) {
                        drawLine(
                            color = particle.color.copy(alpha = (1 - distance / 100) * 0.2f),
                            start = Offset(particle.x, y),
                            end = Offset(other.x, otherY),
                            strokeWidth = 1f
                        )
                    }
                }
            }
        }
        content()
    }
} 