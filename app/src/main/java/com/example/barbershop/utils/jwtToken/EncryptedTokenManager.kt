package com.example.barbershop.utils.jwtToken

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import java.util.Date
import javax.inject.Inject

class EncryptedTokenManager @Inject constructor(
    private val context: Context
) : TokenManager {

    private val sharedPreferences by lazy {
        try {
            createEncryptedSharedPreferences()
        } catch (e: Exception) {
            Log.e("EncryptedTokenManager", "Ошибка инициализации EncryptedSharedPreferences", e)
            // Возвращаем пустые SharedPreferences, чтобы избежать краха
            context.getSharedPreferences("auth_preferences", Context.MODE_PRIVATE)
        }
    }

    override fun saveToken(token: String) {
        try {
            sharedPreferences.edit().putString("jwt_token", token).apply()
        } catch (e: Exception) {
            Log.e("EncryptedTokenManager", "Ошибка при сохранении токена", e)
            handleDecryptionError()
        }
    }

    override fun getToken(): String? {
        return try {
            sharedPreferences.getString("jwt_token", null)
        } catch (e: Exception) {
            Log.e("EncryptedTokenManager", "Ошибка при получении токена", e)
            handleDecryptionError()
            null
        }
    }

    override fun deleteToken() {
        try {
            sharedPreferences.edit().remove("jwt_token").apply()
        } catch (e: Exception) {
            Log.e("EncryptedTokenManager", "Ошибка при удалении токена", e)
            handleDecryptionError()
        }
    }

    fun isTokenValid(token: String?): Boolean {
        if (token.isNullOrEmpty()) return false
        return try {
            val jwt = JWT.decode(token)
            !jwt.expiresAt.before(Date())
        } catch (e: Exception) {
            Log.e("EncryptedTokenManager", "Ошибка валидации токена", e)
            handleDecryptionError()
            false
        }
    }

    private fun handleDecryptionError() {
        try {
            // Очищаем только токен, а не все данные
            sharedPreferences.edit().remove("jwt_token").apply()
        } catch (e: Exception) {
            Log.e("EncryptedTokenManager", "Ошибка при очистке токена", e)
        }
    }

    private fun createEncryptedSharedPreferences(): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "auth_preferences",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}