package io.github.naupyon.arisanak.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtil {
    private val indonesianLocale = Locale("in", "ID")
    private val formatter = NumberFormat.getCurrencyInstance(indonesianLocale).apply {
        maximumFractionDigits = 0
    }

    fun formatCurrency(amount: Double): String {
        // format usually returns "Rp 10.000" or similar depending on platform.
        // We want "Rp10.000" (no space).
        return formatter.format(amount).replace("Rp ", "Rp").replace("Rp", "Rp")
    }
    
    fun formatCurrency(amount: Long): String = formatCurrency(amount.toDouble())
}
