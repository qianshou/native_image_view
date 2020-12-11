#import "NativeImageViewPlugin.h"
#import <SDWebImage/SDWebImage.h>

@implementation NativeImageViewPlugin
//组件注册接口，Flutter自动调用
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"com.tencent.game/native_image_view"
            binaryMessenger:[registrar messenger]];
  NativeImageViewPlugin* instance = [[NativeImageViewPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getImage" isEqualToString:call.method]) {
      [self getImageHandler:call result:result];
  } else {
      result(FlutterMethodNotImplemented);
  }
}

- (void)getImageHandler:(FlutterMethodCall*)call result:(FlutterResult)result{
  if(call.arguments != nil && call.arguments[@"url"] != nil){
      NSString *url = call.arguments[@"url"];
      if([url hasPrefix:@"localImage://"]){
        //获取本地图片
        NSString *imageName = [url stringByReplacingOccurrencesOfString:@"localImage://" withString:@""];
        UIImage *image = [UIImage imageNamed:imageName];
        if(image != nil){
            NSData *imgData = UIImageJPEGRepresentation(image,1.0);
            result(imgData);
        }else{
            result(nil);
        }
      }else {
        //获取网络图片
        UIImage *image = [[SDImageCache sharedImageCache] imageFromCacheForKey:url];
        if(!image){
          //本地无缓存，下载后返回图片
          [[SDWebImageDownloader sharedDownloader] 
            downloadImageWithURL:[[NSURL alloc] initWithString:url] 
            completed:^(UIImage *image, NSData *data, NSError *error, BOOL finished) {
              if(finished){
                result(data);
                [[SDImageCache sharedImageCache] storeImage:image forKey:url completion:nil];
              }
            }];
        }else{
          //本地有缓存，直接返回图片
          NSData *imgData = UIImageJPEGRepresentation(image,1.0);
          result(imgData);
        }
      }
  }
}
@end
