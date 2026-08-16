// Copyright 2026 The Lynxpo Authors. All rights reserved.
// Licensed under the Apache License Version 2.0 that can be found in the
// LICENSE file in the root directory of this source tree.
package com.lynx.explorer.modules;

import android.content.Context;
import android.icu.util.LocaleData;
import android.icu.util.ULocale;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.LayoutDirection;
import androidx.core.os.LocaleListCompat;
import com.lynx.jsbridge.LynxMethod;
import com.lynx.jsbridge.LynxModule;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Android counterpart of the iOS {@code LocalizationModule}. Exposes device
 * localization info to JS via {@code NativeModules.LocalizationModule},
 * faithfully porting the native method surface of Expo's {@code
 * expo-localization} (latest) module. Method names MUST match the iOS
 * methodLookup keys so the shared {@code @lynxpo/mods-localization} accessors
 * resolve on both platforms.
 *
 * <p>Expo wraps these in async TS helpers; the underlying native calls are
 * synchronous, so they are exposed here as synchronous {@link LynxMethod}s
 * returning the raw list-of-maps value. The event/RTL observer surface is
 * intentionally omitted — it requires an async event bridge beyond this
 * module's synchronous contract.
 */
public class LocalizationModule extends LynxModule {

  private static final List<String> USES_IMPERIAL = List.of("US", "LR", "MM");
  private static final List<String> USES_FAHRENHEIT = List.of(
      "AG", "BZ", "VG", "FM", "MH", "MS", "KN", "BS", "CY", "TC",
      "US", "LR", "PW", "KY");

  public LocalizationModule(Context context) {
    super(context);
  }

  @LynxMethod
  public List<Map<String, Object>> getLocales() {
    List<Map<String, Object>> locales = new ArrayList<>();
    LocaleListCompat localeList = LocaleListCompat.getDefault();
    for (int i = 0; i < localeList.size(); i++) {
      Locale locale = localeList.get(i);
      if (locale == null) {
        continue;
      }
      try {
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        Map<String, Object> entry = new HashMap<>();
        entry.put("languageTag", locale.toLanguageTag());
        entry.put("regionCode", getRegionCode(locale));
        entry.put("languageRegionCode", getCountryCode(locale));
        entry.put(
            "textDirection",
            TextUtils.getLayoutDirectionFromLocale(locale) == LayoutDirection.RTL ? "rtl" : "ltr");
        entry.put("languageCode", locale.getLanguage());
        entry.put("languageScriptCode", locale.getScript().isEmpty() ? null : locale.getScript());
        entry.put("decimalSeparator", String.valueOf(symbols.getDecimalSeparator()));
        entry.put("digitGroupingSeparator", String.valueOf(symbols.getGroupingSeparator()));
        entry.put("measurementSystem", getMeasurementSystem(locale));
        entry.put("temperatureUnit", getTemperatureUnit(locale));
        entry.putAll(getCurrencyProperties(locale));
        locales.add(entry);
      } catch (Exception ignored) {
        // skip problematic locale, mirroring Expo
      }
    }
    return locales;
  }

  @LynxMethod
  public List<Map<String, Object>> getCalendars() {
    Map<String, Object> calendar = new HashMap<>();
    calendar.put("calendar", getCalendarType());
    calendar.put("uses24hourClock", uses24HourFormat());
    calendar.put("firstWeekday", Calendar.getInstance().getFirstDayOfWeek());
    calendar.put("timeZone", Calendar.getInstance().getTimeZone().getID());
    List<Map<String, Object>> result = new ArrayList<>();
    result.add(calendar);
    return result;
  }

  private Map<String, Object> getCurrencyProperties(Locale locale) {
    Map<String, Object> props = new HashMap<>();
    try {
      Currency currency = Currency.getInstance(locale);
      props.put("currencyCode", currency.getCurrencyCode());
      props.put("currencySymbol", currency.getSymbol(locale));
      props.put("languageCurrencyCode", currency.getCurrencyCode());
      props.put("languageCurrencySymbol", currency.getSymbol(locale));
    } catch (Exception e) {
      props.put("currencyCode", null);
      props.put("currencySymbol", null);
      props.put("languageCurrencyCode", null);
      props.put("languageCurrencySymbol", null);
    }
    return props;
  }

  private String getMeasurementSystem(Locale locale) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      LocaleData.MeasurementSystem system =
          LocaleData.getMeasurementSystem(ULocale.forLocale(locale));
      if (system == LocaleData.MeasurementSystem.UK) {
        return "uk";
      }
      if (system == LocaleData.MeasurementSystem.US) {
        return "us";
      }
      return "metric";
    }
    String region = getRegionCode(locale);
    if ("uk".equals(region)) {
      return "uk";
    }
    if (USES_IMPERIAL.contains(region)) {
      return "us";
    }
    return "metric";
  }

  private String getCountryCode(Locale locale) {
    String country = locale.getCountry();
    return TextUtils.isEmpty(country) ? null : country;
  }

  private String getRegionCode(Locale locale) {
    String miuiRegion = getSystemProperty("ro.miui.region");
    return (miuiRegion == null || miuiRegion.isEmpty()) ? getCountryCode(locale) : miuiRegion;
  }

  private String getTemperatureUnit(Locale locale) {
    String region = getRegionCode(locale);
    if (region == null) {
      return null;
    }
    return USES_FAHRENHEIT.contains(region) ? "fahrenheit" : "celsius";
  }

  private String getSystemProperty(String key) {
    try {
      Class<?> systemProperties = Class.forName("android.os.SystemProperties");
      java.lang.reflect.Method get = systemProperties.getMethod("get", String.class);
      return (String) get.invoke(systemProperties, key);
    } catch (Exception e) {
      return "";
    }
  }

  private String getCalendarType() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      return Calendar.getInstance().getCalendarType().toString();
    }
    return "gregory";
  }

  private boolean uses24HourFormat() {
    return DateFormat.is24HourFormat(mContext);
  }
}
