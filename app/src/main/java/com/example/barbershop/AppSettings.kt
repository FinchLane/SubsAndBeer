package com.example.barbershop

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.barbershop.data.model.ThemeType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppSettings @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object{
        val BASE_CURRENCY_KEY = stringPreferencesKey("base_currency")
        val NOTIFICATION_TIME = stringPreferencesKey("notification_time")
        val LAST_SYNC_TIME_KEY = longPreferencesKey("last_sync_time")
        val IS_AUTHORIZED_KEY = booleanPreferencesKey("is_authorized")
        val THEME_KEY = stringPreferencesKey("app_theme")
    }

    val themeFlow: Flow<ThemeType> = dataStore.data
        .map { preferences ->
            preferences[THEME_KEY]?.let {
                ThemeType.valueOf(it)
            } ?: ThemeType.DARK
        }

    val isAuthorizedFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[IS_AUTHORIZED_KEY] == true
        }

    val baseCurrencyFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[BASE_CURRENCY_KEY] ?: "RUB"
        }

    val notificationTimeFlow: Flow<String> = dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_TIME] ?: "12:00"
        }

    val lastSyncTimeFlow: Flow<Long> = dataStore.data
        .map { preferences ->
            preferences[LAST_SYNC_TIME_KEY] ?: 0L
        }

    suspend fun setTheme(theme: ThemeType) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

    suspend fun setAuthorized(isAuthorized: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_AUTHORIZED_KEY] = isAuthorized
        }
    }

    suspend fun setBaseCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[BASE_CURRENCY_KEY] = currency
        }
    }

    suspend fun setNotificationTime(time: String) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_TIME] = time
        }
    }

    suspend fun setLastSyncTime(time: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIME_KEY] = time
        }
    }
}