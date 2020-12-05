package world.thismagical.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.entity.PostEntity;
import world.thismagical.entity.RelationEntity;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class RelationDao {

    /*
    lists relations, where either destination or source is object referred to by postAttribution and postId
     */
    public static List<RelationEntity> listRelationsForPost(PostAttribution postAttribution, Long postId, Session session){

        PostEntity postEntity = PostDao.getPostEntityById(postId, postAttribution.getAssociatedClass(), session);

        if (postEntity == null || postEntity.getIndexId() == null){
            Tools.log("ERROR: relations requested for the post that hasn't been found");
            return null;
        }

        return listRelationsForPost(postEntity.getIndexId(), session);
    }

    /**
     * Lists relations that involve the post identified by its index on either side of the relation
     * @param postIndexId ID of corresponding PostIndexItem
     * @param session hibernate session
     * @return list of RelationEntity objects, or null upon error
     */
    public static List<RelationEntity> listRelationsForPost(Long postIndexId, Session session){
        List<RelationEntity> relationEntityList = null;

        if (postIndexId == null){
            return null;
        }

        Query query = session.createQuery("from RelationEntity where dstIndexId = :indexId or srcIndexId = :indexId");
        query.setParameter("indexId", postIndexId);

        try {
            relationEntityList = query.getResultList();
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return relationEntityList;
    }

    public static void saveRelation(RelationEntity relationEntity, Session session){
        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(relationEntity);

        session.flush();
    }

    public static void deleteRelationsInvolvingPost(PostAttribution postAttribution, Long postId, Session session){
        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query deleteQuery = session.createQuery("delete from RelationEntity where (srcAttributionClass = :attrCl and srcObjectId = :objId) or (dstAttributionClass = :attrCl and dstObjectId = :objId)");
        deleteQuery.setParameter("attrCl", postAttribution.getId());
        deleteQuery.setParameter("objId", postId);
        deleteQuery.executeUpdate();

        session.flush();
    }

    public static void deleteRelation(Long relationId, Session session){
        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query deleteQuery = session.createQuery("delete from RelationEntity where id =: relationId");
        deleteQuery.setParameter("relationId", relationId);
        deleteQuery.executeUpdate();

        session.flush();
    }

}
