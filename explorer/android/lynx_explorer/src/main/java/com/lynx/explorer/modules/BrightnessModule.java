// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.lynx.explorer.modules;

import android.provider.Settings;
import android.view.WindowManager;
import com.lynx.jsbridge.LynxMethod;
import com.lynx.jsbridge.LynxModule;

/**
 * Android counterpart of the iOS {@code BrightnessModule}. Exposes screen
 * brightness to JS via {@code NativeModules.BrightnessModule}, faithfully
 * porting the native method surface of Expo's {@code expo-brightness} (latest)
 * module. Method names MUST match the iOS methodLookup keys so the shared
 * {@code @lynxpo/mods-brightness} accessors resolve on both platforms.
 *
 * <p>The event/permission surface of expo-brightness is intentionally omitted —
 * it requires an async event bridge beyond this module's synchronous contract.
 */
public class BrightnessModule extends LynxModule {

  public BrightnessModule(android.content.Context context) {
    super(context);
  }

  @LynxMethod
  public float getBrightness() {
    WindowManager.LayoutParams lp = currentAttributes();
    if (lp == null) {
      return getSystemBrightness();
    }
    if (lp.screenBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
      return getSystemBrightness();
    }
    return lp.screenBrightness;
  }

  @LynxMethod
  public void setBrightness(float brightnessValue) {
    WindowManager.LayoutParams lp = currentAttributes();
    if (lp == null) {
      return;
    }
    lp.screenBrightness = brightnessValue;
    applyAttributes(lp);
  }

  @LynxMethod
  public float getSystemBrightness() {
    try {
      int mode = Settings.System.getInt(
          mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
      if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
        float adj = Settings.System.getFloat(
            mContext.getContentResolver(), "screen_auto_brightness_adj");
        return (adj + 1.0f) / 2;
      }
      int brightness = Settings.System.getInt(
          mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
      return (brightness - 1) / 254f;
    } catch (Settings.SettingNotFoundException e) {
      return 0.5f;
    }
  }

  @LynxMethod
  public boolean isUsingSystemBrightness() {
    WindowManager.LayoutParams lp = currentAttributes();
    return lp != null
        && lp.screenBrightness == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
  }

  @LynxMethod
  public int getSystemBrightnessMode() {
    try {
      int mode = Settings.System.getInt(
          mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
      return mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC ? 1 : 2;
    } catch (Settings.SettingNotFoundException e) {
      return 0;
    }
  }

  private WindowManager.LayoutParams currentAttributes() {
    android.app.Activity activity = getActivity();
    if (activity == null) {
      return null;
    }
    return activity.getWindow().getAttributes();
  }

  private void applyAttributes(WindowManager.LayoutParams lp) {
    android.app.Activity activity = getActivity();
    if (activity == null) {
      return;
    }
    activity.runOnUiThread(() -> activity.getWindow().setAttributes(lp));
  }

  private android.app.Activity getActivity() {
    if (mContext instanceof android.app.Activity) {
      return (android.app.Activity) mContext;
    }
    return null;
  }
}
