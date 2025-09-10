package com.example.data.repository

import com.example.data.local.UserDao
import com.example.data.local.UserEntity
import com.example.data.remote.GitHubService
import com.example.data.remote.ApiUser
import com.example.data.remote.ApiUserDetail
import com.example.data.remote.SearchUsersResponse
import com.example.domain.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    private lateinit var repository: UserRepositoryImpl
    private val service: GitHubService = mockk()
    private val userDao: UserDao = mockk(relaxed = true)

    @Before
    fun setup() {
        repository = UserRepositoryImpl(service, userDao)
    }

    // --- searchUsers ---
    @Test
    fun `searchUsers returns remote results`() = runTest {
        // Arrange
        val dto = ApiUser(id = 1, login = "octocat", avatarUrl = "avatar.png")
        coEvery { service.searchUsers("octocat") } returns SearchUsersResponse(
            items = listOf(dto),
            totalCount = listOf(dto).size
        )

        // Act
        val result = repository.searchUsers("octocat").first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("octocat", result.first().username)
    }

    // --- getUserDetail ---
    @Test
    fun `getUserDetail emits cached first then remote, preserving favorite`() = runTest {
        // Arrange cached
        val cachedEntity = UserEntity(
            id = 1, username = "octocat", avatarUrl = "avatar.png",
            name = "Cached", bio = "From DB", isFavorite = true,
            followers = 0,
            following = 0,
            publicRepos = 0
        )
        every { userDao.getUser("octocat") } returns flowOf(cachedEntity)

        // Arrange remote
        val dto = ApiUserDetail(
            id = 1, login = "octocat", avatarUrl = "avatar.png", name = "Remote", bio = "From API",
            company = null,
            location =  null,
            followers = 0,
            following = 0,
            publicRepos = 0
        )
        coEvery { service.getUser("octocat") } returns dto

        // Act
        val result = repository.getUserDetail("octocat").first()

        // Assert
        assertEquals("octocat", result.username)
        assertEquals(true, result.isFavorite) // favorite preserved
    }

    // --- getFavoriteUsers ---
    @Test
    fun `getFavoriteUsers returns mapped domain objects`() = runTest {
        // Arrange
        val entity = UserEntity(
            id = 2, username = "john", avatarUrl = "avatar.png",
            name = "John", bio = "Dev", isFavorite = true,
            followers = 0,
            following = 0,
            publicRepos = 0
        )
        every { userDao.getFavoriteUsers() } returns flowOf(listOf(entity))

        // Act
        val result = repository.getFavoriteUsers().first()

        // Assert
        assertEquals(1, result.size)
        assertEquals("john", result.first().username)
    }

    // --- addToFavorite ---
    @Test
    fun `addToFavorite updates user with favorite flag`() = runTest {
        val user = User(
            id = 3,
            username = "anna",
            avatarUrl = "a.png",
            name = "Anna",
            bio = "Dev",
            isFavorite = false,
            followers = 0,
            following = 0,
            publicRepos = 0
        )

        repository.addToFavorite(user)

        coVerify { userDao.updateUser(match { it.username == "anna" && it.isFavorite }) }
    }

    // --- removeFromFavorite ---
    @Test
    fun `removeFromFavorite updates user with favorite=false`() = runTest {
        val user = User(
            id = 3,
            username = "anna",
            avatarUrl = "a.png",
            name = "Anna",
            bio = "Dev",
            isFavorite = true,
            followers = 0,
            following = 0,
            publicRepos = 0
        )

        repository.removeFromFavorite(user)

        coVerify { userDao.updateUser(match { it.username == "anna" && !it.isFavorite }) }
    }

    // --- getAllUsers ---
    @Test
    fun `getAllUsers merges remote users with cached favorites`() = runTest {
        // Arrange cached favorite
        val cachedEntity = UserEntity(
            id = 3, username = "favUser", avatarUrl = "fav.png",
            name = "Fav", bio = "Bio", isFavorite = true,
            followers = 0,
            following = 0,
            publicRepos = 0
        )
        every { userDao.getAllUsers() } returns flowOf(listOf(cachedEntity))

        // Arrange remote
        val remoteDto = ApiUser(id = 3, login = "favUser", avatarUrl = "fav.png")
        coEvery { service.getAllUsers(0, 30) } returns listOf(remoteDto)

        // Act
        val result = repository.getAllUsers(0, 30, isOnline = true).first()

        // Assert
        assertEquals(1, result.size)
        assertEquals(true, result.first().isFavorite) // favorite preserved
    }

    @Test
    fun `getAllUsers falls back to cache when service fails`() = runTest {
        // Arrange cached
        val cachedEntity = UserEntity(
            id = 4, username = "cachedUser", avatarUrl = "c.png",
            name = "Cached", bio = "Local", isFavorite = false,
            followers = 0,
            following = 0,
            publicRepos = 0
        )
        every { userDao.getAllUsers() } returns flowOf(listOf(cachedEntity))

        // Arrange remote failure
        coEvery { service.getAllUsers(any(), any()) } throws Exception("Network error")

        // Act
        val result = repository.getAllUsers(0, 30, isOnline = true).first()

        // Assert
        assertEquals("cachedUser", result.first().username)
    }
}
