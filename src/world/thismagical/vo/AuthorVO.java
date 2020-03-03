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

import world.thismagical.entity.AuthorEntity;
import world.thismagical.util.PrivilegeLevel;

public class AuthorVO {
    public Long id;
    public String displayName;
    public String login;

    public Short privilegeLevelId;
    public String privilegeLevelName;
    public String privilegeLevelDescription;

    public AuthorVO(AuthorEntity authorEntity){
        this.id = authorEntity.getAuthorId();
        this.displayName = authorEntity.getDisplayName();
        this.login = authorEntity.getLogin();

        PrivilegeLevel privilegeLevel = authorEntity.getPrivilegeLevel();
        this.privilegeLevelId = privilegeLevel.getId();
        this.privilegeLevelName = privilegeLevel.getName();
        this.privilegeLevelDescription = privilegeLevel.getDescription();
    }
}
