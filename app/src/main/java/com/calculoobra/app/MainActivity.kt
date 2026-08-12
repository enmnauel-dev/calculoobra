package com.calculoobra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.calculoobra.app.ui.CalculoObraApp
import com.calculoobra.app.ui.theme.CalculoObraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculoObraTheme {
                CalculoObraApp()
            }
        }
    }
}