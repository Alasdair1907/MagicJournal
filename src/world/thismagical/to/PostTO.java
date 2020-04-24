package world.thismagical.to;

public class PostTO {
    public Short postAttributionClass;
    public Long postObjectId;
    public BasicPostFilterTO basicPostFilterTO;

    public PostTO() { }
    public PostTO(Short postAttributionClass, Long postObjectId){
        this.postAttributionClass = postAttributionClass;
        this.postObjectId = postObjectId;
    }
}
