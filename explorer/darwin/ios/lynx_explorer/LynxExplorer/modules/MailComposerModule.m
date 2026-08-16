// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#import "MailComposerModule.h"
#import <UIKit/UIKit.h>

@implementation MailComposerModule

+ (NSString *)name {
  return @"MailComposerModule";
}

+ (NSDictionary<NSString *, NSString *> *)methodLookup {
  return @{
    @"isAvailable" : NSStringFromSelector(@selector(isAvailable)),
    @"getClients" : NSStringFromSelector(@selector(getClients)),
    @"compose" : NSStringFromSelector(@selector(compose:body:recipients:)),
  };
}

- (BOOL)isAvailable {
  return [[UIApplication sharedApplication] canOpenURL:[NSURL URLWithString:@"mailto:"]];
}

- (NSArray<NSString *> *)getClients {
  return @[];
}

- (void)compose:(NSString *)subject body:(NSString *)body recipients:(NSArray<NSString *> *)recipients {
  NSMutableString *url = [NSMutableString stringWithString:@"mailto:"];
  if (recipients.count > 0) {
    url = [NSMutableString stringWithFormat:@"mailto:%@", [recipients componentsJoinedByString:@","]];
  }
  NSMutableArray *parts = [NSMutableArray array];
  if (subject.length > 0) [parts addObject:[NSString stringWithFormat:@"subject=%@", subject]];
  if (body.length > 0) [parts addObject:[NSString stringWithFormat:@"body=%@", body]];
  if (parts.count > 0) {
    [url appendFormat:@"?%@", [parts componentsJoinedByString:@"&"]];
  }
  NSString *encoded = [url stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
  [[UIApplication sharedApplication] openURL:[NSURL URLWithString:encoded] options:@{} completionHandler:nil];
}
@end
