package world.thismagical.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.dao.ArticleDao;
import world.thismagical.dao.AuthorDao;
import world.thismagical.dao.FileDao;
import world.thismagical.dao.SessionDao;
import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.SessionEntity;
import world.thismagical.service.*;
import world.thismagical.to.*;
import world.thismagical.vo.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonApi {


    public static JsonAdminResponse<Void> verifySessionGuid(String guid, SessionFactory sessionFactory){
        Session session = Tools.getNewSession(sessionFactory);
        session.beginTransaction();

        Boolean verified = false;
        try {
            verified = AuthorizationService.isSessionValid(guid, session);
        } finally {
            session.close();
        }

        JsonAdminResponse<Void> res = new JsonAdminResponse<Void>();
        res.success = verified;
        return res;
    }


    public static JsonAdminResponse<AuthorizedVO> authorize(String login, String passwordHash, SessionFactory sessionFactory){

        Session session = sessionFactory.openSession();
        session.getTransaction().begin();
        SessionEntity authorSession = null;

        try {
            authorSession = AuthorizationService.authorize(login, passwordHash, session);
        } finally {
            session.getTransaction().commit();
            session.close();
        }

        JsonAdminResponse<AuthorizedVO> res = new JsonAdminResponse<>();
        if (authorSession == null){
            res.success = false;
            res.errorDescription = "Can not authorize user!";
            return res;
        }

        AuthorizedVO authorizedVO = new AuthorizedVO();
        authorizedVO.guid = authorSession.getSessionGuid();
        authorizedVO.privilegeLevelName = authorSession.getPrivilegeLevel().getName();
        authorizedVO.displayName = authorSession.getDisplayName();
        authorizedVO.authorId = authorSession.getAuthorId();

        res.success = true;
        res.data = authorizedVO;

        return res;
    }


    public static JsonAdminResponse<List<AuthorVO>> listAllAuthorsVO(String guid, SessionFactory sessionFactory){

        Session session = sessionFactory.openSession();
        List<AuthorVO> authorsVOList = null;
        JsonAdminResponse<List<AuthorVO>> res = new JsonAdminResponse<>();

         try {
             if (!AuthorizationService.isSessionValid(guid, session)) {
                 res.success = false;
                 res.errorDescription = "Not authorized!";
                 return res;
             }

             authorsVOList = AuthorService.getAllAuthorsVOList(session);
         } finally {
             session.close();
         }

         res.success = true;
         res.data = authorsVOList;
         return res;
    }

    public static JsonAdminResponse<List<PrivilegeVO>> listPrivileges(){
        List<PrivilegeVO> privilegeVOS = new ArrayList<>();
        for (PrivilegeLevel privilegeLevel : PrivilegeLevel.values()){
            PrivilegeVO privilegeVO = new PrivilegeVO();
            privilegeVO.id = privilegeLevel.getId().intValue();
            privilegeVO.name = privilegeLevel.getName();
            privilegeVO.description = privilegeLevel.getDescription();

            privilegeVOS.add(privilegeVO);
        }

        JsonAdminResponse<List<PrivilegeVO>> res = new JsonAdminResponse<>();
        res.data = privilegeVOS;
        return res;
    }

    public static JsonAdminResponse<Void> createNewAuthor(String guid, AuthorEntity newAuthor, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        JsonAdminResponse<Void> res = new JsonAdminResponse<>();

        try {
            if (!AuthorizationService.isSessionValid(guid, PrivilegeLevel.PRIVILEGE_SUPER_USER, session)){
                throw new IllegalAccessException();
            }
        } catch (Exception e) {
            session.close();
            res.success = false;
            res.errorDescription = "Not authorized!";
            return res;
        }

        boolean illegalArgument = false;
        if (newAuthor.getLogin() == null){
            illegalArgument = true;
        }

        if (newAuthor.getPasswd() == null){
            illegalArgument = true;
        }

        if (newAuthor.getDisplayName() == null){
            illegalArgument = true;
        }

        if (newAuthor.getPrivilegeLevel() == null){
            illegalArgument = true;
        }

        if (illegalArgument){
            session.close();
            res.success = false;
            res.errorDescription = "Illegal argument!";
            return res;
        }

        AuthorEntity existingAuthor = AuthorDao.getAuthorEntityByLogin(newAuthor.getLogin(), session);
        if (existingAuthor != null){
            session.close();
            res.success = false;
            res.errorDescription = "Login already exists!";
            return res;
        }

        String plainTextPassword = newAuthor.getPasswd();
        String hashedPassword = Tools.sha256(plainTextPassword);
        newAuthor.setPasswd(hashedPassword);

        session.save(newAuthor);
        session.flush();
        session.close();

        res.success = true;
        return res;
    }

    public static JsonAdminResponse<Void> changeDisplayName(String guid, Long targetAuthorId, String newDisplayName, SessionFactory sessionFactory){
        JsonAdminResponse<Void> jsonAdminResponse = new JsonAdminResponse<>();

        if (targetAuthorId == null || newDisplayName == null || newDisplayName.isEmpty()){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "Illegal argument!";
            return jsonAdminResponse;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            SessionEntity sessionEntity = SessionDao.getSessionEntityByGuid(guid, session);
            if (!sessionEntity.getAuthorId().equals(targetAuthorId)){
                if (!sessionEntity.getPrivilegeLevel().equals(PrivilegeLevel.PRIVILEGE_SUPER_USER)){
                    throw new IllegalAccessException();
                }
            }

            AuthorEntity authorEntity = AuthorDao.getAuthorEntityById(targetAuthorId, session);
            authorEntity.setDisplayName(newDisplayName);
            session.update(authorEntity);
            session.flush();
        } catch (Exception e){
            jsonAdminResponse.success = false;
        } finally {
            session.close();
        }

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Void> changePassword(String guid, Long targetAuthorId, String newUnhashedPassword, SessionFactory sessionFactory){
        JsonAdminResponse<Void> jsonAdminResponse = new JsonAdminResponse<>();
        if (targetAuthorId == null || newUnhashedPassword == null || newUnhashedPassword.isEmpty()){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "Illegal argument!";
            return jsonAdminResponse;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            SessionEntity sessionEntity = SessionDao.getSessionEntityByGuid(guid, session);
            if (!sessionEntity.getAuthorId().equals(targetAuthorId)){
                if (!sessionEntity.getPrivilegeLevel().equals(PrivilegeLevel.PRIVILEGE_SUPER_USER)){
                    throw new IllegalAccessException();
                }
            }

            AuthorEntity authorEntity = AuthorDao.getAuthorEntityById(targetAuthorId, session);

            String newHashedPassword = Tools.sha256(newUnhashedPassword);
            authorEntity.setPasswd(newHashedPassword);
            session.update(authorEntity);
            session.flush();
        } catch (Exception e){
            jsonAdminResponse.success = false;
            return jsonAdminResponse;
        } finally {
            session.close();
        }

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Void> changeAccessLevel(String guid, Long targetAuthorId, Short newAccessLevelId, SessionFactory sessionFactory){
        JsonAdminResponse<Void> jsonAdminResponse = new JsonAdminResponse<>();
        if (targetAuthorId == null || newAccessLevelId == null){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "Illegal argument!";
            return jsonAdminResponse;
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        try {
            SessionEntity sessionEntity = SessionDao.getSessionEntityByGuid(guid, session);
            if (!sessionEntity.getPrivilegeLevel().equals(PrivilegeLevel.PRIVILEGE_SUPER_USER)){
                throw new IllegalAccessException();
            }

            AuthorEntity authorEntity = AuthorDao.getAuthorEntityById(targetAuthorId, session);
            PrivilegeLevel privilegeLevel = PrivilegeLevel.getPrivilegeLevel(newAccessLevelId);

            if (privilegeLevel == null){
                throw new IllegalAccessException();
            }

            authorEntity.setPrivilegeLevel(privilegeLevel);
            session.update(authorEntity);
            session.flush();
        } catch (Exception e){
            jsonAdminResponse.success = false;
            return jsonAdminResponse;
        } finally {
            session.close();
        }

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<PhotoVO> getPhotoVOByPhotoId(Long id, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        PhotoVO photoVO = null;
        JsonAdminResponse<PhotoVO> res = new JsonAdminResponse<>();

        try {
            photoVO = PhotoService.getPhotoVObyPhotoId(id, session);
        } catch (Exception ex){
            Tools.log("[ERROR] getPhotoVOByPhotoId: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error loading photo object";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = photoVO;

        return res;
    }

    public static JsonAdminResponse<List<PhotoVO>> listAllPhotoVOsNoFilter(SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        List<PhotoVO> photoVOList = null;
        JsonAdminResponse<List<PhotoVO>> res = new JsonAdminResponse<>();

        try {
            photoVOList = PhotoService.listAllPhotoVOs(null, session);
        } catch (Exception ex){
            Tools.log("[ERROR] listAllPhotoVOs: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error obtaining list of photos";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = photoVOList;

        return res;
    }

    public static JsonAdminResponse<List<PhotoVO>> listAllPhotoVOs(String guid, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        AuthorEntity currentAuthor = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);
        AuthorEntity authorFilter = null;

        if (currentAuthor.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_USER){
            authorFilter = currentAuthor;
        }

        List<PhotoVO> photoVOList = null;
        JsonAdminResponse<List<PhotoVO>> res = new JsonAdminResponse<>();

        try {
            photoVOList = PhotoService.listAllPhotoVOs(authorFilter, session);
        } catch (Exception ex){
            Tools.log("[ERROR] listAllPhotoVOs: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error obtaining list of photos";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = photoVOList;

        return res;
    }

    public static JsonAdminResponse<ImageVO> getPhotoImageVO(Long parentObjectId, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        ImageVO imageVO = null;

        try {
            List<ImageVO> imageVOList = FileDao.getImages(PostAttribution.PHOTO, Collections.singletonList(parentObjectId), session);
            if (imageVOList != null){
                imageVO = imageVOList.get(0);
            }
        } finally {
            session.close();
        }

        JsonAdminResponse<ImageVO> jsonAdminResponse = new JsonAdminResponse<>();
        jsonAdminResponse.success = true;
        jsonAdminResponse.data = imageVO;

        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Long> saveOrUpdatePhoto(PhotoTO photoTO, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        JsonAdminResponse<Long> res = null;

        try {
            res = PhotoService.createOrUpdatePhoto(photoTO, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<Void> togglePhotoPublish(Long id, String guid, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        JsonAdminResponse<Void> res = null;

        try {
            res = PhotoService.togglePhotoPublish(id, guid, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<Void> deletePhoto(Long id, String guid, SessionFactory sessionFactory){

        JsonAdminResponse<Void> res = null;

        if (id == null){
            res = new JsonAdminResponse<>();
            res.success = false;
            res.errorDescription = "No photo ID provided!";
            return res;
        }

        Session session = sessionFactory.openSession();

        try {
            res = PhotoService.deletePhoto(id, guid, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<TagTO> listTagsForObject(TagTO request, SessionFactory sessionFactory){

        JsonAdminResponse<TagTO> res = null;

        if (request == null || request.objectId == null || request.attribution == null){
            res = new JsonAdminResponse<>();
            res.success = false;
            res.errorDescription = "Invalid parameters in TagTO request!";
            return res;
        }

        Session session = sessionFactory.openSession();

        try {
            res = TagService.listTagsForObjectStr(PostAttribution.getPostAttribution(request.attribution), request.objectId, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<Void> saveOrUpdateTags(TagTO tagTO, String guid, SessionFactory sessionFactory){
        JsonAdminResponse<Void> jar = null;
        if (tagTO == null || tagTO.attribution == null || tagTO.objectId == null){
            jar = new JsonAdminResponse<>();
            jar.success = false;
            jar.errorDescription = "saveOrUpdateTags() invalid argument";
            return jar;
        }

        Session session = sessionFactory.openSession();

        try {
            jar = TagService.saveOrUpdateTags(tagTO, guid, session);
        } finally {
            session.close();
        }

        return jar;
    }

    /**
     * Image Manager - list gallery/article images
     */
    public static JsonAdminResponse<List<ImageVO>> listImageVOs(Short postAttribution, Long objId, SessionFactory sessionFactory){
        JsonAdminResponse<List<ImageVO>> jsonAdminResponse = new JsonAdminResponse<>();

        if (postAttribution == null || objId == null){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "listImageVOs: null argument";
            return jsonAdminResponse;
        }

        Session session = sessionFactory.openSession();

        try {
            List<ImageVO> imageVOList = FileDao.getImages(PostAttribution.getPostAttribution(postAttribution), Collections.singletonList(objId), session);
            jsonAdminResponse.success = true;
            jsonAdminResponse.data = imageVOList;
        } catch (Exception ex){
            jsonAdminResponse.errorDescription = "something went wrong (listImageVOs API)";
            jsonAdminResponse.success = false;
        } finally {
            session.close();
        }

        return jsonAdminResponse;
    }

    public static  JsonAdminResponse<List<GalleryVO>> listAllGalleryVOsNoFilter(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();

        List<GalleryVO> galleryVOList;
        JsonAdminResponse<List<GalleryVO>> res = new JsonAdminResponse<>();

        try {
            galleryVOList = GalleryService.listAllGalleryVOs(null, session);
        } catch (Exception ex){
            Tools.log("[ERROR] listAllGalleryVOsNoFilter: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error obtaining list of photos";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = galleryVOList;

        return res;
    }

    public static JsonAdminResponse<List<GalleryVO>> listAllGalleryVOs(String guid, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        AuthorEntity currentAuthor = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);
        AuthorEntity authorFilter = null;

        if (currentAuthor.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_USER){
            authorFilter = currentAuthor;
        }

        List<GalleryVO> galleryVOList = null;
        JsonAdminResponse<List<GalleryVO>> res = new JsonAdminResponse<>();

        try {
            galleryVOList = GalleryService.listAllGalleryVOs(authorFilter, session);
        } catch (Exception ex){
            Tools.log("[ERROR] listAllGalleryVOs: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error obtaining list of photos";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = galleryVOList;

        return res;
    }

    public static JsonAdminResponse<Long> saveOrUpdateGallery(GalleryTO galleryTO, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        JsonAdminResponse<Long> res;

        try {
            res = GalleryService.createOrUpdateGallery(galleryTO, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<GalleryVO> getGalleryVOByGalleryId(Long id, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        GalleryVO galleryVO = null;
        JsonAdminResponse<GalleryVO> res = new JsonAdminResponse<>();

        try {
            galleryVO = GalleryService.getGalleryVOByGalleryId(id, session);
        } catch (Exception ex){
            Tools.log("[ERROR] getGalleryVOByGalleryId: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error loading gallery object";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = galleryVO;

        return res;
    }

    public static String toString(Object object, ObjectMapper objectMapper){

        if (object == null){
            return "null";
        }

        String res = "";
        try {
            res = objectMapper.writeValueAsString(object);
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        return res;
    }

    public static JsonAdminResponse<Void> toggleGalleryPublish(Long id, String guid, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        JsonAdminResponse<Void> res = null;

        try {
            res = GalleryService.togglePostPublish(id, guid, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<Void> deleteGallery(Long id, String guid, SessionFactory sessionFactory){

        JsonAdminResponse<Void> res = null;

        if (id == null){
            res = new JsonAdminResponse<>();
            res.success = false;
            res.errorDescription = "No gallery ID provided!";
            return res;
        }

        Session session = sessionFactory.openSession();

        try {
            res = GalleryService.deleteGallery(id, guid, session);
        } finally {
            session.close();
        }

        return res;
    }

    // articles

    public static JsonAdminResponse<ImageVO> getArticleTitleImageVO(Long parentObjectId, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        ImageVO imageVO = null;

        try {
            ArticleEntity articleEntity = ArticleDao.getArticleEntityById(parentObjectId, session);
            imageVO = FileDao.getImageById(articleEntity.getTitleImageId(), session);
        } finally {
            session.close();
        }

        JsonAdminResponse<ImageVO> jsonAdminResponse = new JsonAdminResponse<>();
        jsonAdminResponse.success = true;
        jsonAdminResponse.data = imageVO;

        return jsonAdminResponse;
    }

    public static JsonAdminResponse<ArticleVO> getArticleVOByArticleId(Long id, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        ArticleVO articleVO = null;
        JsonAdminResponse<ArticleVO> res = new JsonAdminResponse<>();

        try {
            articleVO = ArticleService.getArticleVObyArticleId(id, session);
        } catch (Exception ex){
            Tools.log("[ERROR] getArticleVOByArticleId: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error loading article object";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = articleVO;

        return res;
    }

    public static JsonAdminResponse<List<ArticleVO>> listAllArticleVOs(String guid, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        AuthorEntity currentAuthor = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);
        AuthorEntity authorFilter = null;

        if (currentAuthor.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_USER){
            authorFilter = currentAuthor;
        }

        List<ArticleVO> articleVOList = null;
        JsonAdminResponse<List<ArticleVO>> res = new JsonAdminResponse<>();

        try {
            articleVOList = ArticleService.listAllArticleVOs(authorFilter, session);
        } catch (Exception ex){
            Tools.log("[ERROR] listAllArticleVOs: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error obtaining list of articles";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = articleVOList;

        return res;
    }

    public static JsonAdminResponse<List<ArticleVO>> listAllArticleVOsNoFilter(SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();

        List<ArticleVO> articleVOList = null;
        JsonAdminResponse<List<ArticleVO>> res = new JsonAdminResponse<>();

        try {
            articleVOList = ArticleService.listAllArticleVOs(null, session);
        } catch (Exception ex){
            Tools.log("[ERROR] listAllArticleVOsNoFilter: "+ex.getMessage());
            ex.printStackTrace();

            res.success = false;
            res.errorDescription = "error obtaining list of articles";

            return res;
        } finally {
            session.close();
        }

        res.success = true;
        res.data = articleVOList;

        return res;
    }

    public static JsonAdminResponse<Long> saveOrUpdateArticle(ArticleTO articleTO, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        JsonAdminResponse<Long> res = null;

        try {
            res = ArticleService.createOrUpdateArticle(articleTO, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<Void> toggleArticlePublish(Long id, String guid, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        JsonAdminResponse<Void> res = null;

        try {
            res = ArticleService.toggleArticlePublish(id, guid, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<Void> deleteArticle(Long id, String guid, SessionFactory sessionFactory){

        JsonAdminResponse<Void> res = null;

        if (id == null){
            res = new JsonAdminResponse<>();
            res.success = false;
            res.errorDescription = "No article ID provided!";
            return res;
        }

        Session session = sessionFactory.openSession();

        try {
            res = ArticleService.deleteArticle(id, guid, session);
        } finally {
            session.close();
        }

        return res;
    }

    public static JsonAdminResponse<Void> setArticleTitleImageId(ArticleTO articleTO, String guid, SessionFactory sessionFactory){

        JsonAdminResponse<Void> res = null;

        if (articleTO == null || articleTO.id == null || articleTO.titleImageId == null){
            res = new JsonAdminResponse<>();
            res.success = false;
            res.errorDescription = "No article ID provided!";
            return res;
        }

        Session session = sessionFactory.openSession();

        try {
            res = ArticleService.setArticleTitleImageId(articleTO, guid, session);
        } finally {
            session.close();
        }

        return res;
    }


    public static JsonAdminResponse<RelationTO> listRelationsForPost(PostTO postTO, SessionFactory sessionFactory){

        JsonAdminResponse<RelationTO> res = new JsonAdminResponse<>();

        if (postTO.postAttributionClass == null || postTO.postObjectId == null){
            res.success = false;
            res.errorDescription = "listRelationsForPost: null argument";
            return res;
        }

        Session session = sessionFactory.openSession();

        try {
            res.data = RelationService.getRelationTO(postTO, session);
            res.success = true;
        } /*catch (Exception ex) {
            res.success = false;
            res.errorDescription = "can't load relations for post "+postTO.postObjectId+" of class "+postTO.postAttributionClass;
            Tools.log("listRelationsForPost: " + ex.getMessage());
        }*/ finally {
            session.close();
        }

        return res;

    }

    public static JsonAdminResponse<Void> createNewRelation(String guid, RelationVO relationVOPartial, SessionFactory sessionFactory){
        JsonAdminResponse<Void> res = new JsonAdminResponse<>();

        Session session = sessionFactory.openSession();

        try {

        } finally {
            session.close();
        }

        res.success = true;

        return res;

    }



}
