package com.example.githubuser.ui.state

sealed class UserUiState<out T> {
    object Loading : UserUiState<Nothing>()
    data class Success<T>(val data: T) : UserUiState<T>()
    data class Error(val message: String) : UserUiState<Nothing>()
}
