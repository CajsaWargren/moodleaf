package com.cajsa.moodleaf.di

import com.cajsa.moodleaf.data.repository.JournalRepository
import com.cajsa.moodleaf.data.repository.JournalRepositoryImpl
import com.cajsa.moodleaf.data.repository.PageElementRepository
import com.cajsa.moodleaf.data.repository.PageElementRepositoryImpl
import com.cajsa.moodleaf.data.repository.WeatherRepository
import com.cajsa.moodleaf.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindJournalRepository(impl: JournalRepositoryImpl): JournalRepository

    @Binds
    @Singleton
    abstract fun bindPageElementRepository(impl: PageElementRepositoryImpl): PageElementRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository
}
