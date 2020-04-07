package world.thismagical.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.dao.*;
import world.thismagical.entity.*;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.PostTO;
import world.thismagical.to.RelationTO;
import world.thismagical.util.BBCodeExtractor;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.RelationClass;
import world.thismagical.vo.ArticleVO;
import world.thismagical.vo.GalleryVO;
import world.thismagical.vo.PhotoVO;
import world.thismagical.vo.RelationVO;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static world.thismagical.dao.FileDao.getImageEntitiesByIds;

public class RelationService {

    public static JsonAdminResponse<RelationTO> getRelationTO(PostTO postTO, Session session){

        if (postTO == null || postTO.postAttributionClass == null || postTO.postObjectId == null){
            return JsonAdminResponse.fail("getRelationTO: null argument");
        }

        List<RelationVO> relationVOList = listRelationsForPost(PostAttribution.getPostAttribution(postTO.postAttributionClass), postTO.postObjectId, session);

        if (relationVOList == null || relationVOList.isEmpty()){
            return JsonAdminResponse.success(RelationTO.getEmpty(postTO));
        }

        List<RelationVO> postsReferToThis = new ArrayList<>();
        List<RelationVO> currentPostRelatesTo = new ArrayList<>();

        postsReferToThis = relationVOList.stream()
                .filter(relationVO -> relationVO.dstAttributionClass.getId().equals(postTO.postAttributionClass) && relationVO.dstObjectId.equals(postTO.postObjectId))
                .collect(Collectors.toList());
        currentPostRelatesTo = relationVOList.stream()
                .filter(relationVO -> relationVO.srcAttributionClass.getId().equals(postTO.postAttributionClass) && relationVO.srcObjectId.equals(postTO.postObjectId))
                .collect(Collectors.toList());

        RelationTO relationTO = new RelationTO();
        relationTO.postTO = postTO;
        relationTO.postsReferToThis = postsReferToThis;
        relationTO.currentPostRelatesTo = currentPostRelatesTo;

        return JsonAdminResponse.success(relationTO);

    }

    public static List<RelationVO> listRelationsForPost(PostAttribution postAttribution, Long postId, Session session){
        List<RelationEntity> relationEntities = RelationDao.listRelationsForPost(postAttribution, postId, session);

        if (relationEntities == null || relationEntities.isEmpty()){
            return null;
        }

        return relationEntityListToVo(relationEntities, session);
    }

