package com.example.wmc_wewatch.ui.main.mvi


//  Одноразовые события (навигация. тосты)
sealed class MainEffect {
    object  NavigateToAdd : MainIntent()
    data class ShowError(val message: String) : MainEffect()
}