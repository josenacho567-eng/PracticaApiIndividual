package com.example.pruebaapis.data

import com.example.pruebaapis.data.remote.retrofitClient
import com.example.pruebaapis.data.remote.rickAndMortyAPIService
import com.example.pruebaapis.model.PersonajeDetallado
import com.example.pruebaapis.model.PersonajeResumen
import com.example.pruebaapis.util.Resultado
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.HttpException

class rickAndMortyRepository(
    private val api: rickAndMortyAPIService = retrofitClient.api
) {

    suspend fun obtenerPersonajes(
        limite: Int,
        desplazamiento: Int,
    ): Resultado<List<PersonajeResumen>> = try {
        val pagina = desplazamiento / limite.coerceAtLeast(1) + 1
        val respuesta = api.obtenerPersonajes(pagina)
        val personajes = respuesta.results.map { personaje ->
            PersonajeResumen(
                id = personaje.id,
                name = personaje.name,
                image = personaje.image,
            )
        }
        Resultado.Exito(personajes)
    } catch (e: CancellationException) {
        throw e // Cancelar la corrutina no es un error.
    } catch (e: HttpException) {
        Resultado.Error("El servidor respondió con un error (código ${e.code()}).")
    } catch (e: IOException) {
        Resultado.Error("No hay conexión a internet. Revisa tu red e inténtalo de nuevo.")
    } catch (e: Exception) {
        Resultado.Error("Ocurrió un error inesperado.")
    }

    suspend fun obtenerDetallePersonaje(id: Int): Resultado<PersonajeDetallado> = try {
        val respuesta = api.obtenerDetallePersonaje(id)
        val detalle = PersonajeDetallado(
            id = respuesta.id,
            name = respuesta.name,
            status = respuesta.status,
            image = respuesta.image,
            origin = respuesta.origin.name,
            species = respuesta.species,
            gender = respuesta.gender,
            location = respuesta.location?.name.orEmpty(),
        )
        Resultado.Exito(detalle)
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Resultado.Error("El servidor respondió con un error (código ${e.code()}).")
    } catch (e: IOException) {
        Resultado.Error("No hay conexión a internet. Revisa tu red e inténtalo de nuevo.")
    } catch (e: Exception) {
        Resultado.Error("Ocurrió un error inesperado.")
    }
}