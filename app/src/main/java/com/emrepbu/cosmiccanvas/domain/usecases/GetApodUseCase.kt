package com.emrepbu.cosmiccanvas.domain.usecases

import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetApodUseCase @Inject constructor(
    private val apodRepository: ApodRepository
) {
    operator fun invoke(date: String): Flow<Result<Apod?>> {
        return apodRepository.getApodByDate(date)
    }
}