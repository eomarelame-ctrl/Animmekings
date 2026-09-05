package com.animekings.app.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.animekings.app.data.repository.AuthRepository
import com.animekings.app.data.repository.ContentRepository
import com.animekings.app.ui.components.CardItem
import kotlinx.coroutines.launch

class HomeViewModel(
    private val contentRepo: ContentRepository = ContentRepository(),
    private val authRepo: AuthRepository = AuthRepository()
) : ViewModel() {

    var mostPopular by mutableStateOf<List<CardItem>>(emptyList())
        private set
    var continueWatching by mutableStateOf<List<CardItem>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val popular = contentRepo.mostPopularAnime()
                mostPopular = popular.map { CardItem(it.id, it.title, it.coverUrl) }

                val userId = authRepo.currentUserId
                continueWatching = if (userId != null) {
                    contentRepo.continueWatching(userId).map {
                        CardItem(it.contentId, it.contentType, null)
                    }
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                // Network/backend not reachable yet (e.g. Supabase keys not configured).
                // Sections simply render empty instead of crashing — this is the
                // "connected but empty" foundation state the app should start in.
                errorMessage = e.message
                mostPopular = emptyList()
                continueWatching = emptyList()
            } finally {
                isLoading = false
            }
        }
    }
}
