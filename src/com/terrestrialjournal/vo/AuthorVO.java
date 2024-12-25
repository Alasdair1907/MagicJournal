package com.terrestrialjournal.vo;
/*
  User: Alasdair
  Date: 12/15/2019
  Time: 12:53 AM                                                                                                    
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
import com.terrestrialjournal.entity.AuthorEntity;
import com.terrestrialjournal.service.AuthorService;
import com.terrestrialjournal.to.PostsTO;
import com.terrestrialjournal.util.PrivilegeLevel;

public class AuthorVO {
    public Long id;
    public String displayName;
    public String login;

    public Short privilegeLevelId;
    public String privilegeLevelName;
    public String privilegeLevelDescription;

    public String pictureFileName;
    public String bio;
    public String email;
    public String website;

    public PostsTO postsTO;
    public boolean hasPosts;

    public AuthorVO(){}

    public AuthorVO(AuthorEntity authorEntity){
        this(authorEntity, false, null);
    }

    /**
     * @param authorEntity AuthorEntity to be converted into AuthorVO
     * @param providePosts true if all posts of this author must be attached to AuthorVO, otherwise false
     * @param session required if providePosts is true, otherwise null
     */
    public AuthorVO(AuthorEntity authorEntity, Boolean providePosts, Session session){

        this.id = authorEntity.getAuthorId();
        this.displayName = authorEntity.getDisplayName();
        this.login = authorEntity.getLogin();

        PrivilegeLevel privilegeLevel = authorEntity.getPrivilegeLevel();
        this.privilegeLevelId = privilegeLevel.getId();
        this.privilegeLevelName = privilegeLevel.getName();
        this.privilegeLevelDescription = privilegeLevel.getDescription();

        this.bio = authorEntity.getBio();
        this.email = authorEntity.getEmail();
        this.website = authorEntity.getPersonalWebsite();


        if (providePosts && session != null){
            this.postsTO = AuthorService.getAuthorPostsVOs(authorEntity.getAuthorId(), session).data;
            if (this.postsTO.isEmpty()){
                this.hasPosts = false;
            } else {
                this.hasPosts = true;
            }
        }
    }
}
