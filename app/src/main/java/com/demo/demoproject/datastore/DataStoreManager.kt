package com.demo.demoproject.datastore


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(private val context: Context) {

    // Save methods
    suspend fun saveString(key: String, value: String) {
        val dataKey = stringPreferencesKey(key)
        context.dataStore.edit { it[dataKey] = value }
    }

    suspend fun saveInt(key: String, value: Int) {
        val dataKey = intPreferencesKey(key)
        context.dataStore.edit { it[dataKey] = value }
    }

    suspend fun saveBoolean(key: String, value: Boolean) {
        val dataKey = booleanPreferencesKey(key)
        context.dataStore.edit { it[dataKey] = value }
    }

    suspend fun saveFloat(key: String, value: Float) {
        val dataKey = floatPreferencesKey(key)
        context.dataStore.edit { it[dataKey] = value }
    }

    suspend fun saveLong(key: String, value: Long) {
        val dataKey = longPreferencesKey(key)
        context.dataStore.edit { it[dataKey] = value }
    }

    // Read methods
    fun getString(key: String): Flow<String?> {
        val dataKey = stringPreferencesKey(key)
        return context.dataStore.data.map { it[dataKey] }
    }

    fun getInt(key: String): Flow<Int?> {
        val dataKey = intPreferencesKey(key)
        return context.dataStore.data.map { it[dataKey] }
    }

    fun getBoolean(key: String): Flow<Boolean?> {
        val dataKey = booleanPreferencesKey(key)
        return context.dataStore.data.map { it[dataKey] }
    }

    fun getFloat(key: String): Flow<Float?> {
        val dataKey = floatPreferencesKey(key)
        return context.dataStore.data.map { it[dataKey] }
    }

    fun getLong(key: String): Flow<Long?> {
        val dataKey = longPreferencesKey(key)
        return context.dataStore.data.map { it[dataKey] }
    }

    // Remove a specific key
    suspend fun remove(key: String, type: PreferenceType) {
        context.dataStore.edit {
            when (type) {
                PreferenceType.STRING -> it.remove(stringPreferencesKey(key))
                PreferenceType.INT -> it.remove(intPreferencesKey(key))
                PreferenceType.BOOLEAN -> it.remove(booleanPreferencesKey(key))
                PreferenceType.FLOAT -> it.remove(floatPreferencesKey(key))
                PreferenceType.LONG -> it.remove(longPreferencesKey(key))
            }
        }
    }

    // Clear all preferences
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}

enum class PreferenceType {
    STRING, INT, BOOLEAN, FLOAT, LONG
}
