package io.github.naupyon.arisanak.domain.usecase.arisan

import io.github.naupyon.arisanak.domain.model.*
import io.github.naupyon.arisanak.domain.repository.ArisanRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val repository: ArisanRepository
) {
    suspend operator fun invoke(name: String, frequency: ArisanFrequency, baseDue: Double, memberNames: List<Pair<String, String?>>) {
        val group = Group(
            name = name,
            frequency = frequency,
            baseDueAmount = baseDue,
            currentIntervalSequence = 1
        )
        val groupId = repository.insertGroup(group)

        val members = memberNames.map { (name, phone) ->
            Member(
                groupId = groupId,
                contactId = null,
                displayName = name,
                phoneNumber = phone,
                customDueAmount = null,
                hasWon = false
            )
        }
        repository.insertMembers(members)

        val interval = Interval(
            groupId = groupId,
            sequenceNumber = 1,
            isCompleted = false
        )
        repository.insertInterval(interval)
    }
}
