package com.example.almaneque.repositories

import com.example.almaneque.models.Post
import com.example.almaneque.Service
import com.example.almaneque.models.ActPoseResponse
import com.example.almaneque.models.Borrador
import com.example.almaneque.models.BorradorResponse
import com.example.almaneque.models.NewPostResponse
import com.example.almaneque.models.PostResponse
import com.example.almaneque.models.postdata
import retrofit2.Call

class PostRepository {

    private val apiService = Service.getApiService()

    fun obtenerPublicaciones(
        nombre: String,
        orden: String,
        categoria: Int? = null
    ): Call<PostResponse> {
        return apiService.obtenerPublicaciones(nombre, orden, categoria)
    }

    fun crearPublicacion(publicacion: Post): Call<NewPostResponse> {
        return apiService.crearPublicacion(publicacion)
    }

    fun obtenerPublicacion(idPub: Int): Call<ActPoseResponse> {
        return apiService.obtenerPublicacion(idPub)
    }

    fun editarPublicacion(publicacion: postdata): Call<NewPostResponse> {
     return apiService.actualizarPublicacion(publicacion)
    }

    fun eliminarPublicacion(idPub: Int): Call<NewPostResponse> {
     return apiService.eliminarPublicacion(idPub)
    }

    fun listarBorradores(): Call<BorradorResponse> {
        return apiService.listarBorradores()
    }

    fun borrarBorrador(idBorrador: Int): Call<NewPostResponse> {
     return apiService.eliminarBorrador(idBorrador)
    }


}
