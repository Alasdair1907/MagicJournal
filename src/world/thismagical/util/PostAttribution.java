package world.thismagical.util;


public enum PostAttribution {
    GALLERY(Tools.int2short(0), "Gallery"),
    PHOTO(Tools.int2short(1), "Photo"),
    ARTICLE(Tools.int2short(2), "Article");

    private Short id;
    private String readable;

    PostAttribution(Short id, String readable){
        this.id = id;
        this.readable = readable;
    }

    public Short getId(){
        return this.id;
    }

    public String getReadable() { return this.readable; }

    public static PostAttribution getPostAttribution(Short id){
        for (PostAttribution imageAttribution : PostAttribution.values()){
            if (imageAttribution.id.equals(id)){
                return imageAttribution;
            }
        }

        throw new IllegalArgumentException("unknown attribution id: "+id);
    }
}
