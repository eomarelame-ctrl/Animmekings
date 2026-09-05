package com.animekings.app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.animekings.app.ui.theme.NeonPurple
import com.animekings.app.ui.theme.TextPrimary

/** Top brand bar: "Anime KINGS" wordmark + search + notification bell (with unread dot). */
@Composable
fun AnimeKingsTopBar(
    hasUnreadNotifications: Boolean,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Anime KINGS",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSearchClick) {
            Icon(Icons.Filled.Search, contentDescription = "بحث", tint = TextPrimary)
        }
        IconButton(onClick = onNotificationsClick) {
            BadgedNotificationIcon(hasUnread = hasUnreadNotifications)
        }
    }
}

@Composable
private fun RowScope.BadgedNotificationIcon(hasUnread: Boolean) {
    Icon(
        Icons.Filled.Notifications,
        contentDescription = "الإشعارات",
        tint = if (hasUnread) NeonPurple else TextPrimary
    )
}
