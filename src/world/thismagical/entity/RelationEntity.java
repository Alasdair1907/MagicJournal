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

import world.thismagical.util.PostAttribution;

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
        return PostAttribution.getImageAttribution(srcAttributionClass);
    }

    public void setSrcAttributionClass(Short srcAttributionClass) {
        this.srcAttributionClass = srcAttributionClass;
    }

    public Long getSrcObjectId() {
        return srcObjectId;
    }

    public void setSrcObjectId(Long srcObjectId) {
        this.srcObjectId = srcObjectId;
    }

    public Short getDstAttributionClass() {
        return dstAttributionClass;
    }

    public void setDstAttributionClass(Short dstAttributionClass) {
        this.dstAttributionClass = dstAttributionClass;
    }

    public Long getDstObjectId() {
        return dstObjectId;
    }

    public void setDstObjectId(Long dstObjectId) {
        this.dstObjectId = dstObjectId;
    }

    public Short getRelationClass() {
        return relationClass;
    }

    public void setRelationClass(Short relationClass) {
        this.relationClass = relationClass;
    }
}
