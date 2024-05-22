package edu.uoc.rng2.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rxjava3.subscribeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.uoc.rng2.R
import edu.uoc.rng2.models.GameResult
import edu.uoc.rng2.ui.formattedDate
import edu.uoc.rng2.ui.icon
import edu.uoc.rng2.ui.iconColor
import edu.uoc.rng2.ui.title

// Función componible para mostrar la pantalla de historial de resultados.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun History(
    viewModel: HistoryViewModel, // ViewModel para la pantalla de historial.
    onBack: () -> Unit, // Función de callback para volver atrás.
    onGameResultClick: (Long) -> Unit, // Función de callback al hacer clic en un resultado del juego.
) {
    // Obteniendo la lista de resultados del juego del ViewModel.
    val gameResults by viewModel.history.subscribeAsState(emptyList())

    // Estructura principal de la pantalla.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Barra superior de la pantalla.
            TopAppBar(
                title = {
                    Text("Histórico") // Título de la barra superior.
                },
                navigationIcon = {
                    // Icono de navegación para volver atrás.
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, // Icono de flecha hacia atrás.
                            contentDescription = null,
                        )
                    }
                }
            )
        },
    ) { it ->
        // Contenido principal de la pantalla.
        if (gameResults.isEmpty()) {
            // Si la lista de resultados está vacía, mostrar un mensaje.
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                Text(stringResource(R.string.empty_history))  // Mensaje indicando que el historial está vacío.
            }
        } else {
            // Si hay resultados en el historial, mostrarlos en una lista.
            LazyColumn(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                // Iterar sobre cada resultado del juego en la lista.
                items(gameResults) { gameResult ->
                    // Mostrar cada resultado del juego.
                    GameResult(
                        gameResult,
                        onGameResultClick = { onGameResultClick(gameResult.id) } // Callback al hacer clic en un resultado del juego.
                    )
                }
            }
        }

    }
}

// Función componible para mostrar un resultado del juego en la lista.
@Composable
private fun GameResult(gameResult: GameResult, onGameResultClick: () -> Unit) {
    // Estructura para mostrar cada resultado del juego en la lista.
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onGameResultClick() } // Hacer clic en un resultado del juego.
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icono del resultado del juego.
        Icon(
            painterResource(id = gameResult.icon),
            contentDescription = null,
            tint = gameResult.iconColor,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),  // Espaciado del icono.
        )

        // Detalles del resultado del juego (título y fecha).
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                gameResult.title, // Título del resultado del juego.
                fontSize = 18.sp,
            )

            Text(
                gameResult.formattedDate, // Fecha formateada del resultado del juego.
            )

            val lat = gameResult.latitude
            val long = gameResult.longitude

            if (lat != null && long != null) {
                Text(
                    "Lat: $lat",
                )

                Text(
                    "Long: $long",
                )
            }


        }
    }
}
