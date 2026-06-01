package io.github.naupyon.arisanak.data.local.entity

import androidx.room.*
import io.github.naupyon.arisanak.domain.model.*

@Entity(tableName = "arisan_group")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val frequency: ArisanFrequency,
    @ColumnInfo(name = "base_due_amount") val baseDueAmount: Double,
    @ColumnInfo(name = "current_interval_sequence") val currentIntervalSequence: Int = 1,
    @ColumnInfo(name = "is_archived") val isArchived: Boolean = false,
    @ColumnInfo(name = "kas_balance") val kasBalance: Double = 0.0,
    @ColumnInfo(name = "eligible_kas_winner_ids") val eligibleKasWinnerIds: String = "[]",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "member",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["group_id"])]
)
data class MemberEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "group_id") val groupId: Long?,
    @ColumnInfo(name = "contact_id") val contactId: String?,
    @ColumnInfo(name = "display_name") val displayName: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String?,
    @ColumnInfo(name = "custom_due_amount") val customDueAmount: Double?,
    @ColumnInfo(name = "start_interval_sequence") val startIntervalSequence: Int = 1,
    @ColumnInfo(name = "has_won") val hasWon: Boolean = false
)

@Entity(
    tableName = "interval",
    foreignKeys = [
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["group_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["group_id"])]
)
data class IntervalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "group_id") val groupId: Long,
    @ColumnInfo(name = "sequence_number") val sequenceNumber: Int,
    @ColumnInfo(name = "start_date") val startDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "winner_member_id") val winnerMemberId: Long? = null
)

@Entity(
    tableName = "payment_log",
    foreignKeys = [
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["member_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = IntervalEntity::class,
            parentColumns = ["id"],
            childColumns = ["interval_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["member_id"]),
        Index(value = ["interval_id"])
    ]
)
data class PaymentLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "member_id") val memberId: Long,
    @ColumnInfo(name = "interval_id") val intervalId: Long,
    @ColumnInfo(name = "amount_paid") val amountPaid: Double,
    @ColumnInfo(name = "is_ditalangi") val isDitalangi: Boolean = false,
    @ColumnInfo(name = "receipt_image_path") val receiptImagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "piutang_log",
    foreignKeys = [
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["member_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["member_id"])]
)
data class PiutangLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "member_id") val memberId: Long,
    @ColumnInfo(name = "amount_debt") val amountDebt: Double,
    @ColumnInfo(name = "amount_repaid") val amountRepaid: Double = 0.0,
    @ColumnInfo(name = "is_settled") val isSettled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

// Mappers
fun GroupEntity.toDomain() = Group(id, name, frequency, baseDueAmount, currentIntervalSequence, isArchived, kasBalance, eligibleKasWinnerIds, createdAt)
fun Group.toEntity() = GroupEntity(id, name, frequency, baseDueAmount, currentIntervalSequence, isArchived, kasBalance, eligibleKasWinnerIds, createdAt)

fun MemberEntity.toDomain() = Member(id, groupId, contactId, displayName, phoneNumber, customDueAmount, startIntervalSequence, hasWon)
fun Member.toEntity() = MemberEntity(id, groupId, contactId, displayName, phoneNumber, customDueAmount, startIntervalSequence, hasWon)

fun IntervalEntity.toDomain() = Interval(id, groupId, sequenceNumber, startDate, isCompleted, winnerMemberId)
fun Interval.toEntity() = IntervalEntity(id, groupId, sequenceNumber, startDate, isCompleted, winnerMemberId)

fun PaymentLogEntity.toDomain() = PaymentLog(id, memberId, intervalId, amountPaid, isDitalangi, receiptImagePath, timestamp)
fun PaymentLog.toEntity() = PaymentLogEntity(id, memberId, intervalId, amountPaid, isDitalangi, receiptImagePath, timestamp)

fun PiutangLogEntity.toDomain() = PiutangLog(id, memberId, amountDebt, amountRepaid, isSettled, timestamp)
fun PiutangLog.toEntity() = PiutangLogEntity(id, memberId, amountDebt, amountRepaid, isSettled, timestamp)
