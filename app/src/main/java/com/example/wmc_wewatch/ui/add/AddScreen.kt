package com.example.wmc_wewatch.ui.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.ui.add.mvi.AddIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onAddMovieClick: (MovieSearchResult) -> Unit,
    onSearchRequested: (String, String) -> Unit,
    viewModel: AddViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Когда isAdding становится true — добавляем фильм
    LaunchedEffect(state.isAdding) {
        if (state.isAdding) {
            state.selectedMovie?.let { movie ->
                onAddMovieClick(movie)
                viewModel.handleIntent(AddIntent.FinishAdding)
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить фильм") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.handleIntent(AddIntent.Reset)
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
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
                value = state.searchQuery,
                onValueChange = {
                    viewModel.handleIntent(AddIntent.UpdateSearchQuery(it))
                },
                label = { Text("Название фильма") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (state.searchQuery.isNotBlank()) {
                                onSearchRequested(state.searchQuery, state.year)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                },
                enabled = !state.isAdding
            )

            // Поле для года
            OutlinedTextField(
                value = state.year,
                onValueChange = {
                    viewModel.handleIntent(AddIntent.UpdateYear(it))
                },
                label = { Text("Год (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isAdding
            )

            // Показываем выбранный фильм
            state.selectedMovie?.let { movie ->
                SelectedMovieCard(movie = movie)
            }

            // Кнопка добавления
            Button(
                onClick = {
                    viewModel.handleIntent(AddIntent.StartAdding)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.selectedMovie != null && !state.isAdding
            ) {
                if (state.isAdding) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Добавить фильм")
                }
            }
        }
    }
}

@Composable
fun SelectedMovieCard(movie: MovieSearchResult) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!movie.Poster.isNullOrEmpty() && movie.Poster != "N/A") {
                AsyncImage(
                    model = movie.Poster,
                    contentDescription = "Постер ${movie.Title}",
                    modifier = Modifier.size(80.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎬", fontSize = 32.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
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