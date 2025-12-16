package com.sdu.threads.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sdu.threads.data.local.entity.CachedUserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM cached_user WHERE id = :id LIMIT 1")
    suspend fun getUser(id: Long): CachedUserEntity?

    @Query("SELECT * FROM cached_user LIMIT 1")
    suspend fun getAnyUser(): CachedUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUser(user: CachedUserEntity)

    @Query("DELETE FROM cached_user")
    suspend fun clearUsers()
}
