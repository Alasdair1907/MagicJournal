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

    public static List<String> preFilterTagEntities(PagingRequestFilter pagingRequestFilter, Session session){

        String geo = "";
        if (Boolean.TRUE.equals(pagingRequestFilter.requireGeo)){
            geo = "parentHasGeo = true and";
        }

        if (pagingRequestFilter.tags == null || pagingRequestFilter.tags.isEmpty()){
            Query query = session.createQuery("select distinct(tag) from TagEntity where " + geo + " attributionClass in :attrs and parentIsPublished = true order by tag asc");
            query.setParameter("attrs", gatherPostAttributionList(pagingRequestFilter));

            return query.getResultList();
        } else {

            Query query = session.createQuery("select distinct(tag) from TagEntity where " + geo + " postIndexItemId in (select postIndexItemId from TagEntity where tag in :tagList and attributionClass in :attrs group by postIndexItemId having count(distinct tag) = :count) and parentIsPublished = true order by tag asc");
            query.setParameter("tagList", pagingRequestFilter.tags);
            query.setParameter("attrs", gatherPostAttributionList(pagingRequestFilter));
            query.setParameter("count",  Integer.valueOf(pagingRequestFilter.tags.size()).longValue());

            return query.getResultList();
        }
    }

    public static List<Long> preloadTagEntities(PagingRequestFilter pagingRequestFilter, Session session){
        if (pagingRequestFilter.tags == null || pagingRequestFilter.tags.isEmpty()){
            return new ArrayList<>();
        }

        String geo = "";
        if (Boolean.TRUE.equals(pagingRequestFilter.requireGeo)){
            geo = "parentHasGeo = true and";
        }

        Query query = session.createQuery("select max(postIndexItemId) from TagEntity where "+geo+" tag in :tagList and parentIsPublished = true and attributionClass in :attrs group by attributionClass, parentObjectId having count(distinct tag) = :count");
        query.setParameter("tagList", pagingRequestFilter.tags);
        query.setParameter("attrs", gatherPostAttributionList(pagingRequestFilter));
        query.setParameter("count", Integer.valueOf(pagingRequestFilter.tags.size()).longValue());

        return query.getResultList();
    }

    public static List<Short> gatherPostAttributionList(PagingRequestFilter pagingRequestFilter){
        List<Short> postAttributionList = new ArrayList<>();

        if (Boolean.TRUE.equals(pagingRequestFilter.needArticles)){
            postAttributionList.add(PostAttribution.ARTICLE.getId());
        }

        if (Boolean.TRUE.equals(pagingRequestFilter.needGalleries)){
            postAttributionList.add(PostAttribution.GALLERY.getId());
        }

        if (Boolean.TRUE.equals(pagingRequestFilter.needPhotos)){
            postAttributionList.add(PostAttribution.PHOTO.getId());
        }

        return postAttributionList;
    }

    public static Predicate preparePredicate(PagingRequestFilter pagingRequestFilter, CriteriaBuilder cb, Root<PostIndexItem> postIndexItemRoot, Session session){

        List<Predicate> predicates = new ArrayList<>();

        if (pagingRequestFilter.tags != null && !pagingRequestFilter.tags.isEmpty()){
            List<Long> postPreloadList = preloadTagEntities(pagingRequestFilter, session);
            CriteriaBuilder.In<Long> thisId = cb.in(postIndexItemRoot.get("id"));
            postPreloadList.forEach(thisId::value);
            predicates.add(thisId);
        }

        if (Boolean.TRUE.equals(pagingRequestFilter.requireGeo)){
            predicates.add(cb.equal(postIndexItemRoot.get("hasGeo"), Boolean.TRUE));
        }

        predicates.add(cb.isTrue(postIndexItemRoot.get("isPublished")));

        List<Short> postAttributionList = gatherPostAttributionList(pagingRequestFilter);
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

        Predicate all = preparePredicate(pagingRequestFilter, cb, postIndexItemRoot, session);

        criteriaQuery.select(cb.count(postIndexItemRoot)).where(all).distinct(true);
        Long res = session.createQuery(criteriaQuery).getSingleResult();
        return res.intValue();
    }

    public static List<PostIndexItem> load(PagingRequestFilter pagingRequestFilter, Integer from, Integer results, Session session){

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<PostIndexItem> criteriaQuery = cb.createQuery(PostIndexItem.class);
        Root<PostIndexItem> postIndexItemRoot = criteriaQuery.from(PostIndexItem.class);
        criteriaQuery.orderBy(cb.desc(postIndexItemRoot.get("creationDate")));

        Predicate all = preparePredicate(pagingRequestFilter, cb, postIndexItemRoot, session);

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

    public static PostIndexItem getItem(PostAttribution postAttribution, Long objectId, Session session){
        Query getQuery = session.createQuery("from PostIndexItem where postAttribution = :postAttributionShort and postId = :postId");
        getQuery.setParameter("postAttributionShort", postAttribution.getId());
        getQuery.setParameter("postId", objectId);

        PostIndexItem postIndexItem = (PostIndexItem) getQuery.getSingleResult();
        return postIndexItem;
    }

    public static void togglePostPublish(PostAttribution postAttribution, Long objectId, Session session){

        PostIndexItem postIndexItem = getItem(postAttribution, objectId, session);

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

    public static void setGeo(String geo, PostAttribution postAttribution, Long objectId, Session session){
        PostIndexItem postIndexItem = getItem(postAttribution, objectId, session);

        if (postIndexItem == null){
            return;
        }

        boolean hasGeo = (geo != null) && ( !geo.isEmpty() );

        postIndexItem.setHasGeo(hasGeo);

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(postIndexItem);
        session.flush();
    }

}
