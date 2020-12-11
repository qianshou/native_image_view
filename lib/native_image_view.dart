import 'dart:typed_data';
import 'package:flutter/services.dart';
import 'package:flutter/material.dart';

class NativeImageView extends StatefulWidget {
  const NativeImageView({Key key, this.url, this.width, this.height})
      : super(key: key);
  final String url;
  final double width;
  final double height;

  @override
  State<StatefulWidget> createState() => _NativeImageViewState();
}

class _NativeImageViewState extends State<NativeImageView> {
  Uint8List _data;
  static const MethodChannel _channel =
      const MethodChannel('com.tencent.game/native_image_view');
  @override
  void initState() {
    super.initState();
    loadImageData();
  }

  loadImageData() async {
    _data = await _channel.invokeMethod("getImage", {"url": widget.url});
    setState(() {});
  }

  @override
  Widget build(BuildContext context) {
    return _data == null
        ? Container(
            color: Colors.grey,
            width: widget.width,
            height: widget.height,
          )
        : Image.memory(
            _data,
            width: widget.width,
            height: widget.height,
            fit: BoxFit.cover,
          );
  }
}
