package com.example.almaneque.repositories

import com.example.almaneque.Service
import com.example.almaneque.models.NewPostResponse
import com.example.almaneque.models.Note
import com.example.almaneque.models.NoteResponse
import com.example.almaneque.models.NoteResponses
import com.example.almaneque.models.notedata

import retrofit2.Call

class NoteRepository {

    private val apiService = Service.getApiService()

    fun crearNota(nota: Note): Call<NewPostResponse> {
        return apiService.crearNota(nota)
    }

    fun obtenerNotas(): Call<NoteResponse> {
        return apiService.obtenerNotas()
    }

    fun eliminarNota(idNota: Int): Call<NewPostResponse> {
        return apiService.eliminarNota(idNota)
    }

    fun obtenerNota(idNota: Int): Call<NoteResponses> {
        return apiService.obtenerNota(idNota)
    }

    fun editarNota(nota: notedata): Call<NewPostResponse> {
        return apiService.editarNota(nota)
    }




}
