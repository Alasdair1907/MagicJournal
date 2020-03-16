package world.thismagical.util;


import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.GalleryEntity;
import world.thismagical.entity.PhotoEntity;
import world.thismagical.entity.PostEntity;

public enum PostAttribution {
    GALLERY(Tools.int2short(0), "Gallery", GalleryEntity.class),
    PHOTO(Tools.int2short(1), "Photo", PhotoEntity.class),
    ARTICLE(Tools.int2short(2), "Article", ArticleEntity.class);

    private Short id;
    private String readable;
    private Class associatedClass;

    PostAttribution(Short id, String readable, Class associatedClass){
        this.id = id;
        this.readable = readable;
        this.associatedClass = associatedClass;
    }

    public Short getId(){
        return this.id;
    }

    public String getReadable() { return this.readable; }

    public Class<PostEntity> getAssociatedClass() { return this.associatedClass; }

    public static PostAttribution getPostAttribution(Short id){
        for (PostAttribution imageAttribution : PostAttribution.values()){
            if (imageAttribution.id.equals(id)){
                return imageAttribution;
            }
        }

        throw new IllegalArgumentException("unknown attribution id: "+id);
    }
}
