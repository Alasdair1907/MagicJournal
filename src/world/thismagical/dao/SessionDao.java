package world.thismagical.dao;
/*
  User: Alasdair
  Date: 12/15/2019
  Time: 6:26 PM                                                                                                    
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
import org.hibernate.query.Query;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.SessionEntity;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SessionDao {

    public static List<SessionEntity> getSessionOlderThan(Integer hours, Session session){
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime = localDateTime.minusHours(hours);

        Query<SessionEntity> query = session.createQuery("from SessionEntity where sessionStarted <= :localDateTime", SessionEntity.class);
        query.setParameter("localDateTime", localDateTime);

        List<SessionEntity> sessionEntityList = null;

        try {
            sessionEntityList = query.getResultList();
        } catch (Exception ex){
            return null;
        }

        return sessionEntityList;
    }

    public static SessionEntity getSessionEntity(String login, Session session){

        if (login == null){
            return null;
        }

        Query<SessionEntity> query = session.createQuery("from SessionEntity where login = :login", SessionEntity.class);
        query.setParameter("login", login);
        SessionEntity sessionEntity = null;

        try {
            sessionEntity = query.getSingleResult();
        } catch (Exception ex){
            return null;
        }

        return sessionEntity;
    }

    public static void updateSession(SessionEntity sessionEntity, Session session){
        session.saveOrUpdate(sessionEntity);
        session.flush();
    }

    public static SessionEntity getSessionEntityByGuid(String guid, Session session){

        if (guid == null){
            return null;
        }

        Query<SessionEntity> query = session.createQuery("from SessionEntity where sessionGuid = :guid", SessionEntity.class);
        query.setParameter("guid", guid);

        SessionEntity authorSession = null;

        try {
            authorSession = query.getSingleResult();
        } catch (Exception e){
            return null;
        }

        if (authorSession != null) {
            AuthorEntity author = AuthorDao.getAuthorEntityByLogin(authorSession.getLogin(), session);
            authorSession.setAuthorId(author.getAuthorId());
            authorSession.setDisplayName(author.getDisplayName());
            authorSession.setPrivilegeLevel(author.getPrivilegeLevel());
        }

        return authorSession;
    }

    public static SessionEntity createNewSession(AuthorEntity authorEntity, Session session){

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        String login = authorEntity.getLogin();
        PrivilegeLevel privilegeLevel = authorEntity.getPrivilegeLevel();

        String guid = UUID.randomUUID().toString();

        SessionEntity sessionEntity = new SessionEntity();
        sessionEntity.setLogin(login);
        sessionEntity.setSessionGuid(guid);
        sessionEntity.setSessionStarted(LocalDateTime.now());
        sessionEntity.setPrivilegeLevel(privilegeLevel);
        sessionEntity.setDisplayName(authorEntity.getDisplayName());
        sessionEntity.setAuthorId(authorEntity.getAuthorId());

        session.saveOrUpdate(sessionEntity);
        session.flush();

        return sessionEntity;
    }

}
