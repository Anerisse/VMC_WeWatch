package com.example.wmc_wewatch.ui.add.mvi

import com.example.wmc_wewatch.api.MovieSearchResult

/*
  Все возможные действия на экране добавления
 */
sealed class AddIntent {
    data class UpdateSearchQuery(val query: String) : AddIntent()
    data class UpdateYear(val year: String) : AddIntent()
    data class SelectMovie(val movie: MovieSearchResult) : AddIntent()
    object StartAdding : AddIntent()
    object FinishAdding : AddIntent()
    object Reset : AddIntent()
    object ClearError : AddIntent()
}