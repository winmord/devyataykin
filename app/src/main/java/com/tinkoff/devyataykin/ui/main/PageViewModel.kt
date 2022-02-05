package com.tinkoff.devyataykin.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageViewModel(gifRequester: GifRequester) : ViewModel() {
    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = gifRequester.description
    val gifUrl: LiveData<String> = gifRequester.gifUrl

    fun setIndex(index: Int) {
        _index.value = index
    }
}