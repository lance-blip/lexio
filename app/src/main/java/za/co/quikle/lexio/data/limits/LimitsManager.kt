package za.co.quikle.lexio.data.limits

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.limitsDataStore: DataStore<Preferences> by preferencesDataStore(name = "limits_prefs")

class LimitsManager(context: Context) {

    private val dataStore = context.limitsDataStore

    private val chatMessagesKey = intPreferencesKey("chat_messages_today")
    private val analysesKey = intPreferencesKey("analyses_today")
    private val lastResetKey = longPreferencesKey("last_reset_date")

    companion object {
        const val MAX_FREE_DAILY_CHATS = 20
        const val MAX_FREE_DAILY_SCENARIOS = 3
    }

    suspend fun getChatMessagesToday(): Int {
        resetIfNewDay()
        return dataStore.data.map { it[chatMessagesKey] ?: 0 }.first()
    }

    suspend fun incrementChatMessages() {
        dataStore.edit { prefs ->
            val current = prefs[chatMessagesKey] ?: 0
            prefs[chatMessagesKey] = current + 1
        }
    }

    suspend fun canSendChatMessage(): Boolean {
        return getChatMessagesToday() < MAX_FREE_DAILY_CHATS
    }

    suspend fun getAnalysesToday(): Int {
        resetIfNewDay()
        return dataStore.data.map { it[analysesKey] ?: 0 }.first()
    }

    suspend fun incrementAnalyses() {
        dataStore.edit { prefs ->
            val current = prefs[analysesKey] ?: 0
            prefs[analysesKey] = current + 1
        }
    }

    suspend fun canAnalyseScenario(): Boolean {
        return getAnalysesToday() < MAX_FREE_DAILY_SCENARIOS
    }

    private suspend fun resetIfNewDay() {
        val todayStart = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        dataStore.edit { prefs ->
            val lastReset = prefs[lastResetKey] ?: 0
            if (lastReset < todayStart) {
                prefs[chatMessagesKey] = 0
                prefs[analysesKey] = 0
                prefs[lastResetKey] = todayStart
            }
        }
    }
}
