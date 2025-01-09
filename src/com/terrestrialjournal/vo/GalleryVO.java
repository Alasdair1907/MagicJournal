package com.terrestrialjournal.vo;
/*
  User: Alasdair
  Date: 1/3/2020
  Time: 11:34 PM                                                                                                    
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

import com.terrestrialjournal.entity.GalleryEntity;
import com.terrestrialjournal.entity.TagEntity;
import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.util.Tools;

import java.time.LocalDateTime;
import java.util.List;

public class GalleryVO implements PostVO {
    public Long id;
    public Long indexId;
    public String title;
    public String description;
    public String seDescription;
    public String tinyDescription;
    public AuthorVO authorVO;
    public LocalDateTime creationDate;
    public String creationDateStr;
    public LocalDateTime lastModified;
    public String gpsCoordinates;
    public Boolean published;
    public List<TagEntity> tagEntityList;

    public String postAttributionStr;
    public Short postAttribution;
    public Boolean isGallery;

    public List<ImageVO> imageVOList;
    public List<ImageVO> galleryRepresentation;

    public String preRender;

    public GalleryVO(GalleryEntity galleryEntity){
        this.id = galleryEntity.getId();
        this.indexId = galleryEntity.getIndexId();
        this.title = galleryEntity.getTitle();
        this.description = galleryEntity.getDescription();
        this.tinyDescription = galleryEntity.getTinyDescription();
        this.authorVO = new AuthorVO(galleryEntity.getAuthor());
        this.creationDate = galleryEntity.getCreationDate();
        this.creationDateStr = Tools.formatDate(galleryEntity.getCreationDate());
        this.lastModified = galleryEntity.getLastModifiedDate();
        this.gpsCoordinates = galleryEntity.getGpsCoordinates();
        this.published = galleryEntity.getPublished();
        this.postAttributionStr = PostAttribution.GALLERY.getReadable();
        this.postAttribution = PostAttribution.GALLERY.getId();
        this.isGallery = true;
        this.preRender = galleryEntity.getPreRender();
        this.seDescription = galleryEntity.getSEDescription();
    }

    @Override
    public Long getId() {
        return id;
    }

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
    public ImageVO getMainImageVO(){
        if (this.imageVOList != null && this.imageVOList.size() > 0){
            return imageVOList.get(0);
        }
        return null;
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

    public String getSEDescription() {
        return seDescription;
    }

    public void setSEDescription(String seDescription) {
        this.seDescription = seDescription;
    }

}
