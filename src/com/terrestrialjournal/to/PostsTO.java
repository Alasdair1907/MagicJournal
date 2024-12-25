package com.terrestrialjournal.to;
/*
  User: Alasdair
  Date: 4/1/2020
  Time: 3:43 PM                                                                                                    
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

import com.terrestrialjournal.vo.ArticleVO;
import com.terrestrialjournal.vo.GalleryVO;
import com.terrestrialjournal.vo.PhotoVO;

import java.util.List;

public class PostsTO {
    public List<ArticleVO> articles;
    public List<PhotoVO> photos;
    public List<GalleryVO> galleries;

    public boolean isEmpty(){
        if (!this.articles.isEmpty()){
            return false;
        }

        if (!this.photos.isEmpty()){
            return false;
        }

        if (!this.galleries.isEmpty()){
            return false;
        }

        return true;
    }
}
