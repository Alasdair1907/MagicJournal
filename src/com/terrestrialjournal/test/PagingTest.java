package com.terrestrialjournal.test;
/*
  User: Alasdair
  Date: 5/15/2020
  Time: 3:53 AM                                                                                                    
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

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.terrestrialjournal.filter.PagingRequestFilter;
import com.terrestrialjournal.service.PagingService;
import com.terrestrialjournal.util.Tools;
import com.terrestrialjournal.vo.PostVOList;

import java.util.Arrays;

public class PagingTest {
    public static void test(SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){

            PagingRequestFilter pagingRequestFilter = new PagingRequestFilter();
            pagingRequestFilter.page = 0;
            //pagingRequestFilter.authorLogin = "admin";
            pagingRequestFilter.needArticles = true;
            pagingRequestFilter.needGalleries = true;
            pagingRequestFilter.needPhotos = true;
            pagingRequestFilter.tags = Arrays.asList("idk", "wtf");

            PostVOList postVOList = PagingService.get(pagingRequestFilter, session);

            Integer breakMe = postVOList.currentPage;

        } catch (Exception ex){
            Tools.handleException(ex);
        }
    }
}
