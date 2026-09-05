package com.animekings.app.data.repository

import com.animekings.app.data.SupabaseClientProvider
import com.animekings.app.data.models.DownloadItem
import io.github.jan.supabase.postgrest.postgrest

class DownloadsRepository {
    private val db = SupabaseClientProvider.client.postgrest

    suspend fun myDownloads(userId: String): List<DownloadItem> =
        db["downloads"].select { filter { eq("user_id", userId) } }.decodeList()
}
