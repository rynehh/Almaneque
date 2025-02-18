package com.example.almaneque.models

data class NoteResponse(
    val status: String,
    val data: List<notedata>?
)
