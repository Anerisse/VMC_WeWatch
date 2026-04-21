package com.example.wmc_wewatch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
    movies: List<Movie>,
    selectedMovieIds: Set<Int>,
    toggleSelection: (Int, Boolean) -> Unit,
    onDeleteMovies: () -> Unit,
    onAddMovie: (MovieSearchResult) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                movies = movies,
                selectedMovieIds = selectedMovieIds,
                isLoading = false,
                onMovieSelected = toggleSelection,
                onDeleteSelected = onDeleteMovies,
                onAddMovie = { navController.navigate("add") }
            )
        }

        composable("add") {
            val addViewModel: AddViewModel = viewModel()

            // Слушаем результат из SearchScreen
            val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

            LaunchedEffect(savedStateHandle) {
                savedStateHandle?.getLiveData<MovieSearchResult>("selected_movie")
                    ?.observeForever { movie ->
                        if (movie != null) {
                            addViewModel.selectMovie(movie)
                            savedStateHandle.remove<MovieSearchResult>("selected_movie")
                        }
                    }
            }

            AddScreen(
                onNavigateBack = {
                    addViewModel.reset()
                    navController.popBackStack()
                },
                onAddMovieClick = { movie ->
                    onAddMovie(movie)
                },
                onSearchRequested = { query, _ ->
                    navController.navigate("search/$query")
                },
                viewModel = addViewModel
            )
        }

        composable(
            route = "search/{query}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            val searchViewModel: SearchViewModel = viewModel()

            SearchScreen(
                query = query,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMovieSelected = { movie ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "selected_movie", movie
                    )
                    navController.popBackStack()
                },
                viewModel = searchViewModel
            )
        }
    }
}