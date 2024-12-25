package com.terrestrialjournal.dao;
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
import com.terrestrialjournal.entity.PostEntity;
import com.terrestrialjournal.filter.BasicPostFilter;
import com.terrestrialjournal.util.Tools;

import javax.persistence.Query;
import javax.persistence.criteria.*;
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
            Predicate titleContains = cb.like(cb.lower(root.<String>get("title")), '%' + basicPostFilter.titleContains.toLowerCase() + '%');
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

    public static <T extends PostEntity> int updateRenderByPostId(Long id, String preRender, Class<T> clazz, Session session){
        if (id == null){
            return 0;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(clazz);
        Root<T> entityRoot = update.from(clazz);
        update.set(entityRoot.get("preRender"), preRender)
                .where(cb.equal(entityRoot.get("id"), id));

        int result = session.createQuery(update).executeUpdate();
        return result;
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

    /**
     *
     * @param id Post ID
     * @param clazz Class of post entity
     * @param session Hibernate session
     * @param <T> A class that extends PostEntity
     * @return Post publish status that has just been set
     */
    public static <T extends PostEntity> Boolean togglePostPublish(Long id, Class<T> clazz, Session session){
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

        return postEntity.getPublished();
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

    public static <T extends PostEntity> void saveEntity(T post, Session session){
        if (post == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(post);
        session.flush();
    }

    public static List<PostEntity> getPostEntitiesByIndexIds(List<Long> indexIds, Boolean publishStatus, Session session){

        String base = "from %s where indexId in :indexIds%s";
        String publishClause = publishStatus == null ? "" : " and published = :published";

        Query articleQuery = session.createQuery(String.format(base, "ArticleEntity", publishClause));
        articleQuery.setParameter("indexIds", indexIds);

        Query photoQuery = session.createQuery(String.format(base, "PhotoEntity", publishClause));
        photoQuery.setParameter("indexIds", indexIds);

        Query galleryQuery = session.createQuery(String.format(base, "GalleryEntity", publishClause));
        galleryQuery.setParameter("indexIds", indexIds);

        if (publishStatus != null){
            articleQuery.setParameter("published", publishStatus);
            photoQuery.setParameter("published", publishStatus);
            galleryQuery.setParameter("published", publishStatus);
        }

        List<PostEntity> resultList = new ArrayList<>();

        resultList.addAll(articleQuery.getResultList());
        resultList.addAll(photoQuery.getResultList());
        resultList.addAll(galleryQuery.getResultList());

        return resultList;
    }

}
