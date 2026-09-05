package com.animekings.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.animekings.app.data.models.DownloadItem
import com.animekings.app.data.repository.AuthRepository
import com.animekings.app.data.repository.DownloadsRepository
import com.animekings.app.ui.theme.TextSecondary

@Composable
fun DownloadsScreen(
    repo: DownloadsRepository = remember { DownloadsRepository() },
    auth: AuthRepository = remember { AuthRepository() }
) {
    var items by remember { mutableStateOf<List<DownloadItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        val uid = auth.currentUserId
        if (uid != null) {
            runCatching { repo.myDownloads(uid) }.onSuccess { items = it }
        }
    }
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        if (items.isEmpty()) {
            Text(stringResource(R.string.empty_downloads), color = TextSecondary)
        } else {
            androidx.compose.foundation.lazy.LazyColumn {
                androidx.compose.foundation.lazy.items(items) { d ->
                    Text("${d.contentType} • ${d.status}", color = TextSecondary, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}
