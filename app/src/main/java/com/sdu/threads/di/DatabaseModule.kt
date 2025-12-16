package com.sdu.threads.di

import android.content.Context
import androidx.room.Room
import com.sdu.threads.data.local.dao.RecentSearchDao
import com.sdu.threads.data.local.dao.UserDao
import com.sdu.threads.data.local.db.ThreadsDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): ThreadsDatabase =
        Room.databaseBuilder(context, ThreadsDatabase::class.java, "threads_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(db: ThreadsDatabase): UserDao = db.userDao()

    @Provides
    fun provideRecentSearchDao(db: ThreadsDatabase): RecentSearchDao = db.recentSearchDao()
}
