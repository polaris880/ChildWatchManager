#!/bin/bash

# 设置Android SDK路径（如果已安装）
export ANDROID_HOME=${ANDROID_HOME:-/opt/android-sdk}
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# 进入项目目录
cd /sessions/cool-fervent-cannon/mnt/APK/ChildWatchManager

echo "=========================================="
echo "儿童观看时长管理APP - APK构建脚本"
echo "=========================================="

# 检查是否有gradlew
if [ -f "gradlew" ]; then
    echo "使用gradle wrapper构建..."
    chmod +x gradlew
    ./gradlew assembleDebug
else
    echo "生成gradle wrapper..."
    gradle wrapper --gradle-version 7.5.1

    if [ -f "gradlew" ]; then
        echo "使用gradle wrapper构建..."
        chmod +x gradlew
        ./gradlew assembleDebug
    else
        echo "使用系统gradle构建..."
        gradle assembleDebug
    fi
fi

# 检查构建结果
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo ""
    echo "=========================================="
    echo "✅ APK构建成功！"
    echo "=========================================="
    echo "APK文件位置: app/build/outputs/apk/debug/app-debug.apk"
    echo "文件大小: $(du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1)"
    echo ""

    # 复制到根目录方便访问
    cp app/build/outputs/apk/debug/app-debug.apk ../ChildWatchManager.apk
    echo "已复制到: ../ChildWatchManager.apk"
else
    echo ""
    echo "=========================================="
    echo "❌ APK构建失败"
    echo "=========================================="
    echo "请检查错误日志"
fi
