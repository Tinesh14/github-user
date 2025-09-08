package com.example.data.di


import com.example.data.remote.GitHubService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val networkModule = module {
    single {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(MoshiConverterFactory.create(get()))
            .client(get())
            .build()
    }

    single { get<Retrofit>().create(GitHubService::class.java) }
}

