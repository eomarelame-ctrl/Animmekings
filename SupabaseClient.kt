package com.animekings.app.data

import com.animekings.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

/**
 * Single Supabase client for the whole app.
 *
 * Only the ANON (public) key ever ships in the app. All access control is
 * enforced server-side by Postgres Row Level Security (see /supabase/schema.sql).
 * The service_role key must never be embedded in client code.
 */
object SupabaseClientProvider {

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}
