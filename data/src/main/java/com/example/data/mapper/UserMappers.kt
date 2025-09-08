package com.example.data.mapper

import com.example.data.remote.ApiUser
import com.example.data.remote.ApiUserDetail
import com.example.domain.model.User


fun ApiUser.toDomain(): User =
    User(id = id, username = login, avatarUrl = avatarUrl, name = null, bio = null)

fun ApiUserDetail.toDomain(): User =
    User(id = id, username = login, avatarUrl = avatarUrl, name = name, bio = bio)
