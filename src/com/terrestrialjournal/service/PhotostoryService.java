package com.terrestrialjournal.service;
/*
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.terrestrialjournal.dao.PhotostoryDao;
import com.terrestrialjournal.dao.FileDao;
import com.terrestrialjournal.dao.TagDao;
import com.terrestrialjournal.entity.ImageFileEntity;
import com.terrestrialjournal.entity.PhotostoryEntity;
import com.terrestrialjournal.entity.AuthorEntity;
import com.terrestrialjournal.entity.TagEntity;
import com.terrestrialjournal.filter.BasicPostFilter;
import com.terrestrialjournal.to.JsonAdminResponse;
import com.terrestrialjournal.to.photostory.*;
import com.terrestrialjournal.util.BBCodeExtractor;
import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.util.Tools;
import com.terrestrialjournal.vo.PhotostoryVO;
import com.terrestrialjournal.vo.ImageVO;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.terrestrialjournal.service.AuthorizationService.getAuthorEntityBySessionGuid;

public class PhotostoryService extends PostService {


    public static PhotostoryVO getPhotostoryVObyPhotostoryId(Long photostoryId, String userGuid, Session session){
        PhotostoryEntity photostoryEntity = (PhotostoryEntity) PhotostoryDao.getPostEntityById(photostoryId, PhotostoryEntity.class, session);
        AuthorizationService.checkUnpublishedViewPrivileges(photostoryEntity, userGuid, session);
        PhotostoryVO photostoryVO = new PhotostoryVO(photostoryEntity);

        photostoryVO.titleImageVO = FileDao.getImageById(photostoryEntity.getTitleImageId(), session);
        photostoryVO.tagEntityList = TagService.listTagsForObject(PostAttribution.PHOTOSTORY, photostoryId, session).data;

        return photostoryVO;
    }

    public static String getPreRenderByPhotostoryId(Long photostoryId, String userGuid, Session session){
        PhotostoryEntity photostoryEntity = (PhotostoryEntity) PhotostoryDao.getPostEntityById(photostoryId, PhotostoryEntity.class, session);
        AuthorizationService.checkUnpublishedViewPrivileges(photostoryEntity, userGuid, session);
        return photostoryEntity.getPreRender();
    }

    public static List<PhotostoryVO> photostoryEntitiesToPhotostoryVOs(List<PhotostoryEntity> photostoryEntityList, Session session){
        List<PhotostoryVO> photostoryVOList = new ArrayList<>();
        List<Long> imageIds = photostoryEntityList.stream().map(PhotostoryEntity::getTitleImageId).collect(Collectors.toList());
        List<Long> photostoryIds = photostoryEntityList.stream().map(PhotostoryEntity::getId).collect(Collectors.toList());

        List<ImageVO> imageVOList = FileDao.getImagesByIds(imageIds, session);
        List<TagEntity> tagEntityList = TagDao.listTagsForObjects(PostAttribution.PHOTOSTORY, photostoryIds, session);

        for (PhotostoryEntity photostoryEntity : photostoryEntityList){
            PhotostoryVO photostoryVO = new PhotostoryVO(photostoryEntity);
            if (imageVOList != null) {
                photostoryVO.titleImageVO = imageVOList.stream().filter(imageVO -> imageVO.thisObjId.equals(photostoryEntity.getTitleImageId())).findAny().orElse(null);
            }

            photostoryVO.tagEntityList = tagEntityList.stream()
                    .filter(it -> it.getAttributionClass() == PostAttribution.PHOTOSTORY && it.getParentObjectId().equals(photostoryEntity.getId())).collect(Collectors.toList());

            photostoryVOList.add(photostoryVO);
        }

        return photostoryVOList;
    }

    @SuppressWarnings("unchecked")
    public static List<PhotostoryVO> listAllPhotostoryVOs(BasicPostFilter basicPostFilter, Session session){
        basicPostFilter.verifyGuid(session);
        List<PhotostoryEntity> photostoryEntityList = (List<PhotostoryEntity>) (List) PhotostoryDao.listAllPosts(basicPostFilter, PhotostoryEntity.class, session);
        return photostoryEntitiesToPhotostoryVOs(photostoryEntityList, session);
    }

    public static List<ImageVO> getImageVOList(PhotostoryVO photostoryVO, Session session){
        if (photostoryVO == null || photostoryVO.getContent() == null){
            return null;
        }
        if (photostoryVO.getContent().PSItems == null){
            return null;
        }
        List<PSItemTO> PSItems = photostoryVO.getContent().PSItems;
        if (PSItems.isEmpty()){
            return null;
        }

        List<Long> imageIds = new ArrayList<>();
        for (PSItemTO psItemTO : PSItems){
            if (psItemTO.getItemType() != PSItemType.PS_ITEM_IMAGE){
                continue;
            }
            Long imageId = ((PSImageTO) psItemTO).getImageID();
            imageIds.add(imageId);
        }

        List<ImageVO> imageVOList = FileDao.getImagesByIds(imageIds, session);
        return imageVOList;
    }

    public static void enrichPhotstoryTOImages(PhotostoryTO photostoryTO, Session session){
        if (photostoryTO == null){
            return;
        }

        if (photostoryTO.content == null){
            return;
        }

        if (photostoryTO.content.PSItems == null || photostoryTO.content.PSItems.isEmpty()){
            return;
        }
        List<PSItemTO> psItems = photostoryTO.content.PSItems;

        List<PSImageTO> imageTOs = new ArrayList<>();
        List<Long> imageFileIds = new ArrayList<>();
        for (PSItemTO psItemTO : psItems){
            if (psItemTO.getItemType() != PSItemType.PS_ITEM_IMAGE){
                continue;
            }

            PSImageTO psImageTO = (PSImageTO) psItemTO;
            if (psImageTO.getImageID() == null){
                continue;
            }

            imageTOs.add(psImageTO);
            imageFileIds.add(psImageTO.getImageID());
        }

        if (imageTOs.isEmpty()){
            return;
        }

        List<ImageFileEntity> imageEntities = FileDao.getImageEntitiesByIds(imageFileIds, session);
        for (PSImageTO psImageTO : imageTOs){
            ImageFileEntity imageEntity = imageEntities.stream()
                    .filter(ife->ife.getId().equals(psImageTO.getImageID()))
                    .findFirst().orElse(null);
            if (imageEntity == null){
                Tools.log("imageEntity null, when psImageTO has image file id " + psImageTO.getImageID());
                continue;
            }

            psImageTO.setPreviewFile(imageEntity.getPreviewFileName());
            psImageTO.setMainFile(imageEntity.getFileName());
        }
    }

    public static JsonAdminResponse<Long> createOrUpdatePhotostory(PhotostoryTO photostoryTO, Session session){

        if (photostoryTO == null){
            return JsonAdminResponse.fail("PhotostoryTO: null argument");
        }

        String sessionGuid = photostoryTO.sessionGuid;

        PhotostoryEntity photostoryEntity = (PhotostoryEntity) PhotostoryDao.getPostEntityById(photostoryTO.id, PhotostoryEntity.class, session);
        AuthorEntity currentAuthorEntity = getAuthorEntityBySessionGuid(sessionGuid, session);

        AuthorEntity photostoryEntityAuthor = null;
        Boolean newPhotostory = false;

        if (photostoryEntity != null){
            photostoryEntityAuthor = photostoryEntity.getAuthor();
        } else {
            newPhotostory = true;
            photostoryEntityAuthor = currentAuthorEntity;
        }

        if (!AuthorizationService.checkPrivileges(photostoryEntityAuthor, currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        if (photostoryEntity == null) {
            photostoryEntity = new PhotostoryEntity();
            photostoryEntity.setAuthor(currentAuthorEntity);
        }

        if (photostoryEntity.getCreationDate() == null){
            photostoryEntity.setCreationDate(LocalDateTime.now());
        }

        photostoryEntity.setLastModifiedDate(LocalDateTime.now());
        photostoryEntity.setTitle(Tools.nullToEmpty(photostoryTO.title));
        photostoryEntity.setDescription(Tools.nullToEmpty(photostoryTO.description));
        photostoryEntity.setSEDescription(Tools.nullToEmpty(photostoryTO.seDescription));
        photostoryEntity.setTinyDescription(Tools.nullToEmpty(photostoryTO.tinyDescription));
        photostoryEntity.setGpsCoordinates(Tools.nullToEmpty(photostoryTO.gpsCoordinates));

        if (photostoryTO.titleImageId != null) {
            photostoryEntity.setTitleImageId(photostoryTO.titleImageId);
        }

        if (photostoryTO.content != null){
            enrichPhotstoryTOImages(photostoryTO, session);
        }

        photostoryEntity.setJsonContent(null);
        if (photostoryTO.content != null){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                photostoryEntity.setJsonContent(objectMapper.writeValueAsString(photostoryTO.content));
            } catch (JsonProcessingException ex){
                throw new RuntimeException(ex);
            }
        }


        photostoryEntity.setPreRender(photostoryTO.preRender);

        savePostGeneralProcedures(newPhotostory, photostoryEntityAuthor, photostoryEntity, PostAttribution.PHOTOSTORY, session);

        if (!newPhotostory) {
            // we're not updating it when the photostory is completely new, because the frontend first creates a completely
            // empty photostory, and when the user puts text into it and clicks 'save', the empty photostory gets updated
            // TODO relationService for photostories
            //RelationService.updatePhotostoryGalleryRelations(photostoryEntity.getId(), photostoryTO.photostoryText, session);
        }

        return JsonAdminResponse.success(photostoryEntity.getId());
    }

    public static JsonAdminResponse<ImageVO> getPhotostoryTitleImageVO(Long parentObjectId, Session session){

        if (parentObjectId == null){
            return JsonAdminResponse.fail("getPhotostoryTitleImageVO: no photostory ID provided");
        }

        PhotostoryEntity photostoryEntity = (PhotostoryEntity) PhotostoryDao.getPostEntityById(parentObjectId, PhotostoryEntity.class, session);
        return JsonAdminResponse.success(FileDao.getImageById(photostoryEntity.getTitleImageId(), session));
    }


    public static JsonAdminResponse<Void> setPhotostoryTitleImageId(PhotostoryTO photostoryTO, String guid, Session session){

        if (photostoryTO == null || photostoryTO.id == null || photostoryTO.titleImageId == null){
            return JsonAdminResponse.fail("setPhotostoryTitleImageId: null argument");
        }

        PhotostoryEntity photostoryEntity = (PhotostoryEntity) PhotostoryDao.getPostEntityById(photostoryTO.id, PhotostoryEntity.class, session);
        AuthorEntity currentAuthorEntity = getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(photostoryEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        PhotostoryDao.updatePhotostoryTitleImageId(photostoryTO.id, photostoryTO.titleImageId, session);

        return JsonAdminResponse.success(null);
    }

}
