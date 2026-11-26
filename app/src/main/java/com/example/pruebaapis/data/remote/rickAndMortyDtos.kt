package com.example.pruebaapis.data.remote

data class PersonajesListResponse(
    val info: InfoPaginaDto,
    val results: List<ListaPersonajesDto>,
)

data class InfoPaginaDto(
    val count: Int,
    val pages: Int,
    val next: String?,
)
data class ListaPersonajesDto(
    val id: Int,
    val name: String,
    val image: String
)

data class detallePersonajeDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String = "",
    val gender: String = "",
    val image: String,
    val origin: OrigenPersonajeDto,
    val location: OrigenPersonajeDto? = null,
)

data class OrigenPersonajeDto(
    val name: String
)