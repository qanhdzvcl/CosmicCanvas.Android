package com.emrepbu.cosmiccanvas.domain.usecases

import com.emrepbu.cosmiccanvas.domain.repositories.ApodRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val apodRepository: ApodRepository
) {
    suspend operator fun invoke(date: String, isFavorite: Boolean) {
        apodRepository.toggleFavorite(date, isFavorite)
    }
}