import 'package:flutter/material.dart';
import 'package:native_image_view/native_image_view.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String url = "https://iknow-pic.cdn.bcebos.com/79f0f736afc379313a380ef3e7c4b74542a91193";
  //String url = "localImage://wdqy.jpeg";

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('example'),
        ),
        body: Center(
        child: NativeImageView(
          url: url,
          width: 300,
          height: 200,
        ),
      ),
    ));
  }
}
