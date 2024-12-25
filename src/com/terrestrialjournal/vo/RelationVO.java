package com.terrestrialjournal.vo;
/*
  User: Alasdair
  Date: 3/4/2020

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

import com.terrestrialjournal.entity.RelationEntity;
import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.util.RelationClass;

public class RelationVO {
    public Long relationId;

    public PostAttribution srcAttributionClass;
    public String srcAttributionClassStr;
    public Short srcAttributionClassShort;
    public Long srcObjectId;
    public Long srcIndexId;
    public String srcObjectTitle;


    public PostAttribution dstAttributionClass;
    public String dstAttributionClassStr;
    public Short dstAttributionClassShort;
    public Long dstObjectId;
    public Long dstIndexId;
    public String dstObjectTitle;

    public RelationClass relationClass;
    public Short relationClassShort;
    public Boolean isAuto;

    public RelationVO(){}

    /**
     * Constructs new RelationVO from relationEntity WITHOUT FILLING IN
     * srcObjectTitle and dstObjectTitle
     * @param relationEntity entity from which this object should be created
     */
    public RelationVO(RelationEntity relationEntity){
        this.relationId = relationEntity.getId();
        
        this.srcAttributionClass = relationEntity.getSrcAttributionClass();
        this.srcAttributionClassStr = this.srcAttributionClass.getReadable();
        this.srcAttributionClassShort = this.srcAttributionClass.getId();
        this.srcObjectId = relationEntity.getSrcObjectId();
        this.srcIndexId = relationEntity.getSrcIndexId();

        this.dstAttributionClass = relationEntity.getDstAttributionClass();
        this.dstAttributionClassStr = this.dstAttributionClass.getReadable();
        this.dstAttributionClassShort = this.dstAttributionClass.getId();
        this.dstObjectId = relationEntity.getDstObjectId();
        this.dstIndexId = relationEntity.getDstIndexId();

        this.relationClass = relationEntity.getRelationClass();
        this.relationClassShort = this.relationClass.getId();
        this.isAuto = this.relationClass.getIsAuto();
    }
}
