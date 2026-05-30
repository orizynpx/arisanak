package io.github.naupyon.arisanak.domain.model

data class PaymentLog(
    val id: Long = 0,
    val memberId: Long,
    val intervalId: Long,
    val amountPaid: Double,
    val isDitalangi: Boolean = false,
    val receiptImagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
