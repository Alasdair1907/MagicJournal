package world.thismagical.service;
/*
  User: Alasdair
  Date: 1/3/2020
  Time: 12:15 AM                                                                                                    
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
import world.thismagical.dao.FileDao;
import world.thismagical.dao.PhotoDao;
import world.thismagical.dao.TagDao;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.ImageFileEntity;
import world.thismagical.entity.PhotoEntity;
import world.thismagical.entity.PostEntity;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.PhotoTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.ImageVO;
import world.thismagical.vo.PhotoVO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PhotoService {

    public static PhotoVO getPhotoVObyPhotoId(Long photoId, Session session){
        PhotoEntity photoEntity = (PhotoEntity) PhotoDao.getPostEntityById(photoId, PhotoEntity.class, session);
        PhotoVO photoVO = new PhotoVO(photoEntity);

        List<ImageVO> imageVOList = FileDao.getImages(PostAttribution.PHOTO, Collections.singletonList(photoEntity.getId()), session);
        if (imageVOList != null && !imageVOList.isEmpty()){
            photoVO.imageVO = imageVOList.get(0);
        }

        return photoVO;
    }

    @SuppressWarnings("unchecked")
    public static List<PhotoVO> listAllPhotoVOs(BasicPostFilter basicPostFilter, Session session){
        List<PhotoEntity> photoEntityList = (List<PhotoEntity>) (List) PhotoDao.listAllPosts(basicPostFilter, PhotoEntity.class, session);
        List<Long> parentObjectIds = photoEntityList.stream().map(PhotoEntity::getId).collect(Collectors.toList());
        List<ImageVO> imageVOList = FileDao.getImages(PostAttribution.PHOTO, parentObjectIds, session);

        List<PhotoVO> photoVOList = new ArrayList<>();
        for (PhotoEntity photoEntity : photoEntityList){
            ImageVO imageVO = null;
            Optional<ImageVO> optional = imageVOList.stream().filter( it -> it.parentObjId.equals(photoEntity.getId())).findAny();
            if (optional.isPresent()) {
                imageVO = optional.get();
            }
            PhotoVO photoVO = new PhotoVO(photoEntity);
            photoVO.imageVO = imageVO;

            photoVOList.add(photoVO);
        }

        return photoVOList;
    }

    public static JsonAdminResponse<Long> createOrUpdatePhoto(PhotoTO photoTO, Session session){

        if (photoTO == null){
            return JsonAdminResponse.fail("PhotoTO: null argument");
        }

        String sessionGuid = photoTO.sessionGuid;

        PhotoEntity photoEntity = (PhotoEntity) PhotoDao.getPostEntityById(photoTO.id, PhotoEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(sessionGuid, session);

        AuthorEntity photoEntityAuthor = null;

        if (photoEntity != null){
            photoEntityAuthor = photoEntity.getAuthor();
        } else {
            photoEntityAuthor = currentAuthorEntity;
        }

        if (!AuthorizationService.checkPrivileges(photoEntityAuthor, currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        if (photoEntity == null) {
            photoEntity = new PhotoEntity();
            photoEntity.setAuthor(currentAuthorEntity);
        }


        if (photoEntity.getCreationDate() == null){
            photoEntity.setCreationDate(LocalDateTime.now());
        }

        photoEntity.setTitle(Tools.nullToEmpty(photoTO.title));
        photoEntity.setDescription(Tools.nullToEmpty(photoTO.description));
        photoEntity.setGpsCoordinates(Tools.nullToEmpty(photoTO.gpsCoordinates));
        photoEntity.setPublished(photoTO.published);

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        List<ImageFileEntity> imageFileEntityList = FileDao.getImageEntities(PostAttribution.PHOTO, Collections.singletonList(photoEntity.getId()), session);
        if (!imageFileEntityList.isEmpty()){
            ImageFileEntity imageFileEntity = imageFileEntityList.get(0);
            imageFileEntity.setTitle(photoTO.title);
            imageFileEntity.setGpsCoordinates(photoTO.gpsCoordinates);
            FileDao.saveOrUpdate(imageFileEntity, session);
        }

        session.saveOrUpdate(photoEntity);
        session.flush();

        return JsonAdminResponse.success(photoEntity.getId());
    }

    public static JsonAdminResponse<Void> deletePhoto(Long id, String guid, Session session){

        if (id == null){
            return JsonAdminResponse.fail("deletePhoto error: no id provided");
        }

        PhotoEntity photoEntity = (PhotoEntity) PhotoDao.getPostEntityById(id, PhotoEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(photoEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        List<ImageVO> photoImages = FileDao.getImages(PostAttribution.PHOTO, Collections.singletonList(id), session);
        FileHandlingService.deleteImages(photoImages, session);

        PhotoDao.deleteEntity(id, PhotoEntity.class, session);
        TagDao.truncateTags(id, PostAttribution.PHOTO.getId(), session);

        return JsonAdminResponse.success(null);
    }

    public static JsonAdminResponse<Void> togglePhotoPublish(Long id, String guid, Session session){

        PhotoEntity photoEntity = (PhotoEntity) PhotoDao.getPostEntityById(id, PhotoEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(photoEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        PhotoDao.togglePostPublish(id, PhotoEntity.class, session);

        return JsonAdminResponse.success(null);
    }

}
