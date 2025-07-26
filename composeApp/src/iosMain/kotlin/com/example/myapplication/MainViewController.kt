package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController { 
    MaterialTheme {
        // iOS平台的安全区域处理
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()  // iOS安全区域适配
        ) {
            App(
                onBackPressed = {
                    // iOS平台使用系统返回手势，通常不需要处理
                }
            )
        }
    }
}