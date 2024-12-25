package com.terrestrialjournal.vo;
/*
  User: Alasdair
  Date: 4/30/2020
  Time: 8:55 PM                                                                                                    
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

public class TagDigestVO {
    public String title;
    public Integer totalPosts;
    public Integer articles;
    public Integer photos;
    public Integer galleries;

    public TagDigestVO(){};
    public TagDigestVO(String title){
        this.title = title;
        this.totalPosts = 0;
        this.articles = 0;
        this.photos = 0;
        this.galleries = 0;
    }

}
