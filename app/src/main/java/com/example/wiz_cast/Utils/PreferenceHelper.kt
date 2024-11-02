package com.example.wiz_cast.Utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences("weather_preferences", Context.MODE_PRIVATE)

    companion object {
        const val KEY_LANGUAGE = "key_language"
        const val KEY_UNITS = "key_units"
        const val DEFAULT_LANGUAGE = "en"
        const val DEFAULT_UNITS = "metric" // Celsius as default
    }

    fun setLanguage(language: String) {
        preferences.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(): String {
        return preferences.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    }

    fun setUnits(units: String) {
        preferences.edit().putString(KEY_UNITS, units).apply()
    }

    fun getUnits(): String {
        return preferences.getString(KEY_UNITS, DEFAULT_UNITS) ?: DEFAULT_UNITS
    }
}
