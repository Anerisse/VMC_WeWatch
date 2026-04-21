package com.example.wmc_wewatch.ui.search.mvi

import com.example.wmc_wewatch.api.MovieSearchResult


//  Состояние экрана поиска

sealed class SearchState {
    object Loading : SearchState()
    data class Success(val results: List<MovieSearchResult>) : SearchState()
    data class Error(val message: String) : SearchState()
    object Empty : SearchState()
}