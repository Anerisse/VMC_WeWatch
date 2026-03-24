package com.example.wmc_wewatch.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    //Получить все фильмы
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<Movie>>

    //Вставить фильм
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    //Обновить фильм
    @Update
    suspend fun updateMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("DELETE FROM movies WHERE id IN (:ids)")
    suspend fun deleteMoviesByIds(ids: List<Int>)
}