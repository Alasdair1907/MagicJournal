package world.thismagical.dao;
/*
  User: Alasdair
  Date: 1/11/2020
  Time: 8:44 PM                                                                                                    
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

import com.sun.istack.NotNull;
import org.hibernate.Session;
import org.hibernate.query.Query;
import world.thismagical.entity.TagEntity;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;

import java.util.List;

public class TagDao {

    public static TagEntity getTag(Long tagId, Session session){

        if (tagId == null){
            return null;
        }

        Query<TagEntity> query = session.createQuery("from TagEntity where id = :tagId", TagEntity.class);
        query.setParameter("tagId", tagId);

        return query.getSingleResult();
    }

    public static List<TagEntity> listTags(PostAttribution objectAttribution, Long objectId, Session session){

        if (objectAttribution == null || objectId == null){
            return null;
        }

        Query<TagEntity> query = session.createQuery("from TagEntity where attributionClass = :attributionClass and parentObjectId = :parentObjectId", TagEntity.class);
        query.setParameter("attributionClass", objectAttribution.getId());
        query.setParameter("parentObjectId", objectId);

        return query.getResultList();
    }

    public static List<TagEntity> listTagsForObjects(PostAttribution attribution, List<Long> objectIds, Session session){

        if (attribution == null || objectIds == null || objectIds.isEmpty()){
            return null;
        }

        Query<TagEntity> query = session.createQuery("from TagEntity where attributionClass = :attribution and parentObjectId in :objectIds", TagEntity.class);
        query.setParameter("attribution", attribution.getId());
        query.setParameter("objectIds", objectIds);

        return query.getResultList();
    }

    public static void addOrUpdateTag(TagEntity tagEntity, Session session){

        if (tagEntity == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(tagEntity);

        session.flush();
    }

    public static void deleteTag(Long tagId, Session session){

        if (tagId == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query delQuery = session.createQuery("delete from TagEntity where id= :tagId");
        delQuery.setParameter("tagId", tagId);

        delQuery.executeUpdate();
        session.flush();
    }

    public static void truncateTags(Long objectId, Short attribution, Session session){

        if (objectId == null || attribution == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query delQuery = session.createQuery("delete from TagEntity where parentObjectId= :objId and attributionClass= :attribution");
        delQuery.setParameter("objId", objectId);
        delQuery.setParameter("attribution", attribution);

        delQuery.executeUpdate();
        session.flush();
    }

    public static void setGeo(String geo, Long parentObjectId, PostAttribution postAttribution, Session session){
        boolean hasGeo = (geo != null) && ( !geo.isEmpty() );
        setGeo(hasGeo, parentObjectId, postAttribution, session);
    }

    public static void setGeo(Boolean hasGeo, Long parentObjectId, PostAttribution postAttribution, Session session){
        Short attribution = postAttribution.getId();

        if (parentObjectId == null || postAttribution == null){
            Tools.log("[ERROR] TagDao: setGeo: null argument");
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query updateQuery = session.createQuery("update TagEntity set parentHasGeo = :hasGeo where parentObjectId= :objId and attributionClass= :attribution");
        updateQuery.setParameter("hasGeo", hasGeo);
        updateQuery.setParameter("objId", parentObjectId);
        updateQuery.setParameter("attribution", attribution);

        updateQuery.executeUpdate();
        session.flush();
    }

    public static void setPublished(Boolean isPublished, Long parentObjectId, PostAttribution postAttribution, Session session){
        Short attribution = postAttribution.getId();

        if (parentObjectId == null || postAttribution == null){
            Tools.log("[ERROR] TagDao: setPublished: null argument");
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query updateQuery = session.createQuery("update TagEntity set parentIsPublished = :isPublished where parentObjectId= :objId and attributionClass= :attribution");
        updateQuery.setParameter("isPublished", isPublished);
        updateQuery.setParameter("objId", parentObjectId);
        updateQuery.setParameter("attribution", attribution);

        updateQuery.executeUpdate();
        session.flush();
    }

}
