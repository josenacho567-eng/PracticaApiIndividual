package com.example.pruebaapis.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pruebaapis.model.PersonajeDetallado
import com.example.pruebaapis.ui.theme.PruebaApisTheme

@Composable
fun PersonajeDetailScreen(
    personajeId: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonajeDetailViewModel = viewModel(key = "personaje_$personajeId") {
        PersonajeDetailViewModel(personajeId = personajeId)
    },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PersonajeDetailContent(
        uiState = uiState,
        onVolver = onVolver,
        onReintentar = viewModel::reintentar,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonajeDetailContent(
    uiState: PersonajeDetailUiState,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.personaje?.name ?: "Detalle del personaje",
                        maxLines = 1,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.cargando -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Cargando información...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                uiState.mensajeError != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.mensajeError,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(onClick = onReintentar) {
                            Text(text = "Reintentar")
                        }
                    }
                }

                uiState.personaje != null -> {
                    DetallePersonajeBody(personaje = uiState.personaje)
                }
            }
        }
    }
}

@Composable
private fun DetallePersonajeBody(
    personaje: PersonajeDetallado,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ElevatedCard(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .aspectRatio(1f),
        ) {
            AsyncImage(
                model = personaje.image,
                contentDescription = personaje.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "#${personaje.id} · ${personaje.name}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(10.dp))

        EstadoPersonajeChip(status = personaje.status)

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Ficha técnica",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(12.dp))

                FilaAtributo(
                    etiqueta = "Estado",
                    valor = traducirEstado(personaje.status),
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                FilaAtributo(
                    etiqueta = "Origen",
                    valor = traducirValor(personaje.origin),
                )

                if (personaje.species.isNotBlank()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    FilaAtributo(
                        etiqueta = "Especie",
                        valor = traducirValor(personaje.species),
                    )
                }

                if (personaje.gender.isNotBlank()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    FilaAtributo(
                        etiqueta = "Género",
                        valor = traducirGenero(personaje.gender),
                    )
                }

                if (personaje.location.isNotBlank()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
                    FilaAtributo(
                        etiqueta = "Última ubicación",
                        valor = traducirValor(personaje.location),
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoPersonajeChip(
    status: String,
    modifier: Modifier = Modifier,
) {
    val colorIndicador = when (status.lowercase()) {
        "alive" -> Color(0xFF2E7D32)
        "dead" -> Color(0xFFC62828)
        else -> Color(0xFF757575)
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(colorIndicador),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = traducirEstado(status),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun FilaAtributo(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
        )
    }
}

private fun traducirEstado(status: String): String = when (status.lowercase()) {
    "alive" -> "Vivo"
    "dead" -> "Muerto"
    "unknown" -> "Desconocido"
    else -> status
}

private fun traducirGenero(gender: String): String = when (gender.lowercase()) {
    "male" -> "Masculino"
    "female" -> "Femenino"
    "genderless" -> "Sin género"
    "unknown" -> "Desconocido"
    else -> gender
}

private fun traducirValor(valor: String): String =
    if (valor.equals("unknown", ignoreCase = true)) "Desconocido" else valor

@Preview(showBackground = true)
@Composable
private fun PersonajeDetailContentPreview() {
    PruebaApisTheme {
        PersonajeDetailContent(
            uiState = PersonajeDetailUiState(
                personaje = PersonajeDetallado(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    image = "",
                    origin = "Earth (C-137)",
                    species = "Human",
                    gender = "Male",
                    location = "Citadel of Ricks",
                ),
            ),
            onVolver = {},
            onReintentar = {},
        )
    }
}
