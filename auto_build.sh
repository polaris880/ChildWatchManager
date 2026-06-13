#!/bin/bash

# ============================================
# 当贝盒子儿童观看时长管理APP - 自动构建脚本
# ============================================
#
# 使用方法：
# 1. 在Mac/Linux上运行此脚本
# 2. 需要预先安装: Java JDK 11+, Android SDK
# 3. 运行: chmod +x auto_build.sh && ./auto_build.sh
#
# ============================================

set -e

echo "=========================================="
echo "  当贝盒子儿童观看时长管理APP"
echo "  自动构建脚本"
echo "=========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# 检查Java
echo "检查Java环境..."
if ! command -v java &> /dev/null; then
    echo -e "${RED}错误: 未找到Java${NC}"
    echo "请安装Java JDK 11或更高版本"
    echo ""
    echo "安装方法:"
    echo "  macOS:   brew install openjdk@11"
    echo "  Ubuntu:  sudo apt install openjdk-11-jdk"
    echo "  Windows: 从 https://adoptium.net 下载安装"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo -e "${GREEN}✓ Java $JAVA_VERSION 已安装${NC}"

# 检查Android SDK
echo ""
echo "检查Android SDK..."

# 尝试常见的SDK路径
SDK_PATHS=(
    "$ANDROID_HOME"
    "$HOME/Android/Sdk"
    "$HOME/Library/Android/sdk"
    "/usr/local/android-sdk"
    "/opt/android-sdk"
)

ANDROID_SDK=""
for path in "${SDK_PATHS[@]}"; do
    if [ -d "$path" ] && [ -f "$path/platform-tools/adb" ]; then
        ANDROID_SDK="$path"
        break
    fi
done

if [ -z "$ANDROID_SDK" ]; then
    echo -e "${YELLOW}警告: 未找到Android SDK${NC}"
    echo ""
    echo "是否要自动安装Android SDK? (y/n)"
    read -r response
    if [[ "$response" =~ ^[Yy]$ ]]; then
        echo ""
        echo "正在安装Android SDK..."
        ANDROID_SDK="$HOME/Android/Sdk"
        mkdir -p "$ANDROID_SDK"

        # 下载Android SDK命令行工具
        echo "下载Android SDK工具..."
        cd /tmp
        if [[ "$OSTYPE" == "darwin"* ]]; then
            SDK_URL="https://dl.google.com/android/repository/commandlinetools-mac-9477386_latest.zip"
        else
            SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip"
        fi

        wget -q "$SDK_URL" -O cmdline-tools.zip || curl -fsSL "$SDK_URL" -o cmdline-tools.zip

        if [ -f cmdline-tools.zip ]; then
            mkdir -p "$ANDROID_SDK/cmdline-tools"
            unzip -q cmdline-tools.zip -d "$ANDROID_SDK/cmdline-tools/"
            mv "$ANDROID_SDK/cmdline-tools/cmdline-tools" "$ANDROID_SDK/cmdline-tools/latest"

            # 设置环境变量
            export ANDROID_HOME="$ANDROID_SDK"
            export PATH="$ANDROID_SDK/cmdline-tools/latest/bin:$ANDROID_SDK/platform-tools:$PATH"

            # 安装必要的SDK组件
            echo "安装SDK组件..."
            yes | sdkmanager --licenses > /dev/null 2>&1
            sdkmanager "platforms;android-33" "build-tools;33.0.2" "platform-tools"

            echo -e "${GREEN}✓ Android SDK 安装完成${NC}"
        else
            echo -e "${RED}错误: 无法下载Android SDK${NC}"
            exit 1
        fi
    else
        echo ""
        echo "请手动安装Android SDK:"
        echo "1. 访问 https://developer.android.com/studio"
        echo "2. 下载Android Studio或Command Line Tools"
        echo "3. 设置ANDROID_HOME环境变量"
        exit 1
    fi
else
    echo -e "${GREEN}✓ Android SDK 已安装: $ANDROID_SDK${NC}"
fi

# 设置环境变量
export ANDROID_HOME="$ANDROID_SDK"
export PATH="$ANDROID_SDK/cmdline-tools/latest/bin:$ANDROID_SDK/platform-tools:$PATH"

# 进入项目目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo ""
echo "项目目录: $(pwd)"
echo ""

# 检查gradle wrapper
if [ ! -f "gradlew" ]; then
    echo "生成Gradle Wrapper..."
    gradle wrapper --gradle-version 7.5.1
    chmod +x gradlew
fi

echo "=========================================="
echo "开始构建APK..."
echo "=========================================="
echo ""

# 清理并构建
./gradlew clean assembleDebug

# 检查构建结果
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK_PATH" ]; then
    APK_SIZE=$(du -h "$APK_PATH" | cut -f1)

    echo ""
    echo -e "${GREEN}=========================================="
    echo "✅ APK 构建成功！"
    echo -e "==========================================${NC}"
    echo ""
    echo "APK信息:"
    echo "  位置: $(pwd)/$APK_PATH"
    echo "  大小: $APK_SIZE"
    echo ""

    # 复制到根目录
    cp "$APK_PATH" "../ChildWatchManager.apk"
    echo "已复制到: $(dirname $(pwd))/ChildWatchManager.apk"
    echo ""

    echo "=========================================="
    echo "下一步: 安装到当贝盒子"
    echo "=========================================="
    echo ""
    echo "方法1: ADB安装（推荐）"
    echo "  1. 当贝盒子开启ADB调试"
    echo "  2. adb connect <当贝盒子IP>:5555"
    echo "  3. adb install ChildWatchManager.apk"
    echo ""
    echo "方法2: U盘安装"
    echo "  1. 复制APK到U盘"
    echo "  2. 插入当贝盒子"
    echo "  3. 使用文件管理器安装"
    echo ""

else
    echo ""
    echo -e "${RED}=========================================="
    echo "❌ 构建失败"
    echo -e "==========================================${NC}"
    echo ""
    echo "请查看上方的错误信息"
    echo "常见问题:"
    echo "  1. 网络连接问题（下载依赖失败）"
    echo "  2. SDK版本不匹配"
    echo "  3. 磁盘空间不足"
    echo ""
    echo "建议使用Android Studio打开项目构建"
    exit 1
fi
