// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#import "BrightnessModule.h"
#import <UIKit/UIKit.h>

@implementation BrightnessModule

+ (NSString *)name {
  return @"BrightnessModule";
}

+ (NSDictionary<NSString *, NSString *> *)methodLookup {
  return @{
    @"getBrightness" : NSStringFromSelector(@selector(getBrightness)),
    @"setBrightness" : NSStringFromSelector(@selector(setBrightness:)),
    @"getSystemBrightness" : NSStringFromSelector(@selector(getSystemBrightness)),
    @"isUsingSystemBrightness" : NSStringFromSelector(@selector(isUsingSystemBrightness)),
    @"getSystemBrightnessMode" : NSStringFromSelector(@selector(getSystemBrightnessMode)),
  };
}

- (double)getBrightness {
  return UIScreen.mainScreen.brightness;
}

- (void)setBrightness:(double)brightnessValue {
  UIScreen.mainScreen.brightness = brightnessValue;
}

- (double)getSystemBrightness {
  return UIScreen.mainScreen.brightness;
}

- (BOOL)isUsingSystemBrightness {
  return YES;
}

- (int)getSystemBrightnessMode {
  return 2; // MANUAL on iOS (system auto-brightness not directly readable)
}
@end
