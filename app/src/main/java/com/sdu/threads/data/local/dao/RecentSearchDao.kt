package com.sdu.threads.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sdu.threads.data.local.entity.RecentSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_search ORDER BY createdAt DESC LIMIT :limit")
    fun observeRecent(limit: Int = 10): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSearch(entity: RecentSearchEntity)

    @Query("DELETE FROM recent_search")
    suspend fun clear()
}
