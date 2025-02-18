package com.example.almaneque.models

data class postdata(
    val id_pub: Int,
    val titulo: String,
    val contenido: String,
    val borrador: Int,
    val id_cate: Int,
    val categoria_nombre: String,
    val archivos: List<archivo>
)

data class archivo(
    val archivo: String,
    val mostrar: Boolean
)