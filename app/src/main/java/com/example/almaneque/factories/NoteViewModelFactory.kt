package com.example.almaneque.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.almaneque.viewmodels.NoteViewModel

class NoteViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}