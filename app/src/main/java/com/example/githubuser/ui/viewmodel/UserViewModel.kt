package com.example.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.User
import com.example.domain.usecase.AddFavoriteUserUseCase
import com.example.domain.usecase.GetAllUsersUseCase
import com.example.domain.usecase.GetFavoriteUsersUseCase
import com.example.domain.usecase.GetUserDetailUseCase
import com.example.domain.usecase.RemoveFavoriteUserUseCase
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
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val getFavoriteUsersUseCase: GetFavoriteUsersUseCase,
    private val addFavoriteUserUseCase: AddFavoriteUserUseCase,
    private val removeFavoriteUserUseCase: RemoveFavoriteUserUseCase
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

    // --- favorites state ---
    private val _favorites = MutableStateFlow<UserUiState<List<User>>>(UserUiState.Loading)
    val favorites: StateFlow<UserUiState<List<User>>> = _favorites.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()


    /** Observe favorite users continuously */
     fun observeFavorites() {
        viewModelScope.launch {
            getFavoriteUsersUseCase()
                .catch { e -> _favorites.value = UserUiState.Error(e.message ?: "Unknown error") }
                .collect { users ->
                    _favorites.value = UserUiState.Success(users)
                }
        }
    }

    fun observeFavoriteStatus(username: String) {
        viewModelScope.launch {
            getFavoriteUsersUseCase().collect { users ->
                println("Users: $users")
                _isFavorite.value = users.any { it.username == username }
            }
        }
    }

    fun addToFavorite(user: User) {
        viewModelScope.launch { addFavoriteUserUseCase(user) }
    }

    fun removeFromFavorite(user: User) {
        viewModelScope.launch { removeFavoriteUserUseCase(user) }
    }

    /** Get all users (cache first, then remote) */
    fun getAllUsersNextPage(isOnline: Boolean) {
        if (isLoadingPage || endReached) return

        viewModelScope.launch {
            isLoadingPage = true
            getAllUsersUseCase(currentPageSince, pageSize, isOnline = isOnline)
                .catch { e ->
                    // Keep old users if error
                    _allUsers.value = UserUiState.Error(e.message ?: "Unknown error")
                }
                .collect { users ->
                    if (users.isEmpty()) {
                        endReached = true
                        currentUsers.addAll(emptyList())
                        _allUsers.value = UserUiState.Success(currentUsers.toList())
                    } else {
                        currentUsers.addAll(users)
                        _allUsers.value = UserUiState.Success(currentUsers.toList())
                        currentPageSince = currentUsers.last().id.toInt()
                    }
                }
            isLoadingPage = false
        }
    }

    fun refreshUsers(isOnline: Boolean) {
        if (isLoadingPage) return

        viewModelScope.launch {
            // Reset pagination
            currentPageSince = 0
            endReached = false
            currentUsers.clear()

            // Load first page
            getAllUsersNextPage(isOnline)
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
