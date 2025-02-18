package com.example.almaneque.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.almaneque.viewmodels.PostViewModel

class PostViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}