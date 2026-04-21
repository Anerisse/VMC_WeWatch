package com.example.wmc_wewatch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wmc_wewatch.data.MovieRepository
import com.example.wmc_wewatch.ui.add.AddViewModel
import com.example.wmc_wewatch.ui.main.MainViewModel
import com.example.wmc_wewatch.ui.search.SearchViewModel

class ViewModelFactory(
    private val repository: MovieRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                MainViewModel(repository) as T
            }

            /*
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                SearchViewModel() as T
            }

             */

            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                @Suppress("UNCHECKED_CAST")
                AddViewModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}