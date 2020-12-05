package world.thismagical.util;

/*
User: Alasd
Date: 12/5/2020
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
public enum ImageFullness {
    FULNESS_CLIENT(8), // so far, only used on the main page
    FULNESS_ADMIN(4), // used in listings
    FULNESS_NO_IMAGES(0);

    private Integer galleriesImages;

    ImageFullness(Integer galleriesImages){
        this.galleriesImages = galleriesImages;
    }

    public Integer getGalleryImagesCount(){
        return this.galleriesImages;
    }

}
