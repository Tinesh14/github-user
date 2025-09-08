package com.example.data.mapper

import com.example.data.local.UserEntity
import com.example.domain.model.User


fun UserEntity.toDomain(): User =
    User(id, username, avatarUrl, name, bio, followers, following, publicRepos)

fun User.toEntity(): UserEntity =
    UserEntity(id, username, avatarUrl, name, bio, followers, following, publicRepos)
