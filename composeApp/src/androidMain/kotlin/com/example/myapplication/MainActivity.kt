package com.example.myapplication

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.compose.BackHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // 锁定竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        setContent {
            MaterialTheme {
                AppWithBackHandler()
            }
        }
    }
}

@Composable
fun AppWithBackHandler() {
    var shouldExit by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // 处理返回键事件
    BackHandler {
        shouldExit = true
    }
    
    // 使用WindowInsets处理状态栏
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        App(
            onBackPressed = {
                shouldExit = true
            }
        )
    }
    
    // 如果用户确认退出，显示退出确认对话框
    if (shouldExit) {
        ExitConfirmationDialog(
            onConfirm = {
                shouldExit = false
                // 真正退出应用
                (context as? ComponentActivity)?.finish()
            },
            onDismiss = {
                shouldExit = false
            }
        )
    }
}

@Composable
fun ExitConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("退出应用")
        },
        text = {
            Text("确定要退出应用吗？")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("退出")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}