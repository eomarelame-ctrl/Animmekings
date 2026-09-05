package com.animekings.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.animekings.app.ui.screens.AccountScreen
import com.animekings.app.ui.screens.DownloadsScreen
import com.animekings.app.ui.screens.FavoritesScreen
import com.animekings.app.ui.screens.HomeScreen
import com.animekings.app.ui.screens.MoviesScreen
import com.animekings.app.ui.screens.SeriesScreen

@Composable
fun AnimeKingsNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = AppRoute.Home.route) {
        composable(AppRoute.Home.route) { HomeScreen() }
        composable(AppRoute.Movies.route) { MoviesScreen() }
        composable(AppRoute.Series.route) { SeriesScreen() }
        composable(AppRoute.Downloads.route) { DownloadsScreen() }
        composable(AppRoute.Favorites.route) { FavoritesScreen() }
        composable(AppRoute.Account.route) { AccountScreen() }
    }
}

@Composable
fun currentRoute(navController: NavHostController): AppRoute {
    val backStackEntry by navController.currentBackStackEntryAsState()
    return AppRoute.fromRoute(backStackEntry?.destination?.route)
}