    public static List<RelationVO> relationEntityListToVo(List<RelationEntity> relationEntityList, Session session){
        if (relationEntityList == null){
            throw new IllegalArgumentException("relationEntityListToVo: null argument");
        }

        Entities entities = gatherRelationsEntities(relationEntityList, session);

        if (entities == null){
            return null;
        }

        List<RelationVO> relationVOList = new ArrayList<>();

        for (RelationEntity relationEntity : relationEntityList){
            RelationVO relationVO = new RelationVO();

            relationVO.relationId = relationEntity.getId();

            relationVO.srcAttributionClass = relationEntity.getSrcAttributionClass();
            relationVO.srcAttributionClassStr = relationEntity.getSrcAttributionClass().getReadable();
            relationVO.srcAttributionClassShort = relationEntity.getSrcAttributionClass().getId();
            relationVO.srcObjectId = relationEntity.getSrcObjectId();

            relationVO.dstAttributionClass = relationEntity.getDstAttributionClass();
            relationVO.dstAttributionClassStr = relationEntity.getDstAttributionClass().getReadable();
            relationVO.dstAttributionClassShort = relationEntity.getDstAttributionClass().getId();
            relationVO.dstObjectId = relationEntity.getDstObjectId();

            relationVO.relationClass = relationEntity.getRelationClass();
            relationVO.relationClassShort = relationEntity.getRelationClass().getId();

            relationVO.isAuto = relationVO.relationClass.getIsAuto();

            // TODO simplify

            switch (relationVO.srcAttributionClass){
                case ARTICLE:
                    relationVO.srcObjectTitle = entities.articleEntities.stream()
                            .filter(articleEntity -> articleEntity.getId().equals(relationVO.srcObjectId))
                            .map(ArticleEntity::getTitle).findFirst().orElse("");
                    break;
                case PHOTO:
                    relationVO.srcObjectTitle = entities.photoEntities.stream()
                            .filter(photoEntity -> photoEntity.getId().equals(relationVO.srcObjectId))
                            .map(PhotoEntity::getTitle).findFirst().orElse("");
                    break;
                case GALLERY:
                    relationVO.srcObjectTitle = entities.galleryEntities.stream()
                            .filter(galleryEntity -> galleryEntity.getId().equals(relationVO.srcObjectId))
                            .map(GalleryEntity::getTitle).findFirst().orElse("");
                    break;
            }

            switch (relationVO.dstAttributionClass){
                case ARTICLE:
                    relationVO.dstObjectTitle = entities.articleEntities.stream()
                            .filter(articleEntity -> articleEntity.getId().equals(relationVO.dstObjectId))
                            .map(ArticleEntity::getTitle).findFirst().orElse("");
                    break;
                case PHOTO:
                    relationVO.dstObjectTitle = entities.photoEntities.stream()
                            .filter(photoEntity -> photoEntity.getId().equals(relationVO.dstObjectId))
                            .map(PhotoEntity::getTitle).findFirst().orElse("");
                    break;
                case GALLERY:
                    relationVO.dstObjectTitle = entities.galleryEntities.stream()
                            .filter(galleryEntity -> galleryEntity.getId().equals(relationVO.dstObjectId))
                            .map(GalleryEntity::getTitle).findFirst().orElse("");
                    break;
            }

            relationVOList.add(relationVO);
        }

        return relationVOList;
    }

    @SuppressWarnings("unchecked")
    public static Entities gatherRelationsEntities(List<RelationEntity> relationEntityList, Session session){
        if (relationEntityList == null){
            throw new IllegalArgumentException("gatherRelationsEntities: null argument");
        }

        Set<Long> articleEntityIds = new HashSet<>();
        Set<Long> photoEntityIds = new HashSet<>();
        Set<Long> galleryEntityIds = new HashSet<>();

        for (RelationEntity relationEntity : relationEntityList){

            Long dstObjectId = relationEntity.getDstObjectId();
            Long srcObjectId = relationEntity.getSrcObjectId();

            switch (relationEntity.getDstAttributionClass()){
                case ARTICLE:
                    articleEntityIds.add(dstObjectId);
                    break;
                case PHOTO:
                    photoEntityIds.add(dstObjectId);
                    break;
                case GALLERY:
                    galleryEntityIds.add(dstObjectId);
                    break;
            }

            switch (relationEntity.getSrcAttributionClass()){
                case ARTICLE:
                    articleEntityIds.add(srcObjectId);
                    break;
                case PHOTO:
                    photoEntityIds.add(srcObjectId);
                    break;
                case GALLERY:
                    galleryEntityIds.add(srcObjectId);
                    break;
            }
        }

        Entities entities = new Entities();
        entities.articleEntities = (List<ArticleEntity>) (List) ArticleDao.getEntitiesByIds(new ArrayList<>(articleEntityIds), ArticleEntity.class, session);
        entities.photoEntities = (List<PhotoEntity>) (List) PhotoDao.getEntitiesByIds(new ArrayList<>(photoEntityIds), PhotoEntity.class, session);
        entities.galleryEntities = (List<GalleryEntity>) (List) GalleryDao.getEntitiesByIds(new ArrayList<>(galleryEntityIds), GalleryEntity.class, session);

        return entities;
    }

    public static JsonAdminResponse<Void> createNewRelation(String guid, RelationVO relationVoPartial, Session session){

        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (authorEntity == null){
            return JsonAdminResponse.fail("user session not found");
        }

        if (!AuthorizationService.userHasGeneralWritePrivileges(authorEntity)){
            return JsonAdminResponse.fail("insufficient privileges");
        }

        RelationEntity relationEntity = relationVoPartialToRelationEntity(relationVoPartial);
        RelationDao.saveRelation(relationEntity, session);

        return JsonAdminResponse.success(null);
    }

