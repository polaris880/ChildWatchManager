#!/bin/bash

# 快速构建脚本 - 适用于已安装Android Studio的环境

echo "=========================================="
echo "儿童观看时长管理APP - 快速构建"
echo "=========================================="

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查JAVA_HOME
if [ -z "$JAVA_HOME" ]; then
    echo -e "${YELLOW}警告: JAVA_HOME未设置${NC}"
    echo "请确保已安装JDK 11或更高版本"
fi

# 检查ANDROID_HOME
if [ -z "$ANDROID_HOME" ]; then
    echo -e "${YELLOW}警告: ANDROID_HOME未设置${NC}"
    echo "请确保已安装Android SDK"
    echo "或使用Android Studio打开项目构建"
fi

# 进入项目目录
PROJECT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$PROJECT_DIR"

echo ""
echo "项目目录: $PROJECT_DIR"
echo ""

# 检查是否有gradlew
if [ ! -f "gradlew" ]; then
    echo "生成Gradle Wrapper..."
    gradle wrapper --gradle-version 7.5.1
    if [ $? -ne 0 ]; then
        echo -e "${RED}错误: 无法生成Gradle Wrapper${NC}"
        echo "请确保已安装Gradle或使用Android Studio"
        exit 1
    fi
fi

# 设置执行权限
chmod +x gradlew

echo "开始构建Debug APK..."
echo ""

# 执行构建
./gradlew clean assembleDebug

# 检查构建结果
if [ $? -eq 0 ]; then
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

    if [ -f "$APK_PATH" ]; then
        APK_SIZE=$(du -h "$APK_PATH" | cut -f1)

        echo ""
        echo -e "${GREEN}=========================================="
        echo "✅ 构建成功！"
        echo -e "==========================================${NC}"
        echo ""
        echo "APK信息:"
        echo "  - 位置: $PROJECT_DIR/$APK_PATH"
        echo "  - 大小: $APK_SIZE"
        echo ""

        # 复制到项目根目录
        cp "$APK_PATH" "ChildWatchManager.apk"
        echo "已复制到: $PROJECT_DIR/ChildWatchManager.apk"
        echo ""

        echo "下一步:"
        echo "  1. 将APK传输到当贝盒子"
        echo "  2. 安装并启动APP"
        echo "  3. 设置家长密码"
        echo "  4. 配置观看时长"
        echo ""
    else
        echo -e "${RED}错误: 找不到APK文件${NC}"
        exit 1
    fi
else
    echo ""
    echo -e "${RED}=========================================="
    echo "❌ 构建失败"
    echo -e "==========================================${NC}"
    echo ""
    echo "请检查以下可能的问题:"
    echo "  1. JAVA_HOME是否正确设置"
    echo "  2. ANDROID_HOME是否正确设置"
    echo "  3. Android SDK是否已安装（API 33）"
    echo "  4. 网络连接是否正常（下载依赖）"
    echo ""
    echo "建议: 使用Android Studio打开项目，它会自动处理所有依赖"
    exit 1
fi
