package world.thismagical.dao;
/*
  User: Alasdair
  Date: 5/14/2020
  Time: 8:09 PM                                                                                                    
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
import world.thismagical.entity.PostIndexItem;
import world.thismagical.entity.TagEntity;
import world.thismagical.filter.PagingRequestFilter;
import world.thismagical.util.PostAttribution;

import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class PagingDao {

    public static Predicate preparePredicate(PagingRequestFilter pagingRequestFilter, CriteriaBuilder cb, Root<PostIndexItem> postIndexItemRoot){
        List<Short> postAttributionList = new ArrayList<>();

        if (pagingRequestFilter.needArticles){
            postAttributionList.add(PostAttribution.ARTICLE.getId());
        }

        if (pagingRequestFilter.needGalleries){
            postAttributionList.add(PostAttribution.GALLERY.getId());
        }

        if (pagingRequestFilter.needPhotos){
            postAttributionList.add(PostAttribution.PHOTO.getId());
        }


        List<Predicate> predicates = new ArrayList<>();

        if (pagingRequestFilter.tags != null && !pagingRequestFilter.tags.isEmpty()){
            Join<PostIndexItem, TagEntity> tagEntityJoin = postIndexItemRoot.join("tagEntityList");

            CriteriaBuilder.In<String> tagIn = cb.in(tagEntityJoin.get("tag"));
            pagingRequestFilter.tags.forEach(tagIn::value);

            predicates.add(tagIn);
        }

        CriteriaBuilder.In<Short> attrIn = cb.in(postIndexItemRoot.get("postAttribution"));
        postAttributionList.forEach(attrIn::value);

        predicates.add(attrIn);

        if (pagingRequestFilter.authorLogin != null && !pagingRequestFilter.authorLogin.isEmpty()){
            Predicate author = cb.equal(postIndexItemRoot.get("authorLogin"), pagingRequestFilter.authorLogin);
            predicates.add(author);
        }

        Predicate all = cb.and(predicates.toArray(new Predicate[0]));
        return all;
    }

    public static Integer count(PagingRequestFilter pagingRequestFilter, Session session){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
        Root<PostIndexItem> postIndexItemRoot = criteriaQuery.from(PostIndexItem.class);

        Predicate all = preparePredicate(pagingRequestFilter, cb, postIndexItemRoot);

        criteriaQuery.select(cb.count(postIndexItemRoot)).where(all).distinct(true);
        Long res = session.createQuery(criteriaQuery).getSingleResult();
        return res.intValue();
    }

    public static List<PostIndexItem> load(PagingRequestFilter pagingRequestFilter, Integer from, Integer results, Session session){

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<PostIndexItem> criteriaQuery = cb.createQuery(PostIndexItem.class);
        Root<PostIndexItem> postIndexItemRoot = criteriaQuery.from(PostIndexItem.class);
        criteriaQuery.orderBy(cb.desc(postIndexItemRoot.get("creationDate")));

        Predicate all = preparePredicate(pagingRequestFilter, cb, postIndexItemRoot);

        criteriaQuery.select(postIndexItemRoot).where(all).distinct(true);
        Query query = session.createQuery(criteriaQuery).setFirstResult(from).setMaxResults(results);

        List<PostIndexItem> postIndexItemList = query.getResultList();
        return postIndexItemList;
    }

    public static void deleteIndex(PostAttribution postAttribution, Long objectId, Session session){
        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query query = session.createQuery("delete from PostIndexItem where postAttribution = :postAttributionShort and postId = :postId");
        query.setParameter("postAttributionShort", postAttribution.getId());
        query.setParameter("postId", objectId);
        query.executeUpdate();

        session.flush();
    }

    public static void togglePostPublish(PostAttribution postAttribution, Long objectId, Session session){

        Query getQuery = session.createQuery("from PostIndexItem where postAttribution = :postAttributionShort and postId = :postId");
        getQuery.setParameter("postAttributionShort", postAttribution.getId());
        getQuery.setParameter("postId", objectId);

        PostIndexItem postIndexItem = (PostIndexItem) getQuery.getSingleResult();

        if (postIndexItem == null){
            return;
        }

        if (Boolean.TRUE.equals(postIndexItem.getPublished())){
            postIndexItem.setPublished(false);
        } else {
            postIndexItem.setPublished(true);
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(postIndexItem);
        session.flush();
    }

}
