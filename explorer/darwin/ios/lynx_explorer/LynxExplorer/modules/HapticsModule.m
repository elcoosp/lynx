// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#import "HapticsModule.h"
#import <UIKit/UIKit.h>

@implementation HapticsModule

+ (NSString *)name {
  return @"HapticsModule";
}

+ (NSDictionary<NSString *, NSString *> *)methodLookup {
  return @{
    @"impactAsync" : NSStringFromSelector(@selector(impactAsync:)),
    @"notificationAsync" : NSStringFromSelector(@selector(notificationAsync:)),
    @"selectionAsync" : NSStringFromSelector(@selector(selectionAsync)),
  };
}

- (void)impactAsync:(int)style {
  UIImpactFeedbackGeneratorFeedbackStyle s = UIImpactFeedbackGeneratorFeedbackStyleLight;
  if (style == 1) s = UIImpactFeedbackGeneratorFeedbackStyleMedium;
  else if (style == 2) s = UIImpactFeedbackGeneratorFeedbackStyleHeavy;
  else if (style == 3) s = UIImpactFeedbackGeneratorFeedbackStyleRigid;
  else if (style == 4) s = UIImpactFeedbackGeneratorFeedbackStyleSoft;
  UIImpactFeedbackGenerator *gen = [[UIImpactFeedbackGenerator alloc] initWithStyle:s];
  [gen impactOccurred];
}

- (void)notificationAsync:(int)type {
  UINotificationFeedbackGeneratorFeedbackType t = UINotificationFeedbackGeneratorFeedbackTypeSuccess;
  if (type == 1) t = UINotificationFeedbackGeneratorFeedbackTypeWarning;
  else if (type == 2) t = UINotificationFeedbackGeneratorFeedbackTypeError;
  UINotificationFeedbackGenerator *gen = [[UINotificationFeedbackGenerator alloc] init];
  [gen notificationOccurred:t];
}

- (void)selectionAsync {
  UISelectionFeedbackGenerator *gen = [[UISelectionFeedbackGenerator alloc] init];
  [gen selectionChanged];

- (void)impactAsync:(NSInteger)style resolve:(LynxCallbackBlock)resolve reject:(LynxCallbackBlock)reject {
  @try { [self impactAsync:style]; resolve(nil); } @catch (NSException *e) { reject(e.reason); }
}
- (void)notificationAsync:(NSInteger)type resolve:(LynxCallbackBlock)resolve reject:(LynxCallbackBlock)reject {
  @try { [self notificationAsync:type]; resolve(nil); } @catch (NSException *e) { reject(e.reason); }
}
- (void)selectionAsync:(LynxCallbackBlock)resolve reject:(LynxCallbackBlock)reject {
  @try { [self selectionAsync]; resolve(nil); } @catch (NSException *e) { reject(e.reason); }
}

}
