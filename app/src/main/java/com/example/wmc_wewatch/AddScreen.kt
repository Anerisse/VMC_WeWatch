package com.example.wmc_wewatch

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wmc_wewatch.api.MovieSearchResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onNavigateBack: () -> Unit,
    onSearchClick: (String, String) -> Unit,
    onAddMovieClick: (MovieSearchResult) -> Unit,  // теперь передаем фильм
    selectedMovie: MovieSearchResult? = null       // опциональный параметр
) {
    var searchQuery by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    // Если есть выбранный фильм - заполняем поля
    LaunchedEffect(selectedMovie) {
        if (selectedMovie != null) {
            searchQuery = selectedMovie.Title
            year = selectedMovie.Year
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить фильм") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Название фильма") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                onSearchClick(searchQuery, year)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                }
            )

            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Год (необязательно)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Показываем постер если есть выбранный фильм
            selectedMovie?.let { movie ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎬",
                            fontSize = 40.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Column {
                            Text("${movie.Title} (${movie.Year})")
                            Text("Жанр: ${movie.Genre}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    selectedMovie?.let { onAddMovieClick(it) }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedMovie != null
            ) {
                Text("Добавить фильм")
            }
        }
    }
}