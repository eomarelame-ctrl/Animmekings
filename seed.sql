-- Optional sample data so the Home screen isn't empty during development.
-- Safe to skip in production. Requires an admin profile to already exist
-- (promote yourself first, see README "Create your first admin").

insert into public.anime (title, synopsis, cover_url, banner_url, is_published, views_count)
values
  ('كينج أوف الأنمي', 'قصة ملحمية عن بطل يسعى ليصبح ملك الأنمي.', null, null, true, 1500),
  ('ظل التنين', 'مغامرة في عالم مليء بالتنانين والأسرار.', null, null, true, 980)
on conflict do nothing;

insert into public.movies (title, synopsis, is_published, views_count)
values ('فيلم الأبطال', 'فيلم أنمي طويل مليء بالحركة.', true, 500)
on conflict do nothing;

insert into public.series (title, synopsis, is_published, views_count)
values ('مسلسل الأساطير', 'مسلسل متعدد المواسم عن أبطال أسطوريين.', true, 700)
on conflict do nothing;
