package com.tencent.game.native_image_view;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

/** NativeImageViewPlugin */
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity

public class NativeImageViewPlugin implements FlutterPlugin, MethodCallHandler {

  private MethodChannel channel;
  private Context context;

  void setContext(Context ctx){
    this.context = ctx;
  }

//新的组件注册接口
@Override
public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
  channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "com.tencent.game/native_image_view");
  channel.setMethodCallHandler(this);
  setContext(flutterPluginBinding.getApplicationContext());
}

@Override
public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
  channel.setMethodCallHandler(null);
}

// Flutter-1.12之前的组件注册接口，功能与onAttachedToEngine一样
public static void registerWith(Registrar registrar) {
  NativeImageViewPlugin plugin = new NativeImageViewPlugin();
  plugin.setContext(registrar.context());
  final MethodChannel channel = new MethodChannel(registrar.messenger(), "com.tencent.game/native_image_view");
  channel.setMethodCallHandler(plugin);
}

@Override
public void onMethodCall(@NonNull final MethodCall call, @NonNull final Result result) {
  if (call.method.equals("getImage")) {
    getImageHandler(call,result);
  } else {
    result.notImplemented();
  }
}

public void getImageHandler(@NonNull final MethodCall call, @NonNull final Result result){
  HashMap map = (HashMap) call.arguments;
    String urlStr = map.get("url").toString();
    Uri uri = Uri.parse(urlStr);
    if("localImage".equals(uri.getScheme())){
      String imageName = uri.getHost();
      int lastIndex = imageName.lastIndexOf(".");
      if(lastIndex > 0){
        imageName = imageName.substring(0,lastIndex);
      }
      String imageUri = "@drawable/"+imageName;
      int imageResource = context.getResources().getIdentifier(imageUri, null, context.getPackageName());
      if(imageResource > 0){
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(),imageResource);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        result.success(byteArray);
      }else{
        result.error("NOT_FOUND","file not found",call.arguments);
      }
    }else {
      Glide.with(context).download(urlStr).into(new CustomTarget<File>() {
        @Override
        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
          byte[] bytesArray = new byte[(int) resource.length()];
          try {
            FileInputStream fis = new FileInputStream(resource);
            fis.read(bytesArray);
            fis.close();
            result.success(bytesArray);
          } catch (IOException e) {
            e.printStackTrace();
            result.error("READ_FAIL",e.toString(),call.arguments);
          }
        }
        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
          super.onLoadFailed(errorDrawable);
          result.error("LOAD_FAIL","image download fail",call.arguments);
        }
        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {
          result.error("LOAD_CLEARED","image load clear",call.arguments);
        }
      });
    }
  }
}
