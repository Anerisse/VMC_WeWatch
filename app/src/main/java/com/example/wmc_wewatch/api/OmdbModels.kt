package com.example.wmc_wewatch.api


data class MovieSearchResponse(
    val Search: List<MovieSearchResult>?,
    val Response: String,
    val Error: String? = null
)

data class MovieSearchResult(
    val Title: String,
    val Year: String,
    val Genre: String,
    val imdbID: String,
    val Poster: String?  // Сюда будет записан URL постера
)