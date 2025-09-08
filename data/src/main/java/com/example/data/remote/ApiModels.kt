package com.example.data.remote

import com.squareup.moshi.Json

data class SearchUsersResponse(
    @Json(name = "total_count") val totalCount: Int,
    val items: List<ApiUser>
)

data class ApiUser(
    val id: Long,
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String
)

data class ApiUserDetail(
    val id: Long,
    val login: String,
    @Json(name = "avatar_url") val avatarUrl: String,
    val name: String?,
    val bio: String?,
    val company: String?,
    val location: String?,
    val followers: Int?,
    val following: Int?,
    @Json(name = "public_repos") val publicRepos: Int?
)
