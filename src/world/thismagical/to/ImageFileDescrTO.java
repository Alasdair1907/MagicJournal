package world.thismagical.to;
/*
  User: Alasdair
  Date: 2/11/2020
  Time: 4:17 PM                                                                                                    
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

public class ImageFileDescrTO {

    public ImageFileDescrTO(){

    }

    public ImageFileDescrTO(ImageFileEntity imageFileEntity){
        this.imageEntityId = imageFileEntity.getId();
        this.title = imageFileEntity.getTitle();
        this.gps = imageFileEntity.getGpsCoordinates();
        this.orderNumber = imageFileEntity.getOrderNumber();
    }

    public Long imageEntityId;
    public String title;
    public String gps;
    public Long orderNumber;
}
