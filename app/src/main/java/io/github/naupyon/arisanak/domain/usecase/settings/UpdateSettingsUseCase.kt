package io.github.naupyon.arisanak.domain.usecase.settings

import io.github.naupyon.arisanak.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend fun updateUserName(name: String) = repository.setUserName(name)
    suspend fun updateLanguage(language: String) = repository.setLanguage(language)
    suspend fun updateColorMode(mode: String) = repository.setColorMode(mode)
    suspend fun updateDarkMode(enabled: Boolean) = repository.setDarkMode(enabled)
    suspend fun updatePinEnabled(enabled: Boolean) = repository.setPinEnabled(enabled)
    suspend fun updatePinCode(pin: String) = repository.setPinCode(pin)
    suspend fun updateReminderTemplate(template: String) = repository.setReminderTemplate(template)
    suspend fun updateWinTemplate(template: String) = repository.setWinTemplate(template)
}
