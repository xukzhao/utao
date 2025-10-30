# 油桃TV Android项目

## 项目简介
这是一个Android TV应用项目，使用Java开发，采用DataBinding架构。主要提供电视直播和在线视频播放功能。

## 技术栈
- **语言**: Java
- **架构**: DataBinding
- **WebView**: 腾讯X5内核
- **平台**: Android TV

## 项目结构

```
app/src/main/
├── java/tv/utao/x5/
│   ├── StartActivity.java          # 启动页
│   ├── MainActivity.java           # 主页面（Web视频点播）
│   ├── LiveActivity.java           # 直播页面
│   ├── BaseActivity.java           # Activity基类
│   ├── BaseWebViewActivity.java    # WebView Activity基类
│   ├── adapter/                    # 适配器
│   ├── api/                        # API接口
│   ├── dao/                        # 数据库操作
│   ├── domain/                     # 数据模型
│   ├── impl/                       # 接口实现
│   ├── service/                    # 服务层
│   ├── util/                       # 工具类
│   └── utils/                      # 工具类
├── res/
│   ├── layout/                     # 布局文件
│   │   ├── activity_start.xml      # 启动页布局
│   │   ├── activity_main.xml       # 主页面布局
│   │   ├── activity_live.xml       # 直播页面布局
│   │   └── dialog_exit.xml         # 退出对话框布局
│   ├── values/                     # 资源值
│   └── drawable/                   # 图片资源
└── assets/                         # 静态资源
    └── tv-web/                     # Web页面资源
```

## 核心页面说明

### 1. StartActivity（启动页）
- 应用入口，负责初始化和检查更新
- 处理X5内核的安装和初始化
- 根据配置跳转到主页面或直播页面

### 2. MainActivity（主页面）
- 基于WebView加载视频点播页面
- 支持遥控器按键控制
- 支持菜单操作（选集、画质、倍速等）
- 支持返回退出对话框

### 3. LiveActivity（直播页面）
- 电视直播功能
- 支持快速切台（上下左右键）
- 支持频道列表选择
- 支持返回退出对话框

## 开发规范

### 1. DataBinding使用规范
- 所有Activity必须继承BaseActivity
- 使用DataBindingUtil.setContentView绑定布局
- 布局文件必须使用`<layout>`根标签
- 在`<data>`标签中定义变量和处理器

示例：
```java
protected ActivityMainBinding binding;

@Override
protected void createInit() {
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    binding.setMenuTitleHandler(new MenuTitleHandler());
}
```

### 2. 布局文件规范
```xml
<?xml version="1.0" encoding="utf-8"?>
<layout xmlns:android="http://schemas.android.com/apk/res/android">
    <data>
        <variable
            name="handler"
            type="tv.utao.x5.Handler" />
    </data>
    
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <!-- 内容 -->
    </FrameLayout>
</layout>
```

### 3. 按键处理规范
- 重写`dispatchKeyEvent`方法处理按键事件
- 区分按键按下（ACTION_DOWN）和抬起（ACTION_UP）
- 返回键需要特殊处理，实现双击退出或弹出对话框

示例：
```java
@Override
public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getAction() == KeyEvent.ACTION_UP) {
        return super.dispatchKeyEvent(event);
    }
    int keyCode = event.getKeyCode();
    if (keyCode == KeyEvent.KEYCODE_BACK) {
        showExitDialog();
        return true;
    }
    return super.dispatchKeyEvent(event);
}
```

### 4. 页面跳转规范
```java
// 跳转到MainActivity
Intent intent = new Intent(this, MainActivity.class);
startActivity(intent);
finish();

// 跳转到LiveActivity
Intent intent = new Intent(this, LiveActivity.class);
startActivity(intent);
finish();
```

### 5. SharedPreferences使用规范
使用`ValueUtil`工具类进行数据存储：
```java
// 保存数据
ValueUtil.putString(context, "key", "value");

// 读取数据
String value = ValueUtil.getString(context, "key", "defaultValue");
```

### 6. 日志规范
使用`LogUtil`进行日志输出：
```java
LogUtil.i(TAG, "信息日志");
LogUtil.e(TAG, "错误日志");
```

### 7. Toast提示规范
使用`ToastUtils`显示提示：
```java
ToastUtils.show(context, "提示信息", Toast.LENGTH_SHORT);
```

## 功能特性

### 1. 退出对话框
- 按返回键弹出退出对话框（右侧1/3屏幕）
- 默认选中"退出"选项
- 再次按返回键或点击确认退出
- 支持"启动首页"切换功能

### 2. 启动首页切换
- 可在退出对话框中切换启动首页（Main/Live）
- 设置会保存到SharedPreferences
- 下次启动时根据设置自动跳转

### 3. 直播功能
- 支持遥控器上下左右快速切台
- 支持频道列表选择
- 记录观看历史

### 4. 视频点播功能
- 支持选集、画质、倍速调节
- 支持播放进度记录
- 支持搜索功能

## 编译和运行

### 环境要求
- Android Studio Arctic Fox或更高版本
- JDK 8或更高版本
- Android SDK API 21+
- Gradle 7.0+

### 编译步骤
1. 克隆项目到本地
2. 使用Android Studio打开项目
3. 等待Gradle同步完成
4. 连接Android TV设备或启动模拟器
5. 点击Run按钮运行

### 打包APK
```bash
# Debug版本
./gradlew assembleDebug

# Release版本
./gradlew assembleRelease
```

## 注意事项

1. **X5内核**: 项目使用腾讯X5内核，首次启动需要下载和安装内核文件
2. **横屏强制**: 所有Activity都设置为横屏模式（`SCREEN_ORIENTATION_LANDSCAPE`）
3. **SingleTask模式**: 主要Activity使用`singleTask`启动模式，避免重复创建
4. **WebView内存**: 注意WebView的内存管理，及时释放资源
5. **焦点处理**: TV应用需要特别注意焦点处理，确保遥控器可以正常导航

## 更新日志

### v1.0.0 (2025-10-03)
- ✅ 新增退出对话框功能
- ✅ 新增启动首页切换功能
- ✅ 优化返回键处理逻辑
- ✅ 完善项目文档

## 许可证
请遵守相关法律法规使用本项目。

## 联系方式
如有问题，请提交Issue或联系开发团队。

