// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.lynx.explorer.modules;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import com.lynx.jsbridge.LynxMethod;
import com.lynx.jsbridge.LynxModule;
import com.lynx.react.bridge.JavaOnlyArray;
import com.lynx.react.bridge.WritableArray;

/**
 * Android counterpart of the iOS DeviceModule. Exposes device/system info to
 * JS via `NativeModules.DeviceModule`. Method names MUST match the iOS
 * methodLookup keys so the shared @lynxpo/mods-device accessors resolve on
 * both platforms.
 */
public class DeviceModule extends LynxModule {

  public DeviceModule(Context context) {
    super(context);
  }

  private boolean isEmulator() {
    return Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Build.MANUFACTURER.contains("Genymotion")
        || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
        || "google_sdk".equals(Build.PRODUCT);
  }

  @LynxMethod
  public boolean isDevice() {
    return !isEmulator();
  }

  @LynxMethod
  public String brand() {
    return Build.BRAND;
  }

  @LynxMethod
  public String manufacturer() {
    return Build.MANUFACTURER;
  }

  @LynxMethod
  public String modelName() {
    return Build.MODEL;
  }

  @LynxMethod
  public String designName() {
    return Build.DEVICE;
  }

  @LynxMethod
  public String productName() {
    return Build.PRODUCT;
  }

  @LynxMethod
  public int deviceYearClass() {
    // Rough heuristic from the Android release year of the active SDK level.
    switch (Build.VERSION.SDK_INT) {
      case 30: // R
        return 2021;
      case 31: // S
      case 32: // S_V2
        return 2022;
      case 33: // TIRAMISU
        return 2023;
      case 34: // UPSIDE_DOWN_CAKE
        return 2024;
      case 35: // VANILLA_ICE_CREAM
        return 2025;
      case 36: // BAKLAVA
        return 2026;
      default:
        return 0;
    }
  }

  @LynxMethod
  public double totalMemory() {
    try {
      ActivityManager am =
          (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
      if (am != null) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.totalMem;
      }
    } catch (Exception ignored) {
      // fall through
    }
    return 0d;
  }

  @LynxMethod
  public int deviceType() {
    // 1 == phone/tablet handheld (Lynx's "device" type).
    return 1;
  }

  @LynxMethod
  public WritableArray supportedCpuArchitectures() {
    WritableArray array = new JavaOnlyArray();
    for (String abi : Build.SUPPORTED_ABIS) {
      array.pushString(abi);
    }
    return array;
  }

  @LynxMethod
  public String osName() {
    return "Android";
  }

  @LynxMethod
  public String osVersion() {
    return Build.VERSION.RELEASE;
  }

  @LynxMethod
  public String osBuildId() {
    return Build.ID;
  }

  @LynxMethod
  public String osInternalBuildId() {
    return Build.VERSION.INCREMENTAL;
  }

  @LynxMethod
  public String osBuildFingerprint() {
    return Build.FINGERPRINT;
  }

  @LynxMethod
  public int platformApiLevel() {
    return Build.VERSION.SDK_INT;
  }

  @LynxMethod
  public String deviceName() {
    return Build.MODEL;
  }
}
