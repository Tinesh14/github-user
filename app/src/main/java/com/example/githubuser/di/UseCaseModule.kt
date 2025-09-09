package com.example.githubuser.di

import com.example.domain.usecase.AddFavoriteUserUseCase
import com.example.domain.usecase.GetAllUsersUseCase
import com.example.domain.usecase.GetFavoriteUsersUseCase
import com.example.domain.usecase.GetUserDetailUseCase
import com.example.domain.usecase.RemoveFavoriteUserUseCase
import com.example.domain.usecase.SearchUsersUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetAllUsersUseCase(get()) }
    single { SearchUsersUseCase(get()) }
    single { GetUserDetailUseCase(get()) }
    single { GetFavoriteUsersUseCase(get()) }
    single { AddFavoriteUserUseCase(get()) }
    single { RemoveFavoriteUserUseCase(get()) }
}
