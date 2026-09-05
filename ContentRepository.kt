package com.animekings.app.data.repository

import com.animekings.app.data.SupabaseClientProvider
import com.animekings.app.data.models.Anime
import com.animekings.app.data.models.Movie
import com.animekings.app.data.models.Series
import com.animekings.app.data.models.WatchProgress
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

/**
 * Reads published catalog content. RLS on the server already restricts
 * writes to admins and reads to is_published = true for anonymous/basic
 * users, so these queries are safe to call from any signed-in or guest state.
 */
class ContentRepository {
    private val db = SupabaseClientProvider.client.postgrest

    suspend fun mostPopularAnime(limit: Long = 10): List<Anime> =
        db["anime"].select {
            filter { eq("is_published", true) }
            order("views_count", Order.DESCENDING)
            limit(limit)
        }.decodeList()

    suspend fun movies(limit: Long = 20): List<Movie> =
        db["movies"].select {
            filter { eq("is_published", true) }
            limit(limit)
        }.decodeList()

    suspend fun series(limit: Long = 20): List<Series> =
        db["series"].select {
            filter { eq("is_published", true) }
            limit(limit)
        }.decodeList()

    /** Continue Watching: this user's in-progress items, most recent first. */
    suspend fun continueWatching(userId: String, limit: Long = 10): List<WatchProgress> =
        db["watch_progress"].select {
            filter { eq("user_id", userId) }
            order("updated_at", Order.DESCENDING)
            limit(limit)
        }.decodeList()
}
