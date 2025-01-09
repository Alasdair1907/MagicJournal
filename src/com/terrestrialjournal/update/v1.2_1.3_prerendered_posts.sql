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
      prerender text
);

ALTER TABLE articles ADD COLUMN prerender text;
ALTER TABLE photos ADD COLUMN prerender text;
ALTER TABLE galleries ADD COLUMN prerender text;

ALTER TABLE articles ADD COLUMN se_description varchar(255);
ALTER TABLE photos ADD COLUMN se_description varchar(255);
ALTER TABLE galleries ADD COLUMN se_description varchar(255);
ALTER TABLE photostories ADD COLUMN se_description varchar(255);