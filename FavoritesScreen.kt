package com.animekings.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.animekings.app.R
import com.animekings.app.data.models.Favorite
import com.animekings.app.data.repository.AuthRepository
import com.animekings.app.data.repository.FavoritesRepository
import com.animekings.app.ui.components.CardItem
import com.animekings.app.ui.components.ContentCard
import com.animekings.app.ui.theme.TextSecondary

@Composable
fun FavoritesScreen(
    repo: FavoritesRepository = remember { FavoritesRepository() },
    auth: AuthRepository = remember { AuthRepository() }
) {
    var items by remember { mutableStateOf<List<Favorite>>(emptyList()) }
    LaunchedEffect(Unit) {
        val uid = auth.currentUserId
        if (uid != null) {
            runCatching { repo.myFavorites(uid) }.onSuccess { items = it }
        }
    }
    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.empty_favorites), color = TextSecondary)
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            items(items, key = { it.id }) { fav ->
                ContentCard(item = CardItem(fav.contentId, fav.contentType, null), onClick = {})
            }
        }
    }
}
