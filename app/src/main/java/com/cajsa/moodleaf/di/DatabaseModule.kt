package com.cajsa.moodleaf.di

import android.content.Context
import androidx.room.Room
import com.cajsa.moodleaf.data.local.AppDatabase
import com.cajsa.moodleaf.data.local.MIGRATION_2_3
import com.cajsa.moodleaf.data.local.MIGRATION_3_4
import com.cajsa.moodleaf.data.local.MIGRATION_4_5
import com.cajsa.moodleaf.data.local.MoodEntryDao
import com.cajsa.moodleaf.data.local.PageElementDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "moodleaf.db")
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideMoodEntryDao(database: AppDatabase): MoodEntryDao = database.moodEntryDao()

    @Provides
    fun providePageElementDao(database: AppDatabase): PageElementDao = database.pageElementDao()
}
