package world.thismagical.vo;
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
import world.thismagical.entity.AuthorEntity;
import world.thismagical.service.AuthorService;
import world.thismagical.to.PostsTO;
import world.thismagical.util.PrivilegeLevel;

public class AuthorVO {
    public Long id;
    public String displayName;
    public String login;

    public Short privilegeLevelId;
    public String privilegeLevelName;
    public String privilegeLevelDescription;

    public PostsTO postsTO;
    public boolean hasPosts;

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

        if (providePosts && session != null){
            this.postsTO = AuthorService.getAuthorPostsVOs(authorEntity.getAuthorId(), session).data;
            if ( (this.postsTO.photos != null && !this.postsTO.photos.isEmpty()) ||
                    (this.postsTO.galleries != null && !this.postsTO.galleries.isEmpty()) ||
                    (this.postsTO.articles != null && !this.postsTO.articles.isEmpty())){
                this.hasPosts = true;
            }
        }
    }
}
