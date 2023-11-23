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
import world.thismagical.dao.*;
import world.thismagical.entity.*;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.ArticleTO;
import world.thismagical.util.BBCodeExtractor;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.ImageVO;
import world.thismagical.vo.ArticleVO;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static world.thismagical.service.AuthorizationService.getAuthorEntityBySessionGuid;

public class ArticleService extends PostService {


    public static ArticleVO getArticleVObyArticleId(Long articleId, String userGuid, Session session){
        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(articleId, ArticleEntity.class, session);
        AuthorizationService.checkUnpublishedViewPrivileges(articleEntity, userGuid, session);
        ArticleVO articleVO = new ArticleVO(articleEntity);

        articleVO.titleImageVO = FileDao.getImageById(articleEntity.getTitleImageId(), session);
        articleVO.tagEntityList = TagService.listTagsForObject(PostAttribution.ARTICLE, articleId, session).data;

        return articleVO;
    }

    public static ArticleVO getArticleVObyArticleIdPreprocessed(Long articleId, String userGuid, Session session){
        ArticleVO articleVO = getArticleVObyArticleId(articleId, userGuid, session);
        articleVO.articleText = BBCodeExtractor.preprocess(articleVO.articleText, session);
        return articleVO;
    }

    public static List<ArticleVO> articleEntitiesToArticleVOs(List<ArticleEntity> articleEntityList, Session session){
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

    @SuppressWarnings("unchecked")
    public static List<ArticleVO> listAllArticleVOs(BasicPostFilter basicPostFilter, Session session){
        basicPostFilter.verifyGuid(session);
        List<ArticleEntity> articleEntityList = (List<ArticleEntity>) (List) ArticleDao.listAllPosts(basicPostFilter, ArticleEntity.class, session);
        return articleEntitiesToArticleVOs(articleEntityList, session);
    }

    public static JsonAdminResponse<Long> createOrUpdateArticle(ArticleTO articleTO, Session session){

        if (articleTO == null){
            return JsonAdminResponse.fail("ArticleTO: null argument");
        }

        String sessionGuid = articleTO.sessionGuid;

        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(articleTO.id, ArticleEntity.class, session);
        AuthorEntity currentAuthorEntity = getAuthorEntityBySessionGuid(sessionGuid, session);

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

        articleEntity.setLastModifiedDate(LocalDateTime.now());
        articleEntity.setTitle(Tools.nullToEmpty(articleTO.title));
        articleEntity.setDescription(Tools.nullToEmpty(articleTO.description));
        articleEntity.setTinyDescription(Tools.nullToEmpty(articleTO.tinyDescription));
        articleEntity.setGpsCoordinates(Tools.nullToEmpty(articleTO.gpsCoordinates));

        if (articleTO.titleImageId != null) {
            articleEntity.setTitleImageId(articleTO.titleImageId);
        }

        articleEntity.setArticleText(articleTO.articleText);

        savePostGeneralProcedures(newArticle, articleEntityAuthor, articleEntity, PostAttribution.ARTICLE, session);

        if (!newArticle) {
            // we're not updating it when the article is completely new, because the frontend first creates a completely
            // empty article, and when the user puts text into it and clicks 'save', the empty article gets updated
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


    public static JsonAdminResponse<Void> setArticleTitleImageId(ArticleTO articleTO, String guid, Session session){

        if (articleTO == null || articleTO.id == null || articleTO.titleImageId == null){
            return JsonAdminResponse.fail("setArticleTitleImageId: null argument");
        }

        ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(articleTO.id, ArticleEntity.class, session);
        AuthorEntity currentAuthorEntity = getAuthorEntityBySessionGuid(guid, session);

        if (!AuthorizationService.checkPrivileges(articleEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        ArticleDao.updateArticleTitleImageId(articleTO.id, articleTO.titleImageId, session);

        return JsonAdminResponse.success(null);
    }

}
