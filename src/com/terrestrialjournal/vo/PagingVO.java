package com.terrestrialjournal.vo;
/*
  User: Alasdair
  Date: 5/13/2020
  Time: 7:39 PM
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

import java.util.ArrayList;
import java.util.List;

public class PagingVO {

    public PostAttribution attribution;
    public Boolean isPopulated;
    public List<Long> ids;
    public List<Object> page;

    public PagingVO(){}
    public PagingVO(PostAttribution postAttribution){

        attribution = postAttribution;
        isPopulated = false;
        ids = new ArrayList<>();
        page = new ArrayList<>();
    }
}