    public static JsonAdminResponse<Void> deleteRelation(String guid, Long relationId, Session session){

        if (guid == null || relationId == null){
            return JsonAdminResponse.fail("deleteRelation: null argument");
        }

        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (authorEntity == null){
            return JsonAdminResponse.fail("session not found");
        }

        if (!AuthorizationService.userHasGeneralWritePrivileges(authorEntity)){
            return JsonAdminResponse.fail("insufficient privileges");
        }

        RelationDao.deleteRelation(relationId, session);

        return JsonAdminResponse.success(null);
    }

    public static Set<Long> getSetOfConcernedPosts(PostTO postTO, PostAttribution targetPostAttribution, Set<Long> postIdsToFilter, Session session){

        Long postId = postTO.postObjectId;
        PostAttribution thisPostAttribution = PostAttribution.getPostAttribution(postTO.postAttributionClass);

        List<RelationVO> relationVOList = listRelationsForPost(thisPostAttribution, postId, session);

        if (relationVOList == null || relationVOList.isEmpty()){
            return new HashSet<>(postIdsToFilter);
        }

        if (postIdsToFilter == null || postIdsToFilter.isEmpty()){
            return new HashSet<>();
        }
        
        Set<Long> res = new HashSet<>(postIdsToFilter);

        for (RelationVO relationVO : relationVOList){
            if (relationVO.dstAttributionClass == thisPostAttribution && relationVO.dstObjectId.equals(postId)){
                if (relationVO.srcAttributionClass == targetPostAttribution){
                    res.remove(relationVO.srcObjectId);
                }
            }

            if (relationVO.srcAttributionClass == thisPostAttribution && relationVO.srcObjectId.equals(postId)){
                if (relationVO.dstAttributionClass == targetPostAttribution){
                    res.remove(relationVO.dstObjectId);
                }
            }
        }

        return res;
    }
    
    public static List<ArticleVO> listConcernedArticlesVOs(PostTO postTO, Session session){

        if (postTO == null || postTO.postObjectId == null || postTO.postAttributionClass == null){
            throw new IllegalArgumentException();
        }

        List<ArticleVO> articleVOList = ArticleService.listAllArticleVOs(null, session);
        if (articleVOList.isEmpty()){
            return new ArrayList<>();
        }

        Set<Long> articleIdSet = articleVOList.stream().map(it -> it.id).collect(Collectors.toSet());

        if (PostAttribution.getPostAttribution(postTO.postAttributionClass) == PostAttribution.ARTICLE){
            articleIdSet.remove(postTO.postObjectId);
        }

        Set<Long> articleIdSetFinal = getSetOfConcernedPosts(postTO, PostAttribution.ARTICLE, articleIdSet, session);
        return articleVOList.stream().filter(it -> articleIdSetFinal.contains(it.id)).collect(Collectors.toList());
    }

    public static List<PhotoVO> listConcernedPhotosVOs(PostTO postTO, Session session){

        if (postTO == null || postTO.postObjectId == null || postTO.postAttributionClass == null){
            throw new IllegalArgumentException();
        }

        List<PhotoVO> photoVOList = PhotoService.listAllPhotoVOs(null, session);
        if (photoVOList.isEmpty()){
            return new ArrayList<>();
        }

        Set<Long> photoIdSet = photoVOList.stream().map(it -> it.id).collect(Collectors.toSet());

        if (PostAttribution.getPostAttribution(postTO.postAttributionClass) == PostAttribution.PHOTO){
            photoIdSet.remove(postTO.postObjectId);
        }

        Set<Long> photoIdSetFinal = getSetOfConcernedPosts(postTO, PostAttribution.PHOTO, photoIdSet, session);
        return photoVOList.stream().filter(it -> photoIdSetFinal.contains(it.id)).collect(Collectors.toList());
    }

