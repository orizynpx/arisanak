package io.github.naupyon.arisanak.presentation.viewmodel

import io.github.naupyon.arisanak.domain.model.*

enum class PaymentState {
    UNPAID, PARTIAL, PAID, DITALANGI_UNPAID, DITALANGI_PARTIAL, DITALANGI_PAID
}

data class MemberPaymentState(
    val member: Member,
    val requiredDue: Double,
    val amountPaid: Double,
    val eligiblePot: Double,
    val state: PaymentState,
    val sisa: Double
)

data class GroupUiState(
    val group: Group,
    val activeInterval: Interval?,
    val members: List<MemberPaymentState>,
    val targetPot: Double,
    val collectedAmount: Double,
    val totalCycles: Int,
    val isReadyToKocok: Boolean,
    val statusText: String
)

data class PiutangDebtorState(
    val member: Member,
    val groupName: String,
    val totalDebt: Double,
    val totalRepaid: Double,
    val isSettled: Boolean
)

data class TransactionHistoryItem(
    val id: Long,
    val memberId: Long,
    val intervalId: Long,
    val memberName: String,
    val groupName: String,
    val amount: Double,
    val isDitalangi: Boolean,
    val timestamp: Long,
    val formattedDate: String,
    val status: PaymentState,
    val receiptImagePath: String? = null
)
