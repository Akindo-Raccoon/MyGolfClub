package com.ud.mygolfclub.ui.session

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREF_NAME = "golf_session"
    private const val KEY_CLIENT_ID = "client_id"
    private const val KEY_CLIENT_NAME = "client_name"
    private const val KEY_CLIENT_PHONE = "client_phone"
    private const val KEY_LOGGED_IN = "logged_in"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveSession(context: Context, id: String, name: String, phone: String) {
        prefs(context).edit()
            .putString(KEY_CLIENT_ID, id)
            .putString(KEY_CLIENT_NAME, name)
            .putString(KEY_CLIENT_PHONE, phone)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false)

    fun getClientId(context: Context): String =
        prefs(context).getString(KEY_CLIENT_ID, "") ?: ""

    fun getClientName(context: Context): String =
        prefs(context).getString(KEY_CLIENT_NAME, "") ?: ""

    fun getClientPhone(context: Context): String =
        prefs(context).getString(KEY_CLIENT_PHONE, "") ?: ""

    fun clearSession(context: Context) {
        prefs(context).edit().clear().apply()
    }
}