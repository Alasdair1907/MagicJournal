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
        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(articleId, ArticleEntity.class, session);
        ArticleVO articleVO = new ArticleVO(articleEntity);

        articleVO.titleImageVO = FileDao.getImageById(articleEntity.getTitleImageId(), session);

        return articleVO;
    }

    @SuppressWarnings("unchecked")
    public static List<ArticleVO> listAllArticleVOs(AuthorEntity authorFilter, Session session){
        List<ArticleEntity> articleEntityList = (List<ArticleEntity>) (List) ArticleDao.listAllPosts(authorFilter, ArticleEntity.class, session);

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

        if (articleTO == null){
            return JsonAdminResponse.fail("ArticleTO: null argument");
        }

        String sessionGuid = articleTO.sessionGuid;

        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(articleTO.id, ArticleEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(sessionGuid, session);

        AuthorEntity articleEntityAuthor = null;

        if (articleEntity != null){
            articleEntityAuthor = articleEntity.getAuthor();
        } else {
            articleEntityAuthor = currentAuthorEntity;
        }

        if (!AuthorizationService.checkPrivileges(articleEntityAuthor, currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
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
        articleEntity.setArticleText(articleTO.articleText);


        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(articleEntity);
        session.flush();

        RelationService.updateArticleGalleryRelations(articleEntity.getId(), articleTO.articleText, session);

        return JsonAdminResponse.success(articleEntity.getId());
    }

    public static JsonAdminResponse<Void> deleteArticle(Long id, String guid, Session session){
        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(id, ArticleEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(articleEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        List<ImageVO> articleImages = FileDao.getImages(PostAttribution.ARTICLE, Collections.singletonList(id), session);
        FileHandlingService.deleteImages(articleImages, session);

        ArticleDao.deleteEntity(id, ArticleEntity.class, session);
        TagDao.truncateTags(id, PostAttribution.ARTICLE.getId(), session);

        return JsonAdminResponse.success(null);
    }

    public static JsonAdminResponse<Void> toggleArticlePublish(Long id, String guid, Session session){

        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(id, ArticleEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(articleEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        ArticleDao.togglePostPublish(id, ArticleEntity.class, session);

        return JsonAdminResponse.success(null);
    }

    public static JsonAdminResponse<Void> setArticleTitleImageId(ArticleTO articleTO, String guid, Session session){

        if (articleTO == null || articleTO.id == null || articleTO.titleImageId == null){
            return JsonAdminResponse.fail("setArticleTitleImageId: null argument");
        }

        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(articleTO.id, ArticleEntity.class, session);
        AuthorEntity currentAuthorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(articleEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        ArticleDao.updateArticleTitleImageId(articleTO.id, articleTO.titleImageId, session);

        return JsonAdminResponse.success(null);
    }

}
