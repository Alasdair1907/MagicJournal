CREATE TABLE authors (
author_id bigserial PRIMARY KEY,
display_name varchar(128),
login varchar(128),
password varchar(128),
privilege_level smallint,
bio varchar(1024),
email varchar(256),
personal_website varchar(256)
);

CREATE TABLE sessions (
login varchar(128) PRIMARY KEY,
session_guid varchar(128),
session_started timestamp,
privilege_level smallint
);

CREATE TABLE image_files (
id bigserial PRIMARY KEY,
file_name varchar(128),
thumbnail_file_name varchar(128),
preview_file_name varchar(128),
original_file_name varchar(256),
title varchar(128),
gps_coordinates varchar(128),
image_attribution_class smallint,
parent_object_id bigint,
order_num bigint,
aspect_ratio float8
);

CREATE TABLE photos (
id bigserial PRIMARY KEY,
index_id bigint,
title varchar(128),
description varchar(1920),
tiny_description varchar(256),
author_id bigint,
creation_date timestamp,
gps_coordinates varchar(128),
published boolean,
prerender text,
se_description varchar(255)
);

CREATE TABLE tags (
id bigserial PRIMARY KEY,
tag_name varchar(128),
attribution_class smallint,
parent_object_id bigint,
post_index_item_id bigint,
parent_has_geo boolean,
parent_is_published boolean
);

CREATE TABLE galleries (
id bigserial PRIMARY KEY,
index_id bigint,
title varchar(128),
description varchar(1920),
tiny_description varchar(256),
author_id bigint,
creation_date timestamp,
gps_coordinates varchar(128),
published boolean,
prerender text,
se_description varchar(255)
);

CREATE TABLE articles (
id bigserial PRIMARY KEY,
index_id bigint,
title varchar(128),
description varchar(1024),
tiny_description varchar(256),
title_image_id bigint,
article_text text,
author_id bigint,
creation_date timestamp,
gps_coordinates varchar(128),
published boolean,
prerender text,
se_description varchar(255)
);

CREATE TABLE relations (
relation_id bigserial PRIMARY KEY,
src_attribution_class smallint,
src_object_id bigint,
src_index_id bigint,
dst_attribution_class smallint,
dst_object_id bigint,
dst_index_id bigint,
relation_class smallint
);

CREATE TABLE keys_values (
key varchar(128) PRIMARY KEY,
value text
);

CREATE TABLE other_files (
id bigserial PRIMARY KEY,
author_id bigint,
original_file_name varchar(256),
file_name varchar(256),
description varchar(256),
display_name varchar(256)
);

CREATE TABLE posts_index (
id bigserial PRIMARY KEY,
post_attribution smallint,
post_id bigint,
author_login varchar(128),
creation_date timestamp,
has_geo boolean
);

CREATE TABLE photostories (
id bigserial PRIMARY KEY,
index_id bigint,
title varchar(128),
description varchar(1024),
tiny_description varchar(256),
title_image_id bigint,
json_content text,
author_id bigint,
creation_date timestamp,
gps_coordinates varchar(128),
published boolean,
prerender text,
se_description varchar(255)
);

INSERT INTO authors (login, password, privilege_level) VALUES ('admin','5E884898DA28047151D0E56F8DC6292773603D0D6AABBDD62A11EF721D1542D8', 2);