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

    private lateinit var repository: MovieRepository
    private val movies = mutableStateOf<List<Movie>>(emptyList())
    private val selectedMovieIds = mutableStateOf<Set<Int>>(emptySet())

    // Состояние навигации
    private val currentScreen = mutableStateOf<Screen>(Screen.Main)

    // Данные для передачи между экранами
    private val searchQuery = mutableStateOf("")
    private val searchYear = mutableStateOf("")
    private val selectedMovie = mutableStateOf<SearchResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MovieDatabase.getInstance(this)
        repository = MovieRepository(database.movieDao())

        loadMovies()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen.value) {
                        Screen.Main -> {
                            MainScreen(
                                movies = movies.value,
                                selectedMovieIds = selectedMovieIds.value,
                                onMovieSelected = { movieId, isSelected ->
                                    toggleMovieSelection(movieId, isSelected)
                                },
                                onDeleteSelected = { deleteSelectedMovies() },
                                onAddMovie = { currentScreen.value = Screen.Add }
                            )
                        }
                        Screen.Add -> {
                            AddScreen(
                                onNavigateBack = { currentScreen.value = Screen.Main },
                                onSearchClick = { query, year ->
                                    searchQuery.value = query
                                    searchYear.value = year
                                    currentScreen.value = Screen.Search
                                },
                                onAddMovieClick = {
                                    // TODO: добавить выбранный фильм в БД
                                    currentScreen.value = Screen.Main
                                }
                            )
                        }
                        Screen.Search -> {
                            SearchScreen(
                                query = searchQuery.value,
                                onNavigateBack = { currentScreen.value = Screen.Add },
                                onMovieSelected = { result ->
                                    selectedMovie.value = result
                                    // Возвращаемся на AddScreen с выбранным фильмом
                                    currentScreen.value = Screen.Add
                                    // TODO: заполнить поля на AddScreen данными result
                                }
                            )
                        }
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
            }
        }
    }
}

// Состояния экранов
enum class Screen {
    Main, Add, Search
}