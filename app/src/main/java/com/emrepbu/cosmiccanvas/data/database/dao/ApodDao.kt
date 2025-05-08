package com.emrepbu.cosmiccanvas.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.emrepbu.cosmiccanvas.data.database.entities.ApodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApodDao {
    @Query("SELECT * FROM apods WHERE date = :date")
    fun getApodByDate(date: String): Flow<ApodEntity?>
    
    @Query("SELECT * FROM apods WHERE date = :date")
    suspend fun getApodByDateSync(date: String): ApodEntity?
    
    @Query("SELECT * FROM apods ORDER BY date DESC")
    fun getAllApods(): Flow<List<ApodEntity>>
    
    @Query("SELECT * FROM apods ORDER BY date DESC LIMIT :limit")
    fun getRecentApods(limit: Int): Flow<List<ApodEntity>>
    
    @Query("SELECT * FROM apods WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getApodsBetweenDates(startDate: String, endDate: String): Flow<List<ApodEntity>>
    
    @Query("SELECT * FROM apods WHERE isFavorite = 1 ORDER BY date DESC")
    fun getFavoriteApods(): Flow<List<ApodEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApod(apod: ApodEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApods(apods: List<ApodEntity>)
    
    @Update
    suspend fun updateApod(apod: ApodEntity)
    
    @Query("UPDATE apods SET isFavorite = :isFavorite WHERE date = :date")
    suspend fun updateFavoriteStatus(date: String, isFavorite: Boolean)
    
    @Query("SELECT * FROM apods WHERE (title LIKE '%' || :keyword || '%' OR explanation LIKE '%' || :keyword || '%') ORDER BY date DESC")
    fun searchApods(keyword: String): Flow<List<ApodEntity>>
    
    @Query("SELECT COUNT(*) FROM apods")
    suspend fun getApodCount(): Int
}