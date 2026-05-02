package za.co.quikle.lexio.data.premium

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.premiumDataStore: DataStore<Preferences> by preferencesDataStore(name = "premium_prefs")

object PremiumManager {

    private lateinit var dataStore: DataStore<Preferences>
    private val premiumKey = booleanPreferencesKey("is_premium")
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium

    fun init(context: Context) {
        dataStore = context.premiumDataStore
        scope.launch {
            dataStore.data.map { preferences ->
                preferences[premiumKey] ?: false
            }.collect { value ->
                _isPremium.value = value
            }
        }
    }

    fun setPremiumStatus(isPremium: Boolean) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[premiumKey] = isPremium
            }
        }
    }
}
