package com.emrepbu.cosmiccanvas.di

import com.emrepbu.cosmiccanvas.data.api.SimpleTranslateService
import com.emrepbu.cosmiccanvas.data.database.dao.TranslationDao
import com.emrepbu.cosmiccanvas.data.repositories.SimpleTranslationRepositoryImpl
import com.emrepbu.cosmiccanvas.domain.repositories.TranslationRepository
import dagger.Module
import dagger.Binds
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Module for simple translation dependencies */
@Module
@InstallIn(SingletonComponent::class)
object SimpleTranslateModule {

    @Provides
    @Singleton
    fun provideSimpleTranslateService(): SimpleTranslateService {
        return SimpleTranslateService()
    }

    @Provides
    @Singleton
    fun provideTranslationRepository(
        translateService: SimpleTranslateService,
        translationDao: TranslationDao
    ): TranslationRepository {
        return SimpleTranslationRepositoryImpl(
            translateService = translateService,
            translationDao = translationDao
        )
    }
}