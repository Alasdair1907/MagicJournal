package world.thismagical.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.dao.AuthorDao;
import world.thismagical.dao.SessionDao;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.SessionEntity;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.PostsTO;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.ArticleVO;
import world.thismagical.vo.AuthorVO;
import world.thismagical.vo.GalleryVO;
import world.thismagical.vo.PhotoVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static world.thismagical.dao.AuthorDao.getAuthorEntityByLogin;

public class AuthorService {
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

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

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

        res.success = true;
        return res;
    }


    public static JsonAdminResponse<PostsTO> getAuthorPostsVOs(Long authorId, Session session){
        AuthorEntity authorEntity = AuthorDao.getAuthorEntityById(authorId, session);

        if (authorEntity == null){
            return JsonAdminResponse.fail("author with this id not found");
        }

        List<ArticleVO> articleVOList = ArticleService.listAllArticleVOs(authorEntity, session);
        List<PhotoVO> photoVOList = PhotoService.listAllPhotoVOs(authorEntity, session);
        List<GalleryVO> galleryVOList = GalleryService.listAllGalleryVOs(authorEntity, session);

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

}
