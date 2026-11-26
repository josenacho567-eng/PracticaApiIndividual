package com.example.pruebaapis.model

data class PersonajeResumen(
    val id: Int,
    val name: String,
    val image: String
)

data class PersonajeDetallado(
    val id: Int,
    val name: String,
    val status: String,
    val image: String,
    val origin: String,
    val species: String = "",
    val gender: String = "",
    val location: String = "",
)