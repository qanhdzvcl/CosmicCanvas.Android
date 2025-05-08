package com.emrepbu.cosmiccanvas.domain.usecases

import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import javax.inject.Inject

class RefreshApodUseCase @Inject constructor(
    private val apodRepository: ApodRepository
) {
    suspend operator fun invoke(date: String): Result<Apod> {
        return apodRepository.refreshApod(date)
    }
}