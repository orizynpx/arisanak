package io.github.naupyon.arisanak.domain.model

data class PiutangLog(
    val id: Long = 0,
    val memberId: Long,
    val amountDebt: Double,
    val amountRepaid: Double = 0.0,
    val isSettled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
