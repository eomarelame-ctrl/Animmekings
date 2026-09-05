package com.animekings.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.animekings.app.data.repository.ContentRepository
import com.animekings.app.ui.components.CardItem
import com.animekings.app.ui.components.ContentCard

@Composable
fun SeriesScreen(repo: ContentRepository = remember { ContentRepository() }) {
    var items by remember { mutableStateOf<List<CardItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        runCatching { repo.series() }
            .onSuccess { list -> items = list.map { CardItem(it.id, it.title, it.coverUrl) } }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        items(items, key = { it.id }) { item ->
            ContentCard(item = item, onClick = {})
        }
    }
}
