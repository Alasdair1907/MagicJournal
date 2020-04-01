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
import world.thismagical.util.Tools;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    @SuppressWarnings("unchecked")
    public static <T extends PostEntity> List<PostEntity> listAllPosts(AuthorEntity authorFilter, Class<T> clazz, Session session){
        List<PostEntity> list = null;

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);
        cq.orderBy(cb.desc(root.get("creationDate")));

        if (authorFilter != null){
            Predicate authorPredicate = cb.equal(root.get("author"), authorFilter);
            cq.select(root).where(authorPredicate);
        } else {
            cq.select(root);
        }

        list = (List<PostEntity>) session.createQuery(cq).list();

        return list;
    }

    public static <T extends PostEntity> PostEntity getPostEntityById(Long id, Class<T> clazz, Session session){
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
            Tools.log(Tools.getStackTraceStr(ex));
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
