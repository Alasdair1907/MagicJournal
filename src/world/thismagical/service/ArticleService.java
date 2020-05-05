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

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.dao.FileDao;
import world.thismagical.dao.ArticleDao;
import world.thismagical.dao.TagDao;
import world.thismagical.entity.AuthorEntity;
import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.TagEntity;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.ArticleTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.PrivilegeLevel;
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
        articleVO.tagEntityList = TagService.listTagsForObject(PostAttribution.ARTICLE, articleId, session).data;

        return articleVO;
    }

    @SuppressWarnings("unchecked")
    public static List<ArticleVO> listAllArticleVOs(BasicPostFilter basicPostFilter, Session session){
        List<ArticleEntity> articleEntityList = (List<ArticleEntity>) (List) ArticleDao.listAllPosts(basicPostFilter, ArticleEntity.class, session);

        List<ArticleVO> articleVOList = new ArrayList<>();
        List<Long> imageIds = articleEntityList.stream().map(ArticleEntity::getTitleImageId).collect(Collectors.toList());
        List<Long> articleIds = articleEntityList.stream().map(ArticleEntity::getId).collect(Collectors.toList());

        List<ImageVO> imageVOList = FileDao.getImagesByIds(imageIds, session);
        List<TagEntity> tagEntityList = TagDao.listTagsForObjects(PostAttribution.ARTICLE, articleIds, session);

        for (ArticleEntity articleEntity : articleEntityList){
            ArticleVO articleVO = new ArticleVO(articleEntity);
            if (imageVOList != null) {
                articleVO.titleImageVO = imageVOList.stream().filter(imageVO -> imageVO.thisObjId.equals(articleEntity.getTitleImageId())).findAny().orElse(null);
            }

            articleVO.tagEntityList = tagEntityList.stream()
                    .filter(it -> it.getAttributionClass() == PostAttribution.ARTICLE && it.getParentObjectId().equals(articleEntity.getId())).collect(Collectors.toList());

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
        Boolean newArticle = false;

        if (articleEntity != null){
            articleEntityAuthor = articleEntity.getAuthor();
        } else {
            newArticle = true;
            articleEntityAuthor = currentAuthorEntity;
        }

        if (!AuthorizationService.checkPrivileges(articleEntityAuthor, currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        if (articleEntity == null) {
            articleEntity = new ArticleEntity();
            articleEntity.setAuthor(currentAuthorEntity);
        }

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

        if (!newArticle) {
            RelationService.updateArticleGalleryRelations(articleEntity.getId(), articleTO.articleText, session);
        }

        return JsonAdminResponse.success(articleEntity.getId());
    }

    public static JsonAdminResponse<ImageVO> getArticleTitleImageVO(Long parentObjectId, Session session){

        if (parentObjectId == null){
            return JsonAdminResponse.fail("getArticleTitleImageVO: no article ID provided");
        }

        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(parentObjectId, ArticleEntity.class, session);
        return JsonAdminResponse.success(FileDao.getImageById(articleEntity.getTitleImageId(), session));
    }

    public static JsonAdminResponse<Void> deleteArticle(Long id, String guid, Session session){

        if (id == null){
            return JsonAdminResponse.fail("deleteArticle: no id provided");
        }

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
