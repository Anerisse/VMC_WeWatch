package com.example.wmc_wewatch.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.data.Movie
import com.example.wmc_wewatch.ui.main.MainScreen
import com.example.wmc_wewatch.ui.add.AddScreen
import com.example.wmc_wewatch.ui.search.SearchScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    // Данные для MainScreen
    movies: List<Movie>,
    selectedMovieIds: Set<Int>,
    toggleSelection: (Int, Boolean) -> Unit,
    onDeleteMovies: () -> Unit,
    // Данные для AddScreen
    selectedMovie: MovieSearchResult?,
    setSelectedMovie: (MovieSearchResult?) -> Unit,
    // Данные для SearchScreen
    searchQuery: String,
    setSearchQuery: (String) -> Unit,
    // Действие добавления фильма
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
                onMovieSelected = toggleSelection,
                onDeleteSelected = onDeleteMovies,
                onAddMovie = { navController.navigate("add") }
            )
        }

        composable("add") {
            AddScreen(
                selectedMovie = selectedMovie,
                onAddMovieClick = onAddMovie,
                onNavigateBack = {
                    setSelectedMovie(null)
                    navController.popBackStack()
                },
                onSearchClick = { query, _ ->
                    setSearchQuery(query)
                    navController.navigate("search")
                }
            )
        }

        composable("search") {
            SearchScreen(
                query = searchQuery,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onMovieSelected = { movie ->
                    setSelectedMovie(movie)
                    navController.popBackStack()  // возвращаемся в Add
                }
            )
        }
    }
}