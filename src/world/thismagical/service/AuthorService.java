package world.thismagical.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.dao.AuthorDao;
import world.thismagical.dao.FileDao;
import world.thismagical.dao.SessionDao;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.ImageFileEntity;
import world.thismagical.entity.SessionEntity;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.PostsTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.ArticleVO;
import world.thismagical.vo.AuthorVO;
import world.thismagical.vo.GalleryVO;
import world.thismagical.vo.PhotoVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static world.thismagical.dao.AuthorDao.getAuthorEntityByLogin;

public class AuthorService {



    public static JsonAdminResponse<Void> changeBasicAuthorParams(String guid, Long targetAuthorId, String newDisplayName, String newPassword, Short newAccessLevelId, Session session){

        if (targetAuthorId == null){
            return JsonAdminResponse.fail("illegal argument");
        }

        if (newDisplayName == null && newPassword == null && newAccessLevelId == null){
            return JsonAdminResponse.fail("nothing to do");
        }

        if (newDisplayName != null && newDisplayName.isEmpty()){
            return JsonAdminResponse.fail("display name can not be empty");
        }

        if (newPassword != null && newPassword.isEmpty()){
            return JsonAdminResponse.fail("password can not be empty");
        }


        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);
        if (currentAuthorEntity == null){
            return JsonAdminResponse.fail("no valid session found");
        }

        AuthorEntity targetAuthorEntity = AuthorDao.getAuthorEntityById(targetAuthorId, session);

        if (!AuthorizationService.checkPrivileges(targetAuthorEntity, currentAuthorEntity)){
            return JsonAdminResponse.fail("not enough privileges to perform this action");
        }

        if (newDisplayName != null) {
            targetAuthorEntity.setDisplayName(newDisplayName);
        }

        if (newPassword != null) {
            targetAuthorEntity.setPasswd(Tools.sha256(newPassword));
        }

        if (newAccessLevelId != null){
            PrivilegeLevel privilegeLevel = PrivilegeLevel.getPrivilegeLevel(newAccessLevelId);
            targetAuthorEntity.setPrivilegeLevel(privilegeLevel);
        }

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.update(targetAuthorEntity);
        session.flush();

