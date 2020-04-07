package world.thismagical.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.dao.*;
import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.SessionEntity;
import world.thismagical.service.*;
import world.thismagical.to.*;
import world.thismagical.vo.*;
import java.util.*;


public class JsonApi {


    public static JsonAdminResponse<Void> verifySessionGuid(String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            if (AuthorizationService.isSessionValid(guid, session)){
                return JsonAdminResponse.success(null);
            }
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("can't verify session guid");
    }


    public static JsonAdminResponse<AuthorizedVO> authorize(String login, String passwordHash, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()){
            return AuthorizationService.authorize(login, passwordHash, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("can not authorize user");
    }


    public static JsonAdminResponse<List<AuthorVO>> listAllAuthorsVO(SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()){
            return JsonAdminResponse.success(AuthorService.getAllAuthorsVOList(session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("can not obtain list of authors");
    }


    public static JsonAdminResponse<List<PrivilegeVO>> listPrivileges(){
        return JsonAdminResponse.success(PrivilegeLevel.getPrivilegesList());
    }

    public static JsonAdminResponse<Void> createNewAuthor(String guid, AuthorEntity newAuthor, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return AuthorService.createNewAuthor(guid, newAuthor, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error creating new author");
    }

    public static JsonAdminResponse<Void> deleteAuthor(String guid, Long targetAuthorId, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()){
            return AuthorService.deleteAuthor(guid, targetAuthorId, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error deleting author");
    }

    public static JsonAdminResponse<Void> changeDisplayName(String guid, Long targetAuthorId, String newDisplayName, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()){
            return AuthorService.changeBasicAuthorParams(guid, targetAuthorId, newDisplayName, null, null, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error changing display name");
    }

    public static JsonAdminResponse<Void> changePassword(String guid, Long targetAuthorId, String newUnhashedPassword, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){
            return AuthorService.changeBasicAuthorParams(guid, targetAuthorId, null, newUnhashedPassword, null, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error changing password");
    }

    public static JsonAdminResponse<Void> changeAccessLevel(String guid, Long targetAuthorId, Short newAccessLevelId, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){
            return AuthorService.changeBasicAuthorParams(guid, targetAuthorId, null, null, newAccessLevelId, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error changing access level");
    }

    public static JsonAdminResponse<AuthorVO> getAuthorVOByGuid(String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return AuthorService.getAuthorVOByGuid(guid, session);
        } catch (Exception ex) {
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("unable to load author data");

    }

    public static JsonAdminResponse<Void> updateAuthorProfile(String guid, AuthorVO authorVO, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()) {
            return AuthorService.updateAuthorProfile(guid, authorVO, session);
        } catch (Exception ex) {
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("unable to update author profile");
    }


    public static JsonAdminResponse<PhotoVO> getPhotoVOByPhotoId(Long id, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(PhotoService.getPhotoVObyPhotoId(id, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error getting photo by id");
    }

    public static JsonAdminResponse<List<PhotoVO>> listAllPhotoVOsNoFilter(SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(PhotoService.listAllPhotoVOs(null, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing photos (no filter)");
    }

    public static JsonAdminResponse<List<PhotoVO>> listAllPhotoVOs(String guid, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(PhotoService.listAllPhotoVOsUserFilter(guid, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing photos");
    }

    public static JsonAdminResponse<ImageVO> getPhotoImageVO(Long parentObjectId, SessionFactory sessionFactory){
        ImageVO imageVO = null;

        try (Session session = sessionFactory.openSession()) {
            List<ImageVO> imageVOList = FileDao.getImages(PostAttribution.PHOTO, Collections.singletonList(parentObjectId), session);
            if (imageVOList != null){
                imageVO = imageVOList.get(0);
            }
            return JsonAdminResponse.success(imageVO);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error getting image for photo");
    }

    public static JsonAdminResponse<Long> saveOrUpdatePhoto(PhotoTO photoTO, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return PhotoService.createOrUpdatePhoto(photoTO, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error saving or updating photo");
    }

    public static JsonAdminResponse<Void> togglePhotoPublish(Long id, String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return PhotoService.togglePhotoPublish(id, guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error toggling photo publish status");
    }

    public static JsonAdminResponse<Void> deletePhoto(Long id, String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return PhotoService.deletePhoto(id, guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error deleting photo");
    }

    public static JsonAdminResponse<TagTO> listTagsForObject(TagTO request, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return TagService.listTagsForObjectStr(request, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing tags for object");
    }

    public static JsonAdminResponse<Void> saveOrUpdateTags(TagTO tagTO, String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return TagService.saveOrUpdateTags(tagTO, guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error saving or updating tags");
    }

    /**
     * Image Manager - list gallery/article images
     */
    public static JsonAdminResponse<List<ImageVO>> listImageVOs(Short postAttribution, Long objId, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return FileDao.getImages(postAttribution, objId, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing images");
    }

    public static  JsonAdminResponse<List<GalleryVO>> listAllGalleryVOsNoFilter(SessionFactory sessionFactory) {

        try (Session session = sessionFactory.openSession()) {
             return JsonAdminResponse.success(GalleryService.listAllGalleryVOs(null, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error obtaining list of photos");
    }

    public static JsonAdminResponse<List<GalleryVO>> listAllGalleryVOs(String guid, SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            return GalleryService.listAllGalleryVOsUserFilter(guid, session);
        } catch (Exception ex) {
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing galleries");
    }

    public static JsonAdminResponse<Long> saveOrUpdateGallery(GalleryTO galleryTO, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return GalleryService.createOrUpdateGallery(galleryTO, session);
        } catch (Exception ex) {
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error saving or updating gallery");
    }

    public static JsonAdminResponse<GalleryVO> getGalleryVOByGalleryId(Long id, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(GalleryService.getGalleryVOByGalleryId(id, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error loading gallery object");
    }

    public static JsonAdminResponse<Void> toggleGalleryPublish(Long id, String guid, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()) {
            return GalleryService.togglePostPublish(id, guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error toggling gallery publish status");
    }

    public static JsonAdminResponse<Void> deleteGallery(Long id, String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return GalleryService.deleteGallery(id, guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error deleting gallery");
    }

    // articles

    public static JsonAdminResponse<ImageVO> getArticleTitleImageVO(Long parentObjectId, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return ArticleService.getArticleTitleImageVO(parentObjectId, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error loading article title image");
    }

    public static JsonAdminResponse<ArticleVO> getArticleVOByArticleId(Long id, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(ArticleService.getArticleVObyArticleId(id, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error loading article data");
    }

    public static JsonAdminResponse<List<ArticleVO>> listAllArticleVOs(String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()){
            return ArticleService.listAllArticleVOsUserFilter(guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing articles");
    }

    public static JsonAdminResponse<List<ArticleVO>> listAllArticleVOsNoFilter(SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(ArticleService.listAllArticleVOs(null, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing articles");
    }

    public static JsonAdminResponse<Long> saveOrUpdateArticle(ArticleTO articleTO, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return ArticleService.createOrUpdateArticle(articleTO, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error saving or updating article");
    }

    public static JsonAdminResponse<Void> toggleArticlePublish(Long id, String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return ArticleService.toggleArticlePublish(id, guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error toggling article publish status");
    }

    public static JsonAdminResponse<Void> deleteArticle(Long id, String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return ArticleService.deleteArticle(id, guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error deleting article");
    }

    public static JsonAdminResponse<Void> setArticleTitleImageId(ArticleTO articleTO, String guid, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return ArticleService.setArticleTitleImageId(articleTO, guid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error setting article title image id");
    }


    public static JsonAdminResponse<RelationTO> listRelationsForPost(PostTO postTO, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return RelationService.getRelationTO(postTO, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing relations for post");
    }

    public static JsonAdminResponse<Void> createNewRelation(String guid, RelationVO relationVOPartial, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return RelationService.createNewRelation(guid, relationVOPartial, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error creating new relation");
    }

    public static JsonAdminResponse<Void> deleteRelation(String guid, Long relationId, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return RelationService.deleteRelation(guid, relationId, session);
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error deleting relation");
    }
    
    public static JsonAdminResponse<List<ArticleVO>> listConcernedArticlesVOs(PostTO postTO, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(RelationService.listConcernedArticlesVOs(postTO, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing concerned articles for relation");
    }

    public static JsonAdminResponse<List<PhotoVO>> listConcernedPhotosVOs(PostTO postTO, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(RelationService.listConcernedPhotosVOs(postTO, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing concerned articles for photo");
    }

    public static JsonAdminResponse<List<GalleryVO>> listConcernedGalleryVOs(PostTO postTO, SessionFactory sessionFactory){

        try (Session session = sessionFactory.openSession()) {
            return JsonAdminResponse.success(RelationService.listConcernedGalleryVOs(postTO, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing concerned galleries for relation");
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



}
