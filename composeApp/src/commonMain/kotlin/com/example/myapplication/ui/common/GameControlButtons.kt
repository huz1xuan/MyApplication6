package com.example.myapplication.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GameControlButtons(
    isPaused: Boolean,
    isGameOver: Boolean,
    themeColor: Color,
    onPauseResume: () -> Unit,
    onRestart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        GameButton(
            text = if (isPaused) "继续" else "暂停",
            onClick = onPauseResume,
            enabled = !isGameOver,
            color = themeColor
        )
        
        GameButton(
            text = "重开",
            onClick = onRestart,
            color = themeColor
        )
        
        GameButton(
            text = "返回",
            onClick = onBack,
            color = themeColor
        )
    }
}

@Composable
private fun GameButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.8f)
        ),
        modifier = modifier.width(120.dp)
    ) {
        Text(text)
    }
} 