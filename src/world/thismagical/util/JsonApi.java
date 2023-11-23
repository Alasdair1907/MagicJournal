package world.thismagical.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.dao.*;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.filter.BasicFileFilter;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.filter.PagingRequestFilter;
import world.thismagical.service.*;
import world.thismagical.to.*;
import world.thismagical.vo.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class JsonApi {

    /*
    todo:
    - all post listings should be limited to some number of posts for unauthenticated users
     */

    public static Map<String, String> actionToErrorMessage;
    public static Map<String, JsonFunction> actionToFunction;
    static {
        actionToErrorMessage = new HashMap<>();
        actionToFunction = new HashMap<>();

        /*
        Homepage main method for listing all three posts at once
         */

        actionToErrorMessage.put("listHomepage", "Error listing posts");
        actionToFunction.put("listHomepage", (JsonApiRequestContext request) -> {

            List<BasicPostFilterTO> postFilters = null;
            if (request.data != null && !request.data.isEmpty()){
                 postFilters =  request.objectMapper.readValue(request.data, new TypeReference<List<BasicPostFilterTO>>(){});
            }

            if (postFilters == null || postFilters.isEmpty()){
                return JsonAdminResponse.fail("Error listing posts");
            }

            List<Object> result = new ArrayList<>();

            // articles
            BasicPostFilter basicPostFilterArticles = BasicPostFilter.fromTO(postFilters.get(0), request.session);
            result.add(ArticleService.listAllArticleVOs(basicPostFilterArticles, request.session));

            // photos
            BasicPostFilter basicPostFilterPhotos = BasicPostFilter.fromTO(postFilters.get(1), request.session);
            result.add(PhotoService.listAllPhotoVOs(basicPostFilterPhotos, request.session));

            // galleries
            BasicPostFilter basicPostFilterGalleries = BasicPostFilter.fromTO(postFilters.get(2), request.session);
            result.add(GalleryService.listAllGalleryVOs(basicPostFilterGalleries, basicPostFilterGalleries.galleryRepresentationImages, request.session));

            // tags
            result.add(TagService.getTagDigestVOList(request.session).data);

            return JsonAdminResponse.success(result);
        });

        /*
        Authorization
         */

        actionToErrorMessage.put("verifySessionGuid", "Unable to verify session guid");
        actionToFunction.put("verifySessionGuid", (JsonApiRequestContext request) -> {
            if (AuthorizationService.isSessionValid(request.userGuid, request.session)){
                return JsonAdminResponse.success(null);
            }
            return JsonAdminResponse.fail("User session can not be verified.");
        });

        actionToErrorMessage.put("authorize", "Unable to authorize user");
        actionToFunction.put("authorize", (JsonApiRequestContext request) -> {
            AuthVO authVO = request.objectMapper.readValue(request.data, AuthVO.class);
            return AuthorizationService.authorize(authVO.login, authVO.passwordHash, request.session);
        });

        actionToErrorMessage.put("authorizeDemo", "Unable to create a demo user");
        actionToFunction.put("authorizeDemo", (JsonApiRequestContext request) -> {
            return AuthorizationService.authorizeDemo(request.session);
        });

        /*
        Authors edit
         */

        actionToErrorMessage.put("listAllAuthorsVO", "Unable to obtain list of authors");
        actionToFunction.put("listAllAuthorsVO", (JsonApiRequestContext request) -> {
            return JsonAdminResponse.success(AuthorService.getAllAuthorsVOList(request.session));
        });

        actionToErrorMessage.put("listPrivileges", "Error obtaining list of privileges");
        actionToFunction.put("listPrivileges", (JsonApiRequestContext request) -> {
            return JsonAdminResponse.success(PrivilegeLevel.getPrivilegesList());
        });

        actionToErrorMessage.put("createNewAuthor", "Error creating new author");
        actionToFunction.put("createNewAuthor", (JsonApiRequestContext request) -> {
            AuthorEntity newAuthor = request.objectMapper.readValue(request.data, AuthorEntity.class);
            return AuthorService.createNewAuthor(request.userGuid, newAuthor, request.fsLocation, request.session);
        });

        actionToErrorMessage.put("changePassword", "Error changing password");
        actionToFunction.put("changePassword", (JsonApiRequestContext request) -> {
            ChangeAuthorDataRequest cadr = request.objectMapper.readValue(request.data, ChangeAuthorDataRequest.class);
            return AuthorService.changeBasicAuthorParams(request.userGuid, cadr.targetAuthorId, null, cadr.newPassword, null, request.session);
        });

        actionToErrorMessage.put("changeDisplayName", "Error changing display name");
        actionToFunction.put("changeDisplayName", (JsonApiRequestContext request) -> {
            ChangeAuthorDataRequest cadr = request.objectMapper.readValue(request.data, ChangeAuthorDataRequest.class);
            return AuthorService.changeBasicAuthorParams(request.userGuid, cadr.targetAuthorId, cadr.newDisplayName, null, null, request.session);
        });

        actionToErrorMessage.put("changeAccessLevel", "Error changing access level");
        actionToFunction.put("changeAccessLevel", (JsonApiRequestContext request) -> {
            ChangeAuthorDataRequest cadr = request.objectMapper.readValue(request.data, ChangeAuthorDataRequest.class);
            return AuthorService.changeBasicAuthorParams(request.userGuid, cadr.targetAuthorId, null, null, cadr.newAccessLevelId, request.session);
        });

        actionToErrorMessage.put("deleteUser", "Error deleting user");
        actionToFunction.put("deleteUser", (JsonApiRequestContext request) -> {
            Long userId = Long.parseLong(request.data);
            return AuthorService.deleteAuthor(request.userGuid, userId, request.session);
        });

        /*
        Author profile
         */

        actionToErrorMessage.put("getAuthorVOByGuid", "Unable to load author data");
        actionToFunction.put("getAuthorVOByGuid", (JsonApiRequestContext request) -> {
            return AuthorService.getAuthorVOByGuid(request.userGuid, request.session);
        });

        actionToErrorMessage.put("getAuthorVOByLogin", "Unable to load author data");
        actionToFunction.put("getAuthorVOByLogin", (JsonApiRequestContext request) -> {
            return AuthorService.getAuthorVOByLogin(request.data, request.session);
        });

        actionToErrorMessage.put("updateAuthorProfile", "Error updating author profile");
        actionToFunction.put("updateAuthorProfile", (JsonApiRequestContext request) -> {
            AuthorVO authorVO = request.objectMapper.readValue(request.data, AuthorVO.class);
            return AuthorService.updateAuthorProfile(request.userGuid, authorVO, request.session);
        });

        /*
        Photos edit
         */

        actionToErrorMessage.put("saveOrUpdatePhoto", "Error saving photo");
        actionToFunction.put("saveOrUpdatePhoto", (JsonApiRequestContext request) -> {
            PhotoTO photoTO = request.objectMapper.readValue(request.data, PhotoTO.class);
            return PhotoService.createOrUpdatePhoto(photoTO, request.session);
        });

        actionToErrorMessage.put("listAllPhotoVOs", "Error listing photos");
        actionToFunction.put("listAllPhotoVOs", (JsonApiRequestContext request) -> {
            BasicPostFilterTO basicPostFilterTO = null;
            if (request.data != null && !request.data.isEmpty()){
                basicPostFilterTO = request.objectMapper.readValue(request.data, BasicPostFilterTO.class);
            }
            BasicPostFilter basicPostFilter = BasicPostFilter.fromTO(basicPostFilterTO, request.session);
            return JsonAdminResponse.success(PhotoService.listAllPhotoVOs(basicPostFilter, request.session));
        });

        actionToErrorMessage.put("getPhotoVOByPhotoId", "Error getting photo by id");
        actionToFunction.put("getPhotoVOByPhotoId", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return JsonAdminResponse.success(PhotoService.getPhotoVObyPhotoId(id, request.userGuid, request.session));
        });

        actionToErrorMessage.put("getPhotoImageVO", "Error getting image for photo");
        actionToFunction.put("getPhotoImageVO", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return PhotoService.getPhotoImageVO(id, request.session);
        });

        actionToErrorMessage.put("togglePhotoPublish", "Error toggling photo publish status");
        actionToFunction.put("togglePhotoPublish", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return PhotoService.togglePostPublishStatus(id, PostAttribution.PHOTO, request.userGuid, request.session);
        });

        actionToErrorMessage.put("deletePhoto", "Error deleting photo");
        actionToFunction.put("deletePhoto", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return PhotoService.deletePost(id, PostAttribution.PHOTO, request.userGuid, request.session);
        });

        /*
        Tag Editor
         */

        actionToErrorMessage.put("listTags", "Error listing tags for object");
        actionToFunction.put("listTags", (JsonApiRequestContext request) -> {
            TagTO tagTO = request.objectMapper.readValue(request.data, TagTO.class);
            return TagService.listTagsForObjectStr(tagTO, request.session);
        });

        actionToErrorMessage.put("saveOrUpdateTags", "Error saving or updating tags");
        actionToFunction.put("saveOrUpdateTags", (JsonApiRequestContext request) -> {
            TagTO tagTO = request.objectMapper.readValue(request.data, TagTO.class);
            return TagService.saveOrUpdateTags(tagTO, request.userGuid, request.session);
        });

        // homepage widget - lists only tags for published posts
        actionToErrorMessage.put("getTagDigestVOList", "Error obtaining list of tags");
        actionToFunction.put("getTagDigestVOList", (JsonApiRequestContext request) -> {
            return TagService.getTagDigestVOList(request.session);
        });

        /*
        Gallery edit
         */

        actionToErrorMessage.put("listAllGalleryVOs", "Error listing galleries");
        actionToFunction.put("listAllGalleryVOs", (JsonApiRequestContext request) -> {
            BasicPostFilterTO basicPostFilterTO = null;
            if (request.data != null && !request.data.isEmpty()){
                basicPostFilterTO = request.objectMapper.readValue(request.data, BasicPostFilterTO.class);
            }
            BasicPostFilter basicPostFilter = BasicPostFilter.fromTO(basicPostFilterTO, request.session);

            Integer galleryRepresentationImages;
            if (basicPostFilter.galleryRepresentationImages == null){
                galleryRepresentationImages = BasicPostFilter.DEFAULT_GALLERY_REPRESENTATION_IMAGES;
            } else {
                galleryRepresentationImages = basicPostFilter.galleryRepresentationImages;
            }

            return JsonAdminResponse.success(GalleryService.listAllGalleryVOs(basicPostFilter, galleryRepresentationImages, request.session));
        });

        actionToErrorMessage.put("saveOrUpdateGallery", "Error saving or updating gallery");
        actionToFunction.put("saveOrUpdateGallery", (JsonApiRequestContext request) -> {
            GalleryTO galleryTO = request.objectMapper.readValue(request.data, GalleryTO.class);
            return GalleryService.createOrUpdateGallery(galleryTO, request.session);
        });

        actionToErrorMessage.put("getGalleryVOByGalleryId", "Error loading gallery object");
        actionToFunction.put("getGalleryVOByGalleryId", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return JsonAdminResponse.success(GalleryService.getGalleryVOByGalleryId(id, request.userGuid, request.session));
        });

        actionToErrorMessage.put("toggleGalleryPublish", "Error toggling gallery publish status");
        actionToFunction.put("toggleGalleryPublish", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return GalleryService.togglePostPublishStatus(id, PostAttribution.GALLERY, request.userGuid, request.session);
        });

        actionToErrorMessage.put("deleteGallery", "Error deleting gallery");
        actionToFunction.put("deleteGallery", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return GalleryService.deletePost(id, PostAttribution.GALLERY, request.userGuid, request.session);
        });

        /*
         Image Manager
         */
        actionToErrorMessage.put("listImageVOs", "Error listing images");
        actionToFunction.put("listImageVOs", (JsonApiRequestContext request) -> {
            ImageUploadTO imageTO = request.objectMapper.readValue(request.data, ImageUploadTO.class);
            return FileDao.getImages(imageTO.imageAttributionClass, imageTO.parentObjectId, request.session);
        });

        actionToErrorMessage.put("deleteFileEntity", "Error deleting file");
        actionToFunction.put("deleteFileEntity", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return FileHandlingService.deleteFile(id, request.userGuid, request.session);
        });

        actionToErrorMessage.put("updateFileDescription", "Error updating file description");
        actionToFunction.put("updateFileDescription", (JsonApiRequestContext request) -> {
            ImageFileDescrTO imageFileDescrTO = request.objectMapper.readValue(request.data, ImageFileDescrTO.class);
            return FileHandlingService.updateFileInfo(imageFileDescrTO, request.userGuid, request.session);
        });

        actionToErrorMessage.put("getImageFileDescrTO", "Error loading image description");
        actionToFunction.put("getImageFileDescrTO", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return FileHandlingService.getImageFileDescrTO(id, request.session);
        });

        /*
        Articles edit
         */

        actionToErrorMessage.put("saveOrUpdateArticle", "Error saving or updating article");
        actionToFunction.put("saveOrUpdateArticle", (JsonApiRequestContext request) -> {
            ArticleTO articleTO = request.objectMapper.readValue(request.data, ArticleTO.class);
            return ArticleService.createOrUpdateArticle(articleTO, request.session);
        });

        actionToErrorMessage.put("listAllArticleVOs", "Error listing articles");
        actionToFunction.put("listAllArticleVOs", (JsonApiRequestContext request) -> {
            BasicPostFilterTO basicPostFilterTO = null;
            if (request.data != null && !request.data.isEmpty()){
                basicPostFilterTO = request.objectMapper.readValue(request.data, BasicPostFilterTO.class);
            }
            BasicPostFilter basicPostFilter = BasicPostFilter.fromTO(basicPostFilterTO, request.session);
            return JsonAdminResponse.success(ArticleService.listAllArticleVOs(basicPostFilter, request.session));
        });

        actionToErrorMessage.put("getArticleVOByArticleId", "Error loading article data");
        actionToFunction.put("getArticleVOByArticleId", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return JsonAdminResponse.success(ArticleService.getArticleVObyArticleId(id, request.userGuid, request.session));
        });

        actionToErrorMessage.put("getArticleVOByArticleIdPreprocessed", "Error loading article data");
        actionToFunction.put("getArticleVOByArticleIdPreprocessed", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return JsonAdminResponse.success(ArticleService.getArticleVObyArticleIdPreprocessed(id, request.userGuid, request.session));
        });

        actionToErrorMessage.put("getArticleTitleImageVO", "Error loading article title image");
        actionToFunction.put("getArticleTitleImageVO", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return ArticleService.getArticleTitleImageVO(id, request.session);
        });

        actionToErrorMessage.put("toggleArticlePublish", "Error toggling article publish status");
        actionToFunction.put("toggleArticlePublish", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return ArticleService.togglePostPublishStatus(id, PostAttribution.ARTICLE, request.userGuid, request.session);
        });

        actionToErrorMessage.put("deleteArticle", "Error deleting article");
        actionToFunction.put("deleteArticle", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return ArticleService.deletePost(id, PostAttribution.ARTICLE, request.userGuid, request.session);
        });

        actionToErrorMessage.put("setArticleTitleImageId", "Error setting article title image ID");
        actionToFunction.put("setArticleTitleImageId", (JsonApiRequestContext request) -> {
            ArticleTO articleTO = request.objectMapper.readValue(request.data, ArticleTO.class);
            return ArticleService.setArticleTitleImageId(articleTO, request.userGuid, request.session);
        });

        /*
        Relations
         */

        actionToErrorMessage.put("listRelationsForPost", "Error listing relations for post");
        actionToFunction.put("listRelationsForPost", (JsonApiRequestContext request) -> {
            PostTO postTO = request.objectMapper.readValue(request.data, PostTO.class);
            return RelationService.getRelationTO(postTO, request.session);
        });

        actionToErrorMessage.put("createNewRelation", "Error creating new relation");
        actionToFunction.put("createNewRelation", (JsonApiRequestContext request) -> {
            RelationVO relationVoPartial = request.objectMapper.readValue(request.data, RelationVO.class);
            return RelationService.createNewRelation(request.userGuid, relationVoPartial, request.session);
        });

        actionToErrorMessage.put("deleteRelation", "Error deleting relation");
        actionToFunction.put("deleteRelation", (JsonApiRequestContext request) -> {
            Long relationId = Long.parseLong(request.data);
            return RelationService.deleteRelation(request.userGuid, relationId, request.session);
        });

        actionToErrorMessage.put("listConcernedArticlesVOs", "Error listing concerned articles for relation");
        actionToFunction.put("listConcernedArticlesVOs", (JsonApiRequestContext request) -> {
            PostTO postTO = request.objectMapper.readValue(request.data, PostTO.class);
            return JsonAdminResponse.success(RelationService.listConcernedPostVOs(postTO, PostAttribution.ARTICLE, request.session));
        });

        actionToErrorMessage.put("listConcernedPhotosVOs", "Error listing concerned photos for relation");
        actionToFunction.put("listConcernedPhotosVOs", (JsonApiRequestContext request) -> {
            PostTO postTO = request.objectMapper.readValue(request.data, PostTO.class);
            return JsonAdminResponse.success(RelationService.listConcernedPostVOs(postTO, PostAttribution.PHOTO, request.session));
        });

        actionToErrorMessage.put("listConcernedGalleryVOs", "Error listing concerned galleries for relation");
        actionToFunction.put("listConcernedGalleryVOs", (JsonApiRequestContext request) -> {
            PostTO postTO = request.objectMapper.readValue(request.data, PostTO.class);
            return JsonAdminResponse.success(RelationService.listConcernedPostVOs(postTO, PostAttribution.GALLERY, request.session));
        });

        /*
         Key value service - settings etc.
         */

        actionToErrorMessage.put("saveKeyValue", "Error saving key-value");
        actionToFunction.put("saveKeyValue", (JsonApiRequestContext request) -> {
            KeyValueTO keyValueTO = request.objectMapper.readValue(request.data, KeyValueTO.class);
            return KeyValueService.setValue(keyValueTO.key, keyValueTO.value, request.userGuid, request.session);
        });

        actionToErrorMessage.put("loadKeyValue", "Error getting key-value");
        actionToFunction.put("loadKeyValue", (JsonApiRequestContext request) -> {
            return KeyValueService.getValue(request.data, request.userGuid, request.session);
        });

        actionToErrorMessage.put("saveSettings", "Error saving settings");
        actionToFunction.put("saveSettings", (JsonApiRequestContext request) -> {
            SettingsTO settingsTO = request.objectMapper.readValue(request.data, SettingsTO.class);
            JsonAdminResponse<Void> res = SettingsService.saveSettings(request.userGuid, settingsTO, request.session);

            if (res.success){
                request.application.setAttribute("settingsTO", null);
            }

            return res;
        });

        actionToErrorMessage.put("getSettingsAuthed", "Error getting settings");
        actionToFunction.put("getSettingsAuthed", (JsonApiRequestContext request) -> {
            return SettingsService.getSettingsAuthed(request.userGuid, request.session);
        });

        actionToErrorMessage.put("getSettingsNoAuth", "Error getting settings");
        actionToFunction.put("getSettingsNoAuth", (JsonApiRequestContext request) -> {
            return SettingsService.getSettingsNoAuth(request.session);
        });

        actionToErrorMessage.put("getAboutPreprocessed", "Error loading preprocessed about page");
        actionToFunction.put("getAboutPreprocessed", (JsonApiRequestContext request) -> {
            return SettingsService.getAboutPreprocessed(request.session);
        });

        /*
        Non-image files
         */

        actionToErrorMessage.put("listOtherFiles", "Error listing files");
        actionToFunction.put("listOtherFiles", (JsonApiRequestContext request) -> {
            BasicFileFilter basicFileFilter = request.objectMapper.readValue(request.data, BasicFileFilter.class);
            return FileHandlingService.listOtherFiles(basicFileFilter, request.session);
        });

        actionToErrorMessage.put("deleteOtherFile", "Error deleting file");
        actionToFunction.put("deleteOtherFile", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return FileHandlingService.deleteOtherFile(request.userGuid, id, request.session);
        });

        actionToErrorMessage.put("getOtherFileById", "Error loading file");
        actionToFunction.put("getOtherFileById", (JsonApiRequestContext request) -> {
            Long id = Long.parseLong(request.data);
            return FileHandlingService.getOtherFileById(id, request.session);
        });

        actionToErrorMessage.put("updateOtherFileInfo", "Error updating file info");
        actionToFunction.put("updateOtherFileInfo", (JsonApiRequestContext request) -> {
            OtherFileTO otherFileTO = request.objectMapper.readValue(request.data, OtherFileTO.class);
            return FileHandlingService.updateOtherFileInfo(otherFileTO, request.session);
        });

        /*
        CDA
         */

        actionToErrorMessage.put("processPagingRequest", "Error processing paging request");
        actionToFunction.put("processPagingRequest", (JsonApiRequestContext request) -> {
            PagingRequestFilter pagingRequestFilter = request.objectMapper.readValue(request.data, PagingRequestFilter.class);
            return JsonAdminResponse.success(PagingService.get(pagingRequestFilter, request.session));
        });

        actionToErrorMessage.put("processPagingRequestUnified", "Error processing paging request");
        actionToFunction.put("processPagingRequestUnified", (JsonApiRequestContext request) -> {
            PagingRequestFilter pagingRequestFilter = request.objectMapper.readValue(request.data, PagingRequestFilter.class);
            PostVOList postVOList = PagingService.get(pagingRequestFilter, request.session);
            PostVOListUnified postVOListUnified = PagingService.unify(postVOList);
            return JsonAdminResponse.success(postVOListUnified);
        });

        actionToErrorMessage.put("getSidePanelPosts", "Error assembling sidepanel posts");
        actionToFunction.put("getSidePanelPosts", (JsonApiRequestContext request) -> {
            SidePanelRequestTO sidePanelRequestTO = request.objectMapper.readValue(request.data, SidePanelRequestTO.class);

            SidePanelPostsTO sidePanelPostsTO = new SidePanelPostsTO();

            PagingRequestFilter pagingRequestFilter = PagingRequestFilter.latest(sidePanelRequestTO.limitLatest);
            PostVOList postVOList = PagingService.get(pagingRequestFilter, request.session);
            PostVOListUnified postVOListUnified = PagingService.unify(postVOList);
            sidePanelPostsTO.latest = postVOListUnified.posts;

            if (sidePanelRequestTO.postId != null && sidePanelRequestTO.postAttribution != null) {
                RelationService.fillRelevantPosts(sidePanelPostsTO, sidePanelRequestTO, request.session);
            }

            return JsonAdminResponse.success(sidePanelPostsTO);

        });

        actionToErrorMessage.put("preFilterTags", "Error preloading tags");
        actionToFunction.put("preFilterTags", (JsonApiRequestContext request) -> {
            PagingRequestFilter pagingRequestFilter = request.objectMapper.readValue(request.data, PagingRequestFilter.class);
            return JsonAdminResponse.success(PagingDao.preFilterTagEntities(pagingRequestFilter, request.session));
        });

        actionToErrorMessage.put("toBase64Utf8", "Error encoding to base64");
        actionToFunction.put("toBase64Utf8", (JsonApiRequestContext request) -> {
            return JsonAdminResponse.success(toBase64(request.data));
        });

        actionToErrorMessage.put("fromBase64Utf8", "Error decoding from base64");
        actionToFunction.put("fromBase64Utf8", (JsonApiRequestContext request) -> {
            return JsonAdminResponse.success(fromBase64(request.data));
        });


    }

    public static String processRequestWithSerialization(HttpServletRequest request, ServletContext application, SessionFactory sessionFactory, ObjectMapper objectMapper){
        String responseSerialized = "";

        try {
            responseSerialized = objectMapper.writeValueAsString(processRequest(request, application, sessionFactory, objectMapper));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return responseSerialized;
    }

    public static JsonAdminResponse processRequest(HttpServletRequest request, ServletContext application, SessionFactory sessionFactory, ObjectMapper objectMapper){

        JsonApiRequestContext jar = new JsonApiRequestContext();
        jar.action = request.getParameter("action");
        jar.data = request.getParameter("data");
        jar.userGuid = request.getParameter("guid");
        jar.fsLocation = request.getSession().getServletContext().getRealPath("/");
        jar.objectMapper = objectMapper;
        jar.application = application;

        if (jar.action == null || jar.action.isBlank()){
            return JsonAdminResponse.fail("Error: no action");
        }
        JsonAdminResponse response;

        try (Session session = sessionFactory.openSession()){
            jar.session = session;
            response = (JsonAdminResponse) actionToFunction.get(jar.action).apply(jar);
        } catch (Exception ex) {
            Tools.handleException(ex);

            if (actionToErrorMessage.containsKey(jar.action)) {
                response = JsonAdminResponse.fail(actionToErrorMessage.get(jar.action));
            } else {
                response = JsonAdminResponse.fail("Error processing request");
            }
        }

        return response;
    }


    public static PostVOListUnified latest(Integer count, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){

            PagingRequestFilter pagingRequestFilter = PagingRequestFilter.latest(count);
            PostVOList postVOList = PagingService.get(pagingRequestFilter, session);
            return PagingService.unify(postVOList);

        } catch (Exception ex){
            Tools.handleException(ex);
            return null;
        }
    }

    public static String toBase64(String input){
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String fromBase64(String input){
        return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
    }

}
