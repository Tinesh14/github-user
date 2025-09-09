package com.example.githubuser.di

import com.example.githubuser.ui.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        UserViewModel(
            getAllUsersUseCase = get(),
            searchUsersUseCase = get(),
            getUserDetailUseCase = get(),
            getFavoriteUsersUseCase = get(),
            addFavoriteUserUseCase = get(),
            removeFavoriteUserUseCase = get()
        )
    }
}
