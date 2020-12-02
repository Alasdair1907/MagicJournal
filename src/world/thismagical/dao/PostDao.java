package world.thismagical.dao;
/*
  User: Alasdair
  Date: 3/16/2020
  Time: 9:04 PM                                                                                                    
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
import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.PostEntity;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.util.Tools;

import javax.persistence.Basic;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class PostDao {

    @SuppressWarnings("unchecked")
    public static <T extends PostEntity> List<PostEntity> getEntitiesByIds(List<Long> ids, Class<T> clazz, Session session){

        if (ids == null || ids.isEmpty()){
            return null;
        }

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(clazz);
        Root<T> articleEntityRoot = cq.from(clazz);

        CriteriaBuilder.In<Long> in = criteriaBuilder.in(articleEntityRoot.get("id"));
        for (Long id: ids){
            in.value(id);
        }

        cq.where(in);

        List<PostEntity> articleEntityList = null;
        try {
            articleEntityList = (List<PostEntity>) session.createQuery(cq).getResultList();
        } catch (Exception ex){
            Tools.log("[WARN] getArticleEntitiesByIds: "+ex.getMessage());
            Tools.log(Tools.getStackTraceStr(ex));
        }

        return articleEntityList;
    }

    public static <T extends PostEntity> Predicate preparePredicateFromFilter(BasicPostFilter basicPostFilter, CriteriaBuilder cb, Root<T> root){
        List<Predicate> predicates = new ArrayList<>();

        if (basicPostFilter.authorEntity != null){
            Predicate authorPredicate = cb.equal(root.get("author"), basicPostFilter.authorEntity);
            predicates.add(authorPredicate);
        }

        if (basicPostFilter.fromDateTime != null){
            Predicate fromDateTime = cb.greaterThanOrEqualTo(root.get("creationDate"), basicPostFilter.fromDateTime);
            predicates.add(fromDateTime);
        }

        if (basicPostFilter.toDateTime != null){
            Predicate toDateTime = cb.lessThanOrEqualTo(root.get("creationDate"), basicPostFilter.toDateTime);
            predicates.add(toDateTime);
        }

        if (basicPostFilter.titleContains != null && !basicPostFilter.titleContains.isEmpty()){
            Predicate titleContains = cb.like(root.get("title"), '%' + basicPostFilter.titleContains + '%');
            predicates.add(titleContains);
        }

        if (basicPostFilter.ids != null && !basicPostFilter.ids.isEmpty()){
            CriteriaBuilder.In<Long> inIds = cb.in(root.get("id"));
            basicPostFilter.ids.forEach(inIds::value);
            predicates.add(inIds);
        }

        if (!basicPostFilter.showUnpublished){
            Predicate showUnpublished = cb.isTrue(root.get("published"));
            predicates.add(showUnpublished);
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    public static <T extends PostEntity> Long countFilter(BasicPostFilter basicPostFilter, Class<T> clazz, Session session){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<T> root = cq.from(clazz);
        Predicate and = preparePredicateFromFilter(basicPostFilter, cb, root);
        cq.select(cb.count(root)).where(and);
        return session.createQuery(cq).getSingleResult();
    }

    public static <T extends PostEntity> Query prepareQueryFromFilter(BasicPostFilter basicPostFilter, Class<T> clazz, Session session){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        cq.orderBy(cb.desc(root.get("creationDate")));

        Predicate and = preparePredicateFromFilter(basicPostFilter, cb, root);
        cq.select(root).where(and);

        Query query = session.createQuery(cq);

        if (basicPostFilter.fromCount != null){
            query = query.setFirstResult(basicPostFilter.fromCount);
        }
        if (basicPostFilter.limit != null){
            query = query.setMaxResults(basicPostFilter.limit);
        }

        return query;
    }

    @SuppressWarnings("unchecked")
    public static <T extends PostEntity> List<PostEntity> listAllPosts(BasicPostFilter basicPostFilter, Class<T> clazz, Session session){

        // todo: unauthorized users shouldn't have the ability to list unlimited amounts of posts at once

        if (basicPostFilter != null && Boolean.TRUE.equals(basicPostFilter.returnEmpty)){
            return new ArrayList<>();
        }

        List<PostEntity> list = null;

        Query query;
        if (basicPostFilter == null){
            query = session.createQuery("from "+clazz.getSimpleName()+" where published = true order by creationDate desc");
            list = (List<PostEntity>) query.getResultList();
        } else {
            query = prepareQueryFromFilter(basicPostFilter, clazz, session);
        }

        list = (List<PostEntity>) query.getResultList();

        return list;
    }

    public static <T extends PostEntity> PostEntity getPostEntityById(Long id, Class<T> clazz, Session session){

        if (id == null){
            return null;
        }

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = criteriaBuilder.createQuery(clazz);
        Root<T> entityRoot = cq.from(clazz);

        Predicate predicateId = criteriaBuilder.equal(entityRoot.get("id"), id);
        cq.where(predicateId);

        PostEntity postEntity = null;
        try {
            postEntity = session.createQuery(cq).getSingleResult();
        } catch (Exception ex){
            Tools.log("[WARN] Can't get entity with id "+id+" type "+clazz.getName());
            Tools.handleException(ex);
        }

        return postEntity;
    }

    public static <T extends PostEntity> void togglePostPublish(Long id, Class<T> clazz, Session session){
        PostEntity postEntity = getPostEntityById(id, clazz, session);

        if (Boolean.TRUE.equals(postEntity.getPublished())){
            postEntity.setPublished(Boolean.FALSE);
        } else {
            postEntity.setPublished(Boolean.TRUE);
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(postEntity);
        session.flush();
    }

    public static <T extends PostEntity> void deleteEntity(Long postId, Class<T> clazz, Session session){

        if (postId == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query delQuery = session.createQuery("delete from "+clazz.getSimpleName()+" where id= :pid");
        delQuery.setParameter("pid", postId);
        delQuery.executeUpdate();

        session.flush();
    }

}
