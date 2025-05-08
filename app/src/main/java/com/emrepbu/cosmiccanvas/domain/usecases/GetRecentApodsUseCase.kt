package com.emrepbu.cosmiccanvas.domain.usecases

import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import com.emrepbu.cosmiccanvas.utils.Constants
import com.emrepbu.cosmiccanvas.utils.DateUtils
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetRecentApodsUseCase @Inject constructor(
    private val apodRepository: ApodRepository,
    private val refreshApodRangeUseCase: RefreshApodRangeUseCase
) {
    operator fun invoke(count: Int = Constants.RECENT_APODS_COUNT): Flow<Result<List<Apod>>> {
        // Try to refresh the data first
        val endDate = DateUtils.getTodayDateString()
        val recentDates = DateUtils.getRecentDates(count)
        val startDate = recentDates.lastOrNull() ?: endDate
        
        return apodRepository.getRecentApods(count).onEach { result ->
            // If we have fewer results than expected, try refreshing from the network
            if (result is Result.Success && result.data.size < count) {
                refreshApodRangeUseCase(startDate, endDate)
            }
        }
    }
}