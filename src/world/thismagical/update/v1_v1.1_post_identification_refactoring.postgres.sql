alter table articles add column index_id bigint;
alter table photos add column index_id bigint;
alter table galleries add column index_id bigint;

update articles set index_id = i.id from posts_index i where i.post_id = articles.id and i.post_attribution = 2;
update photos set index_id = i.id from posts_index i where i.post_id = photos.id and i.post_attribution = 1;
update galleries set index_id = i.id from posts_index i where i.post_id = galleries.id and i.post_attribution = 0;

alter table relations add column src_index_id bigint, add column dst_index_id bigint;

update relations set src_index_id = posts_index.id from posts_index where posts_index.post_id = relations.src_object_id and posts_index.post_attribution = relations.src_attribution_class;
update relations set dst_index_id = posts_index.id from posts_index where posts_index.post_id = relations.dst_object_id and posts_index.post_attribution = relations.dst_attribution_class;