package com.example.wmc_wewatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wmc_wewatch.data.MovieRepository
import com.example.wmc_wewatch.ui.main.MainViewModel

/**
 * Фабрика для создания ViewModel с зависимостями
 * Позволяет передавать репозиторий в конструктор ViewModel
 */
class ViewModelFactory(
    private val repository: MovieRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Проверяем, какую именно ViewModel нужно создать
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository) as T
            }
            // Здесь позже добавим создание других ViewModel (Search, Add)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}