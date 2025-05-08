package com.emrepbu.cosmiccanvas.di

import com.emrepbu.cosmiccanvas.data.repositories.ApodRepositoryImpl
import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindApodRepository(
        apodRepositoryImpl: ApodRepositoryImpl
    ): ApodRepository
}