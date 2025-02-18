package com.example.almaneque.models

import com.google.gson.annotations.SerializedName

data class Post(
    val titulo: String,
    val contenido: String,
    val borrador: Boolean,
    val id_cate: Int,
    val archivos: List<ArchivoRequest>?
)

data class ArchivoRequest(
    val contenido: String
)