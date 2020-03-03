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
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.SessionEntity;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionDao {
    public static SessionEntity getSessionEntity(String login, Session session){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<SessionEntity> criteriaQuery = cb.createQuery(SessionEntity.class);
        Root<SessionEntity> root = criteriaQuery.from(SessionEntity.class);
        CriteriaBuilder.In<String> inClause = cb.in(root.get("login"));
        inClause.value(login);
        CriteriaQuery<SessionEntity> cq = criteriaQuery.select(root).where(inClause);
        SessionEntity authorSession;
        try {
            authorSession = session.createQuery(cq).getSingleResult();
        } catch (Exception e){
            authorSession = null;
        }

        return authorSession;
    }

    public static void updateSession(SessionEntity sessionEntity, Session session){
        session.saveOrUpdate(sessionEntity);
        session.flush();
    }

    public static SessionEntity getSessionEntityByGuid(String guid, Session session){
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaQuery<SessionEntity> criteriaQuery = cb.createQuery(SessionEntity.class);
        Root<SessionEntity> root = criteriaQuery.from(SessionEntity.class);
        CriteriaBuilder.In<String> inClause = cb.in(root.get("sessionGuid"));
        inClause.value(guid);
        CriteriaQuery<SessionEntity> cq = criteriaQuery.select(root).where(inClause);
        SessionEntity authorSession;

        try {
            authorSession = session.createQuery(cq).getSingleResult();
        } catch (Exception e){
            authorSession = null;
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
