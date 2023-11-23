package world.thismagical.service;
/*
  User: Alasdair
  Date: 12/15/2019

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
import world.thismagical.entity.PostEntity;
import world.thismagical.entity.SessionEntity;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.SettingsTO;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.AuthorizedVO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class AuthorizationService {

    public static final String ANONYMOUS_DEMO_PREFIX = "demo_";

    public static Boolean verifyPasswordStrength(String filesLocation, String password) throws Exception {

        if (password == null){
            return Boolean.FALSE;
        }

        if (password.length() < 7){
            return Boolean.FALSE;
        }

        //https://github.com/danielmiessler/SecLists
        Path path = Paths.get(filesLocation, "resources", "top-million-pwds.txt");
        File pwdFile = path.toFile();

        BufferedReader br = new BufferedReader(new FileReader(pwdFile));
        String st;
        boolean matched = false;
        while ((st = br.readLine()) != null){
            if (st.equals(password)){
                matched = true;
                break;
            }
        }

        br.close();

        return !matched;
    }

    public static JsonAdminResponse<AuthorizedVO> authorize(String login, String password, Session session){
        AuthorEntity author = checkLoginPassword(login, password, session);
        if (author == null){
            return JsonAdminResponse.fail("can not authorize user!");
        }

        SessionEntity authorSession = SessionDao.createNewSession(author, session);

        AuthorizedVO authorizedVO = new AuthorizedVO();
        authorizedVO.guid = authorSession.getSessionGuid();
        authorizedVO.privilegeLevelName = authorSession.getPrivilegeLevel().getName();
        authorizedVO.displayName = authorSession.getDisplayName();
        authorizedVO.authorId = authorSession.getAuthorId();
        authorizedVO.login = authorSession.getLogin();

        truncateOutdatedDemos(session);

        return JsonAdminResponse.success(authorizedVO);
    }

    public static void truncateOutdatedDemos(Session session){
        List<SessionEntity> outdatedSessions = SessionDao.getSessionOlderThan(4, session);

        if (outdatedSessions == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        for (SessionEntity sessionEntity : outdatedSessions){
            if (sessionEntity.getLogin().startsWith(ANONYMOUS_DEMO_PREFIX)){
                AuthorEntity demoAuthorEntity = AuthorDao.getAuthorEntityByLogin(sessionEntity.getLogin(), session);
                session.delete(sessionEntity);
                session.delete(demoAuthorEntity);
            }
        }

        session.flush();
    }

    public static JsonAdminResponse<AuthorizedVO> authorizeDemo(Session session){
        SettingsTO settingsTO = SettingsService.getSettings(session);

        if (!Boolean.TRUE.equals(settingsTO.allowDemoAnon)){
            return JsonAdminResponse.fail("Anonymous demo login has been disabled by the admin.");
        }

        String rand = UUID.randomUUID().toString().substring(0,5);
        String name = ANONYMOUS_DEMO_PREFIX + rand;

        AuthorEntity authorEntity = new AuthorEntity();
        authorEntity.setDisplayName(name);
        authorEntity.setLogin(name);
        authorEntity.setPasswd(Tools.sha256(rand));
        authorEntity.setPrivilegeLevel(PrivilegeLevel.PRIVILEGE_DEMO);

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.save(authorEntity);
        session.flush();

        return authorize(authorEntity.getLogin(), rand, session);
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

        if (!isSessionValid(sessionGuid, session)){
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

        // both users and superusers can change demo users' objects

        if (objectAuthorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_DEMO){
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
            // demo users aren't allowed to make any changes whatsoever
            if (currentAuthorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_DEMO){
                return false;
            }
        }

        return true;
    }

    public static void checkUnpublishedViewPrivileges(PostEntity postEntity, String userGuid, Session session){
        if (!Boolean.TRUE.equals(postEntity.getPublished())){
            if (!Boolean.TRUE.equals(AuthorizationService.isSessionValid(userGuid, session))){
                throw new RuntimeException("User not authorized to view unpublished articles");
            }
        }
    }
}