    public static List<GalleryVO> listConcernedGalleryVOs(PostTO postTO, Session session){

        if (postTO == null || postTO.postObjectId == null || postTO.postAttributionClass == null){
            throw new IllegalArgumentException();
        }

        List<GalleryVO> galleryVOList = GalleryService.listAllGalleryVOs(null, session);
        if (galleryVOList.isEmpty()){
            return new ArrayList<>();
        }

        Set<Long> galleryIdSet = galleryVOList.stream().map(it -> it.id).collect(Collectors.toSet());

        if (PostAttribution.getPostAttribution(postTO.postAttributionClass) == PostAttribution.GALLERY){
            galleryIdSet.remove(postTO.postObjectId);
        }

        Set<Long> galleryIdSetFinal = getSetOfConcernedPosts(postTO, PostAttribution.GALLERY, galleryIdSet, session);
        return galleryVOList.stream().filter(it -> galleryIdSetFinal.contains(it.id)).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static void updateArticleGalleryRelations(Long articleId, String articleText, Session session){
        BBCodeExtractor.BBCodeData bbCodeData = BBCodeExtractor.parse(articleText);


        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        // clean out existing dependent relations for this object

        List<RelationEntity> articleRelationEntities = RelationDao.listRelationsForPost(PostAttribution.ARTICLE, articleId, session);
        List<RelationEntity> toDelete = articleRelationEntities.stream()
                .filter(it -> it.getSrcAttributionClass().equals(PostAttribution.ARTICLE) && it.getSrcObjectId().equals(articleId)
                        && it.getRelationClass().equals(RelationClass.RELATION_DEPENDENT))
                .collect(Collectors.toList());

        for (RelationEntity relationEntity : toDelete) {
            session.delete(relationEntity);
        }

        if (bbCodeData.imgIds.isEmpty()){
            session.flush();
            return;
        }

        // get a list of gallery ids
        List<ImageFileEntity> imageFileEntities = FileDao.getImageEntitiesByIds(bbCodeData.imgIds, session);

        if (imageFileEntities == null){
            return;
        }

        Set<Long> galleryEntityIds = imageFileEntities.stream().filter(it->it.getImageAttributionClass().equals(PostAttribution.GALLERY))
                .map(ImageFileEntity::getParentObjectId).collect(Collectors.toSet());

        if (galleryEntityIds.isEmpty()){
            return;
        }

        for (Long galleryId : galleryEntityIds){

            // avoid duplicating manually created relations
            List<RelationEntity> toReplace = articleRelationEntities.stream()
                    .filter(it -> it.getSrcAttributionClass().equals(PostAttribution.ARTICLE) && it.getSrcObjectId().equals(articleId)
                            && it.getDstAttributionClass().equals(PostAttribution.GALLERY) && it.getDstObjectId().equals(galleryId))
                    .collect(Collectors.toList());

            for (RelationEntity relationEntity : toReplace){
                session.delete(relationEntity);
            }

            RelationEntity relationEntity = new RelationEntity();
            relationEntity.setSrcAttributionClass(PostAttribution.ARTICLE);
            relationEntity.setSrcObjectId(articleId);
            relationEntity.setDstAttributionClass(PostAttribution.GALLERY);
            relationEntity.setDstObjectId(galleryId);
            relationEntity.setRelationClass(RelationClass.RELATION_DEPENDENT);

            session.save(relationEntity);
        }

        session.flush();
    }


    public static RelationEntity relationVoPartialToRelationEntity(RelationVO relationVO){
        RelationEntity relationEntity = new RelationEntity();

        relationEntity.setSrcAttributionClass(PostAttribution.getPostAttribution(relationVO.srcAttributionClassShort));
        relationEntity.setSrcObjectId(relationVO.srcObjectId);

        relationEntity.setDstAttributionClass(PostAttribution.getPostAttribution(relationVO.dstAttributionClassShort));
        relationEntity.setDstObjectId(relationVO.dstObjectId);

        relationEntity.setRelationClass(RelationClass.RELATION_RELATED);

        return relationEntity;
    }

    public static class Entities {
        List<ArticleEntity> articleEntities;
        List<PhotoEntity> photoEntities;
        List<GalleryEntity> galleryEntities;
    }

}
