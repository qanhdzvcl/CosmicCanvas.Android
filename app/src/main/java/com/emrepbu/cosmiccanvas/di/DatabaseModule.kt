package com.emrepbu.cosmiccanvas.di

import android.content.Context
import androidx.room.Room
import com.emrepbu.cosmiccanvas.data.database.CosmicDatabase
import com.emrepbu.cosmiccanvas.data.database.dao.ApodDao
import com.emrepbu.cosmiccanvas.data.database.dao.TranslationDao
import com.emrepbu.cosmiccanvas.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideCosmicDatabase(
        @ApplicationContext context: Context
    ): CosmicDatabase {
        return Room.databaseBuilder(
            context,
            CosmicDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideApodDao(database: CosmicDatabase): ApodDao {
        return database.apodDao()
    }
    
    @Provides
    @Singleton
    fun provideTranslationDao(database: CosmicDatabase): TranslationDao {
        return database.translationDao()
    }
}