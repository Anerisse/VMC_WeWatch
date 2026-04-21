package com.example.wmc_wewatch

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import com.example.wmc_wewatch.data.Movie
import com.example.wmc_wewatch.data.MovieDatabase
import kotlinx.coroutines.launch
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.wmc_wewatch.navigation.AppNavHost
import com.example.wmc_wewatch.ui.main.MainViewModel
import com.example.wmc_wewatch.ui.main.mvi.MainEffect
import com.example.wmc_wewatch.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Создаём репозиторий и фабрику ViewModel
        val database = MovieDatabase.getInstance(this)
        val repository = com.example.wmc_wewatch.data.MovieRepository(database.movieDao())
        val factory = ViewModelFactory(repository)

        setContent {
            val context = LocalContext.current

            // Динамическая цветовая схема (Material You)
            val colorScheme = when {
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
                    val isDarkTheme = isSystemInDarkTheme()
                    if (isDarkTheme) {
                        dynamicDarkColorScheme(context)
                    } else {
                        dynamicLightColorScheme(context)
                    }
                }
                else -> {
                    val isDarkTheme = isSystemInDarkTheme()
                    if (isDarkTheme) darkColorScheme() else lightColorScheme()
                }
            }

            // Создаём ViewModel через фабрику
            val viewModel: MainViewModel = viewModel(factory = factory)
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                viewModel.effect.collectLatest { effect ->
                    when (effect) {
                        is MainEffect.NavigateToAdd -> {
                            navController.navigate("add")
                        }
                        is MainEffect.ShowError -> {
                            // Показываем Toast
                             Toast.makeText(this@MainActivity, effect.message, Toast.LENGTH_SHORT).show()
                            println("❌ Ошибка: ${effect.message}")
                        }
                    }
                }
            }



            val mainState by viewModel.state.collectAsState()  // ← единое состояние

            MaterialTheme(colorScheme = colorScheme) {
                AppNavHost(
                    navController = navController,

                    // Данные для главного экрана (теперь один объект)
                    mainState = mainState,

                    // Единый обработчик всех действий
                    onIntent = { intent ->
                        viewModel.handleIntent(intent)
                    },

                    // Добавление фильма
                    onAddMovie = { movie ->
                        println(" Добавление фильма: ${movie.Title}")

                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                val newMovie = Movie(
                                    title = movie.Title,
                                    year = movie.Year,
                                    posterUrl = movie.Poster,
                                    type = movie.Type
                                )
                                repository.insertMovie(newMovie)
                            }

                            // После добавления возвращаемся на главный экран
                            navController.popBackStack("main", inclusive = false)
                        }
                    }
                )
            }
        }
    }
}