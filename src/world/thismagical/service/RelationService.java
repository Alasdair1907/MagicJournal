package world.thismagical.service;

import org.hibernate.Session;
import world.thismagical.dao.ArticleDao;
import world.thismagical.dao.GalleryDao;
import world.thismagical.dao.PhotoDao;
import world.thismagical.dao.RelationDao;
import world.thismagical.entity.ArticleEntity;
import world.thismagical.entity.GalleryEntity;
import world.thismagical.entity.PhotoEntity;
import world.thismagical.entity.RelationEntity;
import world.thismagical.to.PostTO;
import world.thismagical.to.RelationTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.vo.RelationVO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RelationService {

    public static RelationTO getRelationTO(PostTO postTO, Session session){
        List<RelationVO> relationVOList = listRelationsForPost(PostAttribution.getPostAttribution(postTO.postAttributionClass), postTO.postObjectId, session);

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

        return relationTO;

    }

    public static List<RelationVO> listRelationsForPost(PostAttribution postAttribution, Long postId, Session session){
        List<RelationEntity> relationEntities = RelationDao.listRelationsForPost(postAttribution, postId, session);
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
            relationVO.srcAttributionClassShort = relationEntity.getSrcAttributionClass().getId();
            relationVO.srcObjectId = relationEntity.getSrcObjectId();

            relationVO.dstAttributionClass = relationEntity.getDstAttributionClass();
            relationVO.dstAttributionClassShort = relationEntity.getDstAttributionClass().getId();
            relationVO.dstObjectId = relationEntity.getDstObjectId();

            relationVO.relationClass = relationEntity.getRelationClass();
            relationVO.relationClassShort = relationEntity.getRelationClass().getId();

            relationVO.isAuto = relationVO.relationClass.getIsAuto();

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

    public static Entities gatherRelationsEntities(List<RelationEntity> relationEntityList, Session session){
        if (relationEntityList == null){
            throw new IllegalArgumentException("relationEntityListToVo: null argument");
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
        entities.articleEntities = ArticleDao.getArticleEntitiesByIds(new ArrayList<>(articleEntityIds), session);
        entities.photoEntities = PhotoDao.getPhotoEntitiesByIds(new ArrayList<>(photoEntityIds), session);
        entities.galleryEntities = GalleryDao.getGalleryEntitiesByIds(new ArrayList<>(galleryEntityIds), session);

        return entities;
    }

    public static class Entities {
        List<ArticleEntity> articleEntities;
        List<PhotoEntity> photoEntities;
        List<GalleryEntity> galleryEntities;
    }

}
