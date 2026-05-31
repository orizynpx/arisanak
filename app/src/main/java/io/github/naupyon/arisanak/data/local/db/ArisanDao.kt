package io.github.naupyon.arisanak.data.local.db

import androidx.room.*
import io.github.naupyon.arisanak.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArisanDao {
    // Group Queries
    @Query("SELECT * FROM arisan_group ORDER BY created_at DESC")
    fun getAllGroups(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM arisan_group WHERE id = :id")
    fun getGroupById(id: Long): Flow<GroupEntity?>

    @Query("SELECT * FROM arisan_group WHERE id = :id")
    suspend fun getGroupByIdOneShot(id: Long): GroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity): Long

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Delete
    suspend fun deleteGroup(group: GroupEntity)

    // Member Queries
    @Query("SELECT * FROM member")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM member WHERE group_id = :groupId")
    fun getMembersForGroup(groupId: Long): Flow<List<MemberEntity>>

    @Query("SELECT * FROM member WHERE group_id = :groupId")
    suspend fun getMembersForGroupOneShot(groupId: Long): List<MemberEntity>

    @Query("SELECT * FROM member WHERE id = :memberId")
    suspend fun getMemberById(memberId: Long): MemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    // Interval Queries
    @Query("SELECT * FROM interval WHERE group_id = :groupId ORDER BY sequence_number DESC")
    fun getIntervalsForGroup(groupId: Long): Flow<List<IntervalEntity>>

    @Query("SELECT * FROM interval WHERE group_id = :groupId AND is_completed = 0 LIMIT 1")
    fun getActiveIntervalForGroup(groupId: Long): Flow<IntervalEntity?>

    @Query("SELECT * FROM interval WHERE group_id = :groupId AND is_completed = 0 LIMIT 1")
    suspend fun getActiveIntervalForGroupOneShot(groupId: Long): IntervalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterval(interval: IntervalEntity): Long

    @Update
    suspend fun updateInterval(interval: IntervalEntity)

    @Query("SELECT * FROM interval")
    fun getAllIntervals(): Flow<List<IntervalEntity>>

    // PaymentLog Queries
    @Query("SELECT * FROM payment_log ORDER BY timestamp DESC")
    fun getAllPaymentLogs(): Flow<List<PaymentLogEntity>>

    @Query("SELECT * FROM payment_log WHERE interval_id = :intervalId")
    fun getPaymentLogsForInterval(intervalId: Long): Flow<List<PaymentLogEntity>>

    @Query("SELECT * FROM payment_log WHERE interval_id = :intervalId")
    suspend fun getPaymentLogsForIntervalOneShot(intervalId: Long): List<PaymentLogEntity>

    @Query("SELECT * FROM payment_log WHERE member_id = :memberId")
    fun getPaymentLogsForMember(memberId: Long): Flow<List<PaymentLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentLog(paymentLog: PaymentLogEntity): Long

    @Query("DELETE FROM payment_log WHERE id = :id")
    suspend fun deletePaymentLogById(id: Long)

    @Query("DELETE FROM payment_log WHERE member_id = :memberId AND interval_id = :intervalId")
    suspend fun deletePaymentLogsForMemberAndInterval(memberId: Long, intervalId: Long)

    // PiutangLog Queries
    @Query("SELECT * FROM piutang_log ORDER BY timestamp DESC")
    fun getAllPiutangLogs(): Flow<List<PiutangLogEntity>>

    @Query("SELECT * FROM piutang_log WHERE is_settled = 0 ORDER BY timestamp DESC")
    fun getActivePiutangLogs(): Flow<List<PiutangLogEntity>>

    @Query("SELECT * FROM piutang_log WHERE member_id = :memberId AND is_settled = 0")
    suspend fun getActivePiutangLogsForMember(memberId: Long): List<PiutangLogEntity>

    @Query("SELECT * FROM piutang_log WHERE member_id = :memberId")
    fun getPiutangLogsForMember(memberId: Long): Flow<List<PiutangLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPiutangLog(piutangLog: PiutangLogEntity): Long

    @Update
    suspend fun updatePiutangLog(piutangLog: PiutangLogEntity)

    @Query("DELETE FROM piutang_log WHERE id = :id")
    suspend fun deletePiutangLogById(id: Long)

    @Query("DELETE FROM piutang_log WHERE member_id = :memberId")
    suspend fun deletePiutangLogsForMember(memberId: Long)
}
