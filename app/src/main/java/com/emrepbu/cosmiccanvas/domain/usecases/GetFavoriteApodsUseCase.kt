package com.emrepbu.cosmiccanvas.domain.usecases

import com.emrepbu.cosmiccanvas.domain.models.Apod
import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import com.emrepbu.cosmiccanvas.utils.NetworkUtils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteApodsUseCase @Inject constructor(
    private val apodRepository: ApodRepository
) {
    operator fun invoke(): Flow<Result<List<Apod>>> {
        return apodRepository.getFavoriteApods()
    }
}