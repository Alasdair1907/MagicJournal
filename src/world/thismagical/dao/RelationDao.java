package world.thismagical.dao;

import org.hibernate.Session;
import world.thismagical.entity.RelationEntity;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class RelationDao {
    public static List<RelationEntity> listRelationsForPost(PostAttribution postAttribution, Long postId, Session session){
        if (postAttribution == null || postId == null){
            throw new IllegalArgumentException("listRelations: null argument");
        }

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<RelationEntity> cq = cb.createQuery(RelationEntity.class);
        Root<RelationEntity> root = cq.from(RelationEntity.class);

        Predicate postAttributionSrcEqual = cb.equal(root.get("srcAttributionClass"), postAttribution.getId());
        Predicate postIdSrcEqual = cb.equal(root.get("srcObjectId"), postId);
        Predicate src = cb.and(postAttributionSrcEqual, postIdSrcEqual);

        Predicate postAttributionDstEqual = cb.equal(root.get("dstAttributionClass"), postAttribution.getId());
        Predicate postIdDstEqual = cb.equal(root.get("dstObjectId"), postId);
        Predicate dst = cb.and(postAttributionDstEqual, postIdDstEqual);

        Predicate srcOrDst = cb.or(src, dst);

        cq.select(root).where(srcOrDst);

        List<RelationEntity> relationEntityList;
        try {
            relationEntityList =session.createQuery(cq).getResultList();
        } catch (Exception ex){
            Tools.log("listRelationsForPost:" + ex.getMessage());
            relationEntityList = null;
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


}
