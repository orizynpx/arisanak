package io.github.naupyon.arisanak.domain.model

data class Group(
    val id: Long = 0,
    val name: String,
    val frequency: ArisanFrequency,
    val baseDueAmount: Double,
    val currentIntervalSequence: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
