package com.sdu.threads.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sdu.threads.data.local.dao.RecentSearchDao
import com.sdu.threads.data.local.dao.UserDao
import com.sdu.threads.data.local.entity.CachedUserEntity
import com.sdu.threads.data.local.entity.RecentSearchEntity

@Database(
    entities = [CachedUserEntity::class, RecentSearchEntity::class],
    version = 2,
    exportSchema = false
)
abstract class ThreadsDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recentSearchDao(): RecentSearchDao
}
