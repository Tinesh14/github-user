package com.example.domain.repository


import com.example.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsers(since: Int = 0, perPage: Int = 30, isOnline: Boolean = false): Flow<List<User>>
    fun searchUsers(query: String): Flow<List<User>>
    fun getUserDetail(username: String): Flow<User>
}

