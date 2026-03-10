package com.example.wmc_wewatch

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
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.api.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    onNavigateBack: () -> Unit,
    onMovieSelected: (MovieSearchResult) -> Unit
) {
    var searchResults by remember { mutableStateOf<List<MovieSearchResult>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Загружаем результаты при появлении экрана
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            isLoading = true
            errorMessage = null
            try {
                println("🔍 Поиск: $query")  // Лог запроса
                val response = RetrofitInstance.api.searchMovies(query)
                println("📦 Ответ: $response")  // Лог ответа

                if (response.Response == "True") {
                    searchResults = response.Search ?: emptyList()
                    println("✅ Найдено: ${searchResults.size} фильмов")
                } else {
                    errorMessage = response.Error ?: "Ничего не найдено"
                    println("❌ Ошибка API: $errorMessage")
                    searchResults = emptyList()
                }
            } catch (e: Exception) {
                println("💥 Исключение: ${e.message}")
                e.printStackTrace()  // Подробная ошибка
                errorMessage = "Ошибка загрузки: ${e.message}"
                searchResults = emptyList()
            } finally {
                isLoading = false
            }
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Ошибка: $errorMessage")
            }
        } else if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Ничего не найдено")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { result ->
                    SearchResultItem(
                        result = result,
                        onClick = { onMovieSelected(result) }
                    )
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
            // Постер (пока заглушка)
            Text(
                text = "🎬",
                fontSize = 24.sp,
                modifier = Modifier.padding(end = 8.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.Title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "📅 ${result.Year}",
                    style = MaterialTheme.typography.bodyMedium
                )
                // Жанр из API!
                Text(
                    text = "🎭 ${result.Genre}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}