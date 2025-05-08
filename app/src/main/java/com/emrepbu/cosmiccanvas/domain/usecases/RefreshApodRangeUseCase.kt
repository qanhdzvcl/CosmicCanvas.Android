package com.emrepbu.cosmiccanvas.domain.usecases

import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import javax.inject.Inject

class RefreshApodRangeUseCase @Inject constructor(
    private val apodRepository: ApodRepository
) {
    suspend operator fun invoke(startDate: String, endDate: String): Result<List<Apod>> {
        return apodRepository.refreshApodRange(startDate, endDate)
    }
}