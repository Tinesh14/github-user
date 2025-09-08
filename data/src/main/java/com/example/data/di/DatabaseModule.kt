package com.example.data.di


import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.UserRepository
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "app_db")
            .fallbackToDestructiveMigration(false)
            .build()
    }
    single { get<AppDatabase>().userDao() }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
}
