package com.mwema.a2kikao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.mwema.a2kikao.ui.navigation.KikaoNavHost
import com.mwema.a2kikao.ui.theme._2KikaoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _2KikaoTheme {
                val navController = rememberNavController()
                KikaoNavHost(navController = navController)
            }
        }
    }
}
