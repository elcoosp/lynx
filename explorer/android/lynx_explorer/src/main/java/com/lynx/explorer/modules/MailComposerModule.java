// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.lynx.explorer.modules;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import com.lynx.jsbridge.LynxMethod;
import com.lynx.jsbridge.LynxModule;
import java.util.ArrayList;
import java.util.List;

/**
 * Android counterpart of the iOS {@code MailComposerModule}. Exposes mail
 * composition to JS via {@code NativeModules.MailComposerModule}, faithfully
 * porting the native method surface of Expo's {@code expo-mail-composer}
 * (latest) module. Method names MUST match the iOS methodLookup keys so the
 * shared {@code @lynxpo/mods-mail-composer} accessors resolve on both
 * platforms.
 */
public class MailComposerModule extends LynxModule {

  public MailComposerModule(Context context) {
    super(context);
  }

  @LynxMethod
  public boolean isAvailable() {
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
    PackageManager pm = mContext.getPackageManager();
    return intent.resolveActivity(pm) != null;
  }

  @LynxMethod
  public List<String> getClients() {
    List<String> clients = new ArrayList<>();
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
    PackageManager pm = mContext.getPackageManager();
    List<ResolveInfo> resolveInfos =
        pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
    for (ResolveInfo info : resolveInfos) {
      clients.add(info.activityInfo.packageName);
    }
    return clients;
  }

  @LynxMethod
  public void compose(String subject, String body, List<String> recipients) {
    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
    if (recipients != null && !recipients.isEmpty()) {
      intent.putExtra(Intent.EXTRA_EMAIL, recipients.toArray(new String[0]));
    }
    if (subject != null) {
      intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    }
    if (body != null) {
      intent.putExtra(Intent.EXTRA_TEXT, body);
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    mContext.startActivity(intent);
  }
}
