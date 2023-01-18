package android.marc.com.storyapp.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionPreference private constructor(private val dataStore: DataStore<Preferences>){
    fun getSession(): Flow<LoginSession> {
        return dataStore.data.map { preferences ->
            LoginSession(
                preferences[USER_ID_KEY] ?: "",
                preferences[NAME_KEY] ?: "",
                preferences[TOKEN_KEY] ?: "",
            )
        }
    }

    fun getSessionToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun login(session: LoginSession) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = session.userId
            preferences[NAME_KEY] = session.name
            preferences[TOKEN_KEY] = session.token
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = ""
            preferences[NAME_KEY] = ""
            preferences[TOKEN_KEY] = ""
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SessionPreference? = null

        private val USER_ID_KEY = stringPreferencesKey("userId")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): SessionPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}