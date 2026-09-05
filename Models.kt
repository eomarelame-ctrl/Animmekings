package com.animekings.app.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    val role: String = "user", // "user" | "admin" — enforced by DB check + RLS
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Anime(
    val id: String,
    val title: String,
    val slug: String? = null,
    val synopsis: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("banner_url") val bannerUrl: String? = null,
    val type: String? = null, // "anime" | "movie" | "series" grouping
    val status: String? = null,
    @SerialName("release_year") val releaseYear: Int? = null,
    @SerialName("is_published") val isPublished: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Season(
    val id: String,
    @SerialName("anime_id") val animeId: String,
    @SerialName("season_number") val seasonNumber: Int,
    val title: String? = null
)

@Serializable
data class Episode(
    val id: String,
    @SerialName("season_id") val seasonId: String,
    @SerialName("episode_number") val episodeNumber: Int,
    val title: String? = null,
    @SerialName("video_url") val videoUrl: String? = null,
    @SerialName("duration_seconds") val durationSeconds: Int? = null,
    @SerialName("thumbnail_url") val thumbnailUrl: String? = null
)

@Serializable
data class Movie(
    val id: String,
    val title: String,
    val synopsis: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("video_url") val videoUrl: String? = null,
    @SerialName("duration_seconds") val durationSeconds: Int? = null,
    @SerialName("is_published") val isPublished: Boolean = true
)

@Serializable
data class Series(
    val id: String,
    val title: String,
    val synopsis: String? = null,
    @SerialName("cover_url") val coverUrl: String? = null,
    @SerialName("is_published") val isPublished: Boolean = true
)

@Serializable
data class Favorite(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("content_type") val contentType: String, // "anime" | "movie" | "series"
    @SerialName("content_id") val contentId: String,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class WatchProgress(
    val id: String? = null,
    @SerialName("user_id") val userId: String,
    @SerialName("content_type") val contentType: String,
    @SerialName("content_id") val contentId: String,
    @SerialName("episode_id") val episodeId: String? = null,
    @SerialName("position_seconds") val positionSeconds: Int,
    @SerialName("duration_seconds") val durationSeconds: Int? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class ViewRecord(
    val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("content_type") val contentType: String,
    @SerialName("content_id") val contentId: String,
    @SerialName("viewed_at") val viewedAt: String? = null
)

@Serializable
data class DownloadItem(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("content_type") val contentType: String,
    @SerialName("content_id") val contentId: String,
    @SerialName("episode_id") val episodeId: String? = null,
    val status: String = "queued", // queued | downloading | completed | failed
    @SerialName("local_path") val localPath: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class AppNotification(
    val id: String,
    @SerialName("user_id") val userId: String? = null, // null = broadcast to all users
    val title: String,
    val body: String? = null,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)
