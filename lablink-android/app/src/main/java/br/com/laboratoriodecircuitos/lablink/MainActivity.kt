package br.com.laboratoriodecircuitos.lablink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.com.laboratoriodecircuitos.lablink.features.home.LabLinkHomeScreen
import br.com.laboratoriodecircuitos.lablink.ui.theme.LabLinkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LabLinkTheme {
                LabLinkHomeScreen()
            }
        }
    }
}
