package com.example.wmc_wewatch.api

import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String, // Название фильма

    ): MovieSearchResponse

}