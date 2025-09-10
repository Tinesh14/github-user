package com.example.data.repository

import com.example.data.local.UserDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.data.remote.GitHubService
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val service: GitHubService,
    private val userDao: UserDao,
) : UserRepository {

    override fun searchUsers(query: String): Flow<List<User>> = flow {
        // Fetch remote search only
        emit(emptyList()) // or a separate Loading wrapper
        val response = try {
            service.searchUsers(query)
        } catch (e: Exception) {
            null
        }

        val users = response?.items?.map { it.toDomain() } ?: emptyList()
        emit(users)
    }

    /** Get detailed user info — optional caching */
    override fun getUserDetail(username: String): Flow<User> = flow {
        // Emit cached first if exists
        val cachedUser = userDao.getUser(username).first()
        cachedUser?.let { emit(it.toDomain()) }

        try {
            val remote = service.getUser(username).toDomain()

            // Preserve favorite state from cached user
            val mergedUser = if (cachedUser != null) {
                remote.copy(isFavorite = cachedUser.isFavorite)
            } else {
                remote
            }

            // Update DB with merged user
            userDao.insertUser(mergedUser.toEntity())

            emit(remote)
        } catch (e: Exception) {
            cachedUser?.let { emit(it.toDomain()) }
        }
    }

    // --- Favorites ---
    override fun getFavoriteUsers(): Flow<List<User>> =
        userDao.getFavoriteUsers().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addToFavorite(user: User) {
        val entity = user.toEntity().copy(isFavorite = true)
        userDao.updateUser(entity)
    }

    override suspend fun removeFromFavorite(user: User) {
        val entity = user.toEntity().copy(isFavorite = false)
        userDao.updateUser(entity)
    }


    override fun getAllUsers(since: Int, perPage: Int, isOnline: Boolean): Flow<List<User>> = flow {
        // Get cached users
        val cached = userDao.getAllUsers().firstOrNull()?.map { it.toDomain() } ?: emptyList()

        // Calculate which slice of cached to emit for this page
        val pageCached = cached.drop(since).take(perPage)

        val result = if (!isOnline) {
            if (cached.isEmpty()) emptyList() else pageCached
        } else {
            try {
                val remoteUsers = service.getAllUsers(since, perPage).map { it.toDomain() }
                val cachedFavoriteMap = cached.filter { it.isFavorite }.associateBy { it.id }

                val mergedUsers = remoteUsers.map { remoteUser ->
                    cachedFavoriteMap[remoteUser.id]?.let { cachedUser ->
                        remoteUser.copy(isFavorite = cachedUser.isFavorite)
                    } ?: remoteUser
                }

                userDao.insertUsers(mergedUsers.map { it.toEntity() })

                mergedUsers
            } catch (e: Exception) {
                pageCached.ifEmpty { emptyList() }
            }
        }

        emit(result) // 🔥 only one emission
    }
}
