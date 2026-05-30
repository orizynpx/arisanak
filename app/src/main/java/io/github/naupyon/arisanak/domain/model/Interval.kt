package io.github.naupyon.arisanak.domain.model

data class Interval(
    val id: Long = 0,
    val groupId: Long,
    val sequenceNumber: Int,
    val startDate: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val winnerMemberId: Long? = null
)
