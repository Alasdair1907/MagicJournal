package world.thismagical.util;


public enum PostAttribution {
    GALLERY(Tools.int2short(0)),
    PHOTO(Tools.int2short(1)),
    ARTICLE(Tools.int2short(2));

    private Short id;

    PostAttribution(Short id){
        this.id = id;
    }

    public Short getId(){
        return this.id;
    }

    public static PostAttribution getPostAttribution(Short id){
        for (PostAttribution imageAttribution : PostAttribution.values()){
            if (imageAttribution.id.equals(id)){
                return imageAttribution;
            }
        }

        throw new IllegalArgumentException("unknown attribution id: "+id);
    }
}
