package com.example.wmc_wewatch.ui.add.mvi

import com.example.wmc_wewatch.api.MovieSearchResult

//  Единое состояние экрана добавления

data class AddState(
    val searchQuery: String = "",
    val year: String = "",
    val selectedMovie: MovieSearchResult? = null,
    val isAdding: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)