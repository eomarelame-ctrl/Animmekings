package com.animekings.app.data.repository

import com.animekings.app.data.SupabaseClientProvider
import com.animekings.app.data.models.Profile
import io.github.jan.supabase.auth.SessionStatus
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Wraps Supabase Auth. This is the ONLY place in the app allowed to talk to
 * auth.* — every other repository just reads client.auth.currentUserOrNull()
 * for the current uid and lets Postgres RLS decide what that uid may see.
 *
 * Session persistence & refresh are handled by the supabase-kt Auth plugin
 * itself (it stores the session via platform settings and auto-refreshes the
 * access token before it expires) — there is no manual token handling here,
 * which is what keeps the session "secure" rather than something hand-rolled.
 */
class AuthRepository {
    private val client = SupabaseClientProvider.client

    /** Reactive session state — collect this instead of polling currentUserOrNull(). */
    val sessionStatus: StateFlow<SessionStatus> = client.auth.sessionStatus

    val currentUserId: String? get() = client.auth.currentUserOrNull()?.id
    val currentUserEmail: String? get() = client.auth.currentUserOrNull()?.email

    suspend fun signUp(email: String, password: String, displayName: String) {
        val metadata: JsonObject = buildJsonObject { put("display_name", displayName) }
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = metadata
        }
        // A DB trigger (handle_new_user, see /supabase/schema.sql) reads
        // raw_user_meta_data->>'display_name' and creates the matching
        // profiles row with role = 'user'. Role is never sent from the client.
    }

    suspend fun signIn(email: String, password: String) {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    /** Sends a password-reset email via Supabase Auth (no custom SMTP/logic on our side). */
    suspend fun sendPasswordReset(email: String) {
        client.auth.resetPasswordForEmail(email)
    }

    suspend fun myProfile(): Profile? {
        val uid = currentUserId ?: return null
        return client.postgrest["profiles"]
            .select { filter { eq("id", uid) } }
            .decodeSingleOrNull<Profile>()
    }

    suspend fun updateDisplayName(newName: String) {
        val uid = currentUserId ?: return
        // RLS "profiles_update_own" allows this (role stays 'user' server-side,
        // enforced by the policy's with-check regardless of what's sent here).
        client.postgrest["profiles"].update(
            mapOf("display_name" to newName)
        ) {
            filter { eq("id", uid) }
        }
    }
}
