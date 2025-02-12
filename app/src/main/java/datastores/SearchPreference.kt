package datastores

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SearchPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val QUERY_KEY = stringPreferencesKey("query_key")
    }

    val searchQuery: Flow<String> = dataStore.data.catch { ex ->
        if (ex is IOException) {
            emit(
                emptyPreferences()
            )
        } else {
            throw ex
        }
    }.map { prefs -> prefs[QUERY_KEY] ?: "" }

    suspend fun saveQuery(query: String) {
        dataStore.edit { prefs -> prefs[QUERY_KEY] = query }
    }
}