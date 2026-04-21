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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onAddMovieClick: (MovieSearchResult) -> Unit,
    onSearchRequested: (String, String) -> Unit,
    viewModel: AddViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val year by viewModel.year.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val selectedMovie by viewModel.selectedMovie.collectAsState()
    val isAdding by viewModel.isAdding.collectAsState()

    LaunchedEffect(isAdding) {
        if (isAdding) {
            selectedMovie?.let { movie ->
                onAddMovieClick(movie)
                viewModel.finishAdding()
                onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить фильм") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Название фильма") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (searchQuery.isNotBlank()) {
                                onSearchRequested(searchQuery, year)
                            }
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                },
                enabled = !isAdding
            )

            OutlinedTextField(
                value = year,
                onValueChange = { viewModel.updateYear(it) },
                label = { Text("Год (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isAdding
            )

            if (searchState is AddSearchState.Idle) {
                selectedMovie?.let { movie ->
                    SelectedMovieCard(movie = movie)
                }
            }

            Button(
                onClick = { viewModel.startAdding() },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedMovie != null && !isAdding
            ) {
                if (isAdding) {
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
fun SearchResultCard(
    result: MovieSearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!result.Poster.isNullOrEmpty() && result.Poster != "N/A") {
                AsyncImage(
                    model = result.Poster,
                    contentDescription = "Постер ${result.Title}",
                    modifier = Modifier.size(50.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎬", fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.Title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${result.Year} • ${result.Type}",
                    style = MaterialTheme.typography.bodySmall
                )
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