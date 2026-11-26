package com.example.pruebaapis.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface rickAndMortyAPIService {

    @GET("character")
    suspend fun obtenerPersonajes(
        @Query("page") pagina: Int,
    ): PersonajesListResponse

    @GET("character/{id}")
    suspend fun obtenerDetallePersonaje(
        @Path("id") id: Int,
    ): detallePersonajeDto
}