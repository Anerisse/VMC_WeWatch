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

// Временная модель для результатов поиска
data class SearchResult(
    val id: String,           // imdbID
    val title: String,        // Title
    val year: String,         // Year
    val posterUrl: String,    // Poster
    val genre: String         // Genre (для этого экрана)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,                    // Поисковый запрос
    onNavigateBack: () -> Unit,       // Назад
    onMovieSelected: (SearchResult) -> Unit  // Когда выбрали фильм
) {
    // Тестовые данные (пока без API)
    val searchResults = remember(query) {
        generateTestResults(query)
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
        if (searchResults.isEmpty()) {
            // Пустой результат
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Ничего не найдено")
            }
        } else {
            // Список результатов
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
    result: SearchResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick  // Щелчок по карточке выбирает фильм
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Постер (заглушка)
            Text(
                text = "🎬",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                modifier = Modifier.padding(end = 8.dp)
            )

            // Информация о фильме
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "📅 ${result.year}",
                    style = MaterialTheme.typography.bodyMedium
                )
                // Жанр - только на SearchScreen!
                Text(
                    text = "🎭 ${result.genre}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Временная функция для генерации тестовых данных
private fun generateTestResults(query: String): List<SearchResult> {
    if (query.isBlank()) return emptyList()

    return listOf(
        SearchResult(
            id = "tt0137523",
            title = "Fight Club",
            year = "1999",
            posterUrl = "",
            genre = "Drama"
        ),
        SearchResult(
            id = "tt0111161",
            title = "The Shawshank Redemption",
            year = "1994",
            posterUrl = "",
            genre = "Drama"
        ),
        SearchResult(
            id = "tt1375666",
            title = "Inception",
            year = "2010",
            posterUrl = "",
            genre = "Action, Sci-Fi"
        )
    ).filter { it.title.contains(query, ignoreCase = true) }
}