-- ============================================================================
-- Anime KINGS — Supabase foundation schema
-- Run this in Supabase Studio > SQL Editor (or `supabase db push` with the CLI)
-- on a fresh project. Safe to re-run: guarded with IF NOT EXISTS / OR REPLACE.
-- ============================================================================

-- ---------- Extensions ----------
create extension if not exists "pgcrypto"; -- gen_random_uuid()

-- ---------- Roles enum ----------
do $$
begin
  if not exists (select 1 from pg_type where typname = 'app_role') then
    create type app_role as enum ('user', 'admin');
  end if;
end $$;

-- ============================================================================
-- TABLES
-- ============================================================================

-- One row per authenticated user, 1:1 with auth.users.
create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text,
  avatar_url text,
  role app_role not null default 'user',
  created_at timestamptz not null default now()
);

create table if not exists public.anime (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  slug text unique,
  synopsis text,
  cover_url text,
  banner_url text,
  type text default 'anime',
  status text default 'ongoing',
  release_year int,
  views_count bigint not null default 0,
  is_published boolean not null default false,
  created_by uuid references public.profiles(id),
  created_at timestamptz not null default now()
);

create table if not exists public.seasons (
  id uuid primary key default gen_random_uuid(),
  anime_id uuid not null references public.anime(id) on delete cascade,
  season_number int not null,
  title text,
  created_at timestamptz not null default now(),
  unique (anime_id, season_number)
);

create table if not exists public.episodes (
  id uuid primary key default gen_random_uuid(),
  season_id uuid not null references public.seasons(id) on delete cascade,
  episode_number int not null,
  title text,
  video_url text,
  duration_seconds int,
  thumbnail_url text,
  created_at timestamptz not null default now(),
  unique (season_id, episode_number)
);

create table if not exists public.movies (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  synopsis text,
  cover_url text,
  video_url text,
  duration_seconds int,
  views_count bigint not null default 0,
  is_published boolean not null default false,
  created_by uuid references public.profiles(id),
  created_at timestamptz not null default now()
);

create table if not exists public.series (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  synopsis text,
  cover_url text,
  views_count bigint not null default 0,
  is_published boolean not null default false,
  created_by uuid references public.profiles(id),
  created_at timestamptz not null default now()
);

create table if not exists public.favorites (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  content_type text not null check (content_type in ('anime','movie','series')),
  content_id uuid not null,
  created_at timestamptz not null default now(),
  unique (user_id, content_type, content_id)
);

create table if not exists public.watch_progress (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  content_type text not null check (content_type in ('anime','movie','series')),
  content_id uuid not null,
  episode_id uuid references public.episodes(id) on delete set null,
  position_seconds int not null default 0,
  duration_seconds int,
  updated_at timestamptz not null default now(),
  unique (user_id, content_type, content_id, episode_id)
);

create table if not exists public.views (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references public.profiles(id) on delete set null,
  content_type text not null check (content_type in ('anime','movie','series')),
  content_id uuid not null,
  viewed_at timestamptz not null default now()
);

create table if not exists public.downloads (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.profiles(id) on delete cascade,
  content_type text not null check (content_type in ('anime','movie','series')),
  content_id uuid not null,
  episode_id uuid references public.episodes(id) on delete set null,
  status text not null default 'queued' check (status in ('queued','downloading','completed','failed')),
  local_path text,
  created_at timestamptz not null default now()
);

create table if not exists public.notifications (
  id uuid primary key default gen_random_uuid(),
  user_id uuid references public.profiles(id) on delete cascade, -- null = broadcast
  title text not null,
  body text,
  is_read boolean not null default false,
  created_by uuid references public.profiles(id),
  created_at timestamptz not null default now()
);

-- ============================================================================
-- HELPER: is the calling user an admin? (used throughout RLS policies)
-- ============================================================================
create or replace function public.is_admin()
returns boolean
language sql
security definer
set search_path = public
stable
as $$
  select exists (
    select 1 from public.profiles
    where id = auth.uid() and role = 'admin'
  );
$$;

-- ============================================================================
-- AUTO-CREATE PROFILE ON SIGNUP
-- ============================================================================
create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.profiles (id, display_name, role)
  values (new.id, new.raw_user_meta_data->>'display_name', 'user')
  on conflict (id) do nothing;
  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute procedure public.handle_new_user();

-- ============================================================================
-- ROW LEVEL SECURITY
-- ============================================================================
alter table public.profiles enable row level security;
alter table public.anime enable row level security;
alter table public.seasons enable row level security;
alter table public.episodes enable row level security;
alter table public.movies enable row level security;
alter table public.series enable row level security;
alter table public.favorites enable row level security;
alter table public.watch_progress enable row level security;
alter table public.views enable row level security;
alter table public.downloads enable row level security;
alter table public.notifications enable row level security;

-- ---------- profiles ----------
drop policy if exists "profiles_select_own_or_admin" on public.profiles;
create policy "profiles_select_own_or_admin" on public.profiles
  for select using (auth.uid() = id or public.is_admin());

drop policy if exists "profiles_update_own" on public.profiles;
create policy "profiles_update_own" on public.profiles
  for update using (auth.uid() = id) with check (auth.uid() = id and role = 'user');
  -- Regular users can edit their own row but cannot self-promote to admin
  -- (role stays 'user' via the with-check). Admin role changes must be done
  -- by an existing admin via a service-role script or a dedicated admin policy.

