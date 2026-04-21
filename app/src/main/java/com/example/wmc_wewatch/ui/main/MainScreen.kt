package com.example.wmc_wewatch.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wmc_wewatch.data.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    // Данные передаются из ViewModel
    movies: List<Movie>,
    selectedMovieIds: Set<Int>,

    // События передаются во ViewModel
    onMovieSelected: (Int, Boolean) -> Unit,
    onDeleteSelected: () -> Unit,
    onAddMovie: () -> Unit,
    isLoading: Boolean,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMovie) {
                Icon(Icons.Default.Add, contentDescription = "Добавить фильм")
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Мои фильмы") },
                actions = {
                    if (selectedMovieIds.isNotEmpty()) {
                        IconButton(onClick = onDeleteSelected) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить выбранные"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                // Крутилка загрузки
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            movies.isEmpty() -> {
                // Пустой экран (рис.1a)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "🎬",
                            fontSize = 80.sp
                        )
                        Text(
                            text = "Нет выбранных фильмов",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "Нажмите + чтобы добавить фильм",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            else -> {
                // Список фильмов (рис.1b)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding())  // отступ сверху
                        .navigationBarsPadding(),  // отступ снизу
                    contentPadding = PaddingValues(
                        bottom = 80.dp,
                        top = 8.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),  //  чтобы последний элемент не скрывался за FAB
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(movies) { movie ->
                        MovieListItem(
                            movie = movie,
                            isSelected = movie.id in selectedMovieIds,
                            onSelectionChange = { isChecked ->
                                onMovieSelected(movie.id, isChecked)
                            }
                        )
                    }
                }
            }
        }

    }
}
@Composable
fun MovieListItem(
    movie: Movie,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Постер из БД (как в AddScreen)
            if (!movie.posterUrl.isNullOrEmpty() && movie.posterUrl != "N/A") {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = "Постер ${movie.title}",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 8.dp)
                )
            } else {
                Text(
                    text = "🎬",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Информация о фильме
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = movie.year,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Флажок для выбора фильма на удаление
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

