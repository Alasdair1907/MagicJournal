package world.thismagical.dao;
/*
  User: Alasdair
  Date: 2/2/2020
  Time: 9:42 PM                                                                                                    
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
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.GalleryEntity;
import world.thismagical.entity.PhotoEntity;
import world.thismagical.util.Tools;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class GalleryDao {
    public static void deleteGallery(Long galleryId, Session session){

        if (galleryId == null){
            return;
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query delQuery = session.createQuery("delete from GalleryEntity where id= :pid");
        delQuery.setParameter("pid", galleryId);
        delQuery.executeUpdate();

        session.flush();
    }

    public static List<GalleryEntity> listAllGalleries(AuthorEntity authorFilter, Session session){
        List<GalleryEntity> list = null;

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GalleryEntity> cq = cb.createQuery(GalleryEntity.class);
        Root<GalleryEntity> root = cq.from(GalleryEntity.class);
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

    public static GalleryEntity getGalleryEntityById(Long id, Session session){
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<GalleryEntity> cq = criteriaBuilder.createQuery(GalleryEntity.class);
        Root<GalleryEntity> galleryEntityRoot = cq.from(GalleryEntity.class);

        Predicate predicateId = criteriaBuilder.equal(galleryEntityRoot.get("id"), id);
        cq.where(predicateId);

        GalleryEntity galleryEntity = null;
        try {
            galleryEntity = session.createQuery(cq).getSingleResult();
        } catch (Exception ex){
            Tools.log("[WARN] Can't get GalleryEntity with id "+id);
        }

        return galleryEntity;
    }

    public static void toggleGalleryPublish(Long id, Session session){
        GalleryEntity galleryEntity = getGalleryEntityById(id, session);

        if (Boolean.TRUE.equals(galleryEntity.getPublished())){
            galleryEntity.setPublished(Boolean.FALSE);
        } else {
            galleryEntity.setPublished(Boolean.TRUE);
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(galleryEntity);

        session.flush();
    }
}
