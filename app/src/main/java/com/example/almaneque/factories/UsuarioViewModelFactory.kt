package com.example.almaneque.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.almaneque.viewmodels.UsuarioViewModel


class UsuarioViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            return UsuarioViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
