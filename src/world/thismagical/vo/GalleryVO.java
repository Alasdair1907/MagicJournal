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

import world.thismagical.entity.GalleryEntity;
import world.thismagical.entity.PhotoEntity;
import world.thismagical.entity.TagEntity;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GalleryVO implements PostVO {
    public Long id;
    public String title;
    public String description;
    public String tinyDescription;
    public AuthorVO authorVO;
    public LocalDateTime creationDate;
    public String creationDateStr;
    public String gpsCoordinates;
    public Boolean published;
    public List<TagEntity> tagEntityList;

    public String postAttributionStr;
    public Short postAttribution;
    public Boolean isGallery;

    public List<ImageVO> imageVOList;
    public List<ImageVO> galleryRepresentation;

    public GalleryVO(GalleryEntity galleryEntity){
        this.id = galleryEntity.getId();
        this.title = galleryEntity.getTitle();
        this.description = galleryEntity.getDescription();
        this.tinyDescription = galleryEntity.getTinyDescription();
        this.authorVO = new AuthorVO(galleryEntity.getAuthor());
        this.creationDate = galleryEntity.getCreationDate();
        this.creationDateStr = Tools.formatDate(galleryEntity.getCreationDate());
        this.gpsCoordinates = galleryEntity.getGpsCoordinates();
        this.published = galleryEntity.getPublished();
        this.postAttributionStr = PostAttribution.GALLERY.getReadable();
        this.postAttribution = PostAttribution.GALLERY.getId();
        this.isGallery = true;
    }

    @Override
    public Long getId() {
        return id;
    }

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
}
