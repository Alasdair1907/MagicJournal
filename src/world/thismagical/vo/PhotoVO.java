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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PhotoVO {
    public Long id;
    public String title;
    public String description;
    public AuthorVO authorVO;
    public LocalDateTime creationDate;
    public String creationDateStr;
    public String gpsCoordinates;
    public Boolean published;

    public ImageVO imageVO;

    public PhotoVO(PhotoEntity photoEntity){
        this.id = photoEntity.getId();
        this.title = photoEntity.getTitle();
        this.description = photoEntity.getDescription();
        this.authorVO = new AuthorVO(photoEntity.getAuthor());
        this.creationDate = photoEntity.getCreationDate();
        this.creationDateStr = photoEntity.getCreationDate().format(DateTimeFormatter.ISO_DATE_TIME);
        this.gpsCoordinates = photoEntity.getGpsCoordinates();
        this.published = photoEntity.getPublished();
    }

}
