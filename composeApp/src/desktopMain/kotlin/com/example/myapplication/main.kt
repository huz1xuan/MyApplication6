package com.example.myapplication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    var shouldExit by remember { mutableStateOf(false) }
    
    Window(
        onCloseRequest = { shouldExit = true },
        title = "游戏集 - 迷你游戏合集",
        state = WindowState(
            width = 400.dp,
            height = 800.dp,
            position = WindowPosition(alignment = androidx.compose.ui.Alignment.Center)
        ),
        resizable = false  // 禁止调整窗口大小，保持手机比例
    ) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                // 桌面平台的返回键处理
                AppWithBackHandler(
                    onExitRequest = { shouldExit = true }
                )
            }
        }
        
        // 退出确认对话框
        if (shouldExit) {
            ExitConfirmationDialog(
                onConfirm = { 
                    shouldExit = false
                    exitApplication()
                },
                onDismiss = { shouldExit = false }
            )
        }
    }
}

@Composable
fun AppWithBackHandler(
    onExitRequest: () -> Unit = {}
) {
    // 桌面平台使用键盘事件处理返回键
    App(
        onBackPressed = {
            onExitRequest()
        }
    )
}

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            androidx.compose.material3.Text("退出应用")
        },
        text = {
            androidx.compose.material3.Text("确定要退出应用吗？")
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onConfirm) {
                androidx.compose.material3.Text("退出")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text("取消")
            }
        }
    )
}