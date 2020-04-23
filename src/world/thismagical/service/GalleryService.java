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

public class GalleryService {
    public static GalleryVO getGalleryVOByGalleryId(Long galleryId, Session session){
        GalleryEntity galleryEntity = (GalleryEntity) GalleryDao.getPostEntityById(galleryId, GalleryEntity.class, session);
        GalleryVO galleryVO = new GalleryVO(galleryEntity);

        galleryVO.imageVOList = FileDao.getImages(PostAttribution.GALLERY, Collections.singletonList(galleryEntity.getId()), session);

        if (galleryVO.imageVOList != null && !galleryVO.imageVOList.isEmpty()){
            List<ImageVO> galleryRepresentation = new ArrayList<>();
            int i = 4;
            if (galleryVO.imageVOList.size() < 4){
                i = galleryVO.imageVOList.size();
            }

            for (int t = 0; t < i; t++){
                galleryRepresentation.add(galleryVO.imageVOList.get(t));
            }

            galleryVO.galleryRepresentation = galleryRepresentation;
        }

        return galleryVO;
    }


    @SuppressWarnings("unchecked")
    public static List<GalleryVO> listAllGalleryVOs(BasicPostFilter basicPostFilter, Session session){
        List<GalleryEntity> galleryEntityList = (List<GalleryEntity>) (List) GalleryDao.listAllPosts(basicPostFilter, GalleryEntity.class, session);
        List<GalleryVO> galleryVOList = new ArrayList<>();

        for (GalleryEntity galleryEntity : galleryEntityList){
            GalleryVO galleryVO = getGalleryVOByGalleryId(galleryEntity.getId(), session);
            galleryVOList.add(galleryVO);
        }
        
        return galleryVOList;
    }

    public static JsonAdminResponse<Void> togglePostPublish(Long id,  String guid, Session session){

        GalleryEntity galleryEntity = (GalleryEntity) GalleryDao.getPostEntityById(id, GalleryEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(galleryEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        GalleryDao.togglePostPublish(id, GalleryEntity.class, session);

        return JsonAdminResponse.success(null);
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

        if (galleryEntity == null) {
            galleryEntity = new GalleryEntity();
            galleryEntity.setAuthor(currentAuthorEntity);
        }

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

        return JsonAdminResponse.success(galleryEntity.getId());
    }

    public static JsonAdminResponse<Void> deleteGallery(Long id, String guid, Session session){

        if (id == null){
            return JsonAdminResponse.fail("deleteGallery: no gallery id provided");
        }

        GalleryEntity galleryEntity = (GalleryEntity) GalleryDao.getPostEntityById(id, GalleryEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(galleryEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        List<ImageVO> galleryImages = FileDao.getImages(PostAttribution.GALLERY, Collections.singletonList(id), session);
        FileHandlingService.deleteImages(galleryImages, session);

        GalleryDao.deleteEntity(id, GalleryEntity.class, session);
        TagDao.truncateTags(id, PostAttribution.GALLERY.getId(), session);

        return JsonAdminResponse.success(null);
    }


}
