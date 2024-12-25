package com.terrestrialjournal.vo;
/*
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


import com.terrestrialjournal.entity.PhotostoryEntity;
import com.terrestrialjournal.entity.TagEntity;
import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.util.Tools;

import java.time.LocalDateTime;
import java.util.List;

public class PhotostoryVO implements PostVO {
    public Long id;
    public Long indexId;
    public String title;
    public String description;
    public String tinyDescription;
    public AuthorVO authorVO;
    public LocalDateTime creationDate;
    public String creationDateStr;
    public LocalDateTime lastModified;
    public String gpsCoordinates;
    public Boolean published;

    public String postAttributionStr;
    public Short postAttribution;
    public Boolean isPhotostory;

    public String jsonContent;
    public ImageVO titleImageVO;

    public List<TagEntity> tagEntityList;

    public String preRender;

    public PhotostoryVO(PhotostoryEntity photostoryEntity){
        this.id = photostoryEntity.getId();
        this.indexId = photostoryEntity.getIndexId();
        this.title = photostoryEntity.getTitle();
        this.description = photostoryEntity.getDescription();
        this.tinyDescription = photostoryEntity.getTinyDescription();
        this.authorVO = new AuthorVO(photostoryEntity.getAuthor());
        this.creationDate = photostoryEntity.getCreationDate();
        this.creationDateStr = Tools.formatDate(photostoryEntity.getCreationDate());
        this.lastModified = photostoryEntity.getLastModifiedDate();
        this.gpsCoordinates = photostoryEntity.getGpsCoordinates();
        this.published = photostoryEntity.getPublished();
        this.jsonContent = photostoryEntity.getJsonContent();
        this.postAttributionStr = PostAttribution.PHOTOSTORY.getReadable();
        this.postAttribution = PostAttribution.PHOTOSTORY.getId();
        this.isPhotostory = true;
        this.preRender = photostoryEntity.getPreRender();
    }

    @Override
    public Long getId() { return id; }

    @Override
    public Long getIndexId() { return indexId; }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public AuthorVO getAuthorVO() {
        return authorVO;
    }

    @Override
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public String getPostAttributionStr() {
        return postAttributionStr;
    }

    @Override
    public void setPostAttributionStr(String postAttributionStr) {
        this.postAttributionStr = postAttributionStr;
    }

    @Override
    public List<TagEntity> getTagEntityList(){
        return this.tagEntityList;
    }

    @Override
    public Short getPostAttribution() {
        return postAttribution;
    }

    @Override
    public String getTinyDescription() {
        return tinyDescription;
    }

    @Override
    public ImageVO getMainImageVO() {
        return titleImageVO;
    }

    @Override
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public String getPreRender() {
        return preRender;
    }

    @Override
    public void setPreRender(String preRender) {
        this.preRender = preRender;
    }
}