package com.example.githubuser.ui.viewmodel

import app.cash.turbine.test
import com.example.domain.model.User
import com.example.domain.usecase.AddFavoriteUserUseCase
import com.example.domain.usecase.GetAllUsersUseCase
import com.example.domain.usecase.GetFavoriteUsersUseCase
import com.example.domain.usecase.GetUserDetailUseCase
import com.example.domain.usecase.RemoveFavoriteUserUseCase
import com.example.domain.usecase.SearchUsersUseCase
import com.example.githubuser.ui.state.UserUiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UserViewModelTest {

    private val getAllUsersUseCase: GetAllUsersUseCase = mockk()
    private val searchUsersUseCase: SearchUsersUseCase = mockk()
    private val getUserDetailUseCase: GetUserDetailUseCase = mockk()
    private val getFavoriteUsersUseCase: GetFavoriteUsersUseCase = mockk()
    private val addFavoriteUserUseCase: AddFavoriteUserUseCase = mockk(relaxed = true)
    private val removeFavoriteUserUseCase: RemoveFavoriteUserUseCase = mockk(relaxed = true)

    private lateinit var viewModel: UserViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val scope = TestScope(testDispatcher)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        viewModel = UserViewModel(
            getAllUsersUseCase,
            searchUsersUseCase,
            getUserDetailUseCase,
            getFavoriteUsersUseCase,
            addFavoriteUserUseCase,
            removeFavoriteUserUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset to the original Main dispatcher
    }

    @Test
    fun `searchUsers emits Loading then Success`() = runTest(testDispatcher) {
        val query = "octocat"
        val user = listOf(
            User(
                id = 1,
                username = "octocat",
                avatarUrl = "avatar.png",
                name = null,
                bio = null,
                followers = null,
                following = null,
                publicRepos = null,
                isFavorite = true
            )
        )

        // Mock the use case to emit users after some steps
        coEvery { searchUsersUseCase(query) } returns flowOf(user)

        viewModel.searchUsers(query)

        // Test searchResults state flow
        viewModel.searchResults.test {
            // First emission should be Loading
            assertEquals(UserUiState.Loading, awaitItem())

            // Then Success with data
            val success = awaitItem() as UserUiState.Success
            assertEquals(user, success.data)

//            cancelAndIgnoreRemainingEvents()
        }
    }


    @Test
    fun `getUserDetail emits Success with user`() = runTest(testDispatcher) {
        val user = User(1, "octocat", "avatar.png", name = null,
            bio = null,
            followers = null,
            following = null,
            publicRepos = null,
            isFavorite = true)
        coEvery { getUserDetailUseCase("octocat") } returns flowOf(user)

        viewModel.getUserDetail("octocat")

        viewModel.userDetail.test {
            assertEquals(UserUiState.Loading, awaitItem())
            val success = awaitItem() as UserUiState.Success
            assertEquals("octocat", success.data.username)
        }
    }

    @Test
    fun `getAllUsersNextPage updates pagination and appends users`() = runTest(testDispatcher) {
        val usersPage1 = listOf(User(1, "first", "a.png", name = null,
            bio = null,
            followers = null,
            following = null,
            publicRepos = null,
            isFavorite = true))
        coEvery { getAllUsersUseCase(0, any(), true) } returns flowOf(usersPage1)

        viewModel.getAllUsersNextPage(isOnline = true)

        viewModel.allUsers.test {
            assertTrue(awaitItem() is UserUiState.Loading)

            val success = awaitItem() as UserUiState.Success
            assertEquals(1, success.data.size)
            assertEquals("first", success.data.first().username)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `observeFavorites updates favorites state`() = runTest(testDispatcher) {
        val favs = listOf(User(2, "fav", "f.png",name = null,
            bio = null,
            followers = null,
            following = null,
            publicRepos = null,
            isFavorite = true))
        coEvery { getFavoriteUsersUseCase() } returns flowOf(favs)

        viewModel.observeFavorites()

        advanceUntilIdle()

        viewModel.favorites.test {
            val success = awaitItem() as UserUiState.Success
            assertEquals("fav", success.data.first().username)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `observeFavoriteStatus updates isFavorite`() = runTest(testDispatcher) {
        val favs = listOf(User(
            2, "favUser", "f.png",
            name = null,
            bio = null,
            followers = null,
            following = null,
            publicRepos = null,
            isFavorite = true
        ))
        coEvery { getFavoriteUsersUseCase() } returns flowOf(favs)

        viewModel.observeFavoriteStatus("favUser")

        advanceUntilIdle()

        viewModel.isFavorite.test {
            assertEquals(true, awaitItem())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `addToFavorite calls use case`() = runTest(testDispatcher) {
        val user = User(1, "octo", "a.png", name = null,
            bio = null,
            followers = null,
            following = null,
            publicRepos = null,
            isFavorite = true)
        viewModel.addToFavorite(user)
        advanceUntilIdle()
        coVerify { addFavoriteUserUseCase(user) }
    }
}
