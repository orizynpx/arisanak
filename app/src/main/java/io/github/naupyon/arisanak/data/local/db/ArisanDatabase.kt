package io.github.naupyon.arisanak.data.local.db

import androidx.room.*
import io.github.naupyon.arisanak.data.local.entity.*
import io.github.naupyon.arisanak.domain.model.ArisanFrequency

@Database(
    entities = [
        GroupEntity::class,
        MemberEntity::class,
        IntervalEntity::class,
        PaymentLogEntity::class,
        PiutangLogEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ArisanDatabase : RoomDatabase() {
    abstract val arisanDao: ArisanDao

    companion object {
        const val DATABASE_NAME = "arisan_database"
    }
}

class Converters {
    @TypeConverter
    fun fromFrequency(value: ArisanFrequency): String = value.name

    @TypeConverter
    fun toFrequency(value: String): ArisanFrequency = ArisanFrequency.valueOf(value)
}
