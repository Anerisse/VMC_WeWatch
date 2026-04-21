package com.example.wmc_wewatch.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SearchState {
    object Loading : SearchState()
    data class Success(val results: List<MovieSearchResult>) : SearchState()
    data class Error(val message: String) : SearchState()
    object Empty : SearchState()
}

class SearchViewModel : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Empty)
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    /**
     * Выполняет поиск фильмов по запросу
     */
    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _searchState.value = SearchState.Empty
            return
        }

        _searchState.value = SearchState.Loading

        viewModelScope.launch {
            try {
                println("🔍 Поиск: $query")
                val response = RetrofitInstance.api.searchMovies(query)
                println("📦 Ответ: $response")

                when (response.Response) {
                    "True" -> {
                        val results = response.Search ?: emptyList()
                        _searchState.value = if (results.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.Success(results)
                        }
                        println("✅ Найдено: ${results.size} фильмов")
                    }
                    else -> {
                        val errorMsg = response.Error ?: "Ничего не найдено"
                        _searchState.value = SearchState.Error(errorMsg)
                        println("❌ Ошибка API: $errorMsg")
                    }
                }
            } catch (e: Exception) {
                println("💥 Исключение: ${e.message}")
                e.printStackTrace()
                _searchState.value = SearchState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    /**
     * Очищает состояние поиска
     */
    fun clearSearch() {
        _searchState.value = SearchState.Empty
    }
}