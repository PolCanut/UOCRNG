package edu.uoc.rng2.ui.screens.game

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.uoc.rng2.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

const val mainPlayerAlpha = 1f
const val mainPlayerScale = 1f
const val mainPlayerTopPadding: Int = 0
const val secondaryPlayerAlpha = 0.4f
const val secondaryPlayerScale = 0.6f
const val secondaryPlayerTopPadding = 32

private const val TAG = "Game"

// Pantalla principal del juego.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Game(
    viewModel: GameViewModel, // ViewModel que contiene la lógica del juego.
    goToGameResult: (Long) -> Unit, // Función de navegación para ir al resultado del juego.
    onBack: () -> Unit, // Función de navegación para regresar a la pantalla anterior.
) {
    // Observa el estado del jugador actual y la cifra actual del ViewModel.
    val currentPlayer by viewModel.currentPlayer.collectAsState()
    val currentNumber by viewModel.currentNumber.collectAsState()
    val isSavingGame by viewModel.isSavingGame.collectAsState()

    // Se ejecuta cuando la partida termina para navegar al resultado del juego.
    LaunchedEffect(viewModel) {
        viewModel.gameEnded.collectLatest {
            goToGameResult(it)
        }
    }

    // Diseño de la pantalla.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Barra de aplicaciones superior con título y botón de retroceso.
            TopAppBar(
                title = {
                    Text("Partida")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Indicador de progreso si es el turno de la CPU.
            if (currentPlayer == Player.CPU || isSavingGame) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Texto que indica el turno del jugador.
            Text(stringResource(R.string.turn_of))

            // Texto que muestra el tipo de jugador actual (persona o CPU).


            val humanPlayerAlpha: Float
            val humanPlayerScale: Float
            val humanPlayerTopPadding: Int
            val cpuPlayerAlpha: Float
            val cpuPlayerScale: Float
            val cpuPlayerTopPadding: Int

            when (currentPlayer) {
                Player.PERSON -> {
                    humanPlayerScale = mainPlayerScale
                    humanPlayerAlpha = mainPlayerAlpha
                    humanPlayerTopPadding = mainPlayerTopPadding
                    cpuPlayerScale = secondaryPlayerScale
                    cpuPlayerAlpha = secondaryPlayerAlpha
                    cpuPlayerTopPadding = secondaryPlayerTopPadding
                }

                Player.CPU -> {
                    humanPlayerScale = secondaryPlayerScale
                    humanPlayerAlpha = secondaryPlayerAlpha
                    humanPlayerTopPadding = secondaryPlayerTopPadding
                    cpuPlayerScale = mainPlayerScale
                    cpuPlayerAlpha = mainPlayerAlpha
                    cpuPlayerTopPadding = mainPlayerTopPadding
                }
            }

            val humanPlayerAlphaState: State<Float> =
                animateFloatAsState(targetValue = humanPlayerAlpha, label = "humanPlayerAlphaState")

            val humanPlayerScaleState: State<Float> =
                animateFloatAsState(targetValue = humanPlayerScale, label = "humanPlayerScaleState")

            val humanPlayerTopPaddingState: State<Int> =
                animateIntAsState(targetValue = humanPlayerTopPadding, label = "humanPlayerTopPaddingState")

            val cpuPlayerAlphaState: State<Float> =
                animateFloatAsState(targetValue = cpuPlayerAlpha, label = "cpuPlayerAlphaStat")

            val cpuPlayerScaleState: State<Float> =
                animateFloatAsState(targetValue = cpuPlayerScale, label = "cpuPlayerScaleState")

            val cpuPlayerTopPaddingState: State<Int> =
                animateIntAsState(targetValue = cpuPlayerTopPadding, label = "cpuPlayerTopPaddingState")

            Log.e(TAG, "humanPlayerTopPadding: ${humanPlayerTopPaddingState.value}")

            Box(
                modifier = Modifier.height(64.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                Text(
                    stringResource(R.string.player),
                    fontSize = 28.sp,
                    modifier = Modifier
                        .alpha(humanPlayerAlphaState.value)
                        .padding(top = humanPlayerTopPaddingState.value.dp)
                        .scale(humanPlayerScaleState.value)
                )

                Text(
                    stringResource(R.string.cpu),
                    fontSize = 28.sp,
                    modifier = Modifier
                        .alpha(cpuPlayerAlphaState.value)
                        .padding(top = cpuPlayerTopPaddingState.value.dp)
                        .scale(cpuPlayerScaleState.value)
                )
            }

            Box(modifier = Modifier.weight(1f, true))

            // Texto que muestra la cifra actual.
            Text(stringResource(R.string.current_number))

            AnimatedNumericText(currentNumber)

            Box(modifier = Modifier.weight(1f, true))

            // Botón para generar un nuevo número si es el turno del jugador.
            Button(
                onClick = { viewModel.generateNewNumber() },
                enabled = currentPlayer == Player.PERSON && !isSavingGame,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(stringResource(R.string.generate_number))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun AnimatedNumericTextPreview() {
    val value by remember {
        flow {
            delay(2000)
            emit(50)
            delay(2000)
            emit(25)
            delay(2000)
            emit(1)
        }
    }.collectAsState(initial = 100)

    AnimatedNumericText(value)
}

@Composable
fun AnimatedNumericText(currentNumber: Int) {
    AnimatedContent(
        targetState = currentNumber,
        transitionSpec = {
            (
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
            )
                .using(
                    SizeTransform(clip = false)
                )
        }, label = "NumbersChangeAnimations"
    ) { value ->

        Text(
            value.toString(),
            fontSize = 40.sp,
        )
    }
}
