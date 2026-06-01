package io.github.naupyon.arisanak.data.repository

import io.github.naupyon.arisanak.data.local.db.ArisanDao
import io.github.naupyon.arisanak.data.local.entity.*
import io.github.naupyon.arisanak.domain.model.*
import io.github.naupyon.arisanak.domain.repository.ArisanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArisanRepositoryImpl @Inject constructor(
    private val dao: ArisanDao
) : ArisanRepository {
    override fun getAllGroups(): Flow<List<Group>> = 
        dao.getAllGroups().map { list -> list.map { it.toDomain() } }

    override fun getGroupById(id: Long): Flow<Group?> = 
        dao.getGroupById(id).map { it?.toDomain() }

    override suspend fun getGroupByIdOneShot(id: Long): Group? = 
        dao.getGroupByIdOneShot(id)?.toDomain()

    override suspend fun insertGroup(group: Group): Long = 
        dao.insertGroup(group.toEntity())

    override suspend fun updateGroup(group: Group) = 
        dao.updateGroup(group.toEntity())

    override suspend fun deleteGroup(group: Group) = 
        dao.deleteGroup(group.toEntity())

    override fun getAllMembers(): Flow<List<Member>> = 
        dao.getAllMembers().map { list -> list.map { it.toDomain() } }

    override fun getMembersForGroup(groupId: Long): Flow<List<Member>> = 
        dao.getMembersForGroup(groupId).map { list -> list.map { it.toDomain() } }

    override suspend fun getMembersForGroupOneShot(groupId: Long): List<Member> = 
        dao.getMembersForGroupOneShot(groupId).map { it.toDomain() }

    override suspend fun getMemberById(memberId: Long): Member? = 
        dao.getMemberById(memberId)?.toDomain()

    override suspend fun insertMember(member: Member): Long = 
        dao.insertMember(member.toEntity())

    override suspend fun insertMembers(members: List<Member>) = 
        dao.insertMembers(members.map { it.toEntity() })

    override suspend fun updateMember(member: Member) = 
        dao.updateMember(member.toEntity())

    override suspend fun deleteMember(member: Member) = 
        dao.deleteMember(member.toEntity())

    override fun getIntervalsForGroup(groupId: Long): Flow<List<Interval>> = 
        dao.getIntervalsForGroup(groupId).map { list -> list.map { it.toDomain() } }

    override fun getActiveIntervalForGroup(groupId: Long): Flow<Interval?> = 
        dao.getActiveIntervalForGroup(groupId).map { it?.toDomain() }

    override suspend fun getActiveIntervalForGroupOneShot(groupId: Long): Interval? = 
        dao.getActiveIntervalForGroupOneShot(groupId)?.toDomain()

    override suspend fun insertInterval(interval: Interval): Long = 
        dao.insertInterval(interval.toEntity())

    override suspend fun updateInterval(interval: Interval) = 
        dao.updateInterval(interval.toEntity())

    override fun getAllIntervals(): Flow<List<Interval>> =
        dao.getAllIntervals().map { list -> list.map { it.toDomain() } }

    override fun getAllPaymentLogs(): Flow<List<PaymentLog>> = 
        dao.getAllPaymentLogs().map { list -> list.map { it.toDomain() } }

    override fun getPaymentLogsForInterval(intervalId: Long): Flow<List<PaymentLog>> = 
        dao.getPaymentLogsForInterval(intervalId).map { list -> list.map { it.toDomain() } }

    override suspend fun getPaymentLogsForIntervalOneShot(intervalId: Long): List<PaymentLog> = 
        dao.getPaymentLogsForIntervalOneShot(intervalId).map { it.toDomain() }

    override fun getPaymentLogsForMember(memberId: Long): Flow<List<PaymentLog>> = 
        dao.getPaymentLogsForMember(memberId).map { list -> list.map { it.toDomain() } }

    override suspend fun insertPaymentLog(paymentLog: PaymentLog): Long = 
        dao.insertPaymentLog(paymentLog.toEntity())

    override suspend fun deletePaymentLogById(id: Long) = 
        dao.deletePaymentLogById(id)

    override suspend fun deletePaymentLogsForMember(memberId: Long) = 
        dao.deletePaymentLogsForMember(memberId)

    override suspend fun deletePaymentLogsForMemberAndInterval(memberId: Long, intervalId: Long) = 
        dao.deletePaymentLogsForMemberAndInterval(memberId, intervalId)

    override fun getActivePiutangLogs(): Flow<List<PiutangLog>> = 
        dao.getActivePiutangLogs().map { list -> list.map { it.toDomain() } }

    override fun getAllPiutangLogs(): Flow<List<PiutangLog>> = 
        dao.getAllPiutangLogs().map { list -> list.map { it.toDomain() } }

    override suspend fun getActivePiutangLogsForMember(memberId: Long): List<PiutangLog> = 
        dao.getActivePiutangLogsForMember(memberId).map { it.toDomain() }

    override fun getPiutangLogsForMember(memberId: Long): Flow<List<PiutangLog>> = 
        dao.getPiutangLogsForMember(memberId).map { list -> list.map { it.toDomain() } }

    override suspend fun insertPiutangLog(piutangLog: PiutangLog): Long = 
        dao.insertPiutangLog(piutangLog.toEntity())

    override suspend fun updatePiutangLog(piutangLog: PiutangLog) = 
        dao.updatePiutangLog(piutangLog.toEntity())

    override suspend fun deletePiutangLogById(id: Long) = 
        dao.deletePiutangLogById(id)

    override suspend fun deletePiutangLogsForMember(memberId: Long) = 
        dao.deletePiutangLogsForMember(memberId)
}
