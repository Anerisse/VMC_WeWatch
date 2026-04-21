package com.example.wmc_wewatch.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.api.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Состояние поиска для AddScreen
sealed class AddSearchState {
    object Idle : AddSearchState()
    object Loading : AddSearchState()
    data class Success(val results: List<MovieSearchResult>) : AddSearchState()
    data class Error(val message: String) : AddSearchState()
}

class AddViewModel : ViewModel() {

    // Состояние полей ввода
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _year = MutableStateFlow("")
    val year: StateFlow<String> = _year.asStateFlow()

    // Состояние поиска
    private val _searchState = MutableStateFlow<AddSearchState>(AddSearchState.Idle)
    val searchState: StateFlow<AddSearchState> = _searchState.asStateFlow()

    // Выбранный фильм
    private val _selectedMovie = MutableStateFlow<MovieSearchResult?>(null)
    val selectedMovie: StateFlow<MovieSearchResult?> = _selectedMovie.asStateFlow()

    // Состояние добавления в БД
    private val _isAdding = MutableStateFlow(false)
    val isAdding: StateFlow<Boolean> = _isAdding.asStateFlow()

    /**
     * Обновляет поисковый запрос
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // Если поле очистили, сбрасываем состояние поиска
        if (query.isBlank()) {
            _searchState.value = AddSearchState.Idle
        }
    }

    /**
     * Обновляет год
     */
    fun updateYear(newYear: String) {
        _year.value = newYear
    }

    /**
     * Выполняет поиск фильмов
     */
    fun performSearch() {
        val query = _searchQuery.value
        if (query.isBlank()) return

        _searchState.value = AddSearchState.Loading

        viewModelScope.launch {
            try {
                println("🔍 Поиск: $query")
                val response = RetrofitInstance.api.searchMovies(query)
                println("📦 Ответ: $response")

                when (response.Response) {
                    "True" -> {
                        val results = response.Search ?: emptyList()
                        _searchState.value = if (results.isEmpty()) {
                            AddSearchState.Error("Ничего не найдено")
                        } else {
                            AddSearchState.Success(results)
                        }
                        println("✅ Найдено: ${results.size} фильмов")
                    }
                    else -> {
                        val errorMsg = response.Error ?: "Ничего не найдено"
                        _searchState.value = AddSearchState.Error(errorMsg)
                        println("❌ Ошибка API: $errorMsg")
                    }
                }
            } catch (e: Exception) {
                println("💥 Исключение: ${e.message}")
                e.printStackTrace()
                _searchState.value = AddSearchState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    /**
     * Выбирает фильм из результатов поиска
     */
    fun selectMovie(movie: MovieSearchResult) {
        _selectedMovie.value = movie
        // Заполняем поля из выбранного фильма
        _searchQuery.value = movie.Title
        _year.value = movie.Year
        // Сбрасываем состояние поиска
        _searchState.value = AddSearchState.Idle
    }

    /**
     * Очищает выбранный фильм
     */
    fun clearSelectedMovie() {
        _selectedMovie.value = null
    }

    /**
     * Начинает процесс добавления в БД
     */
    fun startAdding() {
        _isAdding.value = true
    }

    /**
     * Завершает процесс добавления
     */
    fun finishAdding() {
        _isAdding.value = false
        _selectedMovie.value = null
        _searchQuery.value = ""
        _year.value = ""
        _searchState.value = AddSearchState.Idle
    }

    /**
     * Сбрасывает всё состояние
     */
    fun reset() {
        _searchQuery.value = ""
        _year.value = ""
        _searchState.value = AddSearchState.Idle
        _selectedMovie.value = null
        _isAdding.value = false
    }
}