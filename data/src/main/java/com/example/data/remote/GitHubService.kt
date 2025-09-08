package com.example.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): SearchUsersResponse

    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): ApiUserDetail

    // Optional: Fetch all users (pagination)
    @GET("users")
    suspend fun getAllUsers(
        @Query("since") since: Int = 0,
        @Query("per_page") perPage: Int = 30
    ): List<ApiUser>
}
