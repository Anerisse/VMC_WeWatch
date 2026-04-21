package com.example.wmc_wewatch.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.ui.search.mvi.SearchIntent
import com.example.wmc_wewatch.ui.search.mvi.SearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    query: String,
    onNavigateBack: () -> Unit,
    onMovieSelected: (MovieSearchResult) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Запускаем поиск при первом появлении
    LaunchedEffect(query) {
        viewModel.handleIntent(SearchIntent.Search(query))
    }

    // Очищаем при уходе
    DisposableEffect(Unit) {
        onDispose {
            viewModel.handleIntent(SearchIntent.Clear)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Результаты поиска: \"$query\"") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val searchState = state) {
            is SearchState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is SearchState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(" ${searchState.message}")
                }
            }

            is SearchState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ничего не найдено")
                }
            }

            is SearchState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchState.results) { result ->
                        SearchResultItem(
                            result = result,
                            onClick = { onMovieSelected(result) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!result.Poster.isNullOrEmpty() && result.Poster != "N/A") {
                AsyncImage(
                    model = result.Poster,
                    contentDescription = "Постер ${result.Title}",
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 8.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎬", fontSize = 32.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.Title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "📅 ${result.Year}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "🎭 ${result.Type}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}