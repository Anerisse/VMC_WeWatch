package com.example.wmc_wewatch.ui.search

import android.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.api.RetrofitInstance

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
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

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // TopAppBar теперь здесь
        TopAppBar(
            title = { Text("Результаты поиска: \"$query\"") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                }
            }
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),

                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),

                contentAlignment = Alignment.Center
            ) {
                Text("Ошибка: $errorMessage")
            }
        } else if (searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Ничего не найдено")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
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
            // Постер через Coil (очень просто!)
            AsyncImage(
                model = result.Poster,
                contentDescription = "Постер ${result.Title}",
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 8.dp),
                error = painterResource(R.drawable.ic_menu_gallery)  // встроенная иконка
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
                Text(
                    text = "🎭 ${result.Type}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}