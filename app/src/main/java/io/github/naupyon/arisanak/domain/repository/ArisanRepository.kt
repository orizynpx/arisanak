package io.github.naupyon.arisanak.domain.repository

import io.github.naupyon.arisanak.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ArisanRepository {
    fun getAllGroups(): Flow<List<Group>>
    fun getGroupById(id: Long): Flow<Group?>
    suspend fun getGroupByIdOneShot(id: Long): Group?
    suspend fun insertGroup(group: Group): Long
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(group: Group)

    fun getAllMembers(): Flow<List<Member>>
    fun getMembersForGroup(groupId: Long): Flow<List<Member>>
    suspend fun getMembersForGroupOneShot(groupId: Long): List<Member>
    suspend fun getMemberById(memberId: Long): Member?
    suspend fun insertMember(member: Member): Long
    suspend fun insertMembers(members: List<Member>)
    suspend fun updateMember(member: Member)
    suspend fun deleteMember(member: Member)

    fun getIntervalsForGroup(groupId: Long): Flow<List<Interval>>
    fun getActiveIntervalForGroup(groupId: Long): Flow<Interval?>
    suspend fun getActiveIntervalForGroupOneShot(groupId: Long): Interval?
    suspend fun insertInterval(interval: Interval): Long
    suspend fun updateInterval(interval: Interval)
    fun getAllIntervals(): Flow<List<Interval>>

    fun getAllPaymentLogs(): Flow<List<PaymentLog>>
    fun getPaymentLogsForInterval(intervalId: Long): Flow<List<PaymentLog>>
    suspend fun getPaymentLogsForIntervalOneShot(intervalId: Long): List<PaymentLog>
    fun getPaymentLogsForMember(memberId: Long): Flow<List<PaymentLog>>
    suspend fun insertPaymentLog(paymentLog: PaymentLog): Long
    suspend fun deletePaymentLogById(id: Long)
    suspend fun deletePaymentLogsForMemberAndInterval(memberId: Long, intervalId: Long)

    fun getActivePiutangLogs(): Flow<List<PiutangLog>>
    fun getAllPiutangLogs(): Flow<List<PiutangLog>>
    suspend fun getActivePiutangLogsForMember(memberId: Long): List<PiutangLog>
    fun getPiutangLogsForMember(memberId: Long): Flow<List<PiutangLog>>
    suspend fun insertPiutangLog(piutangLog: PiutangLog): Long
    suspend fun updatePiutangLog(piutangLog: PiutangLog)
    suspend fun deletePiutangLogById(id: Long)
    suspend fun deletePiutangLogsForMember(memberId: Long)
}
