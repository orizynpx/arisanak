package io.github.naupyon.arisanak.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.naupyon.arisanak.data.local.db.ArisanDao
import io.github.naupyon.arisanak.data.local.db.ArisanDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideArisanDatabase(@ApplicationContext context: Context): ArisanDatabase {
        return Room.databaseBuilder(
            context,
            ArisanDatabase::class.java,
            ArisanDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideArisanDao(database: ArisanDatabase): ArisanDao {
        return database.arisanDao
    }
}
