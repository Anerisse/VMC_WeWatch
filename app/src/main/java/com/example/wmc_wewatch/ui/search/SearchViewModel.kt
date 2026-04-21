package com.example.wmc_wewatch.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmc_wewatch.api.RetrofitInstance
import com.example.wmc_wewatch.ui.search.mvi.SearchIntent
import com.example.wmc_wewatch.ui.search.mvi.SearchState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    // ЕДИНСТВЕННОЕ состояние экрана
    private val _state = MutableStateFlow<SearchState>(SearchState.Empty)
    val state: StateFlow<SearchState> = _state.asStateFlow()

      //    ЕДИНСТВЕННЫЙ метод для обработки ВСЕХ действий

    fun handleIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> searchMovies(intent.query)
            is SearchIntent.Clear -> clear()
        }
    }

    //  ПОИСК ФИЛЬМОВ
    private fun searchMovies(query: String) {
        if (query.isBlank()) {
            _state.value = SearchState.Empty
            return
        }

        _state.value = SearchState.Loading

        viewModelScope.launch {
            try {
                println(" Поиск: $query")
                val response = RetrofitInstance.api.searchMovies(query)
                println(" Ответ: $response")

                _state.value = when (response.Response) {
                    "True" -> {
                        val results = response.Search ?: emptyList()
                        if (results.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.Success(results)
                        }
                    }
                    else -> {
                        val errorMsg = response.Error ?: "Ничего не найдено"
                        SearchState.Error(errorMsg)
                    }
                }
            } catch (e: Exception) {
                println(" Исключение: ${e.message}")
                _state.value = SearchState.Error("Ошибка загрузки: ${e.message}")
            }
        }
    }

    //  ОЧИСТКА
    private fun clear() {
        _state.value = SearchState.Empty
    }
}