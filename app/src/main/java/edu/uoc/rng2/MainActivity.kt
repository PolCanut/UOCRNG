package edu.uoc.rng2

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import edu.uoc.rng2.NavConstants.GAME_RESULT_SCREEN
import edu.uoc.rng2.NavConstants.GAME_RESULT_SCREEN_ARG_GAME_ID
import edu.uoc.rng2.NavConstants.GAME_SCREEN
import edu.uoc.rng2.NavConstants.HELP_SCREEN
import edu.uoc.rng2.NavConstants.HISTORY_SCREEN
import edu.uoc.rng2.NavConstants.WELCOME_SCREEN
import edu.uoc.rng2.models.GameResult
import edu.uoc.rng2.ui.screens.Help
import edu.uoc.rng2.ui.screens.game.Game
import edu.uoc.rng2.ui.screens.game.GameViewModel
import edu.uoc.rng2.ui.screens.gameresult.GameResult
import edu.uoc.rng2.ui.screens.gameresult.GameResultViewModel
import edu.uoc.rng2.ui.screens.history.History
import edu.uoc.rng2.ui.screens.history.HistoryViewModel
import edu.uoc.rng2.ui.screens.sync.SyncViewModel
import edu.uoc.rng2.ui.screens.welcome.Welcome
import edu.uoc.rng2.ui.screens.welcome.WelcomeViewModel
import edu.uoc.rng2.ui.theme.RNG2Theme
import edu.uoc.rng2.ui.title
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val GAME_ID_EXTRA = "gameId"
    }

    @Inject
    lateinit var playbackMediaController: PlaybackMediaController

    private val mainViewModel by viewModels<MainActivityViewModel>()
    private val syncViewModel by viewModels<SyncViewModel>()

    private var screenshotToStore: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameId: Long? = gameIdFromIntent()

        checkPermissions()

        setContent {
            RNG2Theme {
                Surface {
                    MainView(gameId) // Establece la vista principal de la aplicación
                }
            }
        }

        lifecycleScope.launch {
            mainViewModel.currentSong.collectLatest {
                playbackMediaController.playMedia(it.uri)
            }
        }

        // Iniciar sincronización de datos
        syncViewModel.syncData()
    }

    private fun gameIdFromIntent(): Long? {
        return intent.getLongExtra(GAME_ID_EXTRA, -1).run {
            if (this == -1L) {
                null
            } else {
                this
            }
        }
    }

    @Composable
    fun MainView(gameId: Long?) {
        val navController = rememberNavController() // Crea un controlador de navegación

        NavHost(navController, startDestination = WELCOME_SCREEN) {
            composable(WELCOME_SCREEN) {
                val viewModel = hiltViewModel<WelcomeViewModel>() // Obtiene el ViewModel para la pantalla de bienvenida

                Welcome(
                    viewModel = viewModel,
                    onChangeMusic = { changeMusicPicker() },
                    goToHistory = { navController.navigate(HISTORY_SCREEN) }, // Navega a la pantalla de historial
                    goToGame = { navController.navigate(GAME_SCREEN) }, // Navega a la pantalla de juego
                    goToHelp = { navController.navigate(HELP_SCREEN) },
                    exitApp = { finish() }, // Sale de la aplicación
                )
            }
            composable(HISTORY_SCREEN) {
                val viewModel = hiltViewModel<HistoryViewModel>() // Obtiene el ViewModel para la pantalla de historial

                History(
                    onBack = { navController.popBackStack() }, // Regresa atrás en la pila de navegación
                    viewModel = viewModel,
                    onGameResultClick = {
                        navigateToGameResult(navController, it, false)
                    } // Navega a la pantalla de resultados del juego
                )
            }
            composable(GAME_SCREEN) {
                val viewModel = hiltViewModel<GameViewModel>() // Obtiene el ViewModel para la pantalla de juego

                Game(
                    viewModel,
                    goToGameResult = { gameId ->
                        navigateToGameResult(
                            navController = navController, gameResultId = gameId, goBack = true
                        )
                    },
                    onBack = { navController.popBackStack() }, // Regresa atrás en la pila de navegación
                )
            }
            composable(HELP_SCREEN) {
                Help(
                    onBack = { navController.popBackStack() }, // Regresa atrás en la pila de navegación
                )
            }
            composable(
                GAME_RESULT_SCREEN,
                arguments = listOf(navArgument(GAME_RESULT_SCREEN_ARG_GAME_ID) {
                    type = NavType.LongType
                }),
            ) {
                val viewModel = hiltViewModel<GameResultViewModel>() // Obtiene el ViewModel para la pantalla de resultados del juego

                GameResult(
                    viewModel,
                    onAddGameResultToCalendar = { saveGameResultToCalendar(it) },
                    onSaveScreenshot = {
                        screenshotToStore = it
                        showSaveScreenshotPicker()
                    },
                    onBack = { navController.popBackStack() } // Regresa atrás en la pila de navegación
                )
            }
        }

        LaunchedEffect(gameId) {
            if (gameId != null) {
                navigateToGameResult(navController, gameId, false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playbackMediaController.lifecyclePlay()
    }

    override fun onPause() {
        super.onPause()
        playbackMediaController.lifecyclePause()
    }

    private fun saveGameResultToCalendar(gameResult: GameResult) {
        val intent = Intent(Intent.ACTION_EDIT)
        intent.setType("vnd.android.cursor.item/event")
        intent.putExtra("beginTime", gameResult.date)
        intent.putExtra("allDay", true)
        intent.putExtra("endTime", gameResult.date)
        intent.putExtra("title", gameResult.title(this))
        startActivity(intent)
    }

    private val musicChangeResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val uri: Uri? = result.data?.data
                if (uri != null) {
                    mainViewModel.setCustomSong(uri)
                }
            }
        }

    private fun changeMusicPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "audio/mpeg"
        }
        musicChangeResultLauncher.launch(intent)
    }

    private fun navigateToGameResult(
        navController: NavController, gameResultId: Long, goBack: Boolean
    ) {
        val path = GAME_RESULT_SCREEN.replace(
            "{$GAME_RESULT_SCREEN_ARG_GAME_ID}", gameResultId.toString()
        ) // Reemplaza el argumento en la ruta

        if (goBack) {
            navController.popBackStack() // Regresa atrás en la pila de navegación si es necesario
        }

        navController.navigate(path) // Navega a la pantalla de resultados del juego
    }

    private fun checkPermissions() {
        val permissionFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        val permissionCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION

        val pendingPermissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionNotification = Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permissionNotification) != PackageManager.PERMISSION_GRANTED) {
                pendingPermissions.add(permissionNotification)
            }
        }

        if (checkSelfPermission(permissionFineLocation) != PackageManager.PERMISSION_GRANTED) {
            pendingPermissions.add(permissionFineLocation)
        }

        if (checkSelfPermission(permissionCoarseLocation) != PackageManager.PERMISSION_GRANTED) {
            pendingPermissions.add(permissionCoarseLocation)
        }

        if (pendingPermissions.isNotEmpty()) {
            requestPermissions.launch(pendingPermissions.toTypedArray())
        }
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val isNotificationPermissionGranted =
                    permissions[Manifest.permission.POST_NOTIFICATIONS] ?: true

                if (!isNotificationPermissionGranted) {
                    Toast.makeText(
                        this,
                        getString(R.string.notifications_permission_denied_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            val isLocationPermissionGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: true

            if (!isLocationPermissionGranted) {
                Toast.makeText(
                    this,
                    getString(R.string.location_permission_denied_message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun showSaveScreenshotPicker() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/png"
            putExtra(
                Intent.EXTRA_TITLE,
                "screenshort_game-${Calendar.getInstance().timeInMillis}.png"
            )
        }
        launchFilePicker.launch(intent)
    }

    private val launchFilePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri = result.data?.data
            if (result.resultCode == Activity.RESULT_OK && uri != null) {

                try {
                    val screenShot = screenshotToStore

                    if (screenShot != null) {
                        contentResolver.openOutputStream(uri)?.use { fos ->
                            screenShot.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                            fos.flush()
                        }
                    }

                    Toast.makeText(this, getString(R.string.saved_screenshot), Toast.LENGTH_LONG)
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(
                        this,
                        getString(R.string.save_screenshot_error), Toast.LENGTH_SHORT
                    )
                        .show()
                    e.printStackTrace()
                }
            }

            screenshotToStore = null
        }
}
