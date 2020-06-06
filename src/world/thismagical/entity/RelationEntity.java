package world.thismagical.entity;
/*
  User: Alasdair
  Date: 2/29/2020
  Time: 9:52 PM                                                                                                    
                                        `.------:::--...``.`                                        
                                    `-:+hmmoo+++dNNmo-.``/dh+...                                    
                                   .+/+mNmyo++/+hmmdo-.``.odmo -/`                                  
                                 `-//+ooooo++///////:---..``.````-``                                
                           `````.----:::/::::::::::::--------.....--..`````                         
           ```````````...............---:::-----::::---..------------------........```````          
        `:/+ooooooosssssssyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyysssssssssssssssssssssssssssoo+/:`       
          ``..-:/++ossyhhddddddddmmmmmarea51mbobmlazarmmmmmmmddddddddddddddhhyysoo+//:-..``         
                      ```..--:/+oyhddddmmmmmmmmmmmmmmmmmmmmmmmddddys+/::-..````                     
                                 ``.:oshddmmmmmNNNNNNNNNNNmmmhs+:.`                                 
                                       `.-/+oossssyysssoo+/-.`                                      
                                                                                                   
*/

import world.thismagical.to.PostTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.RelationClass;
import world.thismagical.vo.RelationVO;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "relations")
public class RelationEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="relation_id")
    private Long id;

    @Column(name="src_attribution_class")
    private Short srcAttributionClass;

    @Column(name="src_object_id")
    private Long srcObjectId;

    @Column(name="dst_attribution_class")
    private Short dstAttributionClass;

    @Column(name="dst_object_id")
    private Long dstObjectId;

    @Column(name="relation_class")
    private Short relationClass;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PostAttribution getSrcAttributionClass() {
        return PostAttribution.getPostAttribution(srcAttributionClass);
    }

    public void setSrcAttributionClass(PostAttribution postAttribution) {
        this.srcAttributionClass = postAttribution.getId();
    }

    public Long getSrcObjectId() {
        return srcObjectId;
    }

    public void setSrcObjectId(Long srcObjectId) {
        this.srcObjectId = srcObjectId;
    }

    public PostAttribution getDstAttributionClass() {
        return PostAttribution.getPostAttribution(dstAttributionClass);
    }

    public void setDstAttributionClass(PostAttribution postAttribution) {
        this.dstAttributionClass = postAttribution.getId();
    }

    public Long getDstObjectId() {
        return dstObjectId;
    }

    public void setDstObjectId(Long dstObjectId) {
        this.dstObjectId = dstObjectId;
    }

    public RelationClass getRelationClass() {
        return RelationClass.getRelationClass(this.relationClass);
    }

    public void setRelationClass(RelationClass relationClass) {
        this.relationClass = relationClass.getId();
    }

    @Transient
    public PostTO getForeignPostTO(Short thisAttribution, Long thisObjectId){
        if (this.srcAttributionClass.equals(thisAttribution) && this.srcObjectId.equals(thisObjectId)){
            return new PostTO(this.dstAttributionClass, this.getDstObjectId());
        }

        return new PostTO(this.srcAttributionClass, this.getSrcObjectId());
    }


}
