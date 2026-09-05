package com.animekings.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.item
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.animekings.app.R
import com.animekings.app.ui.components.AnimeKingsTopBar
import com.animekings.app.ui.components.ContentRow
import com.animekings.app.ui.components.HeroBanner
import com.animekings.app.ui.components.SectionHeader
import com.animekings.app.ui.theme.BgBlack
import com.animekings.app.ui.theme.TextSecondary

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            AnimeKingsTopBar(
                hasUnreadNotifications = false,
                onSearchClick = { /* TODO: navigate to search */ },
                onNotificationsClick = { /* TODO: navigate to notifications */ }
            )
        }
        item {
            HeroBanner(
                title = viewModel.mostPopular.firstOrNull()?.title ?: "Anime KINGS",
                subtitle = "شاهد أحدث الحلقات والأفلام أولاً بأول",
                bannerUrl = viewModel.mostPopular.firstOrNull()?.coverUrl,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { SectionHeader(stringResource(R.string.continue_watching)) }
        item {
            ContentRow(
                items = viewModel.continueWatching,
                onItemClick = { /* TODO: open player */ }
            ) {
                EmptyRowHint(stringResource(R.string.empty_continue_watching))
            }
        }
        item { SectionHeader(stringResource(R.string.most_popular)) }
        item {
            ContentRow(
                items = viewModel.mostPopular,
                onItemClick = { /* TODO: open details */ }
            ) {
                EmptyRowHint("سيتم عرض المحتوى هنا فور توفره في قاعدة البيانات")
            }
        }
        item { androidx.compose.foundation.layout.Spacer(Modifier.padding(24.dp)) }
    }
}

@Composable
private fun EmptyRowHint(text: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(text = text, color = TextSecondary)
    }
}
