package com.taskforge.mobile.data

import android.content.Context

/**
 * Persists the JWT and username across app restarts.
 * Plain SharedPreferences keeps this demo dependency-free; for production,
 * swap in EncryptedSharedPreferences or the Android Keystore.
 */
class SessionStore(context: Context) {

    private val prefs = context.getSharedPreferences("taskforge_session", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        private set(value) {
            prefs.edit().putString(KEY_TOKEN, value).apply()
        }

    var username: String?
        get() = prefs.getString(KEY_USERNAME, null)
        private set(value) {
            prefs.edit().putString(KEY_USERNAME, value).apply()
        }

    fun save(auth: AuthResponse) {
        token = auth.token
        username = auth.username
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USERNAME = "username"
    }
}
