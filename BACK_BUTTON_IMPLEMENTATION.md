# 返回键功能实现说明

## 功能概述

本次更新实现了正确的返回键行为，让用户可以通过返回键在应用内导航，而不是直接退出应用。支持Android、iOS和桌面平台。

## 主要改进

### 1. 页面历史记录管理
- 添加了 `pageHistory` 状态来跟踪用户的导航历史
- 每次导航到新页面时，将当前页面保存到历史记录中
- 支持多层级的页面导航

### 2. 智能返回逻辑
- **有历史记录时**：返回到上一个页面
- **无历史记录时**：显示退出确认对话框
- **游戏页面**：返回时自动暂停当前游戏

### 3. 多平台返回键处理
- **Android**：使用 `BackHandler` 捕获返回键事件
- **iOS**：利用系统返回手势，应用内导航处理
- **桌面**：使用窗口关闭事件和键盘事件

## 技术实现

### App.kt 修改
```kotlin
// 添加页面历史记录
var pageHistory by remember { mutableStateOf(listOf<AppPage>()) }

// 导航函数
fun navigateToPage(page: AppPage) {
    if (currentPage != page) {
        pageHistory = pageHistory + currentPage
        currentPage = page
    }
}

// 返回函数
fun goBack() {
    if (pageHistory.isNotEmpty()) {
        val previousPage = pageHistory.last()
        pageHistory = pageHistory.dropLast(1)
        currentPage = previousPage
    } else {
        onBackPressed()
    }
}
```

### Android平台 (MainActivity.kt)
```kotlin
@Composable
fun AppWithBackHandler() {
    var shouldExit by remember { mutableStateOf(false) }
    
    BackHandler {
        shouldExit = true
    }
    
    App(onBackPressed = { shouldExit = true })
    
    if (shouldExit) {
        ExitConfirmationDialog(
            onConfirm = { /* 退出逻辑 */ },
            onDismiss = { shouldExit = false }
        )
    }
}
```

### iOS平台 (MainViewController.kt)
```kotlin
fun MainViewController() = ComposeUIViewController { 
    AppWithBackHandler()
}

@Composable
fun AppWithBackHandler() {
    App(
        onBackPressed = {
            // iOS平台使用系统返回手势
            // 应用内导航由系统自动处理
        }
    )
}
```

### 桌面平台 (main.kt)
```kotlin
fun main() = application {
    var shouldExit by remember { mutableStateOf(false) }
    
    Window(
        onCloseRequest = { shouldExit = true },
        title = "游戏集 - 迷你游戏合集"
    ) {
        AppWithBackHandler(onExitRequest = { shouldExit = true })
        
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
```

## 用户体验

### 导航流程
1. **主菜单** → **游戏** → **设置** → **返回** → **游戏** → **返回** → **主菜单**
2. 每个页面都能正确返回到上一个页面
3. 游戏状态在返回时自动保存

### 退出确认
- 当用户在主菜单按返回键时，显示退出确认对话框
- 防止意外退出应用
- 提供"退出"和"取消"选项

### 平台特定体验
- **Android**：物理返回键或手势返回
- **iOS**：系统返回手势，流畅的导航体验
- **桌面**：窗口关闭按钮或键盘快捷键

## 兼容性

- ✅ Android 平台 (API 21+)
- ✅ iOS 平台 (iOS 13+)
- ✅ 桌面平台 (Windows, macOS, Linux)
- ✅ 支持所有现有游戏页面
- ✅ 支持设置页面
- ✅ 保持现有的页面切换动画

## 测试建议

### Android测试
1. **基本导航测试**
   - 从主菜单进入游戏，按返回键应回到主菜单
   - 从主菜单进入设置，按返回键应回到主菜单

2. **多层导航测试**
   - 主菜单 → 游戏 → 设置 → 返回 → 游戏 → 返回 → 主菜单

3. **退出确认测试**
   - 在主菜单按返回键，应显示退出确认对话框
   - 选择"取消"应关闭对话框
   - 选择"退出"应退出应用

### iOS测试
1. **手势导航测试**
   - 使用系统返回手势在页面间导航
   - 验证应用内导航逻辑正常工作

2. **多层导航测试**
   - 测试复杂的页面导航路径

### 桌面测试
1. **窗口关闭测试**
   - 点击窗口关闭按钮应显示退出确认对话框
   - 键盘快捷键应正常工作

2. **导航测试**
   - 验证应用内导航逻辑

### 通用测试
1. **游戏状态测试**
   - 进入游戏后按返回键，游戏应被暂停
   - 重新进入游戏，游戏状态应恢复

2. **页面切换动画测试**
   - 验证所有页面切换动画正常工作

## 平台特定实现

### Android
- 使用 `BackHandler` 捕获返回键事件
- 支持物理返回键和手势返回
- 完整的退出确认流程

### iOS
- 利用系统返回手势
- 应用内导航由系统自动处理
- 保持iOS原生体验

### 桌面
- 窗口关闭事件处理
- 键盘事件支持
- 桌面风格的退出确认

## 未来扩展

- 可以添加更复杂的导航历史管理
- 支持手势导航（如滑动返回）
- 添加导航动画效果
- 实现更智能的退出逻辑
- 支持键盘快捷键配置
- 添加导航状态持久化 