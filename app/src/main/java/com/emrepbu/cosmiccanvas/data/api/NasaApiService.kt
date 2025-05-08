package com.emrepbu.cosmiccanvas.data.api

import com.emrepbu.cosmiccanvas.data.api.models.ApodDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String? = null,
        @Query("thumbs") thumbnails: Boolean = true
    ): Response<ApodDto>
    
    @GET("planetary/apod")
    suspend fun getApodRange(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("thumbs") thumbnails: Boolean = true
    ): Response<List<ApodDto>>
    
    @GET("planetary/apod")
    suspend fun getApodCount(
        @Query("api_key") apiKey: String,
        @Query("count") count: Int,
        @Query("thumbs") thumbnails: Boolean = true
    ): Response<List<ApodDto>>
}