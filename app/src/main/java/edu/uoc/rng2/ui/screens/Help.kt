package edu.uoc.rng2.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.viewinterop.AndroidView
import edu.uoc.rng2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Help(onBack: () -> Unit) {
    // Diseño de la pantalla.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // Barra de aplicaciones superior con título y botón de retroceso.
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.help))
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
    ) { padding ->
        AndroidView(
            modifier = Modifier.padding(padding),
            factory = { context ->
                WebView(context).apply {
                    val language = Locale.current.language

                    val helpFile = if (language == "es") {
                        "help_es.html"
                    } else {
                        "help_en.html"
                    }

                    webViewClient = WebViewClient()

                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    settings.setSupportZoom(true)
                    val helpPage =
                        context.assets.open(helpFile).bufferedReader().use { it.readText() }
                    loadDataWithBaseURL(null, helpPage, "text/html", "UTF-8", null)
                }
            },
        )
    }
}