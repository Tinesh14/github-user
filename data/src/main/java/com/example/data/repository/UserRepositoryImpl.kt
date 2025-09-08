package com.example.data.repository


import com.example.data.local.UserDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.data.remote.GitHubService
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl(
    private val service: GitHubService,
    private val userDao: UserDao
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
        userDao.getUser(username).first()?.let { emit(it.toDomain()) }

        try {
            val remote = service.getUser(username).toDomain()

            // Optional: store detail into DB
            userDao.insertUser(remote.toEntity())

            emit(remote)
        } catch (e: Exception) {
            userDao.getUser(username).first()?.let { emit(it.toDomain()) }
        }
    }

    override fun getAllUsers(since: Int, perPage: Int): Flow<List<User>> = flow {
        // Emit cached users first
        val cached = userDao.getAllUsers().first()
        if (cached.isNotEmpty())  emit(cached.map { it.toDomain() })

        try {
            // Fetch remote users
            val remoteUsers = service.getAllUsers(since, perPage).map { it.toDomain() }

            // Store only getAllUsers result to local DB
            userDao.insertUsers(remoteUsers.map { it.toEntity() })

            // Emit fresh data
            emit(remoteUsers)
        } catch (e: Exception) {
            emit(cached.map { it.toDomain() })
        }
    }
}
