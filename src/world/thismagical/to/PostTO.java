package world.thismagical.to;

import world.thismagical.util.PostAttribution;

public class PostTO {
    public Short postAttributionClass;
    public Long postObjectId;

    public BasicPostFilterTO basicPostFilterTO;

    public PostTO() { }
    public PostTO(Short postAttributionClass, Long postObjectId){
        this.postAttributionClass = postAttributionClass;
        this.postObjectId = postObjectId;
    }

    public PostAttribution getPostAttribution(){
        return PostAttribution.getPostAttribution(this.postAttributionClass);
    }
}
