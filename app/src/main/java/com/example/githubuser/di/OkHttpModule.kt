package com.example.githubuser.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.githubuser.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

val okHttpModule = module {
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Accept", "application/vnd.github.v3+json")
                    .apply {
                        val token = BuildConfig.GITHUB_TOKEN
                        if (token.isNotEmpty()) {
                            header("Authorization", "token $token")
                        }
                    }
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(
                ChuckerInterceptor.Builder(androidContext())
                    .alwaysReadResponseBody(true)
                    .build()
            )
            .build()
    }
}
