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

        repository.updateInterval(activeInterval.copy(isCompleted = true))

        val nextSeq = group.currentIntervalSequence + 1
        repository.updateGroup(group.copy(currentIntervalSequence = nextSeq))

        val newInterval = Interval(
            groupId = groupId,
            sequenceNumber = nextSeq,
            isCompleted = false
        )
        repository.insertInterval(newInterval)
    }
}
