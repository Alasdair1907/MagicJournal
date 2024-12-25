package com.terrestrialjournal.vo;
/*
  User: Alasdair
  Date: 4/13/2020
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

import com.terrestrialjournal.entity.OtherFileEntity;

public class OtherFileVO {
    public Long fileId;
    public AuthorVO authorVO;

    public String originalFileName;
    public String fileName;

    public String displayName;
    public String description;

    public OtherFileVO(){};
    public OtherFileVO(OtherFileEntity otherFileEntity){
        fileId = otherFileEntity.getId();
        authorVO = new AuthorVO(otherFileEntity.getAuthorEntity());
        originalFileName = otherFileEntity.getOriginalFileName();
        fileName = otherFileEntity.getFileName();
        displayName = otherFileEntity.getDisplayName();
        description = otherFileEntity.getDescription();
    }
}
