package com.example.wmc_wewatch.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmc_wewatch.api.MovieSearchResult
import com.example.wmc_wewatch.api.RetrofitInstance
import com.example.wmc_wewatch.ui.add.mvi.AddIntent
import com.example.wmc_wewatch.ui.add.mvi.AddState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddViewModel : ViewModel() {

    // ЕДИНСТВЕННОЕ состояние экрана
    private val _state = MutableStateFlow(AddState())
    val state: StateFlow<AddState> = _state.asStateFlow()

    // ЕДИНСТВЕННЫЙ метод для обработки ВСЕХ действий пользователя

    fun handleIntent(intent: AddIntent) {
        when (intent) {
            is AddIntent.UpdateSearchQuery -> updateSearchQuery(intent.query)
            is AddIntent.UpdateYear -> updateYear(intent.year)
            is AddIntent.SelectMovie -> selectMovie(intent.movie)
            is AddIntent.StartAdding -> startAdding()
            is AddIntent.FinishAdding -> finishAdding()
            is AddIntent.Reset -> reset()
            is AddIntent.ClearError -> clearError()
        }
    }

    //  ОБНОВЛЕНИЕ ПОИСКОВОГО ЗАПРОСА
    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    //  ОБНОВЛЕНИЕ ГОДА
    private fun updateYear(year: String) {
        _state.update { it.copy(year = year) }
    }

    //  ВЫБОР ФИЛЬМА
    private fun selectMovie(movie: MovieSearchResult) {
        _state.update {
            it.copy(
                selectedMovie = movie,
                searchQuery = movie.Title,
                year = movie.Year
            )
        }
    }

    //  НАЧАЛО ДОБАВЛЕНИЯ
    private fun startAdding() {
        _state.update { it.copy(isAdding = true) }
    }

    //  ЗАВЕРШЕНИЕ ДОБАВЛЕНИЯ
    private fun finishAdding() {
        _state.update {
            it.copy(
                isAdding = false,
                selectedMovie = null,
                searchQuery = "",
                year = ""
            )
        }
    }

    //  СБРОС ВСЕГО
    private fun reset() {
        _state.update { AddState() }
    }

    //  ОЧИСТКА ОШИБКИ
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}