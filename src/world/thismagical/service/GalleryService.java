package world.thismagical.service;
/*
  User: Alasdair
  Date: 2/2/2020
  Time: 8:59 PM                                                                                                    
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
import world.thismagical.dao.*;
import world.thismagical.entity.*;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.to.GalleryTO;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.PhotoTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.GalleryVO;
import world.thismagical.vo.ImageVO;
import world.thismagical.vo.PhotoVO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GalleryService extends PostService {

    public static List<GalleryVO> entitiesToVos(List<GalleryEntity> galleryEntityList, Integer imagesInRepresentation, Session session){

        if (galleryEntityList == null || galleryEntityList.isEmpty()){
            return null;
        }

        if (imagesInRepresentation == null){
            imagesInRepresentation = 0;
        }

        List<GalleryVO> res = new ArrayList<>();

        List<Long> galleryIds = new ArrayList<>();
        galleryEntityList.forEach(it -> galleryIds.add(it.getId()));
        List<ImageVO> imageVOListAll = FileDao.getImages(PostAttribution.GALLERY, galleryIds, session);

        List<TagEntity> tagEntityList = TagDao.listTagsForObjects(PostAttribution.GALLERY, galleryIds, session);

        for (GalleryEntity galleryEntity : galleryEntityList){
            GalleryVO galleryVO = new GalleryVO(galleryEntity);
            galleryVO.imageVOList = imageVOListAll.stream().filter(it -> it.parentObjId.equals(galleryEntity.getId())).collect(Collectors.toList());

            if (!galleryVO.imageVOList.isEmpty() && imagesInRepresentation > 0) {
                List<ImageVO> galleryRepresentation = new ArrayList<>();

                int i = imagesInRepresentation;
                if (galleryVO.imageVOList.size() < imagesInRepresentation) {
                    i = galleryVO.imageVOList.size();
                }

                for (int t = 0; t < i; t++) {
                    galleryRepresentation.add(galleryVO.imageVOList.get(t));
                }

                galleryVO.galleryRepresentation = galleryRepresentation;
            }

            galleryVO.tagEntityList = tagEntityList.stream()
                    .filter(it -> it.getAttributionClass() == PostAttribution.GALLERY && it.getParentObjectId().equals(galleryEntity.getId())).collect(Collectors.toList());

            res.add(galleryVO);
        }

        return res;
    }

    public static GalleryVO getGalleryVOByGalleryId(Long galleryId, Session session){
        GalleryEntity galleryEntity = (GalleryEntity) GalleryDao.getPostEntityById(galleryId, GalleryEntity.class, session);
        if (galleryEntity == null){
            return null;
        }

        return entitiesToVos(Collections.singletonList(galleryEntity), BasicPostFilter.DEFAULT_GALLERY_REPRESENTATION_IMAGES, session).get(0);
    }


    @SuppressWarnings("unchecked")
    public static List<GalleryVO> listAllGalleryVOs(BasicPostFilter basicPostFilter, Integer imagesInRepresentation, Session session){
        basicPostFilter.verifyGuid(session);
        List<GalleryEntity> galleryEntityList = (List<GalleryEntity>) (List) GalleryDao.listAllPosts(basicPostFilter, GalleryEntity.class, session);

        if (galleryEntityList == null || galleryEntityList.isEmpty()){
            return new ArrayList<>();
        }

        return entitiesToVos(galleryEntityList, imagesInRepresentation, session);
    }

    public static JsonAdminResponse<Long> createOrUpdateGallery(GalleryTO galleryTO, Session session){

        if (galleryTO == null){
            return JsonAdminResponse.fail("GalleryTO: null argument");
        }

        String sessionGuid = galleryTO.sessionGuid;

        GalleryEntity galleryEntity = (GalleryEntity) GalleryDao.getPostEntityById(galleryTO.id, GalleryEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(sessionGuid, session);

        AuthorEntity galleryEntityAuthor = null;

        if (galleryEntity != null){
            galleryEntityAuthor = galleryEntity.getAuthor();
        } else {
            galleryEntityAuthor = currentAuthorEntity;
        }

        if (!AuthorizationService.checkPrivileges(galleryEntityAuthor, currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        Boolean newGallery = false;

        if (galleryEntity == null) {
            galleryEntity = new GalleryEntity();
            galleryEntity.setAuthor(currentAuthorEntity);
            newGallery = true;
        }

        if (galleryEntity.getCreationDate() == null){
            galleryEntity.setCreationDate(LocalDateTime.now());
        }
        galleryEntity.setLastModifiedDate(LocalDateTime.now());

        galleryEntity.setTitle(Tools.nullToEmpty(galleryTO.title));
        galleryEntity.setTinyDescription(Tools.nullToEmpty(galleryTO.tinyDescription));
        galleryEntity.setDescription(Tools.nullToEmpty(galleryTO.description));
        galleryEntity.setGpsCoordinates(Tools.nullToEmpty(galleryTO.gpsCoordinates));

        if (newGallery) {
            galleryEntity.setPublished(Boolean.FALSE);
        }

        savePostGeneralProcedures(newGallery, currentAuthorEntity, galleryEntity, PostAttribution.GALLERY, session);

        return JsonAdminResponse.success(galleryEntity.getId());
    }



}
