package com.domaners.everyminute

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.domaners.everyminute.ui.EveryMinuteApp
import com.domaners.everyminute.ui.theme.EveryMinuteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EveryMinuteTheme {
                EveryMinuteApp()
            }
        }
    }
}
