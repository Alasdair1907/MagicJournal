package com.terrestrialjournal.util;


import com.terrestrialjournal.entity.*;
import com.terrestrialjournal.vo.ArticleVO;
import com.terrestrialjournal.vo.GalleryVO;
import com.terrestrialjournal.vo.PhotoVO;
import com.terrestrialjournal.vo.PhotostoryVO;

public enum PostAttribution {
    GALLERY(Tools.int2short(0), "Gallery", GalleryEntity.class, GalleryVO.class, true),
    PHOTO(Tools.int2short(1), "Photo", PhotoEntity.class, PhotoVO.class, true),
    ARTICLE(Tools.int2short(2), "Article", ArticleEntity.class, ArticleVO.class, true),
    PROFILE(Tools.int2short(3), "Author Profile", null, null, false),
    PHOTOSTORY(Tools.int2short(4), "Collage", PhotostoryEntity.class, PhotostoryVO.class, true);

    private Short id;
    private String readable;
    private Class associatedClass;
    private Class voClass;

    private Boolean preRenderable;

    PostAttribution(Short id, String readable, Class associatedClass, Class voClass, Boolean preRenderable){
        this.id = id;
        this.readable = readable;
        this.associatedClass = associatedClass;
        this.voClass = voClass;
        this.preRenderable = preRenderable;
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

    public Boolean isPreRenderable(){
        return this.preRenderable;
    }
}
