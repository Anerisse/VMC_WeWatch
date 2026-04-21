package com.example.wmc_wewatch.ui.main.mvi

import com.example.wmc_wewatch.data.Movie

//  Состояние главного экрана

data class MainState (
    val movies: List<Movie> = emptyList(),
    val selectedIds: Set<Int> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null
)