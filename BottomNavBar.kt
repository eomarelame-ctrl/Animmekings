package com.animekings.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.animekings.app.R
import com.animekings.app.ui.navigation.AppRoute
import com.animekings.app.ui.theme.CardDark
import com.animekings.app.ui.theme.NeonPurple
import com.animekings.app.ui.theme.TextSecondary

data class BottomTab(val route: AppRoute, val labelRes: Int, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val bottomTabs = listOf(
    BottomTab(AppRoute.Home, R.string.nav_home, Icons.Filled.Home),
    BottomTab(AppRoute.Movies, R.string.nav_movies, Icons.Filled.Movie),
    BottomTab(AppRoute.Series, R.string.nav_series, Icons.Filled.Tv),
    BottomTab(AppRoute.Downloads, R.string.nav_downloads, Icons.Filled.Download),
    BottomTab(AppRoute.Favorites, R.string.nav_favorites, Icons.Filled.Favorite),
    BottomTab(AppRoute.Account, R.string.nav_account, Icons.Filled.AccountCircle),
)

@Composable
fun AnimeKingsBottomBar(currentRoute: AppRoute, onSelect: (AppRoute) -> Unit) {
    NavigationBar(containerColor = CardDark, contentColor = Color.White) {
        bottomTabs.forEach { tab ->
            val selected = currentRoute == tab.route
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(tab.route) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(stringResourceCompat(tab.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonPurple,
                    selectedTextColor = NeonPurple,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = CardDark
                )
            )
        }
    }
}

@Composable
private fun stringResourceCompat(id: Int) = androidx.compose.ui.res.stringResource(id)
