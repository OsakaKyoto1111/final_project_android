package com.sdu.threads.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_search")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val createdAt: Long
)
