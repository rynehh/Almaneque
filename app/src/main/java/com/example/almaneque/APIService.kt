package com.example.almaneque

import com.example.almaneque.models.ActPoseResponse
import com.example.almaneque.models.Borrador
import com.example.almaneque.models.BorradorResponse
import com.example.almaneque.models.Credenciales
import com.example.almaneque.models.NewPostResponse
import com.example.almaneque.models.Note
import com.example.almaneque.models.NoteResponse
import com.example.almaneque.models.NoteResponses
import com.example.almaneque.models.Post
import com.example.almaneque.models.PostResponse
import com.example.almaneque.models.Response
import com.example.almaneque.models.UserResponse
import com.example.almaneque.models.Usuario
import com.example.almaneque.models.notedata
import com.example.almaneque.models.postdata
import com.example.almaneque.models.userupdate
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    //Rutas de Usuarios
    @POST("api/usuario/registro")
    fun registrarUsuario(@Body usuario: Usuario): Call<Response>

    @POST("api/usuario/login")
    fun iniciarSesion(@Body credenciales: Credenciales): Call<Response>

    @POST("api/usuario/logout")
    fun cerrarSesion(): Call<Response>

    @GET("api/usuario/infobasica")
    fun obtenerInformacionBasica(): Call<Response>

    @GET("api/usuario/infocompleta")
    fun obtenerInfoCompletaUsuario(): Call<UserResponse>

    @POST("api/usuario/actualizar")
    fun actualizarUsuario(@Body usuario: userupdate): Call<NewPostResponse>

    //Rutas de publicaciones

    @GET("api/publicacion/listar")
    fun obtenerPublicaciones(@Query("nombre") nombre: String? = null,
                             @Query("orden") orden: String? = null,
                             @Query("categoria") categoria: Int? = null): Call<PostResponse>

    @POST("api/publicacion/crear")
    fun crearPublicacion(@Body publicacion: Post): Call<NewPostResponse>

    @GET("api/publicacion/mispublicaciones")
    fun listarMisPublicaciones(@Query("nombre") nombre: String?,
                               @Query("orden") orden: String = "cronologico",
                               @Query("categoria") categoria: Int?): Call<PostResponse>

    //QUE ESTA PASANDO AQUI??
    @GET("api/publicacion/obtener")
    fun obtenerPublicacion(@Query("id_pub") idPub: Int): Call<ActPoseResponse>

    @POST("api/publicacion/editar")
    fun actualizarPublicacion(@Body publicacion: postdata): Call<NewPostResponse>

    // TENGO MIEDO
    @DELETE("api/publicacion/eliminar")
    fun eliminarPublicacion(@Query("id_pub") idPub: Int): Call<NewPostResponse>

    // Borradores

    @GET("api/borradores/listar")
    fun listarBorradores(): Call<BorradorResponse>

    @DELETE("api/borradores/eliminar")
    fun eliminarBorrador(@Query("id_pub") idBorrador: Int): Call<NewPostResponse>

    // Rutas de Notas
    @POST("api/nota/crear")
    fun crearNota(@Body nota: Note): Call<NewPostResponse>
    @GET("api/nota/listar")
    fun obtenerNotas(): Call<NoteResponse>
    @DELETE("api/nota/eliminar")
    fun eliminarNota(@Query("id_nota") idNota: Int): Call<NewPostResponse>
    @GET("api/nota/obtener")
    fun obtenerNota(@Query("id_nota") idNota: Int): Call<NoteResponses>
    @PUT("api/nota/editar")
    fun editarNota(@Body nota: notedata): Call<NewPostResponse>



}