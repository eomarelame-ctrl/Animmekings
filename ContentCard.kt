package com.animekings.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.LazyRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.animekings.app.ui.theme.CardDark
import com.animekings.app.ui.theme.TextPrimary

data class CardItem(
    val id: String,
    val title: String,
    val coverUrl: String?
)

/** A single poster card: cover art + title, used inside horizontal rails. */
@Composable
fun ContentCard(item: CardItem, onClick: (CardItem) -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick(item) }
    ) {
        AsyncImage(
            model = item.coverUrl,
            contentDescription = item.title,
            modifier = Modifier
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
        )
        Text(
            text = item.title,
            color = TextPrimary,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

/** Horizontal rail used for "Continue Watching" / "Most Popular" sections. */
@Composable
fun ContentRow(
    items: List<CardItem>,
    onItemClick: (CardItem) -> Unit,
    emptyPlaceholder: @Composable () -> Unit
) {
    if (items.isEmpty()) {
        emptyPlaceholder()
        return
    }
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
        items(items, key = { it.id }) { item ->
            ContentCard(item = item, onClick = onItemClick)
            androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
        }
    }
}
