package com.example.almaneque.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.almaneque.models.PostResponse
import com.example.almaneque.models.Response
import com.example.almaneque.models.postdata
import com.example.almaneque.repositories.ProfileRepository
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class ProfileViewModel : ViewModel() {

    val publicacionesResponse = MutableLiveData<List<postdata>>()
    private val repository = ProfileRepository()

    var filtroNombre: String = ""
    var filtroOrden: String = "cronologico"
    var filtroCategoria: Int? = null

    val errorResponse = emptyList<postdata>()

    fun obtenerMisPublicaciones() {
        repository.listarMisPublicaciones(filtroNombre, filtroOrden, filtroCategoria).enqueue(object : Callback<PostResponse> {
            override fun onResponse(call: Call<PostResponse>, response: RetrofitResponse<PostResponse>) {
                if (response.isSuccessful) {
                    publicacionesResponse.postValue(response.body()?.data ?: errorResponse)
                } else {
                    publicacionesResponse.postValue(errorResponse)
                }
            }

            override fun onFailure(call: Call<PostResponse>, t: Throwable) {
                publicacionesResponse.postValue(errorResponse)
            }
        })
    }

    fun actualizarFiltros(nombre: String = "", orden: String = "cronologico", categoria: Int? = null) {
        filtroNombre = nombre
        filtroOrden = orden
        filtroCategoria = categoria
        obtenerMisPublicaciones()
    }
}
