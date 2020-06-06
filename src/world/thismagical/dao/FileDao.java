package world.thismagical.dao;
/*
  User: Alasdair
  Date: 12/30/2019
  Time: 9:22 PM                                                                                                    
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


import org.hibernate.Criteria;
import org.hibernate.NullPrecedence;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import world.thismagical.entity.ImageFileEntity;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;
import world.thismagical.vo.ImageVO;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

public class FileDao {

    public static void deleteImageEntityById(Long imageEntityId, Session session){
        if (imageEntityId == null){
            throw new IllegalArgumentException("deleteImageEntityById: null id");
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        Query query = session.createQuery("delete from ImageFileEntity where id= :id");
        query.setParameter("id", imageEntityId);
        query.executeUpdate();

        session.flush();
    }

    public static List<ImageFileEntity> getImageEntitiesByIds(List<Long> imageEntityIds, Session session){
        if (imageEntityIds == null || imageEntityIds.isEmpty()){
            throw new IllegalArgumentException("getImageEntitiesByIDs: imageEntityId is NULL");
        }

        Query query = session.createQuery("from ImageFileEntity where id in :ids");
        query.setParameter("ids", imageEntityIds);
        return query.getResultList();
    }

    public static List<ImageFileEntity> getImageEntities(PostAttribution imageAttribution, List<Long> parentObjectIdList, Session session){

        if (imageAttribution == null){
            throw new IllegalArgumentException("getImageEntities: imageAttribution is NULL");
        }

        if (parentObjectIdList == null || parentObjectIdList.isEmpty()){
            return null;
        }



        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ImageFileEntity> cq = cb.createQuery(ImageFileEntity.class);
        Root<ImageFileEntity> root = cq.from(ImageFileEntity.class);

        CriteriaBuilder.In<Long> parentObjectIdIn = cb.in(root.get("parentObjectId"));
        for (Long parentObjectId : parentObjectIdList){
            parentObjectIdIn.value(parentObjectId);
        }

        cq.orderBy(cb.asc(root.get("orderNumber")), cb.desc(root.get("id")));

        Predicate attributionPredicate = cb.equal(root.get("imageAttributionClass"), imageAttribution.getId());
        Predicate and = cb.and(attributionPredicate, parentObjectIdIn);

        cq.select(root).where(and);
        List<ImageFileEntity> files;
        try {
            files = session.createQuery(cq).getResultList();
        } catch (Exception ex){
            Tools.log("[WARN] getImageEntities: "+ex.getMessage());
            return null;
        }

        return files;
    }

    public static List<ImageVO> getImagesByIds(List<Long> imageIds, Session session){
        if (imageIds == null || imageIds.isEmpty()){
            return null;
        }

        List<ImageFileEntity> imageFileEntityList = getImageEntitiesByIds(imageIds, session);

        if (imageFileEntityList == null || imageFileEntityList.isEmpty()){
            return null;
        }

        List<ImageVO> imageVOList = new ArrayList<>();

        for (ImageFileEntity imageFileEntity : imageFileEntityList){
            imageVOList.add(new ImageVO(imageFileEntity));
        }

        return imageVOList;
    }

    public static ImageVO getImageById(Long imageObjectId, Session session){
        if (imageObjectId == null){
            return null;
        }

        List<ImageFileEntity> imageFileEntityList = getImageEntitiesByIds(Collections.singletonList(imageObjectId), session);

        if (imageFileEntityList == null || imageFileEntityList.isEmpty()){
            return null;
        }

        ImageVO imageVO = new ImageVO(imageFileEntityList.get(0));
        return imageVO;
    }

    public static JsonAdminResponse<List<ImageVO>> getImages(Short imageAttributionId, Long parentObjectId, Session session){
        if (imageAttributionId == null || parentObjectId == null){
            return JsonAdminResponse.fail("getImages: null argument");
        }

        PostAttribution postAttribution = PostAttribution.getPostAttribution(imageAttributionId);

        return JsonAdminResponse.success(getImages(postAttribution, Collections.singletonList(parentObjectId), session));
    }

    public static List<ImageVO> getImages(PostAttribution imageAttribution, List<Long> parentObjectIdList, Session session){

        if (parentObjectIdList == null || parentObjectIdList.isEmpty()){
            return null;
        }

        List<ImageFileEntity> files = getImageEntities(imageAttribution, parentObjectIdList, session);

        if (files == null){
            return null;
        }

        List<ImageVO> imageVOList = new ArrayList<>();

       for (ImageFileEntity imageFileEntity : files){
            ImageVO imageVO = new ImageVO(imageFileEntity);
            imageVOList.add(imageVO);
        }

        return imageVOList;
    }

    public static void saveOrUpdate(ImageFileEntity imageFileEntity, Session session){
        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(imageFileEntity);
        session.flush();
    }

    public static void flushList(List<ImageFileEntity> imageFileEntityList, Session session){
        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        for (ImageFileEntity imageFileEntity : imageFileEntityList){
            session.save(imageFileEntity);
        }

        session.flush();
    }
}
