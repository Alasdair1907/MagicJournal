package world.thismagical.service;
/*
  User: Alasdair
  Date: 12/15/2019
  Time: 4:30 PM                                                                                                    
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
import world.thismagical.dao.AuthorDao;
import world.thismagical.dao.SessionDao;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.SessionEntity;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class AuthorizationService {

    public static SessionEntity authorize(String login, String password, Session session){
        AuthorEntity author = checkLoginPassword(login, password, session);
        if (author == null){
            return null;
        }

        SessionEntity authorSession = SessionDao.createNewSession(author, session);

        return authorSession;
    }

    public static Boolean isSessionValid(String sessionGuid, Session session){
        List<PrivilegeLevel> nullList = null;
        return isSessionValid(sessionGuid, nullList, session);
    }

    public static Boolean isSessionValid(String sessionGuid, PrivilegeLevel privilegeLevel, Session session){
        List<PrivilegeLevel> privilegeLevelList = new ArrayList<>();
        privilegeLevelList.add(privilegeLevel);
        return isSessionValid(sessionGuid, privilegeLevelList, session);
    }

    public static Boolean isSessionValid(String sessionGuid, List<PrivilegeLevel> privilegeLevelList, Session session){
        SessionEntity sessionEntity = SessionDao.getSessionEntityByGuid(sessionGuid, session);
        if (sessionEntity == null){
            return false;
        }

        Long diff = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) - sessionEntity.getSessionStarted().toEpochSecond(ZoneOffset.UTC);
        if ( (diff/3600) > 4 ){
            return false;
        }

        if (privilegeLevelList != null){
            Short sessionPrivilegeId = sessionEntity.getPrivilegeLevel().getId();
            Boolean privilegeOk = false;

            for (PrivilegeLevel privilegeLevel : privilegeLevelList){
                if (privilegeLevel.getId().equals(sessionPrivilegeId)){
                    privilegeOk = true;
                    break;
                }
            }

            if (!privilegeOk){
                return false;
            }
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        sessionEntity.setSessionStarted(LocalDateTime.now());
        session.saveOrUpdate(sessionEntity);

        session.flush();

        return true;
    }

    /**
     * if there is user 'login' with password 'password', returns AuthorEntity object.
     * otherwise, returns null.
     */
    public static AuthorEntity checkLoginPassword(String login, String password, Session session){

        AuthorEntity author = AuthorDao.getAuthorEntityByLogin(login, session);

        if (author == null){
            return null;
        }

        String hash;
        try {
            hash = Tools.sha256(password);
        } catch (Exception e){
            Tools.log("Tools.sha256 error");
            return null;
        }

        if (!author.getPasswd().equals(hash)){
            return null;
        }

        return author;
    }


    public static AuthorEntity getAuthorEntityBySessionGuid(String sessionGuid, Session session){

        if (sessionGuid == null){
            return null;
        }

        SessionEntity sessionEntity = SessionDao.getSessionEntityByGuid(sessionGuid, session);

        if (sessionEntity == null || sessionEntity.getAuthorId() == null){
            return null;
        }

        AuthorEntity authorEntity = AuthorDao.getAuthorEntityById(sessionEntity.getAuthorId(), session);
        return authorEntity;
    }

    public static Boolean userHasGeneralWritePrivileges(AuthorEntity author){

        if (author == null){
            return false;
        }

        if (author.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_SUPER_USER){
            return true;
        }

        if (author.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_USER){
            return true;
        }

        return false;
    }

    public static Boolean checkPrivileges(AuthorEntity objectAuthorEntity, AuthorEntity currentAuthorEntity){

        if (objectAuthorEntity == null || currentAuthorEntity == null){
            return false;
        }

        // both users and superusers can remove test users' objects

        if (objectAuthorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_TEST){
            if (currentAuthorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_SUPER_USER || currentAuthorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_USER){
                return true;
            } else {
                return false;
            }
        }

        // only superuser is allowed to remove others' posts

        if ( currentAuthorEntity.getAuthorId() != objectAuthorEntity.getAuthorId()){

            if (currentAuthorEntity.getPrivilegeLevel() != PrivilegeLevel.PRIVILEGE_SUPER_USER){
                return false;
            }

        } else {
            // test users aren't allowed to make any changes whatsoever
            if (currentAuthorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_TEST){
                return false;
            }
        }

        return true;
    }
}
