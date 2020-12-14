# native_image_view

在Flutter嵌入已有原生项目的场景中，比较常见的一个问题是Flutter和原生项目中都使用了同一张图片时，两侧会分别进行存储，即该图片会被存储两次。

不同于Weex、Hippy等基于JS的跨平台框架是依赖于原生进行图片的获取和显示，Flutter是自行进行图片的管理并直接通过Skia引擎直接进行绘制的。

针对这一问题作者开发一个Flutter插件(native_image_view)将Flutter图片的下载和缓存工作交给Native实现，通过PlatformChannel进行图片二进制数据的传输，Flutter端则仅负责图片的绘制。

此外，我们还可以定义一个特殊协议，用于处理本地图片的调用，同时解决Flutter无法复用原生项目本地图片的问题。

## Getting Started

**安装插件**
在pubspec.yaml文件中添加：
```
dependencies:
  native_image_view: ^0.0.1
```
在flutter项目目录中安装插件
```
flutter pub get
```
**使用插件**
```
import 'package:native_image_view/native_image_view.dart';
……
//网络图片
String url = "https://iknow-pic.cdn.bcebos.com/79f0f736afc379313a380ef3e7c4b74542a91193";
//本地图片
//String url = "localImage://wdqy.jpeg";
NativeImageView(
          url: url,
          width: 300,
          height: 200,
        ),
```