drop policy if exists "profiles_admin_manage" on public.profiles;
create policy "profiles_admin_manage" on public.profiles
  for all using (public.is_admin()) with check (public.is_admin());

-- ---------- public catalog tables: anime / movies / series ----------
-- Anyone (including anonymous, if anon key allows) can read published content.
-- Only admins can insert/update/delete.
drop policy if exists "anime_select_published_or_admin" on public.anime;
create policy "anime_select_published_or_admin" on public.anime
  for select using (is_published = true or public.is_admin());

drop policy if exists "anime_admin_write" on public.anime;
create policy "anime_admin_write" on public.anime
  for all using (public.is_admin()) with check (public.is_admin());

drop policy if exists "movies_select_published_or_admin" on public.movies;
create policy "movies_select_published_or_admin" on public.movies
  for select using (is_published = true or public.is_admin());

drop policy if exists "movies_admin_write" on public.movies;
create policy "movies_admin_write" on public.movies
  for all using (public.is_admin()) with check (public.is_admin());

drop policy if exists "series_select_published_or_admin" on public.series;
create policy "series_select_published_or_admin" on public.series
  for select using (is_published = true or public.is_admin());

drop policy if exists "series_admin_write" on public.series;
create policy "series_admin_write" on public.series
  for all using (public.is_admin()) with check (public.is_admin());

-- ---------- seasons / episodes: readable if parent anime is published ----------
drop policy if exists "seasons_select" on public.seasons;
create policy "seasons_select" on public.seasons
  for select using (
    public.is_admin() or exists (
      select 1 from public.anime a where a.id = anime_id and a.is_published = true
    )
  );

drop policy if exists "seasons_admin_write" on public.seasons;
create policy "seasons_admin_write" on public.seasons
  for all using (public.is_admin()) with check (public.is_admin());

drop policy if exists "episodes_select" on public.episodes;
create policy "episodes_select" on public.episodes
  for select using (
    public.is_admin() or exists (
      select 1 from public.seasons s
      join public.anime a on a.id = s.anime_id
      where s.id = season_id and a.is_published = true
    )
  );

drop policy if exists "episodes_admin_write" on public.episodes;
create policy "episodes_admin_write" on public.episodes
  for all using (public.is_admin()) with check (public.is_admin());

-- ---------- user-owned tables: favorites / watch_progress / downloads ----------
drop policy if exists "favorites_owner" on public.favorites;
create policy "favorites_owner" on public.favorites
  for all using (auth.uid() = user_id or public.is_admin())
  with check (auth.uid() = user_id);

drop policy if exists "watch_progress_owner" on public.watch_progress;
create policy "watch_progress_owner" on public.watch_progress
  for all using (auth.uid() = user_id or public.is_admin())
  with check (auth.uid() = user_id);

drop policy if exists "downloads_owner" on public.downloads;
create policy "downloads_owner" on public.downloads
  for all using (auth.uid() = user_id or public.is_admin())
  with check (auth.uid() = user_id);

-- ---------- views (analytics): anyone can log a view, only admin can read all ----------
drop policy if exists "views_insert_any" on public.views;
create policy "views_insert_any" on public.views
  for insert with check (auth.uid() = user_id or user_id is null);

drop policy if exists "views_select_admin_or_own" on public.views;
create policy "views_select_admin_or_own" on public.views
  for select using (public.is_admin() or auth.uid() = user_id);

-- ---------- notifications: own + broadcast (user_id is null); admin manages ----------
drop policy if exists "notifications_select_own_or_broadcast" on public.notifications;
create policy "notifications_select_own_or_broadcast" on public.notifications
  for select using (auth.uid() = user_id or user_id is null or public.is_admin());

drop policy if exists "notifications_update_own_read_state" on public.notifications;
create policy "notifications_update_own_read_state" on public.notifications
  for update using (auth.uid() = user_id) with check (auth.uid() = user_id);

drop policy if exists "notifications_admin_write" on public.notifications;
create policy "notifications_admin_write" on public.notifications
  for insert with check (public.is_admin());

drop policy if exists "notifications_admin_delete" on public.notifications;
create policy "notifications_admin_delete" on public.notifications
  for delete using (public.is_admin());

-- ============================================================================
-- STORAGE BUCKETS (posters/banners/videos). Run once.
-- ============================================================================
insert into storage.buckets (id, name, public)
  values ('covers', 'covers', true)
  on conflict (id) do nothing;

insert into storage.buckets (id, name, public)
  values ('videos', 'videos', false)
  on conflict (id) do nothing;

-- Public can view cover images; only admins can upload/replace/delete them.
drop policy if exists "covers_public_read" on storage.objects;
create policy "covers_public_read" on storage.objects
  for select using (bucket_id = 'covers');

drop policy if exists "covers_admin_write" on storage.objects;
create policy "covers_admin_write" on storage.objects
  for all using (bucket_id = 'covers' and public.is_admin())
  with check (bucket_id = 'covers' and public.is_admin());

-- Videos are private; only admins manage them, and only signed URLs (created
-- server-side or via an admin session) should ever be handed to the app.
drop policy if exists "videos_admin_only" on storage.objects;
create policy "videos_admin_only" on storage.objects
  for all using (bucket_id = 'videos' and public.is_admin())
  with check (bucket_id = 'videos' and public.is_admin());
