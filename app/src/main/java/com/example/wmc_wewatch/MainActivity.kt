package com.example.wmc_wewatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.data.Movie
import com.example.wmc_wewatch.data.MovieDatabase
import com.example.wmc_wewatch.data.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Typography
import androidx.navigation.compose.rememberNavController
import com.example.wmc_wewatch.navigation.AppNavHost
import com.example.wmc_wewatch.ui.add.AddScreen
import com.example.wmc_wewatch.ui.main.MainScreen
import com.example.wmc_wewatch.ui.search.SearchScreen


class MainActivity : ComponentActivity() {

    private lateinit var repository: MovieRepository
    private val movies = mutableStateOf<List<Movie>>(emptyList())
    private val selectedMovieIds = mutableStateOf<Set<Int>>(emptySet())


    // Данные для передачи между экранами
    private val searchQuery = mutableStateOf("")
    private val selectedMovie = mutableStateOf<MovieSearchResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MovieDatabase.getInstance(this)
        repository = MovieRepository(database.movieDao())

        loadMovies()

        setContent {
            val context = LocalContext.current
            val colorScheme = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                // Для Android 12+ используем динамические цвета от системы
                val isDarkTheme = isSystemInDarkTheme()
                if (isDarkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
            } else {
                // Для старых версий Android используем стандартную тему Material 3
                val isDarkTheme = isSystemInDarkTheme()
                if (isDarkTheme) {
                    androidx.compose.material3.darkColorScheme()
                } else {
                    androidx.compose.material3.lightColorScheme()
                }
            }
            val navController = rememberNavController()

            MaterialTheme(colorScheme = colorScheme) {
                AppNavHost(
                    navController = navController,

                    movies = movies.value,
                    selectedMovieIds = selectedMovieIds.value,
                    toggleSelection = { id, checked -> toggleMovieSelection(id, checked) },
                    onDeleteMovies = { deleteSelectedMovies() },

                    selectedMovie = selectedMovie.value,
                    setSelectedMovie = { selectedMovie.value = it },

                    searchQuery = searchQuery.value,
                    setSearchQuery = { searchQuery.value = it },

                    onAddMovie = { movie ->
                        println("🎬 Добавление фильма: ${movie.Title}")
                        lifecycleScope.launch(Dispatchers.IO) {
                            val newMovie = Movie(
                                title = movie.Title,
                                year = movie.Year,
                                posterUrl = movie.Poster,
                                type = movie.Type
                            )
                            repository.insertMovie(newMovie)

                            withContext(Dispatchers.Main) {
                                selectedMovie.value = null
                                searchQuery.value = ""
                                // Возвращаемся на главный экран
                                navController.popBackStack("main", inclusive = false)
                            }
                        }
                    }
                )
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

