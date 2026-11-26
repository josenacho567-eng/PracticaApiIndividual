package com.example.pruebaapis.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pruebaapis.data.rickAndMortyRepository
import com.example.pruebaapis.model.PersonajeDetallado
import com.example.pruebaapis.util.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonajeDetailUiState(
    val personaje: PersonajeDetallado? = null,
    val cargando: Boolean = false,
    val mensajeError: String? = null,
)

class PersonajeDetailViewModel(
    private val personajeId: Int,
    private val repository: rickAndMortyRepository = rickAndMortyRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonajeDetailUiState())
    val uiState: StateFlow<PersonajeDetailUiState> = _uiState.asStateFlow()

    init {
        cargarDetalle()
    }

    fun cargarDetalle() {
        if (_uiState.value.cargando) return

        _uiState.update { estado ->
            estado.copy(
                cargando = true,
                mensajeError = null,
            )
        }

        viewModelScope.launch {
            when (val resultado = repository.obtenerDetallePersonaje(personajeId)) {
                is Resultado.Exito -> {
                    _uiState.update { estado ->
                        estado.copy(
                            personaje = resultado.datos,
                            cargando = false,
                            mensajeError = null,
                        )
                    }
                }

                is Resultado.Error -> {
                    _uiState.update { estado ->
                        estado.copy(
                            cargando = false,
                            mensajeError = resultado.mensaje,
                        )
                    }
                }
            }
        }
    }

    fun reintentar() {
        cargarDetalle()
    }
}
