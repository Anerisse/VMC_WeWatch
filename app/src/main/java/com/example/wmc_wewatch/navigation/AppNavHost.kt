package com.example.wmc_wewatch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.data.Movie
import com.example.wmc_wewatch.ui.add.AddScreen
import com.example.wmc_wewatch.ui.add.AddViewModel
import com.example.wmc_wewatch.ui.main.MainScreen
import com.example.wmc_wewatch.ui.search.SearchScreen
import com.example.wmc_wewatch.ui.search.SearchViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    // Данные для MainScreen
    movies: List<Movie>,
    selectedMovieIds: Set<Int>,
    toggleSelection: (Int, Boolean) -> Unit,
    onDeleteMovies: () -> Unit,
    // Действие добавления фильма
    onAddMovie: (MovieSearchResult) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // Главный экран
        composable("main") {
            MainScreen(
                movies = movies,
                selectedMovieIds = selectedMovieIds,
                onMovieSelected = toggleSelection,
                onDeleteSelected = onDeleteMovies,
                onAddMovie = {
                    navController.navigate("add")
                }
            )
        }

        // Экран добавления
        composable("add") {
            val addViewModel: AddViewModel = viewModel()

            AddScreen(
                onNavigateBack = {
                    addViewModel.reset()
                    navController.popBackStack()
                },
                onAddMovieClick = { movie ->
                    onAddMovie(movie)
                },
                onSearchRequested = { query, year ->
                    // Сохраняем запрос и переходим на экран поиска
                    addViewModel.updateSearchQuery(query)
                    addViewModel.updateYear(year)
                    navController.navigate("search")
                },
                viewModel = addViewModel
            )
        }

        // Экран поиска
        composable("search") {
            val searchViewModel: SearchViewModel = viewModel()
            // Получаем AddViewModel из предыдущего экрана
            val addViewModel: AddViewModel = viewModel()

            val searchQuery by addViewModel.searchQuery.collectAsState()

            SearchScreen(
                query = searchQuery,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMovieSelected = { movie ->
                    addViewModel.selectMovie(movie)
                    navController.popBackStack()
                },
                viewModel = searchViewModel
            )
        }
    }
}