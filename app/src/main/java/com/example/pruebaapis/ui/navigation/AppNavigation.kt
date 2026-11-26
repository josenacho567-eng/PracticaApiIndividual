package com.example.pruebaapis.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pruebaapis.ui.detail.PersonajeDetailScreen
import com.example.pruebaapis.ui.list.PersonajesListScreen

object RutasApp {
    const val LISTA_PERSONAJES = "personajes_list"
    const val ARG_PERSONAJE_ID = "personajeId"
    const val DETALLE_PERSONAJE = "personaje_detail/{$ARG_PERSONAJE_ID}"

    fun crearRutaDetalle(personajeId: Int): String = "personaje_detail/$personajeId"
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasApp.LISTA_PERSONAJES,
        modifier = modifier,
    ) {
        composable(route = RutasApp.LISTA_PERSONAJES) {
            PersonajesListScreen(
                onPersonajeClick = { personajeId ->
                    navController.navigate(RutasApp.crearRutaDetalle(personajeId))
                },
            )
        }

        composable(
            route = RutasApp.DETALLE_PERSONAJE,
            arguments = listOf(
                navArgument(RutasApp.ARG_PERSONAJE_ID) {
                    type = NavType.IntType
                },
            ),
        ) { backStackEntry ->
            val personajeId = backStackEntry.arguments?.getInt(RutasApp.ARG_PERSONAJE_ID)
                ?: return@composable

            PersonajeDetailScreen(
                personajeId = personajeId,
                onVolver = { navController.popBackStack() },
            )
        }
    }
}
