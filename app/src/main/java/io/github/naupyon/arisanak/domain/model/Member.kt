package io.github.naupyon.arisanak.domain.model

data class Member(
    val id: Long = 0,
    val groupId: Long?,
    val contactId: String?,
    val displayName: String,
    val phoneNumber: String?,
    val customDueAmount: Double?,
    val startIntervalSequence: Int = 1,
    val hasWon: Boolean = false
)
