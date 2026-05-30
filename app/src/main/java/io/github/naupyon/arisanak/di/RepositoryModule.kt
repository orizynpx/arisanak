package io.github.naupyon.arisanak.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.naupyon.arisanak.data.repository.ArisanRepositoryImpl
import io.github.naupyon.arisanak.data.repository.SettingsRepositoryImpl
import io.github.naupyon.arisanak.domain.repository.ArisanRepository
import io.github.naupyon.arisanak.domain.repository.SettingsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideArisanRepository(impl: ArisanRepositoryImpl): ArisanRepository = impl

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }
}
