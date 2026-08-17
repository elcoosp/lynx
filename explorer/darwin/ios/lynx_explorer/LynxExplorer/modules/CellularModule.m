// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.

#import "CellularModule.h"
#import <UIKit/UIKit.h>

@implementation CellularModule

+ (NSString *)name {
  return @"CellularModule";
}

+ (NSDictionary<NSString *, NSString *> *)methodLookup {
  return @{
    @"getCellularGeneration" : NSStringFromSelector(@selector(getCellularGeneration)),
    @"getIsoCountryCode" : NSStringFromSelector(@selector(getIsoCountryCode)),
    @"getCarrierName" : NSStringFromSelector(@selector(getCarrierName)),
    @"getMobileCountryCode" : NSStringFromSelector(@selector(getMobileCountryCode)),
    @"getMobileNetworkCode" : NSStringFromSelector(@selector(getMobileNetworkCode)),
  };
}

- (int)getCellularGeneration {
  CTTelephonyNetworkInfo *netinfo = [[CTTelephonyNetworkInfo alloc] init];
  NSString *rat = netinfo.serviceCurrentRadioAccessTechnology.allValues.firstObject;
  if ([rat isEqualToString:CTRadioAccessTechnologyGPRS] ||
      [rat isEqualToString:CTRadioAccessTechnologyEdge] ||
      [rat isEqualToString:CTRadioAccessTechnologyCDMA1x]) {
    return 1;
  }
  if ([rat isEqualToString:CTRadioAccessTechnologyWCDMA] ||
      [rat isEqualToString:CTRadioAccessTechnologyHSDPA] ||
      [rat isEqualToString:CTRadioAccessTechnologyHSUPA] ||
      [rat isEqualToString:CTRadioAccessTechnologyCDMAEVDORev0] ||
      [rat isEqualToString:CTRadioAccessTechnologyCDMAEVDORevA] ||
      [rat isEqualToString:CTRadioAccessTechnologyCDMAEVDORevB] ||
      [rat isEqualToString:CTRadioAccessTechnologyeHRPD]) {
    return 2;
  }
  if ([rat isEqualToString:CTRadioAccessTechnologyLTE]) {
    return 3;
  }
  if (@available(iOS 14.1, *)) {
    if ([rat isEqualToString:CTRadioAccessTechnologyNRNSA] ||
        [rat isEqualToString:CTRadioAccessTechnologyNR]) {
      return 4;
    }
  }
  return 0;
}

- (NSString *)getIsoCountryCode {
  CTTelephonyNetworkInfo *netinfo = [[CTTelephonyNetworkInfo alloc] init];
  CTCarrier *carrier = netinfo.serviceSubscriberCellularProviders.allValues.firstObject;
  return carrier.isoCountryCode ?: nil;
}

- (NSString *)getCarrierName {
  CTTelephonyNetworkInfo *netinfo = [[CTTelephonyNetworkInfo alloc] init];
  CTCarrier *carrier = netinfo.serviceSubscriberCellularProviders.allValues.firstObject;
  return carrier.carrierName ?: nil;
}

- (NSString *)getMobileCountryCode {
  CTTelephonyNetworkInfo *netinfo = [[CTTelephonyNetworkInfo alloc] init];
  CTCarrier *carrier = netinfo.serviceSubscriberCellularProviders.allValues.firstObject;
  return carrier.mobileCountryCode ?: nil;
}

- (NSString *)getMobileNetworkCode {
  CTTelephonyNetworkInfo *netinfo = [[CTTelephonyNetworkInfo alloc] init];
  CTCarrier *carrier = netinfo.serviceSubscriberCellularProviders.allValues.firstObject;
  return carrier.mobileNetworkCode ?: nil;

- (void)getCellularGenerationAsync:(LynxCallbackBlock)resolve reject:(LynxCallbackBlock)reject {
  @try { resolve(@([self getCellularGeneration])); } @catch (NSException *e) { reject(e.reason); }
}
- (void)getIsoCountryCodeAsync:(LynxCallbackBlock)resolve reject:(LynxCallbackBlock)reject {
  @try { resolve([self getIsoCountryCode]); } @catch (NSException *e) { reject(e.reason); }
}
- (void)getCarrierNameAsync:(LynxCallbackBlock)resolve reject:(LynxCallbackBlock)reject {
  @try { resolve([self getCarrierName]); } @catch (NSException *e) { reject(e.reason); }
}
- (void)getMobileCountryCodeAsync:(LynxCallbackBlock)resolve reject:(LynxCallbackBlock)reject {
  @try { resolve([self getMobileCountryCode]); } @catch (NSException *e) { reject(e.reason); }
}
- (void)getMobileNetworkCodeAsync:(LynxCallbackBlock)resolve reject:(LynxCallbackBlock)reject {
  @try { resolve([self getMobileNetworkCode]); } @catch (NSException *e) { reject(e.reason); }
}

}
