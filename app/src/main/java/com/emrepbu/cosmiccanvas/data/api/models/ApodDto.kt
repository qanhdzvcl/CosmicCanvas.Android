package com.emrepbu.cosmiccanvas.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApodDto(
    @Json(name = "date") val date: String,
    @Json(name = "title") val title: String,
    @Json(name = "explanation") val explanation: String,
    @Json(name = "url") val url: String,
    @Json(name = "media_type") val mediaType: String,
    @Json(name = "thumbnail_url") val thumbnailUrl: String? = null,
    @Json(name = "copyright") val copyright: String? = null,
    @Json(name = "hdurl") val hdUrl: String? = null,
    @Json(name = "service_version") val serviceVersion: String? = null
)