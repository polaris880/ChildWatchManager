# 在线构建APK指南

## 方法1: 使用GitHub Actions（免费）

### 步骤1: 创建GitHub仓库
1. 访问 https://github.com 并登录
2. 点击 "New repository"
3. 仓库名: `ChildWatchManager`
4. 设为 Public
5. 点击 "Create repository"

### 步骤2: 上传项目文件
将 `ChildWatchManager` 文件夹中的所有文件上传到GitHub仓库。

### 步骤3: 创建GitHub Actions工作流
在仓库中创建文件: `.github/workflows/build.yml`

```yaml
name: Build Android APK

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Build Debug APK
      run: ./gradlew assembleDebug

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

### 步骤4: 触发构建
1. 推送代码到GitHub
2. 访问仓库的 "Actions" 标签
3. 等待构建完成（约5-10分钟）
4. 下载生成的APK artifact

---

## 方法2: 使用Replit（在线IDE）

### 步骤1: 创建Replit项目
1. 访问 https://replit.com
2. 创建新的 Repl
3. 选择 "Bash" 语言
4. 命名为 `android-builder`

### 步骤2: 上传代码
将整个 `ChildWatchManager` 文件夹上传到Replit。

### 步骤3: 安装依赖
在Replit Shell中运行:

```bash
# 安装Java
sudo apt update
sudo apt install openjdk-11-jdk

# 安装Android SDK
mkdir -p ~/android-sdk
cd ~/android-sdk
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip commandlinetools-linux-9477386_latest.zip
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/

export ANDROID_HOME=~/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

# 安装SDK组件
yes | sdkmanager --licenses
sdkmanager "platforms;android-33" "build-tools;33.0.2"

# 进入项目并构建
cd ~/ChildWatchManager
chmod +x gradlew
./gradlew assembleDebug
```

### 步骤4: 下载APK
构建完成后，在文件管理器中找到: `app/build/outputs/apk/debug/app-debug.apk`

---

## 方法3: 使用Android Studio（本地，最简单）

### 步骤1: 下载Android Studio
访问: https://developer.android.com/studio

### 步骤2: 安装并打开项目
1. 安装Android Studio
2. 启动后选择 "Open an existing project"
3. 选择 `ChildWatchManager` 文件夹
4. 等待Gradle同步（首次约5-10分钟）

### 步骤3: 构建APK
1. 菜单: Build → Build Bundle(s) / APK(s) → Build APK(s)
2. 等待构建完成
3. 点击 "locate" 打开APK所在文件夹

---

## 推荐方案

**最简单**: 方法3 (Android Studio)
- 无需配置，一键构建
- 最适合初学者

**无需安装**: 方法1 (GitHub Actions)
- 全自动构建
- 无需本地环境
- 免费

**在线开发**: 方法2 (Replit)
- 浏览器中完成所有操作
- 适合快速测试

---

## 获取APK后

### 安装到当贝盒子

**ADB安装（推荐）**:
```bash
# 1. 当贝盒子开启ADB调试
#    设置 → 关于 → 连续点击版本号7次
#    返回 → 开发者选项 → 开启USB调试

# 2. 连接设备
adb connect <当贝盒子IP>:5555

# 3. 安装APK
adb install ChildWatchManager.apk

# 4. 启动应用
adb shell am start -n com.childwatch.manager/.MainActivity
```

**U盘安装**:
1. 复制APK到U盘
2. 将U盘插入当贝盒子
3. 使用文件管理器打开U盘
4. 点击APK文件安装

---

## 常见问题

**Q: 构建失败怎么办？**
A: 检查网络连接，确保能下载Gradle依赖。使用Android Studio通常能自动解决大部分问题。

**Q: APK安装失败？**
A: 确保当贝盒子开启了"允许安装未知来源应用"。

**Q: APP无法正常运行？**
A: 检查是否授予了必要的权限（悬浮窗、后台运行等）。

---

**当前项目已完成，随时可以构建！** 🚀
