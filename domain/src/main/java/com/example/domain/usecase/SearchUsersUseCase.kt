package com.example.domain.usecase

import com.example.domain.repository.UserRepository

class SearchUsersUseCase(private val repository: UserRepository) {
    operator fun invoke(query: String) = repository.searchUsers(query)
}
