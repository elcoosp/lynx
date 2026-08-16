// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#import "KeepAwakeModule.h"
#import <UIKit/UIKit.h>

@implementation KeepAwakeModule

+ (NSString *)name {
  return @"KeepAwakeModule";
}

+ (NSDictionary<NSString *, NSString *> *)methodLookup {
  return @{
    @"activate" : NSStringFromSelector(@selector(activate)),
    @"deactivate" : NSStringFromSelector(@selector(deactivate)),
    @"isActivated" : NSStringFromSelector(@selector(isActivated)),
  };
}

- (void)activate {
  [UIApplication.sharedApplication setIdleTimerDisabled:YES];
}

- (void)deactivate {
  [UIApplication.sharedApplication setIdleTimerDisabled:NO];
}

- (BOOL)isActivated {
  return UIApplication.sharedApplication.isIdleTimerDisabled;
}
@end
