package io.github.naupyon.arisanak.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import io.github.naupyon.arisanak.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val LANGUAGE = stringPreferencesKey("language")
        val COLOR_MODE = stringPreferencesKey("color_mode")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val PIN_ENABLED = booleanPreferencesKey("pin_enabled")
        val PIN_CODE = stringPreferencesKey("pin_code")
        val REMINDER_TEMPLATE = stringPreferencesKey("reminder_template")
        val WIN_TEMPLATE = stringPreferencesKey("win_template")
    }

    override fun getUserName(): Flow<String> = context.dataStore.data.map { it[PreferencesKeys.USER_NAME] ?: "Ibu Aminah" }
    override suspend fun setUserName(name: String) { context.dataStore.edit { it[PreferencesKeys.USER_NAME] = name } }

    override fun getLanguage(): Flow<String> = context.dataStore.data.map { it[PreferencesKeys.LANGUAGE] ?: "ID" }
    override suspend fun setLanguage(language: String) { context.dataStore.edit { it[PreferencesKeys.LANGUAGE] = language } }

    override fun getColorMode(): Flow<String> = context.dataStore.data.map { it[PreferencesKeys.COLOR_MODE] ?: "Default" }
    override suspend fun setColorMode(mode: String) { context.dataStore.edit { it[PreferencesKeys.COLOR_MODE] = mode } }

    override fun isDarkMode(): Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.DARK_MODE] ?: false }
    override suspend fun setDarkMode(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.DARK_MODE] = enabled } }

    override fun isPinEnabled(): Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.PIN_ENABLED] ?: false }
    override suspend fun setPinEnabled(enabled: Boolean) { context.dataStore.edit { it[PreferencesKeys.PIN_ENABLED] = enabled } }

    override fun getPinCode(): Flow<String> = context.dataStore.data.map { it[PreferencesKeys.PIN_CODE] ?: "" }
    override suspend fun setPinCode(pin: String) { context.dataStore.edit { it[PreferencesKeys.PIN_CODE] = pin } }

    private val DEFAULT_REMINDER = "Halo [NamaAnggota], sekadar mengingatkan untuk iuran arisan kelompok [NamaGrup]. Sisa tagihan Anda adalah Rp [SisaTagihan]. Terima kasih!"
    override fun getReminderTemplate(): Flow<String> = context.dataStore.data.map { it[PreferencesKeys.REMINDER_TEMPLATE] ?: DEFAULT_REMINDER }
    override suspend fun setReminderTemplate(template: String) { context.dataStore.edit { it[PreferencesKeys.REMINDER_TEMPLATE] = template } }

    private val DEFAULT_WIN = "Selamat kepada [NamaAnggota] telah memenangkan kocokan arisan kelompok [NamaGrup] dengan total pot Rp [NominalPot]! 🎉"
    override fun getWinTemplate(): Flow<String> = context.dataStore.data.map { it[PreferencesKeys.WIN_TEMPLATE] ?: DEFAULT_WIN }
    override suspend fun setWinTemplate(template: String) { context.dataStore.edit { it[PreferencesKeys.WIN_TEMPLATE] = template } }
}
