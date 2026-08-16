// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#import "StoreReviewModule.h"
#import <UIKit/UIKit.h>

@implementation StoreReviewModule

+ (NSString *)name {
  return @"StoreReviewModule";
}

+ (NSDictionary<NSString *, NSString *> *)methodLookup {
  return @{
    @"isAvailable" : NSStringFromSelector(@selector(isAvailable)),
    @"requestReview" : NSStringFromSelector(@selector(requestReview)),
  };
}

- (BOOL)isAvailable {
  if (@available(iOS 10.3, *)) {
    return [SKStoreReviewController class] != nil;
  }
  return NO;
}

- (void)requestReview {
  if (@available(iOS 10.3, *)) {
    [SKStoreReviewController requestReview];
  }
}
@end
