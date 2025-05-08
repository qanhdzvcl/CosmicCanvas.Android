package com.emrepbu.cosmiccanvas.data.repositories

import com.emrepbu.cosmiccanvas.data.api.NasaApiService
import com.emrepbu.cosmiccanvas.data.database.dao.ApodDao
import com.emrepbu.cosmiccanvas.data.preferences.UserPreferencesManager
import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import com.emrepbu.cosmiccanvas.utils.toApod
import com.emrepbu.cosmiccanvas.utils.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApodRepositoryImpl @Inject constructor(
    private val nasaApiService: NasaApiService,
    private val apodDao: ApodDao,
    private val userPreferencesManager: UserPreferencesManager
) : ApodRepository {
    
    override fun getApodByDate(date: String): Flow<Result<Apod?>> = flow {
        emit(Result.Loading)
        
        // Emit from database first
        try {
            apodDao.getApodByDate(date).collect { entity ->
                if (entity != null) {
                    emit(Result.Success(entity.toApod()))
                } else {
                    // If not in database, fetch from network
                    val result = refreshApod(date)
                    emit(result)
                }
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getRecentApods(count: Int): Flow<Result<List<Apod>>> {
        return apodDao.getRecentApods(count)
            .map { entities ->
                if (entities.isNotEmpty()) {
                    Result.Success(entities.map { it.toApod() })
                } else {
                    Result.Loading
                }
            }
            .flowOn(Dispatchers.IO)
    }
    
    override fun getApodsBetweenDates(startDate: String, endDate: String): Flow<Result<List<Apod>>> {
        return apodDao.getApodsBetweenDates(startDate, endDate)
            .map { entities ->
                Result.Success(entities.map { it.toApod() })
            }
            .flowOn(Dispatchers.IO)
    }
    
    override fun getFavoriteApods(): Flow<Result<List<Apod>>> {
        return apodDao.getFavoriteApods()
            .map { entities ->
                Result.Success(entities.map { it.toApod() })
            }
            .flowOn(Dispatchers.IO)
    }
    
    override suspend fun refreshApod(date: String): Result<Apod> {
        return try {
            val apiKey = userPreferencesManager.getApiKey()
            
            val response = nasaApiService.getApod(
                apiKey = apiKey,
                date = date,
                thumbnails = true
            )
            
            if (response.isSuccessful) {
                val apodDto = response.body()
                if (apodDto != null) {
                    val apodEntity = apodDto.toEntity()
                    apodDao.insertApod(apodEntity)
                    Result.Success(apodEntity.toApod())
                } else {
                    Result.Error(Exception("Empty response body"))
                }
            } else {
                Result.Error(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun refreshApodRange(startDate: String, endDate: String): Result<List<Apod>> {
        return try {
            val apiKey = userPreferencesManager.getApiKey()
            
            val response = nasaApiService.getApodRange(
                apiKey = apiKey,
                startDate = startDate,
                endDate = endDate,
                thumbnails = true
            )
            
            if (response.isSuccessful) {
                val apodDtos = response.body()
                if (apodDtos != null) {
                    val apodEntities = apodDtos.map { it.toEntity() }
                    apodDao.insertApods(apodEntities)
                    Result.Success(apodEntities.map { it.toApod() })
                } else {
                    Result.Error(Exception("Empty response body"))
                }
            } else {
                Result.Error(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    override suspend fun toggleFavorite(date: String, isFavorite: Boolean) {
        apodDao.updateFavoriteStatus(date, isFavorite)
    }
    
    override fun searchApods(keyword: String): Flow<Result<List<Apod>>> {
        return apodDao.searchApods(keyword)
            .map { entities ->
                Result.Success(entities.map { it.toApod() })
            }
            .flowOn(Dispatchers.IO)
    }
}