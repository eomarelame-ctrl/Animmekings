package com.animekings.app

import android.app.Application

class AnimeKingsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Touch the client once at process start so Auth session restore runs early.
        com.animekings.app.data.SupabaseClientProvider.client
    }
}
