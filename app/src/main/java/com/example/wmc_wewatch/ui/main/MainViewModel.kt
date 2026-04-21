package com.example.wmc_wewatch.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmc_wewatch.data.Movie
import com.example.wmc_wewatch.data.MovieRepository
import com.example.wmc_wewatch.ui.main.mvi.MainIntent
import com.example.wmc_wewatch.ui.main.mvi.MainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    // ЕДИНСТВЕННОЕ состояние экрана
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        // При создании ViewModel сразу загружаем фильмы
        handleIntent(MainIntent.LoadMovies)
    }

    /**
     * ЕДИНСТВЕННЫЙ метод для обработки ВСЕХ действий пользователя
     */
    fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.LoadMovies -> loadMovies()
            is MainIntent.ToggleSelection -> toggleSelection(intent.id, intent.isSelected)
            is MainIntent.DeleteSelected -> deleteSelected()
            is MainIntent.ClearError -> clearError()
            else -> {}
        }
    }


    /**
     * Загружает все фильмы из БД и подписывается на изменения
     */
    private fun loadMovies() {
        viewModelScope.launch {
            // Показываем загрузку
            _state.update { it.copy(isLoading = true) }

            // Подписываемся на Flow из БД
            repository.getAllMovies.collect { movies ->
                _state.update {
                    it.copy(
                        movies = movies,
                        isLoading = false
                    )
                }
            }
        }
    }

    //  ВЫБОР ФИЛЬМА
    private fun toggleSelection(id: Int, isSelected: Boolean) {
        _state.update { currentState ->
            val newSelectedIds = if (isSelected) {
                currentState.selectedIds + id
            } else {
                currentState.selectedIds - id
            }
            currentState.copy(selectedIds = newSelectedIds)
        }
    }

    // 🗑 УДАЛЕНИЕ ВЫБРАННЫХ
    private fun deleteSelected() {
        val idsToDelete = _state.value.selectedIds.toList()
        if (idsToDelete.isEmpty()) return

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                repository.deleteMoviesByIds(idsToDelete)
                _state.update {
                    it.copy(
                        selectedIds = emptySet(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    //  ОЧИСТКА ОШИБКИ
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}