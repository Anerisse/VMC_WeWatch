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
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.data.Movie
import com.example.wmc_wewatch.data.MovieDatabase
import com.example.wmc_wewatch.data.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.compose.BackHandler

class MainActivity : ComponentActivity() {

    private lateinit var repository: MovieRepository
    private val movies = mutableStateOf<List<Movie>>(emptyList())
    private val selectedMovieIds = mutableStateOf<Set<Int>>(emptySet())

    // Состояние навигации
    private val currentScreen = mutableStateOf<Screen>(Screen.Main)

    // Данные для передачи между экранами
    private val searchQuery = mutableStateOf("")
    private val selectedMovie = mutableStateOf<MovieSearchResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MovieDatabase.getInstance(this)
        repository = MovieRepository(database.movieDao())

        loadMovies()

        setContent {
            MaterialTheme {

                BackHandler {
                    when (currentScreen.value) {
                        Screen.Main -> finish()
                        Screen.Add -> currentScreen.value = Screen.Main
                        Screen.Search -> currentScreen.value = Screen.Add
                    }
                }

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
                                onNavigateBack = {
                                    selectedMovie.value = null
                                    currentScreen.value = Screen.Main
                                },
                                onSearchClick = { query, year ->
                                    searchQuery.value = query
                                    currentScreen.value = Screen.Search
                                },
                                onAddMovieClick = { movie ->
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val newMovie = Movie(
                                            title = movie.Title,
                                            year = movie.Year,
                                            posterUrl = movie.Poster,
                                            type = movie.Type,

                                        )
                                        repository.insertMovie(newMovie)
                                        withContext(Dispatchers.Main) {
                                            selectedMovie.value = null
                                            currentScreen.value = Screen.Main
                                        }
                                    }
                                },
                                selectedMovie = selectedMovie.value
                            )
                        }
                        Screen.Search -> {
                            SearchScreen(
                                query = searchQuery.value,
                                onNavigateBack = {
                                    println("🔙 Навигация: Search -> Add")
                                    currentScreen.value = Screen.Add
                                },
                                onMovieSelected = { result ->
                                    println("✅ Выбран фильм: ${result.Title}")
                                    selectedMovie.value = result
                                    currentScreen.value = Screen.Add
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
                    println("📽️ Загружено фильмов: ${movieList.size}")
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
        val ids = selectedMovieIds.value.toList()

        println("🗑️ Нажата кнопка удаления. ID: $ids")

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                repository.deleteMoviesByIds(ids)

                withContext(Dispatchers.Main) {
                    println("✅ Удалено из БД: $ids")
                    selectedMovieIds.value = emptySet()
                }
            } catch (e: Exception) {
                println("❌ Ошибка удаления: ${e.message}")
            }
        }
    }
}

// Состояния экранов
enum class Screen {
    Main, Add, Search
}