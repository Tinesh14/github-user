package com.example.domain.model


data class User(
    val id: Long,
    val username: String,
    val avatarUrl: String,
    val name: String?,
    val bio: String?,
    val followers: Int?,
    val following: Int?,
    val publicRepos: Int?,
    val isFavorite: Boolean = false
)
