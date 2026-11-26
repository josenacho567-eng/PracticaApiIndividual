package com.example.pruebaapis.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebaapis.data.rickAndMortyRepository
import com.example.pruebaapis.model.PersonajeResumen
import com.example.pruebaapis.util.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonajesListUiState(
    val personajes: List<PersonajeResumen> = emptyList(),
    val cargando: Boolean = false,
    val cargandoMas: Boolean = false,
    val mensajeError: String? = null,
    val finDeLista: Boolean = false,
)

class PersonajesListViewModel(
    private val repository: rickAndMortyRepository = rickAndMortyRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonajesListUiState())
    val uiState: StateFlow<PersonajesListUiState> = _uiState.asStateFlow()

    init {
        cargarPersonajes()
    }

    fun cargarPersonajes(reiniciar: Boolean = false) {
        val estadoActual = _uiState.value
        if (estadoActual.cargando || estadoActual.cargandoMas) return
        if (!reiniciar && estadoActual.finDeLista) return

        val desplazamiento = if (reiniciar) 0 else estadoActual.personajes.size

        _uiState.update { estado ->
            estado.copy(
                personajes = if (reiniciar) emptyList() else estado.personajes,
                cargando = desplazamiento == 0,
                cargandoMas = desplazamiento > 0,
                mensajeError = null,
                finDeLista = !reiniciar && estado.finDeLista,
            )
        }

        viewModelScope.launch {
            when (
                val resultado = repository.obtenerPersonajes(
                    limite = TAMANO_PAGINA,
                    desplazamiento = desplazamiento,
                )
            ) {
                is Resultado.Exito -> {
                    _uiState.update { estado ->
                        val personajesActualizados =
                            (estado.personajes + resultado.datos).distinctBy { it.id }

                        estado.copy(
                            personajes = personajesActualizados,
                            cargando = false,
                            cargandoMas = false,
                            mensajeError = null,
                            finDeLista = resultado.datos.size < TAMANO_PAGINA,
                        )
                    }
                }

                is Resultado.Error -> {
                    _uiState.update { estado ->
                        estado.copy(
                            cargando = false,
                            cargandoMas = false,
                            mensajeError = resultado.mensaje,
                        )
                    }
                }
            }
        }
    }

    fun reintentar() {
        cargarPersonajes(reiniciar = _uiState.value.personajes.isEmpty())
    }

    fun limpiarError() {
        _uiState.update { estado -> estado.copy(mensajeError = null) }
    }

    private companion object {
        const val TAMANO_PAGINA = 20
    }
}
