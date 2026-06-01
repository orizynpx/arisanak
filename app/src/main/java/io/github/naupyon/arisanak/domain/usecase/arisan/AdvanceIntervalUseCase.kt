package io.github.naupyon.arisanak.domain.usecase.arisan

import io.github.naupyon.arisanak.domain.model.Interval
import io.github.naupyon.arisanak.domain.repository.ArisanRepository
import javax.inject.Inject

class AdvanceIntervalUseCase @Inject constructor(
    private val repository: ArisanRepository
) {
    suspend operator fun invoke(groupId: Long) {
        val group = repository.getGroupByIdOneShot(groupId) ?: return
        val activeInterval = repository.getActiveIntervalForGroupOneShot(groupId) ?: return
        val members = repository.getMembersForGroupOneShot(groupId)

        repository.updateInterval(activeInterval.copy(isCompleted = true))
        
        // Handle "Kas" accumulation if the winner had a limited eligible pot
        val winnerId = activeInterval.winnerMemberId
        if (winnerId != null) {
            val winner = members.find { it.id == winnerId }
            if (winner != null) {
                repository.updateMember(winner.copy(hasWon = true))
                
                val requiredDue = winner.customDueAmount ?: group.baseDueAmount
                val totalCycles = members.size
                val remainingCycles = (totalCycles - (winner.startIntervalSequence - 1)).coerceAtLeast(1)
                val eligiblePot = requiredDue * remainingCycles
                
                val totalCollected = members.sumOf { it.customDueAmount ?: group.baseDueAmount }
                val leftover = totalCollected - eligiblePot
                
                if (leftover > 0) {
                    repository.updateGroup(group.copy(kasBalance = group.kasBalance + leftover))
                }
            }
        }

        if (group.currentIntervalSequence >= members.size) {
            // Group ends and is archived
            repository.updateGroup(repository.getGroupByIdOneShot(groupId)!!.copy(isArchived = true))
        } else {
            val updatedGroup = repository.getGroupByIdOneShot(groupId)!!
            val nextSeq = updatedGroup.currentIntervalSequence + 1
            repository.updateGroup(updatedGroup.copy(currentIntervalSequence = nextSeq))

            val newInterval = Interval(
                groupId = groupId,
                sequenceNumber = nextSeq,
                isCompleted = false
            )
            repository.insertInterval(newInterval)
        }
    }
}
