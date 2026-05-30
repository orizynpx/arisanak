package io.github.naupyon.arisanak.domain.usecase.arisan

import io.github.naupyon.arisanak.domain.model.Member
import io.github.naupyon.arisanak.domain.repository.ArisanRepository
import javax.inject.Inject

class DrawWinnerUseCase @Inject constructor(
    private val repository: ArisanRepository
) {
    suspend operator fun invoke(groupId: Long, overrideMemberId: Long? = null, onWinnerDrawn: (Member) -> Unit) {
        val members = repository.getMembersForGroupOneShot(groupId)
        val eligible = members.filter { !it.hasWon && it.groupId == groupId }
        
        val winner = if (overrideMemberId != null) {
            members.find { it.id == overrideMemberId }
        } else {
            if (eligible.isEmpty()) {
                members.forEach { repository.updateMember(it.copy(hasWon = false)) }
                members.randomOrNull()
            } else {
                eligible.randomOrNull()
            }
        }

        if (winner != null) {
            val activeInterval = repository.getActiveIntervalForGroupOneShot(groupId)
            if (activeInterval != null) {
                repository.updateInterval(activeInterval.copy(winnerMemberId = winner.id))
            }
            repository.updateMember(winner.copy(hasWon = true))
            onWinnerDrawn(winner)
        }
    }
}
