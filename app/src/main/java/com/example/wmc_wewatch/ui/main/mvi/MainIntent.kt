package com.example.wmc_wewatch.ui.main.mvi

 // Все возможные действия пользователя на главном экране

sealed class MainIntent {
    object LoadMovies: MainIntent()
    data class ToggleSelection(val id: Int, val isSelected: Boolean): MainIntent()
    object DeleteSelected : MainIntent()
    object ClearError : MainIntent()
    object NavigateToAdd : MainIntent()
}