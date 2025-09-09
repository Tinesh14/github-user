package com.example.domain.usecase

import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetAllUsersUseCase(private val repository: UserRepository) {
    operator fun invoke(since: Int = 0, perPage: Int = 30, isOnline: Boolean = false): Flow<List<User>> = repository.getAllUsers(since, perPage, isOnline)
}
