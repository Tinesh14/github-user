package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    val username: String,
    val avatarUrl: String,
    val name: String?,
    val bio: String?,
    val followers: Int?,
    val following: Int?,
    val publicRepos: Int?
)
