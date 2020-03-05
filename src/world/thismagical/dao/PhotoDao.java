package world.thismagical.dao;
/*
  User: Alasdair
  Date: 1/2/2020
  Time: 5:37 PM                                                                                                    
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
import org.hibernate.SessionFactory;
import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.PhotoEntity;
import world.thismagical.util.Tools;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class PhotoDao {

    public static List<PhotoEntity> listAllPhotos(SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        List<PhotoEntity> list = null;

        try {
            list = listAllPhotos(null, session);
        } finally {
            session.close();
        }

        return list;
    }

    public static void deletePhoto(Long photoId, Session session){

        if (photoId == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query delQuery = session.createQuery("delete from PhotoEntity where id= :pid");
        delQuery.setParameter("pid", photoId);
        delQuery.executeUpdate();

        session.flush();
    }

    public static List<PhotoEntity> listAllPhotos(AuthorEntity authorFilter, Session session){
        List<PhotoEntity> list = null;

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<PhotoEntity> cq = cb.createQuery(PhotoEntity.class);
        Root<PhotoEntity> root = cq.from(PhotoEntity.class);
        cq.orderBy(cb.asc(root.get("creationDate")));

        if (authorFilter != null){
            Predicate authorPredicate = cb.equal(root.get("author"), authorFilter);
            cq.select(root).where(authorPredicate);
        } else {
            cq.select(root);
        }

        list = session.createQuery(cq).list();

        return list;
    }

    public static PhotoEntity getPhotoEntityById(Long id, Session session){
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<PhotoEntity> cq = criteriaBuilder.createQuery(PhotoEntity.class);
        Root<PhotoEntity> photoEntityRoot = cq.from(PhotoEntity.class);

        Predicate predicateId = criteriaBuilder.equal(photoEntityRoot.get("id"), id);
        cq.where(predicateId);

        PhotoEntity photoEntity = null;
        try {
            photoEntity = session.createQuery(cq).getSingleResult();
        } catch (Exception ex){
            Tools.log("[WARN] Can't get PhotoEntity with id "+id);
        }

        return photoEntity;
    }

    public static List<PhotoEntity> getPhotoEntitiesByIds(List<Long> ids, Session session){
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<PhotoEntity> cq = criteriaBuilder.createQuery(PhotoEntity.class);
        Root<PhotoEntity> photoEntityRoot = cq.from(PhotoEntity.class);

        CriteriaBuilder.In<Long> in = criteriaBuilder.in(photoEntityRoot.get("id"));
        for (Long id: ids){
            in.value(id);
        }

        cq.where(in);

        List<PhotoEntity> photoEntityList = null;
        try {
            photoEntityList = session.createQuery(cq).getResultList();
        } catch (Exception ex){
            Tools.log("[WARN] getPhotoEntitiesByIds: "+ex.getMessage());
        }

        return photoEntityList;
    }

    public static void togglePhotoPublish(Long id, Session session){
        PhotoEntity photoEntity = getPhotoEntityById(id, session);

        if (Boolean.TRUE.equals(photoEntity.getPublished())){
            photoEntity.setPublished(Boolean.FALSE);
        } else {
            photoEntity.setPublished(Boolean.TRUE);
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(photoEntity);

        session.flush();
    }
}
