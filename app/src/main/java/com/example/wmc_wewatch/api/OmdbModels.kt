package com.example.wmc_wewatch.api
import java.io.Serializable

data class MovieSearchResponse(
    val Search: List<MovieSearchResult>?,
    val Response: String,
    val Error: String? = null
): Serializable

data class MovieSearchResult(
    val Title: String,
    val Year: String,
    val Type: String,
    val imdbID: String,
    val Poster: String?  // Сюда будет записан URL постера
): Serializable