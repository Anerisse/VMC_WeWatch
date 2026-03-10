package com.example.wmc_wewatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.wmc_wewatch.data.Movie
import com.example.wmc_wewatch.data.MovieDatabase
import com.example.wmc_wewatch.data.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    // Model
    private lateinit var repository: MovieRepository

    // Состояние для UI
    private val movies = mutableStateOf<List<Movie>>(emptyList())
    private val selectedMovieIds = mutableStateOf<Set<Int>>(emptySet())

    // Состояние для навигации
    private val showAddScreen = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация БД и репозитория
        val database = MovieDatabase.getInstance(this)
        repository = MovieRepository(database.movieDao())

        // Загружаем фильмы из БД
        loadMovies()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showAddScreen.value) {
                        // Показываем экран добавления
                        AddScreen(
                            onNavigateBack = { showAddScreen.value = false },
                            onSearchClick = {
                                // Позже здесь будет открытие SearchScreen
                                println("Ищем: $")
                            },
                            onAddMovieClick = {
                                // Позже здесь будет добавление фильма
                                println("Добавляем фильм")
                                showAddScreen.value = false
                            }
                        )
                    } else {
                        MainScreen(
                            movies = movies.value,
                            selectedMovieIds = selectedMovieIds.value,
                            onMovieSelected = { movieId, isSelected ->
                                toggleMovieSelection(movieId, isSelected)
                            },
                            onDeleteSelected = { deleteSelectedMovies() },
                            onAddMovie = { showAddScreen.value = true }
                        )
                    }
                }
            }
        }
    }

    private fun loadMovies() {
        lifecycleScope.launch(Dispatchers.IO) {

            repository.getAllMovies.collect { movieList ->
                withContext(Dispatchers.Main) {
                    movies.value = movieList
                }
            }
        }
    }

    private fun toggleMovieSelection(movieId: Int, isSelected: Boolean) {
        selectedMovieIds.value = if (isSelected) {
            selectedMovieIds.value + movieId
        } else {
            selectedMovieIds.value - movieId
        }
    }

    private fun deleteSelectedMovies() {
        lifecycleScope.launch(Dispatchers.IO) {
            repository.deleteSelectedMovies()
            withContext(Dispatchers.Main) {
                selectedMovieIds.value = emptySet()
                // Список обновится автоматически через Flow в loadMovies
            }
        }
    }
}