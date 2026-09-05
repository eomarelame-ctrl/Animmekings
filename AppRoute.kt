package com.animekings.app.ui.navigation

sealed class AppRoute(val route: String) {
    data object Home : AppRoute("home")
    data object Movies : AppRoute("movies")
    data object Series : AppRoute("series")
    data object Downloads : AppRoute("downloads")
    data object Favorites : AppRoute("favorites")
    data object Account : AppRoute("account")

    companion object {
        fun fromRoute(route: String?): AppRoute = when (route) {
            Movies.route -> Movies
            Series.route -> Series
            Downloads.route -> Downloads
            Favorites.route -> Favorites
            Account.route -> Account
            else -> Home
        }
    }
}
