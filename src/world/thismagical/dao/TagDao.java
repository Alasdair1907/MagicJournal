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
import world.thismagical.entity.TagEntity;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TagDao {

    public static TagEntity getTag(Long tagId, Session session){

        if (tagId == null){
            return null;
        }

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<TagEntity> cq = cb.createQuery(TagEntity.class);
        Root<TagEntity> root = cq.from(TagEntity.class);

        Predicate eqTagId = cb.equal(root.get("id"), tagId);

        cq.select(root).where(eqTagId);

        TagEntity tagEntity = null;
        try {
            tagEntity = session.createQuery(cq).getSingleResult();
        } catch (Exception e){
            Tools.log("[WARN] getTag("+tagId+") error: "+e.getMessage());
        }

        return tagEntity;
    }

    public static List<TagEntity> listTags(PostAttribution objectAttribution, Long objectId, Session session){

        if (objectAttribution == null || objectId == null){
            return null;
        }

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<TagEntity> cq = cb.createQuery(TagEntity.class);
        Root<TagEntity> root = cq.from(TagEntity.class);

        Predicate eqAttribution = cb.equal(root.get("attributionClass"), objectAttribution.getId());
        Predicate eqObjectId = cb.equal(root.get("parentObjectId"), objectId);
        Predicate and = cb.and(eqAttribution, eqObjectId);

        cq.select(root).where(and);

        List<TagEntity> tagEntityList = new ArrayList<>();

        try {
            tagEntityList = session.createQuery(cq).list();
        } catch (Exception e){
            Tools.log("[WARN] listTags exception: "+e.getMessage());
        }

        return tagEntityList;
    }

    public static void addOrUpdateTag(@NotNull TagEntity tagEntity, Session session){

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(tagEntity);

        session.flush();
    }

    public static void deleteTag(@NotNull Long tagId, Session session){

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query delQuery = session.createQuery("delete from TagEntity where id= :tagId");
        delQuery.setParameter("tagId", tagId);

        delQuery.executeUpdate();
        session.flush();
    }

    public static void truncateTags(@NotNull Long objectId, @NotNull Short attribution, Session session){
        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query delQuery = session.createQuery("delete from TagEntity where parentObjectId= :objId and attributionClass= :attribution");
        delQuery.setParameter("objId", objectId);
        delQuery.setParameter("attribution", attribution);

        delQuery.executeUpdate();
        session.flush();
    }

}
