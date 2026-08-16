// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.lynx.explorer.modules;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import com.lynx.jsbridge.LynxMethod;
import com.lynx.jsbridge.LynxModule;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Android counterpart of the iOS {@code NetworkModule}. Exposes network state to
 * JS via {@code NativeModules.NetworkModule}, faithfully porting the native
 * method surface of Expo's {@code expo-network} (latest) module. Method names
 * MUST match the iOS methodLookup keys so the shared {@code @lynxpo/mods-
 * network} accessors resolve on both platforms.
 *
 * <p>The event/subscription surface of expo-network is intentionally omitted —
 * it requires an async event bridge beyond this module's synchronous contract.
 */
public class NetworkModule extends LynxModule {

  public NetworkModule(Context context) {
    super(context);
  }

  @LynxMethod
  public String getIpAddress() {
    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface nif = interfaces.nextElement();
        if (nif.isLoopback() || !nif.isUp()) {
          continue;
        }
        Enumeration<InetAddress> addresses = nif.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
            return addr.getHostAddress();
          }
        }
      }
    } catch (Exception ignored) {
      // fall through to connectivity-manager path
    }
    return null;
  }

  @LynxMethod
  public Map<String, Object> getNetworkState() {
    Map<String, Object> state = new HashMap<>();
    ConnectivityManager cm =
        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm == null) {
      state.put("isConnected", false);
      state.put("isInternetReachable", false);
      state.put("type", 0); // UNKNOWN
      state.put("isWifiEnabled", false);
      return state;
    }
    NetworkCapabilities caps = null;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Network network = cm.getActiveNetwork();
      caps = network != null ? cm.getNetworkCapabilities(network) : null;
    }
    boolean connected = caps != null;
    state.put("isConnected", connected);
    state.put("isInternetReachable", connected);
    if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
      state.put("type", 1); // WIFI
    } else if (caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
      state.put("type", 2); // CELLULAR
    } else {
      state.put("type", 0); // UNKNOWN
    }
    boolean wifiEnabled = false;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      // method deprecated but safe to call below Q
      wifiEnabled = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null
          && cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
    }
    state.put("isWifiEnabled", wifiEnabled);
    return state;
  }
}
