package com.example.wmc_wewatch.ui.search.mvi

//  Действия на экране поиска

sealed class SearchIntent {
    data class Search(val query: String) : SearchIntent()
    object Clear : SearchIntent()
}