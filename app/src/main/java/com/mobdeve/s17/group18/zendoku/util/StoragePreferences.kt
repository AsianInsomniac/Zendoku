package com.mobdeve.s17.group18.zendoku.util

import android.content.Context
import android.content.SharedPreferences

class StoragePreferences(context: Context) {
    private val appPreferences: SharedPreferences
    private val PREFS = "appPreferences"
    fun saveIntPreferences(key: String?, value: Int) {
        val prefsEditor = appPreferences.edit()
        prefsEditor.putInt(key, value)
        prefsEditor.commit()
    }

    fun saveStringPreferences(key: String?, value: String?) {
        val prefsEditor = appPreferences.edit()
        prefsEditor.putString(key, value)
        prefsEditor.commit()
    }

    fun getIntPreferences(key: String?): Int {
        return appPreferences.getInt(key, -1)
    }

    fun getStringPreferences(key: String?): String? {
        return appPreferences.getString(key, "")
    }

    init {
        appPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }
}