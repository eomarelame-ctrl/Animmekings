package com.animekings.app.data.repository

import com.animekings.app.data.SupabaseClientProvider
import com.animekings.app.data.models.AppNotification
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class NotificationsRepository {
    private val db = SupabaseClientProvider.client.postgrest

    /** Returns this user's personal notifications plus any broadcast (user_id IS NULL). */
    suspend fun myNotifications(userId: String): List<AppNotification> =
        db["notifications"].select {
            filter { or { eq("user_id", userId); isNull("user_id") } }
            order("created_at", Order.DESCENDING)
        }.decodeList()
}
