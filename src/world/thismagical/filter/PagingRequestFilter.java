package world.thismagical.filter;
/*
  User: Alasdair
  Date: 5/13/2020
  Time: 8:14 PM                                                                                                    
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

import java.util.List;

public class PagingRequestFilter {
    public Boolean needArticles;
    public Boolean needPhotos;
    public Boolean needGalleries;

    public Integer page;

    public List<String> tags;
    public String authorLogin;

    public Integer itemsPerPage;

    public static PagingRequestFilter latest(Integer count){
        PagingRequestFilter pagingRequestFilter = new PagingRequestFilter();
        pagingRequestFilter.needArticles = true;
        pagingRequestFilter.needPhotos = true;
        pagingRequestFilter.needGalleries = true;
        pagingRequestFilter.itemsPerPage = count;
        return pagingRequestFilter;
    }
}
