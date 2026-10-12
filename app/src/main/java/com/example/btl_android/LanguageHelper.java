package com.example.btl_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.preference.PreferenceManager;

import java.util.Locale;

public class LanguageHelper {
    public static void changeLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        config.setLocale(locale);
        resources.updateConfiguration(config, displayMetrics);
    }

    // Lấy ngôn ngữ đã được lưu trong SharedPreferences
    public static void loadLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String languageCode = prefs.getString("language", "vi"); // Mặc định là "vi"
        changeLocale(context, languageCode);
    }
}
