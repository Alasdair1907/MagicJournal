package com.terrestrialjournal.entity;
/*
  User: Alasdair
  Date: 3/16/2020
  Time: 9:09 PM                                                                                                    
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

import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.vo.PostVO;

import java.time.LocalDateTime;

public interface PostEntity {
    Long getId();
    void setId(Long id);

    String getTitle();
    void setTitle(String title);

    String getDescription();
    void setDescription(String description);

    String getSEDescription();
    void setSEDescription(String seDescription);

    String getTinyDescription();
    void setTinyDescription(String tinyDescription);

    AuthorEntity getAuthor();
    void setAuthor(AuthorEntity authorEntity);

    LocalDateTime getCreationDate();
    void setCreationDate(LocalDateTime creationDate);

    String getGpsCoordinates();
    void setGpsCoordinates(String gpsCoordinates);

    Boolean getPublished();
    void setPublished(Boolean published);

    LocalDateTime getLastModifiedDate();
    void setLastModifiedDate(LocalDateTime lastModifiedDate);

    Long getIndexId();
    void setIndexId(Long id);

    PostAttribution getPostAttribution();

    PostVO toBaseVO();

    String getPreRender();

    void setPreRender(String preRender);
}
