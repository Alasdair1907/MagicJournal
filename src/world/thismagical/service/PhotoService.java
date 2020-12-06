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
import world.thismagical.dao.*;
import world.thismagical.entity.*;
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

public class PhotoService extends PostService {

    public static PhotoVO getPhotoVObyPhotoId(Long photoId, Session session){
        PhotoEntity photoEntity = (PhotoEntity) PhotoDao.getPostEntityById(photoId, PhotoEntity.class, session);
        PhotoVO photoVO = new PhotoVO(photoEntity);

        List<ImageVO> imageVOList = FileDao.getImages(PostAttribution.PHOTO, Collections.singletonList(photoEntity.getId()), session);
        if (imageVOList != null && !imageVOList.isEmpty()){
            photoVO.imageVO = imageVOList.get(0);
            photoVO.tagEntityList = TagService.listTagsForObject(PostAttribution.PHOTO, photoId, session).data;
        }

        return photoVO;
    }

    public static List<PhotoVO> photoEntitiesToVOs(List<PhotoEntity> photoEntityList, Session session){
        List<Long> parentObjectIds = photoEntityList.stream().map(PhotoEntity::getId).collect(Collectors.toList());
        List<ImageVO> imageVOList = FileDao.getImages(PostAttribution.PHOTO, parentObjectIds, session);

        List<Long> photoIds = photoEntityList.stream().map(PhotoEntity::getId).collect(Collectors.toList());
        List<TagEntity> tagEntityList = TagDao.listTagsForObjects(PostAttribution.PHOTO, photoIds, session);

        List<PhotoVO> photoVOList = new ArrayList<>();
        for (PhotoEntity photoEntity : photoEntityList){
            ImageVO imageVO = imageVOList.stream().filter( it -> it.parentObjId.equals(photoEntity.getId())).findAny().orElse(null);
            PhotoVO photoVO = new PhotoVO(photoEntity);
            photoVO.imageVO = imageVO;

            photoVO.tagEntityList = tagEntityList.stream()
                    .filter(it -> it.getAttributionClass() == PostAttribution.PHOTO && it.getParentObjectId().equals(photoEntity.getId())).collect(Collectors.toList());

            photoVOList.add(photoVO);
        }

        return photoVOList;
    }

    @SuppressWarnings("unchecked")
    public static List<PhotoVO> listAllPhotoVOs(BasicPostFilter basicPostFilter, Session session){
        basicPostFilter.verifyGuid(session);
        List<PhotoEntity> photoEntityList = (List<PhotoEntity>) (List) PhotoDao.listAllPosts(basicPostFilter, PhotoEntity.class, session);
        return photoEntitiesToVOs(photoEntityList, session);
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

        Boolean newPhoto = false;

        if (photoEntity == null) {
            photoEntity = new PhotoEntity();
            photoEntity.setAuthor(currentAuthorEntity);
            newPhoto = true;
        }


        if (photoEntity.getCreationDate() == null){
            photoEntity.setCreationDate(LocalDateTime.now());
        }
        photoEntity.setLastModifiedDate(LocalDateTime.now());

        photoEntity.setTitle(Tools.nullToEmpty(photoTO.title));
        photoEntity.setTinyDescription(Tools.nullToEmpty(photoTO.tinyDescription));
        photoEntity.setDescription(Tools.nullToEmpty(photoTO.description));
        photoEntity.setGpsCoordinates(Tools.nullToEmpty(photoTO.gpsCoordinates));
        if (newPhoto) {
            photoEntity.setPublished(photoTO.published);
        }

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

        savePostGeneralProcedures(newPhoto, currentAuthorEntity, photoEntity, PostAttribution.PHOTO, session);

        return JsonAdminResponse.success(photoEntity.getId());
    }

    public static JsonAdminResponse<ImageVO> getPhotoImageVO(Long parentObjectId, Session session){

        ImageVO imageVO = null;
        List<ImageVO> imageVOList = FileDao.getImages(PostAttribution.PHOTO, Collections.singletonList(parentObjectId), session);

        if (imageVOList != null){
            imageVO = imageVOList.get(0);
        }

        return JsonAdminResponse.success(imageVO);
    }

}
