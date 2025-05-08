package com.emrepbu.cosmiccanvas.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emrepbu.cosmiccanvas.data.database.dao.ApodDao
import com.emrepbu.cosmiccanvas.data.database.dao.TranslationDao
import com.emrepbu.cosmiccanvas.data.database.entities.ApodEntity
import com.emrepbu.cosmiccanvas.data.database.entities.TranslationEntity

@Database(
    entities = [ApodEntity::class, TranslationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CosmicDatabase : RoomDatabase() {
    abstract fun apodDao(): ApodDao
    abstract fun translationDao(): TranslationDao
}