package io.github.naupyon.arisanak.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getUserName(): Flow<String>
    suspend fun setUserName(name: String)

    fun getLanguage(): Flow<String>
    suspend fun setLanguage(language: String)

    fun getColorMode(): Flow<String>
    suspend fun setColorMode(mode: String)

    fun isDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)

    fun isPinEnabled(): Flow<Boolean>
    suspend fun setPinEnabled(enabled: Boolean)

    fun getPinCode(): Flow<String>
    suspend fun setPinCode(pin: String)

    fun getReminderTemplate(): Flow<String>
    suspend fun setReminderTemplate(template: String)

    fun getWinTemplate(): Flow<String>
    suspend fun setWinTemplate(template: String)
}
