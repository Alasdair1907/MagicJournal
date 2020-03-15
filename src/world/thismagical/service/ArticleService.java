package world.thismagical.service;
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

import org.hibernate.Session;
import world.thismagical.dao.FileDao;
import world.thismagical.dao.ArticleDao;
import world.thismagical.dao.TagDao;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.ArticleEntity;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.ArticleTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.Tools;
import world.thismagical.vo.ImageVO;
import world.thismagical.vo.ArticleVO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ArticleService {

    public static ArticleVO getArticleVObyArticleId(Long articleId, Session session){
        ArticleEntity articleEntity = ArticleDao.getArticleEntityById(articleId, session);
        ArticleVO articleVO = new ArticleVO(articleEntity);

        articleVO.titleImageVO = FileDao.getImageById(articleEntity.getTitleImageId(), session);

        return articleVO;
    }

    public static List<ArticleVO> listAllArticleVOs(AuthorEntity authorFilter, Session session){
        List<ArticleEntity> articleEntityList = ArticleDao.listAllArticles(authorFilter, session);

        List<ArticleVO> articleVOList = new ArrayList<>();
        List<Long> imageIds = articleEntityList.stream().map(ArticleEntity::getTitleImageId).collect(Collectors.toList());

        List<ImageVO> imageVOList = FileDao.getImagesByIds(imageIds, session);

        for (ArticleEntity articleEntity : articleEntityList){
            ArticleVO articleVO = new ArticleVO(articleEntity);
            if (imageVOList != null) {
                articleVO.titleImageVO = imageVOList.stream().filter(imageVO -> imageVO.thisObjId.equals(articleEntity.getTitleImageId())).findAny().orElse(null);
            }
            articleVOList.add(articleVO);
        }

        return articleVOList;
    }

    public static JsonAdminResponse<Long> createOrUpdateArticle(ArticleTO articleTO, Session session){

        JsonAdminResponse<Long> jsonAdminResponse = new JsonAdminResponse<>();

        if (articleTO == null){
            return JsonAdminResponse.fail("ArticleTO: null argument");
        }

        String sessionGuid = articleTO.sessionGuid;

        ArticleEntity articleEntity = ArticleDao.getArticleEntityById(articleTO.id, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(sessionGuid, session);

        AuthorEntity articleEntityAuthor = null;

        if (articleEntity != null){
            articleEntityAuthor = articleEntity.getAuthor();
        } else {
            articleEntityAuthor = currentAuthorEntity;
        }

        if (!AuthorizationService.checkPrivileges(articleEntityAuthor, currentAuthorEntity, jsonAdminResponse)){
            return jsonAdminResponse;
        }

        if (articleEntity == null) {
            articleEntity = new ArticleEntity();
        }

        articleEntity.setAuthor(currentAuthorEntity);

        if (articleEntity.getCreationDate() == null){
            articleEntity.setCreationDate(LocalDateTime.now());
        }

        articleEntity.setTitle(Tools.nullToEmpty(articleTO.title));
        articleEntity.setDescription(Tools.nullToEmpty(articleTO.description));
        articleEntity.setGpsCoordinates(Tools.nullToEmpty(articleTO.gpsCoordinates));
        articleEntity.setPublished(articleTO.published);

        if (articleTO.titleImageId != null) {
            articleEntity.setTitleImageId(articleTO.titleImageId);
        }
        articleEntity.setArticleText(articleTO.articleText); // TODO: analyze file usage with Link Service

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(articleEntity);
        session.flush();

        jsonAdminResponse.data = articleEntity.getId();
        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Void> deleteArticle(Long id, String guid, Session session){
        ArticleEntity articleEntity = ArticleDao.getArticleEntityById(id, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        JsonAdminResponse<Void> jsonAdminResponse = new JsonAdminResponse<>();

        if (!AuthorizationService.checkPrivileges(articleEntity.getAuthor(), currentAuthorEntity, jsonAdminResponse)){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "unauthorized action";
            return jsonAdminResponse;
        }

        List<ImageVO> articleImages = FileDao.getImages(PostAttribution.ARTICLE, Collections.singletonList(id), session);
        FileHandlingService.deleteImages(articleImages, session);

        ArticleDao.deleteArticle(id, session);
        TagDao.truncateTags(id, PostAttribution.ARTICLE.getId(), session);

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Void> toggleArticlePublish(Long id, String guid, Session session){

        ArticleEntity articleEntity = ArticleDao.getArticleEntityById(id, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        JsonAdminResponse<Void> jsonAdminResponse = new JsonAdminResponse<>();

        if (!AuthorizationService.checkPrivileges(articleEntity.getAuthor(), currentAuthorEntity, jsonAdminResponse)){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "unauthorized action";
            return jsonAdminResponse;
        }

        ArticleDao.toggleArticlePublish(id, session);

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Void> setArticleTitleImageId(ArticleTO articleTO, String guid, Session session){

        JsonAdminResponse<Void> jsonAdminResponse = new JsonAdminResponse<>();
        if (articleTO == null || articleTO.id == null || articleTO.titleImageId == null){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "setArticleTitleImageId: null argument";
            return jsonAdminResponse;
        }

        ArticleEntity articleEntity = ArticleDao.getArticleEntityById(articleTO.id, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(articleEntity.getAuthor(), currentAuthorEntity, jsonAdminResponse)){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "unauthorized action";
            return jsonAdminResponse;
        }

        ArticleDao.updateArticleTitleImageId(articleTO.id, articleTO.titleImageId, session);

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

}
