package com.maverick.kmjshowroom

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

fun getEncryptedPrefs(context: Context) = EncryptedSharedPreferences.create(
    "secure_prefs",
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

fun saveToken(context: Context, token: String) {
    val prefs = getEncryptedPrefs(context)
    prefs.edit().putString("access_token", token).apply()
}

fun getToken(context: Context): String? {
    val prefs = getEncryptedPrefs(context)
    return prefs.getString("access_token", null)
}

fun clearToken(context: Context) {
    val prefs = getEncryptedPrefs(context)
    prefs.edit().clear().apply()
}
