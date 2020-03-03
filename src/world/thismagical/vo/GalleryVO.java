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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GalleryVO {
    public Long id;
    public String title;
    public String description;
    public AuthorVO authorVO;
    public LocalDateTime creationDate;
    public String creationDateStr;
    public String gpsCoordinates;
    public Boolean published;

    public List<ImageVO> imageVOList;
    public List<ImageVO> galleryRepresentation;

    public GalleryVO(GalleryEntity galleryEntity){
        this.id = galleryEntity.getId();
        this.title = galleryEntity.getTitle();
        this.description = galleryEntity.getDescription();
        this.authorVO = new AuthorVO(galleryEntity.getAuthor());
        this.creationDate = galleryEntity.getCreationDate();
        this.creationDateStr = galleryEntity.getCreationDate().format(DateTimeFormatter.ISO_DATE_TIME);
        this.gpsCoordinates = galleryEntity.getGpsCoordinates();
        this.published = galleryEntity.getPublished();
    }

}
