package com.example.myapplication.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PauseOverlay(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onBack: (() -> Unit)?,
    themeColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White,
                            Color.White.copy(alpha = 0.9f)
                        )
                    ),
                    RoundedCornerShape(24.dp)
                )
                .padding(32.dp)
        ) {
            Text(
                "游戏暂停",
                fontSize = 32.sp * scale,
                fontWeight = FontWeight.Bold,
                color = themeColor
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onResume,
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                modifier = Modifier.width(200.dp)
            ) {
                Text("继续游戏", fontSize = 18.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                modifier = Modifier.width(200.dp)
            ) {
                Text("重新开始", fontSize = 18.sp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            onBack?.let {
                Button(
                    onClick = it,
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text("返回主菜单", fontSize = 18.sp)
                }
            }
        }
    }
} 