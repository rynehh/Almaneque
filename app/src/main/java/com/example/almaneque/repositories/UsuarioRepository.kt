package com.example.almaneque.repositories

import com.example.almaneque.models.Credenciales
import com.example.almaneque.models.Response
import com.example.almaneque.models.Usuario
import com.example.almaneque.Service
import com.example.almaneque.models.NewPostResponse
import com.example.almaneque.models.UserResponse
import com.example.almaneque.models.userupdate
import retrofit2.Call

// Usuario repositorio nos servira para poder mandar a llamar las funciones de la api
// De esta forma podemos tener todo de manera mas ordenada en cuestion a las funciones de la api
// Aqui solo van las solicitudes de la api con relacion a los usuarios
// Es un intermedieria entre la API y la vista
class UsuarioRepository {

    private val apiService = Service.getApiService()

    fun registrarUsuario(usuario: Usuario): Call<Response> {
        return apiService.registrarUsuario(usuario)
    }

    fun iniciarSesion(credenciales: Credenciales): Call<Response> {
        return apiService.iniciarSesion(credenciales)
    }

    fun cerrarSesion(): Call<Response> {
        return apiService.cerrarSesion()
    }
    fun obtenerInfoBasicaUsuario(): Call<Response> {
        return apiService.obtenerInformacionBasica()
    }

    fun obtenerInfoCompletaUsuario(): Call<UserResponse> {
        return apiService.obtenerInfoCompletaUsuario()
    }

    // Actualizar la información del usuario autenticado
    fun actualizarUsuario(usuario: userupdate): Call<NewPostResponse> {
        return apiService.actualizarUsuario(usuario)
    }
}
