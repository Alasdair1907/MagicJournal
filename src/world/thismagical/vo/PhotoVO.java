package world.thismagical.vo;
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

import world.thismagical.entity.PhotoEntity;
import world.thismagical.entity.TagEntity;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PhotoVO implements PostVO {
    public Long id;
    public Long indexId;
    public String title;
    public String description;
    public String tinyDescription;
    public AuthorVO authorVO;
    public LocalDateTime creationDate;
    public LocalDateTime lastModified;
    public String creationDateStr;
    public String gpsCoordinates;
    public Boolean published;
    public List<TagEntity> tagEntityList;

    public String postAttributionStr;
    public Short postAttribution;
    public Boolean isPhoto;

    public ImageVO imageVO;

    public PhotoVO(PhotoEntity photoEntity){
        this.id = photoEntity.getId();
        this.indexId = photoEntity.getIndexId();
        this.title = photoEntity.getTitle();
        this.description = photoEntity.getDescription();
        this.tinyDescription = photoEntity.getTinyDescription();
        this.authorVO = new AuthorVO(photoEntity.getAuthor());
        this.creationDate = photoEntity.getCreationDate();
        this.creationDateStr = Tools.formatDate(photoEntity.getCreationDate());
        this.lastModified = photoEntity.getLastModifiedDate();
        this.gpsCoordinates = photoEntity.getGpsCoordinates();
        this.published = photoEntity.getPublished();
        this.postAttributionStr = PostAttribution.PHOTO.getReadable();
        this.postAttribution = PostAttribution.PHOTO.getId();
        isPhoto = true;
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
    public ImageVO getMainImageVO() {
        return this.imageVO;
    }

    @Override
    public LocalDateTime getLastModified() {
        return lastModified;
    }

}
