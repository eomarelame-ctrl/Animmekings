# Anime KINGS — Foundation

A real (non-static) Android app foundation: Kotlin + Jetpack Compose UI wired
directly to a Supabase backend (Auth + Postgres + Storage), Arabic RTL,
dark/purple-neon theme, matching the requested reference layout.

## What's actually here

**Android app** (`app/`) — Jetpack Compose, RTL forced, dark/neon theme:
- Home: brand top bar (search + notification bell), hero banner, "متابعة المشاهدة"
  (Continue Watching), "الأكثر شعبية" (Most Popular) — both read live from Supabase
  and render an in-language empty state instead of fake placeholder data.
- Movies / Series: grid screens querying `movies` / `series` tables.
- Downloads / Favorites: query the signed-in user's own rows.
- Account: real email/password sign-up, sign-in, sign-out, and profile/role display
  via Supabase Auth — this is a working screen, not a mockup.
- Bottom navigation: Home, Movies, Series, Downloads, Favorites, Account.

**Backend** (`supabase/schema.sql`) — run once on a Supabase project:
- Tables: `profiles`, `anime`, `seasons`, `episodes`, `movies`, `series`,
  `favorites`, `watch_progress`, `views`, `downloads`, `notifications`.
- `app_role` enum (`user`/`admin`) on `profiles`, with a trigger that
  auto-creates a `profiles` row (role `user`) on every signup.
- Row Level Security on every table: public/anon can only read `is_published`
  content; users can only read/write their own `favorites`, `watch_progress`,
  `downloads`; only admins can write catalog content or send notifications.
  Users cannot self-promote to admin (enforced in the `profiles` update policy).
- Two Storage buckets: `covers` (public read, admin write) and `videos`
  (private, admin-only — the app should only ever receive short-lived signed
  URLs for video, never public links).
- `supabase/seed.sql` (optional) adds a few sample rows so Home isn't empty.

## What I could and couldn't verify in this sandbox

This sandbox has **no Android SDK/emulator, no Gradle, and no network access**,
so I could not run `gradle build`, launch an emulator, or provision a live
Supabase project from here — and no reference image was actually attached to
your message, so the design above follows your written description (dark
black/purple neon, hero banner, content cards, etc.) rather than a pixel copy.

What I did verify directly:
- Every generated `.kt`, `.sql`, and `.xml` file has balanced
  braces/parens/brackets (33 files checked, 0 issues).
- Every Kotlin file declares the correct `com.animekings.app` package.
- The schema creates all 11 requested tables, enables RLS on all 11, and
  defines 25 policies covering read/write/owner/admin cases; the two storage
  buckets are created with matching policies.

What still needs a real machine to test:
1. Open this folder in Android Studio (Koala+) — it will resolve Gradle/Compose
   automatically. That first sync is the real build test.
2. Create a Supabase project, run `supabase/schema.sql` in the SQL editor,
   then optionally `supabase/seed.sql`.
3. Put your project's URL and anon key into `gradle.properties` (or pass as
   `-PSUPABASE_URL=... -PSUPABASE_ANON_KEY=...`) — never the `service_role` key.
4. Run the app on a device/emulator, sign up a user, and confirm a matching
   row appears in `profiles` with `role = 'user'`.

## Create your first admin

New signups always get `role = 'user'` (by design — RLS blocks self-promotion).
To create an admin, run this once in the Supabase SQL editor after signing up
normally:

```sql
update public.profiles set role = 'admin' where id = 'THE-USER-UUID';
```

## Next steps (not yet built)
- Search screen, notification list/detail, episode player, actual on-device
  file download pipeline (the `downloads` table exists; there's no
  DownloadManager/WorkManager wiring yet), and an admin content-management UI.

---

## Stage 2 — Authentication & Account system

Built on top of Stage 1 without touching Home/the existing design.

**New Arabic RTL screens** (`ui/screens/`): `LoginScreen`, `RegisterScreen`,
`ForgotPasswordScreen`, `ProfileScreen` — all real Supabase Auth calls, no
mock state. `AccountScreen` is now a thin router that picks one of these
based on `AuthViewModel`'s live session state.

**`AuthViewModel`** is the single source of truth for "logged in as whom":
it collects `client.auth.sessionStatus` (a reactive stream from the Auth
plugin) so every screen updates the instant a session appears, expires, or
is signed out — nothing polls or hand-tracks a boolean.

**Security model**
- Registration sends `display_name` as user metadata only; it never sends a
  role. The existing `handle_new_user` trigger (Stage 1) creates the
  `profiles` row with `role = 'user'`, always.
- The `profiles_update_own` RLS policy (Stage 1, unchanged) lets a user edit
  their own `display_name` but its `with check (... and role = 'user')`
  clause rejects any update attempt that isn't `role = 'user'` — so even a
  tampered client request cannot self-promote to admin. Promotion is a
  manual SQL step by an existing admin (see "Create your first admin" above).
- `AuthViewModel.isAdmin` reads `profile.role` fetched under RLS — it's a
  UI convenience, not the security boundary. The real boundary is Postgres
  RLS, which is why there is currently just one harmless admin-only text
  line in `ProfileScreen` and no admin *functions* for a client-side check
  to gate — there's nothing yet for a normal user to reach even if that
  check were bypassed.
- Session persistence/refresh is handled entirely by the supabase-kt Auth
  plugin (not hand-rolled token storage).

**Testing done here vs. what needs your machine**
Same sandbox limits as Stage 1 (no Android SDK, no network) apply, so I
could not run a live signup/login/reset against a real project. Verified
in-sandbox: all 39 project files still balance syntactically, all 30 Kotlin
files declare the correct package, no leftover calls to the old `signUp`
signature, and the role-escalation guard in `schema.sql` is unchanged and
still in force. **You should manually verify on a real device/project**
before calling this phase done:
1. Register a new user → confirm a `profiles` row appears with `role='user'`.
2. Log in / log out → confirm `ProfileScreen` and `LoginScreen` swap
   correctly and the session survives an app restart.
3. Request a password reset → confirm the email arrives (check Supabase Auth
   email settings/SMTP if not).
4. Promote one user to `admin` via SQL → confirm only that account sees the
   "لوحة تحكم المسؤول" line, and that a non-admin PATCH to `profiles.role`
   via the REST API (e.g. with curl) is rejected by RLS.

**Reference image**: you attached the Home screen design with this stage's
message; per this stage's own instructions ("keep the existing Home screen
and design unchanged") I did not apply it yet. It's saved at
`assets/reference/stage1_home_reference.png` for a dedicated Home-restyle
pass whenever you're ready for it.
