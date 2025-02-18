package com.example.almaneque.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.almaneque.models.Credenciales
import com.example.almaneque.models.NewPostResponse
import com.example.almaneque.models.Response
import com.example.almaneque.models.UserResponse
import com.example.almaneque.models.Usuario
import com.example.almaneque.models.alluserdata
import com.example.almaneque.models.userupdate
import com.example.almaneque.repositories.UsuarioRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response as RetrofitResponse

class UsuarioViewModel : ViewModel() {

    val usuarioBasicoResponse = MutableLiveData<Response>()
    private val repository = UsuarioRepository()

    val registroResponse = MutableLiveData<Response>()
    val loginResponse = MutableLiveData<Response>()

    val usuarioCompletoResponse = MutableLiveData<alluserdata>()
    val actualizacionResultado = MutableLiveData<NewPostResponse>()

    val errorResponse = Response("Error", "Hubo un problema al procesar la solicitud", data = null)
    //val errorResponse2 = UserResponse("Error", "Hubo un problema al procesar la solicitud", data = null)
    fun registrarUsuario(usuario: Usuario) {
        repository.registrarUsuario(usuario).enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: RetrofitResponse<Response>) {
                if (response.isSuccessful) {
                    registroResponse.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                registroResponse.postValue(errorResponse)
            }
        })
    }

    fun iniciarSesion(credenciales: Credenciales) {
        repository.iniciarSesion(credenciales).enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: RetrofitResponse<Response>) {
                if (response.isSuccessful) {
                    loginResponse.postValue(response.body())
                } else {
                    loginResponse.postValue(errorResponse)
                }

            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                loginResponse.postValue(errorResponse)
            }
        })
    }

    fun cerrarSesion(){
        repository.cerrarSesion().enqueue(object: Callback<Response>{

            override fun onResponse(call: Call<Response>, response: RetrofitResponse<Response>) {
                if (response.isSuccessful) {
                    loginResponse.postValue(response.body())
                } else {
                    loginResponse.postValue(errorResponse)
                }

            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                loginResponse.postValue(errorResponse)
            }

        })
    }

    fun obtenerInfoBasicaUsuario() {
        repository.obtenerInfoBasicaUsuario().enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: RetrofitResponse<Response>) {
                if (response.isSuccessful) {
                    usuarioBasicoResponse.postValue(response.body())
                } else {
                    usuarioBasicoResponse.postValue(errorResponse)
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                usuarioBasicoResponse.postValue(errorResponse)
            }
        })
    }

    fun obtenerInfoCompletaUsuario() {
        repository.obtenerInfoCompletaUsuario().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: RetrofitResponse<UserResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    usuarioCompletoResponse.postValue(response.body()?.data ?: null)
                } else {
                    usuarioCompletoResponse.postValue( null ?: null )
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                usuarioCompletoResponse.postValue(null ?: null)
            }
        })
    }

    fun actualizarUsuario(usuario: userupdate) {
        repository.actualizarUsuario(usuario).enqueue(object : Callback<NewPostResponse> {
            override fun onResponse(call: Call<NewPostResponse>, response: RetrofitResponse<NewPostResponse>) {
                actualizacionResultado.postValue(response.body())
            }

            override fun onFailure(call: Call<NewPostResponse>, t: Throwable) {
                actualizacionResultado.postValue(NewPostResponse("error", "Error en la conexión"))
            }
        })
    }




}
