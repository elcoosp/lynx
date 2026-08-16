// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#import "ClipboardModule.h"
#import <UIKit/UIKit.h>

@implementation ClipboardModule

+ (NSString *)name {
  return @"ClipboardModule";
}

+ (NSDictionary<NSString *, NSString *> *)methodLookup {
  return @{
    @"getString" : NSStringFromSelector(@selector(getString)),
    @"setString" : NSStringFromSelector(@selector(setString:)),
    @"hasString" : NSStringFromSelector(@selector(hasString)),
  };
}

- (NSString *)getString {
  UIPasteboard *pb = UIPasteboard.generalPasteboard;
  return pb.string ?: [pb.string isEqualToString:@""] ? @"" : nil;
}

- (void)setString:(NSString *)text {
  UIPasteboard.generalPasteboard.string = text;
}

- (BOOL)hasString {
  return UIPasteboard.generalPasteboard.string.length > 0;
}
@end
