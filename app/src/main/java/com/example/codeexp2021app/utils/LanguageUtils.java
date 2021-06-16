package com.example.codeexp2021app.utils;

import android.text.TextUtils;

import java.util.Locale;

public class LanguageUtils {

    public static String getDefaultLanguageCode() {
        final Locale locale = Locale.getDefault();
        final StringBuilder language = new StringBuilder(locale.getLanguage());
        final String country = locale.getCountry();
        if (!TextUtils.isEmpty(country)) {
            language.append("-");
            language.append(country);
        }
        return language.toString();
    }
}
