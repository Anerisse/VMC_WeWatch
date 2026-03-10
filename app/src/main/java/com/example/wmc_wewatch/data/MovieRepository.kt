package com.example.wmc_wewatch.data


import kotlinx.coroutines.flow.Flow

class MovieRepository(private val movieDao: MovieDao) {

    // Получить список всех фильмов
    val getAllMovies: Flow<List<Movie>> = movieDao.getAllMovies()

    // Добавить фильм
    suspend fun insertMovie(movie: Movie) {
        movieDao.insertMovie(movie)
    }

    // Удалить фильм
    suspend fun deleteMovie(movie: Movie) {
        movieDao.deleteMovie(movie)
    }

    // Удалить выбранные фильмы
    suspend fun deleteSelectedMovies() {
        movieDao.deleteSelectedMovies()
    }

    suspend fun updateMovie(movie: Movie) {
        movieDao.updateMovie(movie)
    }
}