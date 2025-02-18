package com.example.almaneque.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.almaneque.models.Note
import com.example.almaneque.models.NewPostResponse
import com.example.almaneque.models.NoteResponse
import com.example.almaneque.models.NoteResponses
import com.example.almaneque.models.notedata
import com.example.almaneque.repositories.NoteRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class NoteViewModel : ViewModel() {

    val crearNotaResponse = MutableLiveData<NewPostResponse>()
    val notasResponse = MutableLiveData<List<notedata>>()
    val eliminarNotaResponse = MutableLiveData<NewPostResponse>()
    val notaResponse = MutableLiveData<notedata>()
    private val repository = NoteRepository()

    fun crearNota(nota: Note) {
        repository.crearNota(nota).enqueue(object : Callback<NewPostResponse> {
            override fun onResponse(call: Call<NewPostResponse>, response: RetrofitResponse<NewPostResponse>) {
                if (response.isSuccessful) {

                    crearNotaResponse.postValue(response.body())
                } else {
                    crearNotaResponse.postValue(
                        NewPostResponse("error", "Error al crear nota")
                    )
                }
            }

            override fun onFailure(call: Call<NewPostResponse>, t: Throwable) {
                crearNotaResponse.postValue(
                    NewPostResponse("error", "Fallo en la conexión")
                )
            }
        })
    }

    fun obtenerNotas() {
        repository.obtenerNotas().enqueue(object : Callback<NoteResponse> {
            override fun onResponse(call: Call<NoteResponse>, response: RetrofitResponse<NoteResponse>) {
                if (response.isSuccessful) {
                    notasResponse.postValue(response.body()?.data ?: emptyList())
                } else {
                    notasResponse.postValue(emptyList())
                }
            }

            override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                notasResponse.postValue(emptyList())
            }
        })
    }

    fun obtenerNota(idNota: Int) {
        repository.obtenerNota(idNota).enqueue(object : Callback<NoteResponses> {
            override fun onResponse(call: Call<NoteResponses>, response: RetrofitResponse<NoteResponses>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    notaResponse.postValue(response.body()?.data ?: null)
                } else {
                    Log.e("ObtenerNota", "Error al obtener la nota")
                }
            }

            override fun onFailure(call: Call<NoteResponses>, t: Throwable) {
                Log.e("ObtenerNota", "Fallo en la conexión: ${t.message}")
            }
        })
    }
    fun editarNota(nota: notedata) {
        repository.editarNota(nota).enqueue(object : Callback<NewPostResponse> {
            override fun onResponse(call: Call<NewPostResponse>, response: RetrofitResponse<NewPostResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Log.d("EditarNota", "Nota editada correctamente")
                } else {
                    Log.e("EditarNota", "Error al editar la nota")
                }
            }

            override fun onFailure(call: Call<NewPostResponse>, t: Throwable) {
                Log.e("EditarNota", "Fallo en la conexión: ${t.message}")
            }
        })
    }

    fun eliminarNota(idNota: Int) {
        repository.eliminarNota(idNota).enqueue(object : Callback<NewPostResponse> {
            override fun onResponse(call: Call<NewPostResponse>, response: RetrofitResponse<NewPostResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    // Filtra la nota eliminada de la lista de notas y actualiza el LiveData
                    val notasActualizadas = notasResponse.value?.filter { it.id_nota != idNota }
                    notasResponse.postValue(notasActualizadas?: emptyList())
                    Log.d("EliminarNota", "Nota eliminada correctamente")
                } else {
                    Log.e("EliminarNota", "Error al eliminar la nota: ${response.body()?.message}")
                }
            }

            override fun onFailure(call: Call<NewPostResponse>, t: Throwable) {
                Log.e("EliminarNota", "Fallo en la conexión: ${t.message}")
            }
        })
    }

}
