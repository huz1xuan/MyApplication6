package com.example.myapplication.ui

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import com.example.myapplication.ui.common.EnhancedMainMenuBackground


@Composable
fun MainMenuScreen(
    onGameSelected: (Int) -> Unit,
    onSettingsClick: () -> Unit = {}
) {
    
    EnhancedMainMenuBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        // 游戏标题和卡片
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // 标题 - 增强动画效果
            val titleInfiniteTransition = rememberInfiniteTransition()
            val titleGlow by titleInfiniteTransition.animateFloat(
                initialValue = 0.8f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2500, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            val titleAlpha by titleInfiniteTransition.animateFloat(
                initialValue = 0.7f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            
            Text(
                "🎮 迷你游戏集",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier
                    .scale(1.2f * titleGlow)
                    .alpha(titleAlpha)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 游戏卡片网格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GameCard(
                    title = "2048",
                    description = "数字合并挑战",
                    backgroundColor = Color(0xFFFFA000),
                    onClick = { onGameSelected(0) }
                )
                GameCard(
                    title = "贪吃蛇",
                    description = "经典街机游戏",
                    backgroundColor = Color(0xFF4CAF50),
                    onClick = { onGameSelected(1) }
                )
            }
            
            // 记忆翻牌游戏 - 居中显示
            GameCard(
                title = "记忆翻牌",
                description = "考验记忆力",
                backgroundColor = Color(0xFF7B1FA2),
                onClick = { onGameSelected(2) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 设置按钮
            GameCard(
                title = "⚙️ 设置",
                description = "游戏配置选项",
                backgroundColor = Color(0xFF607D8B),
                onClick = onSettingsClick,
                modifier = Modifier.width(160.dp)
            )
        }
    }
}

@Composable
private fun AnimatedBackground() {
    val particles = remember { List(50) { 
        Particle(
            x = Random.nextFloat() * 1000,
            y = Random.nextFloat() * 2000,
            size = Random.nextFloat() * 10 + 5,
            speed = Random.nextFloat() * 2 + 1,
            color = when (Random.nextInt(4)) {
                0 -> Color(0xFFFFA000)
                1 -> Color(0xFF4CAF50)
                2 -> Color(0xFF7B1FA2)
                else -> Color(0xFFE91E63)
            }.copy(alpha = 0.3f)
        )
    } }
    
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = (particle.y - time * size.height * particle.speed) % size.height
            drawCircle(
                color = particle.color,
                radius = particle.size,
                center = Offset(particle.x, y)
            )
            
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
}

@Composable
private fun GameCard(
    title: String,
    description: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isHovered by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    
    // 入场动画
    val infiniteTransition = rememberInfiniteTransition()
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // 悬停动画
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isHovered -> 1.08f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (isHovered) 8f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (isHovered) 1f else 0.9f,
        animationSpec = tween(300)
    )
    
    // 颜色动画
    val animatedColor by animateColorAsState(
        targetValue = if (isHovered) {
            backgroundColor.copy(red = minOf(1f, backgroundColor.red + 0.1f))
        } else {
            backgroundColor
        },
        animationSpec = tween(300),
        label = "card_color"
    )

    Card(
        modifier = modifier
            .size(160.dp)
            .scale(scale)
            .rotate(rotation)
            .alpha(alpha)
            .clickable {
                onClick()
            }
            .pointerHoverIcon(PointerIcon.Hand),
        colors = CardDefaults.cardColors(
            containerColor = animatedColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 12.dp else 6.dp
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = shimmer * 0.1f),
                            Color.Transparent,
                            Color.White.copy(alpha = (1f - shimmer) * 0.1f)
                        ),
                        start = Offset(-100f, -100f),
                        end = Offset(100f, 100f)
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.scale(if (isHovered) 1.1f else 1f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.alpha(if (isHovered) 1f else 0.8f)
                )
            }
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val color: Color
) 