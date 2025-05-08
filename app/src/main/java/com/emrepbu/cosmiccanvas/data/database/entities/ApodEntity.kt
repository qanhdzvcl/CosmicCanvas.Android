package com.emrepbu.cosmiccanvas.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apods")
data class ApodEntity(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val mediaType: String,
    val thumbnailUrl: String,
    val copyright: String,
    val isFavorite: Boolean = false
)