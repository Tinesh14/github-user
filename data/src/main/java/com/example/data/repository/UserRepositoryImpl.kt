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

class UserRepositoryImpl(
    private val service: GitHubService,
    private val userDao: UserDao,
) : UserRepository {

    override fun searchUsers(query: String): Flow<List<User>> = flow {
        // Fetch remote search only
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

            // Only update DB if user already exists
            if (cachedUser != null) {
                userDao.updateUser(remote.toEntity())
            }

            emit(remote)
        } catch (e: Exception) {
            cachedUser?.let { emit(it.toDomain()) }
        }
    }

    override fun getAllUsers(since: Int, perPage: Int, isOnline: Boolean): Flow<List<User>> = flow {
        // Get cached users
        val cached = userDao.getAllUsers().firstOrNull()?.map { it.toDomain() } ?: emptyList()

        // Calculate which slice of cached to emit for this page
        val pageCached = cached.drop(since).take(perPage)
        if (pageCached.isNotEmpty()) emit(pageCached)

        if (!isOnline){
            if (cached.isEmpty()) emit(emptyList())
            return@flow
        }

        try {
            // Fetch remote users
            val remoteUsers = service.getAllUsers(since, perPage).map { it.toDomain() }

            // Store remote users in DB
            userDao.insertUsers(remoteUsers.map { it.toEntity() })

            // Emit remote users
            emit(remoteUsers)
        } catch (e: Exception) {
            // If error, fallback to cached page only
            if (pageCached.isNotEmpty()) {
                emit(pageCached)
            } else {
                // If cache is empty, emit empty list
                emit(emptyList())
            }
        }
    }
}
