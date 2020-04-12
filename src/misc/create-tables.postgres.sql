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
order_num bigint
);

CREATE TABLE photos (
id bigserial PRIMARY KEY,
title varchar(128),
description varchar(1024),
author_id bigint,
creation_date timestamp,
gps_coordinates varchar(128),
published boolean
);

CREATE TABLE tags (
id bigserial PRIMARY KEY,
tag_name varchar(128),
attribution_class smallint,
parent_object_id bigint
);

CREATE TABLE galleries (
id bigserial PRIMARY KEY,
title varchar(128),
description varchar(1024),
author_id bigint,
creation_date timestamp,
gps_coordinates varchar(128),
published boolean
);

CREATE TABLE articles (
id bigserial PRIMARY KEY,
title varchar(128),
description varchar(1024),
title_image_id bigint,
article_text text,
author_id bigint,
creation_date timestamp,
gps_coordinates varchar(128),
published boolean
);

CREATE TABLE relations (
relation_id bigserial PRIMARY KEY,
src_attribution_class smallint,
src_object_id bigint,
dst_attribution_class smallint,
dst_object_id bigint,
relation_class smallint
);

CREATE TABLE keys_values (
key varchar(128) PRIMARY KEY,
value text
);

INSERT INTO authors (login, password, privilege_level) VALUES ('admin','5E884898DA28047151D0E56F8DC6292773603D0D6AABBDD62A11EF721D1542D8', 2);