package com.example.githubuser.di

import com.example.domain.usecase.GetAllUsersUseCase
import com.example.domain.usecase.GetUserDetailUseCase
import com.example.domain.usecase.SearchUsersUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetAllUsersUseCase(get()) }       // get() = UserRepository
    single { SearchUsersUseCase(get()) }
    single { GetUserDetailUseCase(get()) }
}
