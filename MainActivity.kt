package com.animekings.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.animekings.app.ui.components.AnimeKingsBottomBar
import com.animekings.app.ui.navigation.AnimeKingsNavHost
import com.animekings.app.ui.navigation.currentRoute
import com.animekings.app.ui.theme.AnimeKingsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnimeKingsTheme {
                AnimeKingsRoot()
            }
        }
    }
}

@Composable
fun AnimeKingsRoot() {
    val navController = rememberNavController()
    val route = currentRoute(navController)

    Scaffold(
        bottomBar = {
            AnimeKingsBottomBar(currentRoute = route) { target ->
                navController.navigate(target.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) { padding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(padding)) {
            AnimeKingsNavHost(navController = navController)
        }
    }
}
