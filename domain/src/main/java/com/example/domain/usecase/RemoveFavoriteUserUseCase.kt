package com.example.domain.usecase

import com.example.domain.model.User
import com.example.domain.repository.UserRepository

class RemoveFavoriteUserUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(user: User) = repository.removeFromFavorite(user)
}