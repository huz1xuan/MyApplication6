package com.example.myapplication.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.util.SettingsManager
import com.example.myapplication.ui.common.ElegantSettingsBackground

@Composable
fun SettingsScreen(
    settingsManager: SettingsManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var soundEnabled by remember { mutableStateOf(settingsManager.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(settingsManager.vibrationEnabled) }
    var gameDifficulty by remember { mutableStateOf(settingsManager.gameDifficulty) }
    var themeMode by remember { mutableStateOf(settingsManager.themeMode) }
    var animationsEnabled by remember { mutableStateOf(settingsManager.animationsEnabled) }
    var gameSpeed by remember { mutableStateOf(settingsManager.gameSpeed) }
    var autoSaveEnabled by remember { mutableStateOf(settingsManager.autoSaveEnabled) }
    var showHints by remember { mutableStateOf(settingsManager.showHints) }
    var language by remember { mutableStateOf(settingsManager.language) }
    var accessibilityMode by remember { mutableStateOf(settingsManager.accessibilityMode) }
    var dataUsage by remember { mutableStateOf(settingsManager.dataUsage) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    ElegantSettingsBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 标题栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                        .scale(1.1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回",
                        tint = Color(0xFF1976D2)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "⚙️ 设置中心",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E2E2E),
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 游戏设置组
                item {
                    SettingsGroup(title = "🎮 游戏设置") {
                        // 游戏难度
                        SettingCard {
                            Column {
                                SettingItem(
                                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                                    title = "游戏难度",
                                    subtitle = "调整游戏挑战程度"
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("简单", "普通", "困难").forEachIndexed { index, text ->
                                        FilterChip(
                                            onClick = { 
                                                gameDifficulty = index
                                                settingsManager.gameDifficulty = index
                                            },
                                            label = { Text(text) },
                                            selected = gameDifficulty == index,
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF4285F4),
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 音效设置组
                item {
                    SettingsGroup(title = "🔊 音效设置") {
                        // 音效开关
                        SettingCard {
                            SettingItem(
                                icon = Icons.AutoMirrored.Filled.VolumeUp,
                                title = "音效",
                                subtitle = "游戏音效和背景音乐",
                                trailing = {
                                    Switch(
                                        checked = soundEnabled,
                                        onCheckedChange = { 
                                            soundEnabled = it
                                            settingsManager.soundEnabled = it
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color(0xFF4285F4),
                                            checkedTrackColor = Color(0xFF4285F4).copy(alpha = 0.3f),
                                            uncheckedThumbColor = Color(0xFF9AA0A6),
                                            uncheckedTrackColor = Color(0xFF9AA0A6).copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            )
                        }

                        // 震动反馈
                        SettingCard {
                            SettingItem(
                                icon = Icons.Default.Vibration,
                                title = "震动反馈",
                                subtitle = "触摸和操作时的震动",
                                trailing = {
                                    Switch(
                                        checked = vibrationEnabled,
                                        onCheckedChange = { 
                                            vibrationEnabled = it
                                            settingsManager.vibrationEnabled = it
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color(0xFF34A853),
                                            checkedTrackColor = Color(0xFF34A853).copy(alpha = 0.3f),
                                            uncheckedThumbColor = Color(0xFF9AA0A6),
                                            uncheckedTrackColor = Color(0xFF9AA0A6).copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                // 界面设置组
                item {
                    SettingsGroup(title = "🎨 界面设置") {
                        // 主题模式
                        SettingCard {
                            Column {
                                SettingItem(
                                    icon = Icons.Default.Palette,
                                    title = "主题模式",
                                    subtitle = "选择应用外观"
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("跟随系统", "浅色", "深色").forEachIndexed { index, text ->
                                        FilterChip(
                                            onClick = { 
                                                themeMode = index
                                                settingsManager.themeMode = index
                                            },
                                            label = { Text(text) },
                                            selected = themeMode == index,
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF9C27B0),
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // 动画效果
                        SettingCard {
                            SettingItem(
                                icon = Icons.Default.Animation,
                                title = "动画效果",
                                subtitle = "界面动画和过渡效果",
                                trailing = {
                                    Switch(
                                        checked = animationsEnabled,
                                        onCheckedChange = { 
                                            animationsEnabled = it
                                            settingsManager.animationsEnabled = it
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color(0xFFEA4335),
                                            checkedTrackColor = Color(0xFFEA4335).copy(alpha = 0.3f),
                                            uncheckedThumbColor = Color(0xFF9AA0A6),
                                            uncheckedTrackColor = Color(0xFF9AA0A6).copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            )
                        }

                        // 语言设置
                        SettingCard {
                            Column {
                                SettingItem(
                                    icon = Icons.Default.Language,
                                    title = "语言设置",
                                    subtitle = "选择应用语言"
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("简体中文", "English", "日本語").forEachIndexed { index, text ->
                                        FilterChip(
                                            onClick = { 
                                                language = index
                                                settingsManager.language = index
                                            },
                                            label = { Text(text) },
                                            selected = language == index,
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFFFF9800),
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 游戏体验组
                item {
                    SettingsGroup(title = "🎯 游戏体验") {
                        // 游戏速度
                        SettingCard {
                            Column {
                                SettingItem(
                                    icon = Icons.Default.Speed,
                                    title = "游戏速度",
                                    subtitle = "调整游戏进行速度: ${(gameSpeed * 10).toInt() / 10.0}x"
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Slider(
                                    value = gameSpeed,
                                    onValueChange = { 
                                        gameSpeed = it
                                        settingsManager.gameSpeed = it
                                    },
                                    valueRange = 0.5f..2.0f,
                                    steps = 14,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFF4285F4),
                                        activeTrackColor = Color(0xFF4285F4),
                                        inactiveTrackColor = Color(0xFF4285F4).copy(alpha = 0.3f)
                                    )
                                )
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("0.5x", fontSize = 12.sp, color = Color.Gray)
                                    Text("2.0x", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                        }

                        // 自动保存
                        SettingCard {
                            SettingItem(
                                icon = Icons.Default.Save,
                                title = "自动保存",
                                subtitle = "自动保存游戏进度",
                                trailing = {
                                    Switch(
                                        checked = autoSaveEnabled,
                                        onCheckedChange = { 
                                            autoSaveEnabled = it
                                            settingsManager.autoSaveEnabled = it
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color(0xFFFBBC04),
                                            checkedTrackColor = Color(0xFFFBBC04).copy(alpha = 0.3f),
                                            uncheckedThumbColor = Color(0xFF9AA0A6),
                                            uncheckedTrackColor = Color(0xFF9AA0A6).copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            )
                        }

                        // 游戏提示
                        SettingCard {
                            SettingItem(
                                icon = Icons.AutoMirrored.Filled.Help,
                                title = "游戏提示",
                                subtitle = "显示操作提示和帮助",
                                trailing = {
                                    Switch(
                                        checked = showHints,
                                        onCheckedChange = { 
                                            showHints = it
                                            settingsManager.showHints = it
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color(0xFF9C27B0),
                                            checkedTrackColor = Color(0xFF9C27B0).copy(alpha = 0.3f),
                                            uncheckedThumbColor = Color(0xFF9AA0A6),
                                            uncheckedTrackColor = Color(0xFF9AA0A6).copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                // 辅助功能组
                item {
                    SettingsGroup(title = "♿ 辅助功能") {
                        // 无障碍模式
                        SettingCard {
                            SettingItem(
                                icon = Icons.Default.Accessibility,
                                title = "无障碍模式",
                                subtitle = "增强可访问性支持",
                                trailing = {
                                    Switch(
                                        checked = accessibilityMode,
                                        onCheckedChange = { 
                                            accessibilityMode = it
                                            settingsManager.accessibilityMode = it
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color(0xFF00BCD4),
                                            checkedTrackColor = Color(0xFF00BCD4).copy(alpha = 0.3f),
                                            uncheckedThumbColor = Color(0xFF9AA0A6),
                                            uncheckedTrackColor = Color(0xFF9AA0A6).copy(alpha = 0.3f)
                                        )
                                    )
                                }
                            )
                        }

                        // 数据使用
                        SettingCard {
                            Column {
                                SettingItem(
                                    icon = Icons.Default.DataUsage,
                                    title = "数据使用",
                                    subtitle = "控制数据使用量"
                                )
                                
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("低", "中", "高").forEachIndexed { index, text ->
                                        FilterChip(
                                            onClick = { 
                                                dataUsage = index
                                                settingsManager.dataUsage = index
                                            },
                                            label = { Text(text) },
                                            selected = dataUsage == index,
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFF4CAF50),
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 系统设置组
                item {
                    SettingsGroup(title = "⚙️ 系统设置") {
                        // 重置设置
                        SettingCard {
                            SettingItem(
                                icon = Icons.Default.Restore,
                                title = "重置设置",
                                subtitle = "恢复所有设置到默认值",
                                onClick = { showResetDialog = true }
                            )
                        }

                        // 关于应用
                        SettingCard {
                            SettingItem(
                                icon = Icons.Default.Info,
                                title = "关于应用",
                                subtitle = "版本信息和开发者信息",
                                onClick = { showAboutDialog = true }
                            )
                        }
                    }
                }

                // 底部间距
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // 重置确认对话框
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("重置设置", fontWeight = FontWeight.Bold) },
            text = { Text("确定要将所有设置恢复到默认值吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        settingsManager.resetToDefaults()
                        // 更新UI状态
                        soundEnabled = true
                        vibrationEnabled = true
                        gameDifficulty = 1
                        themeMode = 0
                        animationsEnabled = true
                        gameSpeed = 1.0f
                        autoSaveEnabled = true
                        showHints = true
                        language = 0
                        accessibilityMode = false
                        dataUsage = 1
                        showResetDialog = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 关于应用对话框
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("关于应用", fontWeight = FontWeight.Bold) },
            text = { 
                Column {
                    Text("🎮 迷你游戏集")
                    Text("版本: 1.0.0")
                    Text("开发者: 游戏工作室")
                    Text("")
                    Text("包含游戏:")
                    Text("• 2048 数字合并")
                    Text("• 贪吃蛇")
                    Text("• 记忆翻牌")
                    Text("")
                    Text("感谢您的使用！")
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("确定")
                }
            }
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E2E2E),
            modifier = Modifier
                .background(
                    Color.White.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun SettingCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing)
    )
    
    val animatedScale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .alpha(animatedAlpha),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.95f),
                            Color.White.copy(alpha = 0.85f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF5F6368),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF202124)
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color(0xFF5F6368)
            )
        }
        
        trailing?.invoke()
    }
} 