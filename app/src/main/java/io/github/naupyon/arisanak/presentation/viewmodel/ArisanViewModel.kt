package io.github.naupyon.arisanak.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.naupyon.arisanak.domain.model.*
import io.github.naupyon.arisanak.domain.repository.ArisanRepository
import io.github.naupyon.arisanak.domain.usecase.arisan.*
import io.github.naupyon.arisanak.domain.usecase.settings.GetSettingsUseCase
import io.github.naupyon.arisanak.domain.usecase.settings.UpdateSettingsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ArisanViewModel @Inject constructor(
    private val repository: ArisanRepository,
    private val createGroupUseCase: CreateGroupUseCase,
    private val registerPaymentUseCase: RegisterPaymentUseCase,
    private val executeTalangiUseCase: ExecuteTalangiUseCase,
    private val drawWinnerUseCase: DrawWinnerUseCase,
    private val advanceIntervalUseCase: AdvanceIntervalUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    val settings = getSettingsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun updateDarkMode(enabled: Boolean) = viewModelScope.launch { updateSettingsUseCase.updateDarkMode(enabled) }
    fun updateColorMode(mode: String) = viewModelScope.launch { updateSettingsUseCase.updateColorMode(mode) }
    fun updateLanguage(lang: String) = viewModelScope.launch { updateSettingsUseCase.updateLanguage(lang) }
    fun updateUserName(name: String) = viewModelScope.launch { updateSettingsUseCase.updateUserName(name) }
    fun updatePinEnabled(enabled: Boolean) = viewModelScope.launch { updateSettingsUseCase.updatePinEnabled(enabled) }
    fun updatePinCode(pin: String) = viewModelScope.launch { updateSettingsUseCase.updatePinCode(pin) }
    fun updateReminderTemplate(template: String) = viewModelScope.launch { updateSettingsUseCase.updateReminderTemplate(template) }
    fun updateWinTemplate(template: String) = viewModelScope.launch { updateSettingsUseCase.updateWinTemplate(template) }

    val groupsUiState: StateFlow<List<GroupUiState>> = combine(
        repository.getAllGroups(),
        repository.getAllMembers(),
        repository.getAllPaymentLogs()
    ) { groups, allMembers, allLogs ->
        groups.map { group ->
            val members = allMembers.filter { it.groupId == group.id }
            val intervals = repository.getIntervalsForGroup(group.id).firstOrNull() ?: emptyList()
            val activeInterval = intervals.find { !it.isCompleted } ?: intervals.firstOrNull()
            
            if (activeInterval == null) {
                GroupUiState(group, null, emptyList(), 0.0, 0.0, false, "Selesai")
            } else {
                val logs = allLogs.filter { it.intervalId == activeInterval.id }
                val memberStates = members.map { member ->
                    val memberLogs = logs.filter { it.memberId == member.id }
                    val requiredDue = member.customDueAmount ?: group.baseDueAmount
                    val isDitalangi = memberLogs.any { it.isDitalangi }
                    val totalPaid = memberLogs.sumOf { it.amountPaid }

                    val state = when {
                        isDitalangi -> PaymentState.DITALANGI
                        totalPaid >= requiredDue -> PaymentState.PAID
                        totalPaid > 0 -> PaymentState.PARTIAL
                        else -> PaymentState.UNPAID
                    }
                    MemberPaymentState(
                        member = member,
                        requiredDue = requiredDue,
                        amountPaid = totalPaid,
                        state = state,
                        sisa = (requiredDue - totalPaid).coerceAtLeast(0.0)
                    )
                }

                val targetPot = memberStates.sumOf { it.requiredDue }
                val collectedAmount = logs.sumOf { it.amountPaid }
                val allCompleted = memberStates.all { it.state == PaymentState.PAID || it.state == PaymentState.DITALANGI }

                GroupUiState(
                    group = group,
                    activeInterval = activeInterval,
                    members = memberStates,
                    targetPot = targetPot,
                    collectedAmount = collectedAmount,
                    isReadyToKocok = allCompleted,
                    statusText = if (allCompleted) "Waktunya Kocok" else "Kas Belum Lengkap"
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val piutangDebtors: StateFlow<List<PiutangDebtorState>> = repository.getActivePiutangLogs().map { logs ->
        logs.mapNotNull { log ->
            val member = repository.getMemberById(log.memberId)
            val group = member?.groupId?.let { repository.getGroupByIdOneShot(it) }
            member?.let {
                PiutangDebtorState(
                    member = it,
                    groupName = group?.name ?: "Arisan",
                    totalDebt = log.amountDebt,
                    totalRepaid = log.amountRepaid,
                    isSettled = log.isSettled
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPiutangBalance: StateFlow<Double> = piutangDebtors.map { list ->
        list.filter { !it.isSettled }.sumOf { it.totalDebt - it.totalRepaid }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val transactionHistory: StateFlow<List<TransactionHistoryItem>> = combine(
        repository.getAllPaymentLogs(),
        repository.getAllMembers(),
        repository.getAllGroups(),
        repository.getAllIntervals()
    ) { logs, members, groups, intervals ->
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
        logs.mapNotNull { log ->
            val member = members.find { it.id == log.memberId }
            val interval = intervals.find { it.id == log.intervalId }
            val group = intervals.find { it.id == log.intervalId }?.let { inv ->
                groups.find { it.id == inv.groupId }
            }
            if (member != null && group != null) {
                TransactionHistoryItem(
                    id = log.id,
                    memberName = member.displayName,
                    groupName = group.name,
                    amount = log.amountPaid,
                    isDitalangi = log.isDitalangi,
                    timestamp = log.timestamp,
                    formattedDate = sdf.format(Date(log.timestamp)),
                    receiptImagePath = log.receiptImagePath
                )
            } else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createGroup(name: String, frequency: ArisanFrequency, baseDue: Double, members: List<Pair<String, String?>>) {
        viewModelScope.launch { createGroupUseCase(name, frequency, baseDue, members) }
    }

    fun addMemberMidCycle(groupId: Long, name: String, phone: String?, isCatchUp: Boolean) {
        viewModelScope.launch {
            val activeInterval = repository.getActiveIntervalForGroupOneShot(groupId) ?: return@launch
            val memberId = repository.insertMember(Member(groupId = groupId, contactId = null, displayName = name, phoneNumber = phone, customDueAmount = null))
            if (isCatchUp) {
                val group = repository.getGroupByIdOneShot(groupId) ?: return@launch
                val missedCount = activeInterval.sequenceNumber - 1
                if (missedCount > 0) {
                    repository.insertPaymentLog(PaymentLog(memberId = memberId, intervalId = activeInterval.id, amountPaid = group.baseDueAmount * missedCount))
                }
            }
        }
    }

    fun removeMember(member: Member) = viewModelScope.launch {
        repository.updateMember(member.copy(groupId = null))
    }

    fun payCustomAmount(memberId: Long, intervalId: Long, amount: Double, imagePath: String? = null) = viewModelScope.launch {
        registerPaymentUseCase(memberId, intervalId, amount, false, imagePath)
    }

    fun payFullAmount(memberId: Long, intervalId: Long, requiredDue: Double) = viewModelScope.launch {
        val logs = repository.getPaymentLogsForIntervalOneShot(intervalId).filter { it.memberId == memberId }
        val totalPaid = logs.sumOf { it.amountPaid }
        val delta = requiredDue - totalPaid
        if (delta > 0) registerPaymentUseCase(memberId, intervalId, delta, false)
    }

    fun talangiMember(memberId: Long, intervalId: Long, requiredDue: Double) = viewModelScope.launch {
        executeTalangiUseCase(memberId, intervalId, requiredDue)
    }

    fun payPiutang(memberId: Long, amount: Double) = viewModelScope.launch {
        val logs = repository.getActivePiutangLogsForMember(memberId).filter { !it.isSettled }
        var remaining = amount
        for (log in logs) {
            val debt = log.amountDebt - log.amountRepaid
            val pay = minOf(remaining, debt)
            repository.updatePiutangLog(log.copy(amountRepaid = log.amountRepaid + pay, isSettled = (log.amountRepaid + pay) >= log.amountDebt))
            remaining -= pay
            if (remaining <= 0) break
        }
    }

    fun advanceInterval(groupId: Long) = viewModelScope.launch { advanceIntervalUseCase(groupId) }

    fun winnerDraw(groupId: Long, overrideMemberId: Long? = null, onWinnerDrawn: (Member) -> Unit) = viewModelScope.launch {
        drawWinnerUseCase(groupId, overrideMemberId, onWinnerDrawn)
    }
    
    fun getIntervalsForGroup(groupId: Long): Flow<List<Interval>> = repository.getIntervalsForGroup(groupId)
}
