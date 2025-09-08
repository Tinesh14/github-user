package com.example.domain.usecase

import com.example.domain.repository.UserRepository

class GetUserDetailUseCase(private val repository: UserRepository) {
    operator fun invoke(username: String) = repository.getUserDetail(username)
}
