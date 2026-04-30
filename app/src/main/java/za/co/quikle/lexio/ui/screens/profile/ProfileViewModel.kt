package za.co.quikle.lexio.ui.screens.profile

import android.app.Application
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import za.co.quikle.lexio.dataStore
import za.co.quikle.lexio.util.Constants

data class ProfileUiState(
    val isLoggedIn: Boolean = false,
    val userEmail: String = "",
    val userName: String = "Guest User",
    val isPremium: Boolean = false,
    val isDarkMode: Boolean = false,
    val isNotificationsEnabled: Boolean = true,
    val savedConversationsCount: Int = 0,
    val savedRightsCount: Int = 0,
    val showLanguageSheet: Boolean = false,
    val showDisclaimerSheet: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val dataStore = application.dataStore

    init {
        viewModelScope.launch {
            val prefs = dataStore.data.first()
            val darkMode = prefs[booleanPreferencesKey(Constants.DARK_MODE_KEY)] ?: false
            _uiState.value = _uiState.value.copy(isDarkMode = darkMode)
        }
    }

    fun toggleDarkMode() {
        val newValue = !_uiState.value.isDarkMode
        _uiState.value = _uiState.value.copy(isDarkMode = newValue)
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(Constants.DARK_MODE_KEY)] = newValue
            }
        }
    }

    fun toggleNotifications() {
        _uiState.value = _uiState.value.copy(
            isNotificationsEnabled = !_uiState.value.isNotificationsEnabled
        )
    }

    fun showLanguageSheet() {
        _uiState.value = _uiState.value.copy(showLanguageSheet = true)
    }

    fun hideLanguageSheet() {
        _uiState.value = _uiState.value.copy(showLanguageSheet = false)
    }

    fun showDisclaimerSheet() {
        _uiState.value = _uiState.value.copy(showDisclaimerSheet = true)
    }

    fun hideDisclaimerSheet() {
        _uiState.value = _uiState.value.copy(showDisclaimerSheet = false)
    }
}
