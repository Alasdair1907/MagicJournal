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

    public static <T> Boolean checkPrivileges(AuthorEntity objectAuthorEntity, AuthorEntity currentAuthorEntity, JsonAdminResponse<T> jsonAdminResponse){

        if (currentAuthorEntity == null){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "Not authorized!";
            return false;
        }

        if (objectAuthorEntity == null){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "Internal error: object has no author!";
            return false;
        }

        if ( currentAuthorEntity.getAuthorId() != objectAuthorEntity.getAuthorId()){
            // only superuser is allowed to do that!
            if (currentAuthorEntity.getPrivilegeLevel() != PrivilegeLevel.PRIVILEGE_SUPER_USER){
                jsonAdminResponse.success = false;
                jsonAdminResponse.errorDescription = "Not enough privileges!";
                return false;
            }
        } else {
            if (currentAuthorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_TEST){
                jsonAdminResponse.success = false;
                jsonAdminResponse.errorDescription = "Test users are not allowed to modify content!";
                return false;
            }
        }

        return true;
    }
}
