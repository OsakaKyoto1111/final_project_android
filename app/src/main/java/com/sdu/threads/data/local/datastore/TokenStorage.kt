package com.sdu.threads.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "threads_prefs")

class TokenStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val appContext = context.applicationContext

    val tokenFlow: Flow<String> = appContext.dataStore.data
        .map { prefs -> prefs[TOKEN_KEY].orEmpty() }

    suspend fun saveToken(token: String) {
        appContext.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun clear() {
        appContext.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }


    suspend fun getToken(): String = tokenFlow.first()

    private companion object {
        val TOKEN_KEY: Preferences.Key<String> = stringPreferencesKey("access_token")
    }
}
