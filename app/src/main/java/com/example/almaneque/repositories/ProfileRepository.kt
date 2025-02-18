package com.example.almaneque.repositories

import com.example.almaneque.Service
import com.example.almaneque.models.PostResponse
import retrofit2.Call

class ProfileRepository {

    private val apiService = Service.getApiService()

    fun listarMisPublicaciones(
        nombre: String?,
        orden: String,
        categoria: Int? = null
    ): Call<PostResponse> {
        return apiService.listarMisPublicaciones(nombre, orden, categoria)
    }
}
