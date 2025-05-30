package com.example.movildev.repository

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DS_NAME = "session"
private val Context.dataStore by preferencesDataStore(DS_NAME)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val USER_ID = intPreferencesKey("user_id")
    }

    val loggedInUserId: Flow<Int?> =
        context.dataStore.data.map { it[Keys.USER_ID] }

    suspend fun setLoggedInUserId(id: Int) =
        context.dataStore.edit { it[Keys.USER_ID] = id }

    suspend fun clearSession() =
        context.dataStore.edit { it.remove(Keys.USER_ID) }
}