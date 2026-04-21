package com.example.wmc_wewatch.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wmc_wewatch.data.Movie
import com.example.wmc_wewatch.data.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    // Состояние списка фильмов
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    // Состояние выбранных фильмов для удаления
    private val _selectedMovieIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedMovieIds: StateFlow<Set<Int>> = _selectedMovieIds.asStateFlow()

    // Флаг загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMovies()
    }

    /**
     * Загружает все фильмы из БД и подписывается на изменения
     */
    private fun loadMovies() {
        viewModelScope.launch {
            // Flow из Room автоматически будет обновлять _movies при изменении БД
            repository.getAllMovies.collect { movieList ->
                _movies.value = movieList
                println("📽️ Загружено фильмов: ${movieList.size}")
            }
        }
    }

    /**
     * Переключает выбор фильма (выбран/не выбран)
     */
    fun toggleMovieSelection(movieId: Int, isSelected: Boolean) {
        _selectedMovieIds.value = if (isSelected) {
            _selectedMovieIds.value + movieId
        } else {
            _selectedMovieIds.value - movieId
        }
    }

    /**
     * Очищает выбор после удаления или отмены
     */
    fun clearSelection() {
        _selectedMovieIds.value = emptySet()
    }

    /**
     * Удаляет выбранные фильмы из БД
     */
    fun deleteSelectedMovies() {
        val idsToDelete = _selectedMovieIds.value.toList()
        if (idsToDelete.isEmpty()) return

        println("🗑️ Удаление фильмов с ID: $idsToDelete")

        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteMoviesByIds(idsToDelete)
                _selectedMovieIds.value = emptySet()
                println("✅ Удалено из БД: $idsToDelete")
            } catch (e: Exception) {
                println("❌ Ошибка удаления: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}