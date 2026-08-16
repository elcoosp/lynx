// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#import "ScreenOrientationModule.h"
#import <UIKit/UIKit.h>

@implementation ScreenOrientationModule

+ (NSString *)name {
  return @"ScreenOrientationModule";
}

+ (NSDictionary<NSString *, NSString *> *)methodLookup {
  return @{
    @"getOrientation" : NSStringFromSelector(@selector(getOrientation)),
    @"getOrientationLock" : NSStringFromSelector(@selector(getOrientationLock)),
    @"lock" : NSStringFromSelector(@selector(lock:)),
    @"lockPlatform" : NSStringFromSelector(@selector(lockPlatform:)),
    @"supportsOrientationLock" : NSStringFromSelector(@selector(supportsOrientationLock)),
  };
}

- (int)getOrientation {
  UIDeviceOrientation dev = UIDevice.currentDevice.orientation;
  switch (dev) {
    case UIDeviceOrientationPortrait: return 1;
    case UIDeviceOrientationPortraitUpsideDown: return 2;
    case UIDeviceOrientationLandscapeLeft: return 3;
    case UIDeviceOrientationLandscapeRight: return 4;
    default: return 0;
  }
}

- (int)getOrientationLock {
  return 8; // SENSOR on iOS
}

- (void)lock:(int)orientation {
  UIInterfaceOrientation o = UIInterfaceOrientationPortrait;
  if (orientation == 3) o = UIInterfaceOrientationLandscapeLeft;
  else if (orientation == 4) o = UIInterfaceOrientationLandscapeRight;
  else if (orientation == 2) o = UIInterfaceOrientationPortraitUpsideDown;
  if (@available(iOS 16.0, *)) {
    [UIApplication.sharedApplication connectedScenes].allObjects.firstObject ?
      [((UIWindowScene *)[UIApplication.sharedApplication connectedScenes].allObjects.firstObject)
       requestGeometryUpdateWithPreferences:[[UIWindowSceneGeometryPreferencesIOS alloc] initWithInterfaceOrientations:1 << o]
       errorHandler:^(NSError *e){}] : nil;
  } else {
    [[UIDevice currentDevice] setValue:@(o) forKey:@"orientation"];
  }
}

- (void)lockPlatform:(int)orientationLock {
  // Platform lock not directly mappable on iOS; no-op.
}

- (BOOL)supportsOrientationLock {
  return NO;
}
@end
