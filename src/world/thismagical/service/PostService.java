package world.thismagical.service;
/*
  User: Alasdair
  Date: 7/15/2020
  Time: 11:34 PM                                                                                                    
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
import world.thismagical.dao.PostDao;
import world.thismagical.dao.TagDao;
import world.thismagical.entity.PostEntity;
import world.thismagical.util.PostAttribution;

public class PostService {
    public static void updateTagPublish(Long postId, PostAttribution postAttribution, Session session){
        PostEntity postEntity = PostDao.getPostEntityById(postId, postAttribution.getAssociatedClass(), session);
        TagDao.setPublished(postEntity.getPublished(), postId, postAttribution, session);
    }
}
