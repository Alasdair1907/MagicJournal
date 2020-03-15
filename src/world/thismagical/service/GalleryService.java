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
import world.thismagical.dao.FileDao;
import world.thismagical.dao.GalleryDao;
import world.thismagical.dao.PhotoDao;
import world.thismagical.dao.TagDao;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.GalleryEntity;
import world.thismagical.entity.ImageFileEntity;
import world.thismagical.entity.PhotoEntity;
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

public class GalleryService {
    public static GalleryVO getGalleryVOByGalleryId(Long galleryId, Session session){
        GalleryEntity galleryEntity = GalleryDao.getGalleryEntityById(galleryId, session);
        GalleryVO galleryVO = new GalleryVO(galleryEntity);

        galleryVO.imageVOList = FileDao.getImages(PostAttribution.GALLERY, Collections.singletonList(galleryEntity.getId()), session);

        if (galleryVO.imageVOList != null && !galleryVO.imageVOList.isEmpty()){
            List<ImageVO> galleryRepresentation = new ArrayList<>();
            // TODO: consider a better way of selecting gallery representation images
            int i = 4;
            if (galleryVO.imageVOList.size() < 4){
                i = galleryVO.imageVOList.size();
            }

            while (i > 0){
                galleryRepresentation.add(galleryVO.imageVOList.get(i-1));
                i--;
            }

            galleryVO.galleryRepresentation = galleryRepresentation;
        }

        return galleryVO;
    }

    public static List<GalleryVO> listAllGalleryVOs(AuthorEntity authorFilter, Session session){
        List<GalleryEntity> galleryEntityList = GalleryDao.listAllGalleries(authorFilter, session);
        List<GalleryVO> galleryVOList = new ArrayList<>();

        for (GalleryEntity galleryEntity : galleryEntityList){
            GalleryVO galleryVO = getGalleryVOByGalleryId(galleryEntity.getId(), session);
            galleryVOList.add(galleryVO);
        }
        
        return galleryVOList;
    }

    public static JsonAdminResponse<Void> togglePostPublish(Long id,  String guid, Session session){

        GalleryEntity galleryEntity = GalleryDao.getGalleryEntityById(id, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        JsonAdminResponse<Void> jsonAdminResponse = new JsonAdminResponse<>();

        if (!AuthorizationService.checkPrivileges(galleryEntity.getAuthor(), currentAuthorEntity, jsonAdminResponse)){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "unauthorized action";
            return jsonAdminResponse;
        }

        GalleryDao.toggleGalleryPublish(id, session);

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Long> createOrUpdateGallery(GalleryTO galleryTO, Session session){

        JsonAdminResponse<Long> jsonAdminResponse = new JsonAdminResponse<>();

        if (galleryTO == null){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "GalleryTO: null argument";
            return jsonAdminResponse;
        }

        String sessionGuid = galleryTO.sessionGuid;

        GalleryEntity galleryEntity = GalleryDao.getGalleryEntityById(galleryTO.id, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(sessionGuid, session);

        AuthorEntity galleryEntityAuthor = null;

        if (galleryEntity != null){
            galleryEntityAuthor = galleryEntity.getAuthor();
        } else {
            galleryEntityAuthor = currentAuthorEntity;
        }

        if (!AuthorizationService.checkPrivileges(galleryEntityAuthor, currentAuthorEntity, jsonAdminResponse)){
            return jsonAdminResponse;
        }

        if (galleryEntity == null) {
            galleryEntity = new GalleryEntity();
        }

        galleryEntity.setAuthor(currentAuthorEntity);

        if (galleryEntity.getCreationDate() == null){
            galleryEntity.setCreationDate(LocalDateTime.now());
        }

        galleryEntity.setTitle(Tools.nullToEmpty(galleryTO.title));
        galleryEntity.setDescription(Tools.nullToEmpty(galleryTO.description));
        galleryEntity.setGpsCoordinates(Tools.nullToEmpty(galleryTO.gpsCoordinates));
        galleryEntity.setPublished(galleryTO.published);

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(galleryEntity);
        session.flush();

        jsonAdminResponse.data = galleryEntity.getId();
        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Void> deleteGallery(Long id, String guid, Session session){
        GalleryEntity galleryEntity = GalleryDao.getGalleryEntityById(id, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        JsonAdminResponse<Void> jsonAdminResponse = new JsonAdminResponse<>();

        if (!AuthorizationService.checkPrivileges(galleryEntity.getAuthor(), currentAuthorEntity, jsonAdminResponse)){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "unauthorized action";
            return jsonAdminResponse;
        }

        List<ImageVO> galleryImages = FileDao.getImages(PostAttribution.GALLERY, Collections.singletonList(id), session);
        FileHandlingService.deleteImages(galleryImages, session);

        GalleryDao.deleteGallery(id, session);
        TagDao.truncateTags(id, PostAttribution.GALLERY.getId(), session);

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }


}
