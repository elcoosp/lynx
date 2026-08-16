// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.lynx.explorer.modules;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import com.lynx.jsbridge.LynxMethod;
import com.lynx.jsbridge.LynxModule;
import java.util.HashMap;
import java.util.Map;

/**
 * Android counterpart of the iOS {@code ImagePickerModule}. Exposes permission
 * status to JS via {@code NativeModules.ImagePickerModule}, faithfully porting
 * the native method surface of Expo's {@code expo-image-picker} (latest)
 * module. Method names MUST match the iOS methodLookup keys so the shared
 * {@code @lynxpo/mods-image-picker} accessors resolve on both platforms.
 *
 * <p>Only the synchronous permission-status getters are exposed here; the async
 * {@code launchCameraAsync}/{@code launchImageLibraryAsync} calls require a
 * promise/activity-result bridge beyond this module's synchronous contract and
 * are intentionally omitted.
 */
public class ImagePickerModule extends LynxModule {

  public ImagePickerModule(Context context) {
    super(context);
  }

  @LynxMethod
  public Map<String, Object> getCameraPermissions() {
    return permissionStatus(Manifest.permission.CAMERA);
  }

  @LynxMethod
  public Map<String, Object> getMediaLibraryPermissions() {
    return permissionStatus(Manifest.permission.READ_EXTERNAL_STORAGE);
  }

  private Map<String, Object> permissionStatus(String permission) {
    Map<String, Object> result = new HashMap<>();
    int check = ContextCompat.checkSelfPermission(mContext, permission);
    boolean granted = check == PackageManager.PERMISSION_GRANTED;
    result.put("status", granted ? "granted" : "undetermined");
    result.put("granted", granted);
    result.put("canAskAgain", true);
    result.put("expires", "never");
    return result;
  }
}
