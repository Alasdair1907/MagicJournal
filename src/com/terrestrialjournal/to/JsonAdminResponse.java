package com.terrestrialjournal.to;
/*
  User: Alasdair
  Date: 12/25/2019
  Time: 6:01 PM                                                                                                    
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

public class JsonAdminResponse<T> {
    public Boolean success;
    public String errorDescription;
    public T data;

    public static <I> JsonAdminResponse<I> fail(String errorDescription){
        JsonAdminResponse<I> res = new JsonAdminResponse<>();
        res.success = false;
        res.errorDescription = errorDescription;
        return res;
    }

    public static <I> JsonAdminResponse<I> success(I data){
        JsonAdminResponse<I> res = new JsonAdminResponse<>();
        res.success = true;
        res.data = data;
        return res;
    }
}