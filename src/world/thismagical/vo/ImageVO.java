package world.thismagical.vo;
/*
  User: Alasdair
  Date: 1/3/2020
  Time: 11:52 PM                                                                                                    
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

import world.thismagical.entity.ImageFileEntity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ImageVO {
    public Long parentObjId;
    public Long thisObjId;

    public String thumbnail;
    public String preview;
    public String image;

    public String title;
    public String gpsCoordinates;
    
    public ImageVO() {
        
    }
    
    public ImageVO(ImageFileEntity imageFileEntity){
        this.parentObjId = imageFileEntity.getParentObjectId();
        this.thisObjId = imageFileEntity.getId();

        this.thumbnail = imageFileEntity.getThumbnailFileName();
        this.preview = imageFileEntity.getPreviewFileName();
        this.image = imageFileEntity.getFileName();

        this.title = imageFileEntity.getTitle();
        this.gpsCoordinates = imageFileEntity.getGpsCoordinates();
    }

    public List<String> getAllFilesList(){

        List<String> res = new ArrayList<>();

        if (this.thumbnail != null) {
            res.add(this.thumbnail);
        }

        if (this.preview != null) {
            res.add(this.preview);
        }

        if (this.image != null) {
            res.add(this.image);
        }

        return res;
    }
}
