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
import com.example.wmc_wewatch.ui.add.AddScreen
import com.example.wmc_wewatch.ui.main.MainScreen
import com.example.wmc_wewatch.ui.search.SearchScreen


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
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography(),
                shapes = Shapes()
            ) {

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        // FAB показываем только на главном экране
                        if (currentScreen.value == Screen.Main) {
                            FloatingActionButton(
                                onClick = { currentScreen.value = Screen.Add }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Добавить фильм")
                            }
                        }
                    }
                ) { innerPadding ->


                    BackHandler {
                        when (currentScreen.value) {
                            Screen.Main -> finish()
                            Screen.Add -> currentScreen.value = Screen.Main
                            Screen.Search -> currentScreen.value = Screen.Add
                        }
                    }

                    // Применяем отступы к контенту
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