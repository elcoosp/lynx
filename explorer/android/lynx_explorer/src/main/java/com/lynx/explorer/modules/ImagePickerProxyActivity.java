// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.lynx.explorer.modules;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;

/**
 * Transparent, no-UI activity that hosts the system camera / photo picker and
 * runtime-permission requests on behalf of {@link ImagePickerModule}. The
 * Explorer host activity does not forward {@code onActivityResult} or
 * permission callbacks to modules, so those flows run from this standalone
 * activity instead, then resolve the pending JS promise.
 */
public class ImagePickerProxyActivity extends Activity {

  private static final int REQ = ImagePickerModule.REQUEST_CODE;
  private static final int REQ_PERMISSIONS = 0x5052; // "PR"

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent starter = getIntent();
    if (starter == null) {
      finish();
      return;
    }

    String[] perms = starter.getStringArrayExtra(ImagePickerModule.EXTRA_REQUEST_PERMISSIONS);
    if (perms != null && perms.length > 0) {
      requestPermissions(perms, REQ_PERMISSIONS);
      return;
    }

    int action = starter.getIntExtra(ImagePickerModule.EXTRA_PICKER_ACTION, -1);
    Intent target;
    if (action == ImagePickerModule.ACTION_CAMERA) {
      String cameraUri = starter.getStringExtra(ImagePickerModule.EXTRA_CAMERA_URI);
      target = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      if (cameraUri != null) {
        Uri uri = Uri.parse(cameraUri);
        target.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // The camera app must be able to write the captured photo back to our
        // FileProvider URI; grant it explicitly (provider allows it).
        target.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        target.setClipData(android.content.ClipData.newRawUri("", uri));
      }
    } else {
      target = new Intent(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
          ? Intent.ACTION_OPEN_DOCUMENT
          : Intent.ACTION_PICK);
      target.setType("image/*");
      target.addCategory(Intent.CATEGORY_OPENABLE);
    }
    try {
      startActivityForResult(target, REQ);
    } catch (Exception e) {
      ImagePickerModule.rejectPicked(e.getMessage());
      finish();
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode != REQ_PERMISSIONS) {
      finish();
      return;
    }
    // expo-image-picker resolves with the status of the first requested perm.
    String perm = permissions.length > 0 ? permissions[0] : null;
    if (perm != null) {
      ImagePickerModule.resolvePermission(this, perm);
    } else {
      ImagePickerModule.rejectPicked("No permission requested");
    }
    finish();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode != REQ) {
      finish();
      return;
    }
    if (resultCode != Activity.RESULT_OK || data == null) {
      if (resultCode == Activity.RESULT_OK
          && getIntent().getIntExtra(ImagePickerModule.EXTRA_PICKER_ACTION, -1)
              == ImagePickerModule.ACTION_CAMERA) {
        Uri uri = ImagePickerModule.consumePendingCameraUri();
        ImagePickerModule.resolvePicked(uri, false);
      } else {
        ImagePickerModule.resolvePicked(null, true);
      }
      finish();
      return;
    }
    Uri uri = data.getData();
    if (uri == null) {
      uri = ImagePickerModule.consumePendingCameraUri();
    }
    ImagePickerModule.resolvePicked(uri, false);
    finish();
  }
}
