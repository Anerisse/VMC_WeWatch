package com.example.wmc_wewatch

import androidx.compose.foundation.background
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
import coil.compose.AsyncImage
import com.example.wmc_wewatch.api.MovieSearchResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    onNavigateBack: () -> Unit,
    onSearchClick: (String, String) -> Unit,
    onAddMovieClick: (MovieSearchResult) -> Unit,
    selectedMovie: MovieSearchResult? = null
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
            // Поле для названия фильма
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

            // Поле для года (необязательное)
            OutlinedTextField(
                value = year,
                onValueChange = { year = it },
                label = { Text("Год (необязательно)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Показываем информацию о выбранном фильме с постером
            selectedMovie?.let { movie ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Постер из интернета (если есть)
                        if (!movie.Poster.isNullOrEmpty() && movie.Poster != "N/A") {
                            AsyncImage(
                                model = movie.Poster,
                                contentDescription = "Постер ${movie.Title}",
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 12.dp)
                            )
                        } else {
                            // Заглушка если нет постера
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(end = 12.dp)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "🎬",
                                    fontSize = 32.sp
                                )
                            }
                        }

                        // Информация о фильме
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = movie.Title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "📅 ${movie.Year}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "🎭 ${movie.Type}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Кнопка добавления фильма в БД
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