package com.example.almaneque.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.almaneque.repositories.ProfileRepository
import com.example.almaneque.viewmodels.ProfileViewModel

class ProfileViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
