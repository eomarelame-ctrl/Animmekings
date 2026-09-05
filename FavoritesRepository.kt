package com.animekings.app.data.repository

import com.animekings.app.data.SupabaseClientProvider
import com.animekings.app.data.models.Favorite
import io.github.jan.supabase.postgrest.postgrest

class FavoritesRepository {
    private val db = SupabaseClientProvider.client.postgrest

    suspend fun myFavorites(userId: String): List<Favorite> =
        db["favorites"].select { filter { eq("user_id", userId) } }.decodeList()

    suspend fun add(userId: String, contentType: String, contentId: String) {
        db["favorites"].insert(
            mapOf(
                "user_id" to userId,
                "content_type" to contentType,
                "content_id" to contentId
            )
        )
    }

    suspend fun remove(userId: String, contentId: String) {
        db["favorites"].delete {
            filter {
                eq("user_id", userId)
                eq("content_id", contentId)
            }
        }
    }
}
