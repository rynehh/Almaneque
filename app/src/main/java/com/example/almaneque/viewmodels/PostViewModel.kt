package com.example.almaneque.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.almaneque.models.ActPoseResponse
import com.example.almaneque.models.Borrador
import com.example.almaneque.models.BorradorResponse
import com.example.almaneque.models.Draft
import com.example.almaneque.models.NewPostResponse
import com.example.almaneque.models.Post
import com.example.almaneque.models.PostResponse
import com.example.almaneque.models.postdata
import com.example.almaneque.repositories.PostRepository
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class PostViewModel : ViewModel() {

    val publicacionesResponse = MutableLiveData<List<postdata>>()
    val crearPublicacionResponse = MutableLiveData<NewPostResponse>()
    val publicacionResponse = MutableLiveData<postdata?>() // Cambiado a aceptar valores nulos
    val edicionPublicacionResponse = MutableLiveData<NewPostResponse>() // Para la respuesta de edición
    private val repository = PostRepository()
    private val _borradoresResponse = MutableLiveData<List<Borrador>>()
    val borradoresResponse: MutableLiveData<List<Borrador>> = MutableLiveData()

    private val _eliminarBorradorResponse = MutableLiveData<NewPostResponse?>()
    val eliminarBorradorResponse: LiveData<NewPostResponse?> get() = _eliminarBorradorResponse


    var filtroNombre: String = ""
    var filtroOrden: String = ""
    var filtroCategoria: Int? = null

    val errorResponse = emptyList<postdata>()

    fun obtenerPublicaciones() {
        repository.obtenerPublicaciones(filtroNombre, filtroOrden, filtroCategoria)
            .enqueue(object : Callback<PostResponse> {
                override fun onResponse(
                    call: Call<PostResponse>,
                    response: RetrofitResponse<PostResponse>
                ) {
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

    fun obtenerPublicacion(idPublicacion: Int) {
        repository.obtenerPublicacion(idPublicacion).enqueue(object : Callback<ActPoseResponse> {
            override fun onResponse(call: Call<ActPoseResponse>, response: RetrofitResponse<ActPoseResponse>) {
                if (response.isSuccessful && response.body()?.status == "success" && response.body()?.data != null) {
                    publicacionResponse.postValue(response.body()?.data)
                } else {
                    Log.e("ObtenerPost", "Error al obtener el Post")

                }
            }

            override fun onFailure(call: Call<ActPoseResponse>, t: Throwable)
            {
                Log.e("ObtenerPost", "Fallo al obtener el Post", t)

            }
        })
    }

    fun listarBorradores() {
        repository.listarBorradores().enqueue(object : Callback<BorradorResponse> {
            override fun onResponse(
                call: Call<BorradorResponse>,
                response: RetrofitResponse<BorradorResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    borradoresResponse.postValue(response.body()?.data)
                } else {
                    Log.e("PostViewModel", "Error al listar borradores: ${response.errorBody()?.string()}")
                    borradoresResponse.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<BorradorResponse>, t: Throwable) {
                Log.e("PostViewModel", "Fallo al listar borradores: ${t.message}")
                borradoresResponse.postValue(emptyList())
            }
        })
    }


    fun eliminarBorrador(idBorrador: Int) {
        repository.borrarBorrador(idBorrador).enqueue(object : Callback<NewPostResponse> {
            override fun onResponse(call: Call<NewPostResponse>, response: RetrofitResponse<NewPostResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    // Filtrar los borradores actualizados eliminando el borrador correspondiente
                    val borradoresActualizados = borradoresResponse.value?.filter { it.id_pub != idBorrador }
                    borradoresResponse.postValue(borradoresActualizados ?: emptyList())
                    Log.d("PostViewModel", "Borrador eliminado correctamente")
                } else {
                    Log.e("PostViewModel", "Error al eliminar el borrador: ${response.body()?.message}")
                    // Manejar error, como notificar a la UI
                }
            }

            override fun onFailure(call: Call<NewPostResponse>, t: Throwable) {
                Log.e("PostViewModel", "Fallo al eliminar el borrador: ${t.message}", t)
                // Manejar fallo, como notificar a la UI
            }
        })
    }



    fun editarPublicacion(publicacion: postdata) {
        repository.editarPublicacion(publicacion).enqueue(object : Callback<NewPostResponse> {
            override fun onResponse(
                call: Call<NewPostResponse>,
                response: RetrofitResponse<NewPostResponse>
            ) {
                if (response.isSuccessful) {
                    edicionPublicacionResponse.postValue(response.body())
                } else {
                    edicionPublicacionResponse.postValue(
                        NewPostResponse("error", "Error al editar publicación")
                    )
                }
            }

            override fun onFailure(call: Call<NewPostResponse>, t: Throwable) {
                edicionPublicacionResponse.postValue(
                    NewPostResponse("error", "Fallo en la conexión")
                )
            }
        })
    }

    fun actualizarFiltros(nombre: String = "", orden: String = "", categoria: Int? = null) {
        filtroNombre = nombre
        filtroOrden = orden
        filtroCategoria = categoria
        obtenerPublicaciones()
    }

    fun eliminarPublicacion(idPublicacion: Int) {
        repository.eliminarPublicacion(idPublicacion).enqueue(object : Callback<NewPostResponse> {
            override fun onResponse(call: Call<NewPostResponse>, response: RetrofitResponse<NewPostResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val publicacionesActualizadas = publicacionesResponse.value?.filter { it.id_pub != idPublicacion }
                    publicacionesResponse.postValue(publicacionesActualizadas ?: emptyList())
                    Log.d("PostViewModel", "Publicación eliminada correctamente")

                } else {
                    Log.e("PostViewModel", "Error al eliminar la publicación: ${response.body()?.message}")
                    // Manejar error, como notificar a la UI

                }
            }

            override fun onFailure(call: Call<NewPostResponse>, t: Throwable) {
                Log.e("PostViewModel", "Fallo al eliminar la publicación: ${t.message}", t)
                // Manejar fallo, como notificar a la UI

            }
        })
    }


    fun crearPublicacion(publicacion: Post) {
        repository.crearPublicacion(publicacion).enqueue(object : Callback<NewPostResponse> {
            override fun onResponse(
                call: Call<NewPostResponse>,
                response: RetrofitResponse<NewPostResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("RespuestaServidor", response.body().toString())
                    Log.d("API Request", Gson().toJson(publicacion))
                    crearPublicacionResponse.postValue(response.body())
                } else {
                    crearPublicacionResponse.postValue(
                        NewPostResponse("error", "Error al crear publicación")
                    )
                }
            }

            override fun onFailure(call: Call<NewPostResponse>, t: Throwable) {
                crearPublicacionResponse.postValue(
                    NewPostResponse("error", "Fallo en la conexión")
                )
            }
        })
    }
}

