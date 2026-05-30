package io.github.naupyon.arisanak.domain.usecase.settings

import io.github.naupyon.arisanak.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class AppSettings(
    val userName: String,
    val language: String,
    val colorMode: String,
    val isDarkMode: Boolean,
    val isPinEnabled: Boolean,
    val pinCode: String,
    val reminderTemplate: String,
    val winTemplate: String
)

class GetSettingsUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        val f1 = repository.getUserName().map { it as Any? }
        val f2 = repository.getLanguage().map { it as Any? }
        val f3 = repository.getColorMode().map { it as Any? }
        val f4 = repository.isDarkMode().map { it as Any? }
        val f5 = repository.isPinEnabled().map { it as Any? }
        val f6 = repository.getPinCode().map { it as Any? }
        val f7 = repository.getReminderTemplate().map { it as Any? }
        val f8 = repository.getWinTemplate().map { it as Any? }

        return combine(f1, f2, f3, f4, f5, f6, f7, f8) { args ->
            AppSettings(
                userName = args[0] as String,
                language = args[1] as String,
                colorMode = args[2] as String,
                isDarkMode = args[3] as Boolean,
                isPinEnabled = args[4] as Boolean,
                pinCode = args[5] as String,
                reminderTemplate = args[6] as String,
                winTemplate = args[7] as String
            )
        }
    }
}
