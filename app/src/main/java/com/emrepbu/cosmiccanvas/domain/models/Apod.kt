package com.emrepbu.cosmiccanvas.domain.models

data class Apod(
    val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val mediaType: String,
    val thumbnailUrl: String,
    val copyright: String,
    val isFavorite: Boolean = false
) {
    val isImage: Boolean
        get() = mediaType == "image"
        
    val isVideo: Boolean
        get() = mediaType == "video"
}