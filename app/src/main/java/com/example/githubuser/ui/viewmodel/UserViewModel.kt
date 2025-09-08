package com.example.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.User
import com.example.domain.usecase.GetAllUsersUseCase
import com.example.domain.usecase.GetUserDetailUseCase
import com.example.domain.usecase.SearchUsersUseCase
import com.example.githubuser.ui.state.UserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class UserViewModel(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getUserDetailUseCase: GetUserDetailUseCase
) : ViewModel() {

    private val _allUsers = MutableStateFlow<UserUiState<List<User>>>(UserUiState.Loading)
    val allUsers: StateFlow<UserUiState<List<User>>> = _allUsers.asStateFlow()

    private val _searchResults = MutableStateFlow<UserUiState<List<User>>>(UserUiState.Success(emptyList()))
    val searchResults: StateFlow<UserUiState<List<User>>> = _searchResults.asStateFlow()

    private val _userDetail = MutableStateFlow<UserUiState<User>>(UserUiState.Loading)
    val userDetail: StateFlow<UserUiState<User>> = _userDetail.asStateFlow()

    private var currentPageSince = 0
    var isLoadingPage = false
    private var endReached = false
    private val pageSize = 30
    private val currentUsers = mutableListOf<User>()

    /** Get all users (cache first, then remote) */
    fun getAllUsersNextPage() {
        if (isLoadingPage || endReached) return

        viewModelScope.launch {
            isLoadingPage = true
            getAllUsersUseCase(currentPageSince, pageSize)
                .catch { e ->
                    // Keep old users if error
                    _allUsers.value = UserUiState.Error(e.message ?: "Unknown error")
                }
                .collect { users ->
                    if (users.isEmpty()) {
                        endReached = true
                    } else {
                        currentUsers.addAll(users)
                        _allUsers.value = UserUiState.Success(currentUsers.toList())
                        currentPageSince = currentUsers.last().id.toInt()
                    }
                }
            isLoadingPage = false
        }
    }

    /** Search users (remote only) */
    fun searchUsers(query: String) {
        viewModelScope.launch {
            searchUsersUseCase(query)
                .onStart { _searchResults.value = UserUiState.Loading }
                .catch { e -> _searchResults.value = UserUiState.Error(e.message ?: "Unknown error") }
                .collect { users -> _searchResults.value = UserUiState.Success(users) }
        }
    }

    /** Get user detail (cache first, then remote) */
    fun getUserDetail(username: String) {
        viewModelScope.launch {
            getUserDetailUseCase(username)
                .onStart { _userDetail.value = UserUiState.Loading }
                .catch { e -> _userDetail.value = UserUiState.Error(e.message ?: "Unknown error") }
                .collect { user -> _userDetail.value = UserUiState.Success(user) }
        }
    }
}
