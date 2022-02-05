package com.tinkoff.devyataykin.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PageViewModelFactory(private val gifRequester: GifRequester): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = PageViewModel(gifRequester) as T
}