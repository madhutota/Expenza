package com.madhu.expenza

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.madhu.expenza.ui.screens.DashboardScreen
import com.madhu.expenza.ui.theme.ExpenzaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenzaTheme {
                DashboardScreen()
            }
        }
    }
}