# 儿童观看时长管理APP

## 📱 项目简介

当贝盒子H5 Pro儿童观看时长管理APP，实现：
- ✅ 开机自启动
- ✅ 家长密码保护
- ✅ 每日/单次/间隔时长管理
- ✅ 弹性时间配置（工作日/周末）
- ✅ 自动锁定机制
- ✅ 后台持续监控

---

## 🚀 一键设置（仅需5分钟）

### 步骤1：创建新仓库

1. 访问：https://github.com/new
2. **Repository name**: `ChildWatchManager`
3. **选择 Public**（重要！）
4. **不要** 勾选 "Add a README file"（重要！）
5. 点击 **"Create repository"**

---

### 步骤2：上传项目文件

**方法A：拖拽上传（推荐，最简单）**

1. 打开刚创建的空仓库页面
2. 点击 **"uploading an existing repository"** 链接
3. 打开Windows文件资源管理器，进入 `D:\ai\APK\ChildWatchManager`
4. **全选所有文件**（Ctrl+A）
5. **拖拽** 到GitHub页面的上传区域
6. 等待上传完成
7. 点击 **"Commit changes"**

**方法B：使用Git命令**

打开PowerShell，运行：

```powershell
# 进入项目目录
cd D:\ai\APK\ChildWatchManager

# 初始化Git
git init
git add .
git commit -m "Initial commit"

# 关联仓库（替换为您的仓库地址）
git remote add origin https://github.com/polaris880/ChildWatchManager.git
git branch -M main
git push -u origin main
```

---

### 步骤3：等待自动构建

1. 访问仓库的 **"Actions"** 标签
2. 等待5-10分钟，直到显示绿色 ✓
3. 点击完成的构建任务
4. 在 **"Artifacts"** 部分下载APK

---

## 📲 安装到当贝盒子

### 方法1：ADB安装（推荐）

```bash
# 1. 开启当贝盒子ADB调试
#    设置 → 关于 → 连续点击"版本号"7次
#    返回 → 开发者选项 → 开启"USB调试"

# 2. 连接设备
adb connect <当贝盒子IP>:5555

# 3. 安装APK
adb install app-debug.apk

# 4. 启动APP
adb shell am start -n com.childwatch.manager/.MainActivity
```

### 方法2：U盘安装

1. 将APK复制到U盘
2. 插入当贝盒子
3. 使用文件管理器找到并安装

---

## 🎯 功能配置

### 首次使用

1. **启动APP** → 进入主界面
2. **点击设置** → 系统提示设置密码
3. **设置6位数字密码**
4. **配置观看时长**：
   - 工作日：120分钟
   - 周末：180分钟
   - 单次时长：30分钟
   - 休息间隔：30分钟
   - 休息时长：5分钟
5. **开启开机自启动** 和 **监控**
6. **保存设置**

### 日常使用

- APP会自动在开机时启动
- 时间到达自动锁定
- 输入家长密码解锁
- 随时可修改设置

---

## 📁 项目结构

```
ChildWatchManager/
├── .github/workflows/
│   └── build.yml              ← 自动构建配置
├── app/
│   ├── src/main/
│   │   ├── java/com/childwatch/manager/
│   │   │   ├── MainActivity.java
│   │   │   ├── SettingsActivity.java
│   │   │   ├── LockActivity.java
│   │   │   ├── AlertActivity.java
│   │   │   ├── services/
│   │   │   │   └── WatchdogService.java
│   │   │   └── utils/
│   │   │       ├── ConfigManager.java
│   │   │       └── TimeManager.java
│   │   └── res/
│   │       ├── layout/
│   │       ├── values/
│   │       └── drawable/
│   └── build.gradle
├── build.gradle
├── settings.gradle
├── gradle.properties
└── README.md
```

---

## 🔧 技术栈

- **语言**: Java
- **最低SDK**: Android 8.0 (API 26)
- **目标SDK**: Android 13 (API 33)
- **构建工具**: Gradle 8.0 + Android Gradle Plugin 8.1.0
- **依赖**: AndroidX, Material Design

---

## ❓ 常见问题

**Q: 构建失败怎么办？**
A: 检查Actions页面的错误日志。通常是版本问题，我已配置正确的版本组合。

**Q: APK在哪里下载？**
A: Actions → 完成的构建 → Artifacts → 下载ZIP

**Q: 如何更新代码？**
A: 修改文件后重新上传，会自动触发新的构建。

**Q: 安装后无法启动？**
A: 确保开启了"允许安装未知来源应用"。

---

## 📞 技术支持

如有问题，请查看：
- [GitHub Actions文档](https://docs.github.com/en/actions)
- [Android开发文档](https://developer.android.com)

---

## 版本历史

- **v1.0.0** (2026-06-12): 初始版本，核心功能完成

---

**构建完成后，您的APP就可以安装使用了！** 🎉
