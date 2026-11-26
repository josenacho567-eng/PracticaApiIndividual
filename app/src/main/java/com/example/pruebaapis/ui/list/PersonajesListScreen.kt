package com.example.pruebaapis.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pruebaapis.model.PersonajeResumen
import com.example.pruebaapis.ui.theme.PruebaApisTheme
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun PersonajesListScreen(
    onPersonajeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonajesListViewModel = viewModel { PersonajesListViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PersonajesListContent(
        uiState = uiState,
        onPersonajeClick = onPersonajeClick,
        onCargarMas = { viewModel.cargarPersonajes(reiniciar = false) },
        onRecargar = { viewModel.cargarPersonajes(reiniciar = true) },
        onReintentar = viewModel::reintentar,
        onLimpiarError = viewModel::limpiarError,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonajesListContent(
    uiState: PersonajesListUiState,
    onPersonajeClick: (Int) -> Unit,
    onCargarMas: () -> Unit,
    onRecargar: () -> Unit,
    onReintentar: () -> Unit,
    onLimpiarError: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridState = rememberLazyGridState()

    LaunchedEffect(
        gridState,
        uiState.personajes.size,
        uiState.cargando,
        uiState.cargandoMas,
        uiState.finDeLista,
        uiState.mensajeError,
    ) {
        snapshotFlow {
            val totalItems = gridState.layoutInfo.totalItemsCount
            val ultimoVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && ultimoVisible >= totalItems - UMBRAL_CARGA_PAGINACION
        }
            .distinctUntilChanged()
            .collect { debeCargarMas ->
                if (
                    debeCargarMas &&
                    !uiState.cargando &&
                    !uiState.cargandoMas &&
                    !uiState.finDeLista &&
                    uiState.mensajeError == null
                ) {
                    onCargarMas()
                }
            }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Rick & Morty",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Directorio de personajes",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onRecargar,
                        enabled = !uiState.cargando && !uiState.cargandoMas,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar lista",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                uiState.cargando && uiState.personajes.isEmpty() -> {
                    EstadoCargandoInicial()
                }

                uiState.mensajeError != null && uiState.personajes.isEmpty() -> {
                    EstadoErrorPantallaCompleta(
                        mensaje = uiState.mensajeError,
                        onReintentar = onReintentar,
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 160.dp),
                        state = gridState,
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        items(
                            items = uiState.personajes,
                            key = { personaje -> personaje.id },
                        ) { personaje ->
                            PersonajeItemCard(
                                personaje = personaje,
                                onClick = { onPersonajeClick(personaje.id) },
                            )
                        }

                        if (uiState.cargandoMas) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        if (uiState.mensajeError != null && uiState.personajes.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                BannerErrorPaginacion(
                                    mensaje = uiState.mensajeError,
                                    onReintentar = onReintentar,
                                    onCerrar = onLimpiarError,
                                )
                            }
                        }

                        if (uiState.finDeLista && uiState.personajes.isNotEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = "Has llegado al final de la lista",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonajeItemCard(
    personaje: PersonajeResumen,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                AsyncImage(
                    model = personaje.image,
                    contentDescription = personaje.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Surface(
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                ) {
                    Text(
                        text = "#${personaje.id}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            ) {
                Text(
                    text = personaje.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ver detalles",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun EstadoCargandoInicial(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Cargando personajes...",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun EstadoErrorPantallaCompleta(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = onReintentar) {
            Text(text = "Reintentar")
        }
    }
}

@Composable
private fun BannerErrorPaginacion(
    mensaje: String,
    onReintentar: () -> Unit,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCerrar) {
                    Text(text = "Cerrar")
                }
                Button(onClick = onReintentar) {
                    Text(text = "Reintentar")
                }
            }
        }
    }
}

private const val UMBRAL_CARGA_PAGINACION = 4

@Preview(showBackground = true)
@Composable
private fun PersonajesListContentPreview() {
    PruebaApisTheme {
        PersonajesListContent(
            uiState = PersonajesListUiState(
                personajes = listOf(
                    PersonajeResumen(1, "Rick Sanchez", ""),
                    PersonajeResumen(2, "Morty Smith", ""),
                    PersonajeResumen(3, "Summer Smith", ""),
                    PersonajeResumen(4, "Beth Smith", ""),
                ),
            ),
            onPersonajeClick = {},
            onCargarMas = {},
            onRecargar = {},
            onReintentar = {},
            onLimpiarError = {},
        )
    }
}
