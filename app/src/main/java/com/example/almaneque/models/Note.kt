package com.example.almaneque.models

import java.sql.Date

data class Note(
    val titulo: String,
    val regado: String,
    val luz: String,
    val fertilizacion: String,
    val clima: String,
    val descripcion: String
)