        return JsonAdminResponse.success(null);
    }

    public static void loadProfilePicture(AuthorVO authorVO, Session session){
        List<ImageFileEntity> profilePicture = FileDao.getImageEntities(PostAttribution.PROFILE, Collections.singletonList(authorVO.id), session);
        if (profilePicture != null && !profilePicture.isEmpty()){
            authorVO.pictureFileName = profilePicture.get(0).getThumbnailFileName();
        }
    }

    public static JsonAdminResponse<AuthorVO> getAuthorVOByGuid(String guid, Session session){
        if (guid == null){
            return JsonAdminResponse.fail("null guid");
        }

        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (authorEntity == null){
            return JsonAdminResponse.fail("invalid session guid");
        }

        AuthorVO authorVO = new AuthorVO(authorEntity);
        loadProfilePicture(authorVO, session);

        return JsonAdminResponse.success(authorVO);
    }

    public static List<AuthorVO> getAllAuthorsVOList(Session session){
        List<AuthorEntity> authorEntities = AuthorDao.listAllAuthors(session);
        List<AuthorVO> authorVOList = new ArrayList<>();

        for (AuthorEntity authorEntity : authorEntities){
            AuthorVO authorVO = new AuthorVO(authorEntity, true, session);

            /*
            we don't want potentially huge lists of posts attached to every author in this list.
             */
            authorVO.postsTO = null;
            authorVOList.add(authorVO);
        }

        return authorVOList;
    }

    public static JsonAdminResponse<Void> createNewAuthor(String guid, AuthorEntity newAuthor, Session session){


        if (!AuthorizationService.isSessionValid(guid, PrivilegeLevel.PRIVILEGE_SUPER_USER, session)){
            return JsonAdminResponse.fail("only superuser can do that");
        }

        if (newAuthor.getLogin() == null || newAuthor.getPasswd() == null || newAuthor.getDisplayName() == null || newAuthor.getPrivilegeLevel() == null){
            JsonAdminResponse.fail("createNewAuthor: null argument");
        }

        if (newAuthor.getLogin().isEmpty() || newAuthor.getPasswd().isEmpty() || newAuthor.getDisplayName().isEmpty()){
            JsonAdminResponse.fail("Can not create new author: all parameters must be filled");
        }

        AuthorEntity existingAuthor = AuthorDao.getAuthorEntityByLogin(newAuthor.getLogin(), session);
        if (existingAuthor != null){
            JsonAdminResponse.fail("user with this login already exists");
        }

        String plainTextPassword = newAuthor.getPasswd();
        String hashedPassword = Tools.sha256(plainTextPassword);
        newAuthor.setPasswd(hashedPassword);

        session.save(newAuthor);
        session.flush();

        return JsonAdminResponse.success(null);
    }


    public static JsonAdminResponse<PostsTO> getAuthorPostsVOs(Long authorId, Session session){
        AuthorEntity authorEntity = AuthorDao.getAuthorEntityById(authorId, session);

        if (authorEntity == null){
            return JsonAdminResponse.fail("author with this id not found");
        }

        BasicPostFilter basicPostFilter = new BasicPostFilter();
        basicPostFilter.authorEntity = authorEntity;

        List<ArticleVO> articleVOList = ArticleService.listAllArticleVOs(basicPostFilter, session);
        List<PhotoVO> photoVOList = PhotoService.listAllPhotoVOs(basicPostFilter, session);
        List<GalleryVO> galleryVOList = GalleryService.listAllGalleryVOs(basicPostFilter, session);

        PostsTO postsTO = new PostsTO();
        postsTO.articles = articleVOList;
        postsTO.galleries = galleryVOList;
        postsTO.photos = photoVOList;

        return JsonAdminResponse.success(postsTO);
    }

    public static JsonAdminResponse<Void> deleteAuthor(String guid, Long authorId, Session session){

        if (guid == null){
            return JsonAdminResponse.fail("authorization error");
        }
        if (authorId == null){
            return JsonAdminResponse.fail("null authorId");
        }

        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);
        AuthorEntity targetAuthorEntity = AuthorDao.getAuthorEntityById(authorId, session);

        if (currentAuthorEntity == null){
            return JsonAdminResponse.fail("authorization error");
        }
        if (targetAuthorEntity == null){
            return JsonAdminResponse.fail("invalid target author id");
        }
        if (!AuthorizationService.checkPrivileges(targetAuthorEntity, currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        PostsTO authorPosts = getAuthorPostsVOs(targetAuthorEntity.getAuthorId(), session).data;
        if (authorPosts != null && !authorPosts.isEmpty()){
            Tools.log("[WARN] attempt to delete user with posts. epic bug or hacking attempt. action by: "+currentAuthorEntity.getLogin()+" attempt to delete: "+targetAuthorEntity.getLogin());
            return JsonAdminResponse.fail("user has posts");
        }


        SessionEntity sessionEntity = SessionDao.getSessionEntity(targetAuthorEntity.getLogin(), session);

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        if (sessionEntity != null) {
            session.delete(sessionEntity);
        }
        session.delete(targetAuthorEntity);

        session.flush();
        return JsonAdminResponse.success(null);
    }

    public static JsonAdminResponse<Void> updateAuthorProfile(String guid, AuthorVO authorVO, Session session){
        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (authorEntity == null){
            return JsonAdminResponse.fail("unauthorized action");
        }

        if (!authorEntity.getLogin().equals(authorVO.login)){
            Tools.log("[ERROR] updateAuthorProfile: requested change for "+authorVO.login+" by "+authorEntity.getLogin());
            return JsonAdminResponse.fail("invalid operation");
        }

        authorEntity.setBio(authorVO.bio);
        authorEntity.setEmail(authorVO.email);
        authorEntity.setPersonalWebsite(authorVO.website);

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.update(authorEntity);
        session.flush();

        return JsonAdminResponse.success(null);
    }

}
