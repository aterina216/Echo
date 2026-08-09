package com.example.echo.di

import com.example.echo.data.repository.RadioRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun provideRepository(): RadioRepository = RadioRepository()
}