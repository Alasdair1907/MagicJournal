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
import org.hibernate.SessionFactory;
import world.thismagical.dao.*;
import world.thismagical.entity.*;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.to.*;
import world.thismagical.util.*;
import world.thismagical.vo.*;

import javax.management.relation.Relation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static world.thismagical.dao.FileDao.getImageEntitiesByIds;

/*
This service is responsible for "Associated posts" and "Related posts" listings in the panel.

Associated relations between posts are created whenever an image from a gallery or a photo is used in an article. The relation is
created for both sides - when a user views the article, he will see a list of associated photos and galleries, and when he views either a
gallery or a photo he will see associated articles.

Related relations are created by an author manually and any types of posts can be connected. Relation works in both ways.

Associated and Related posts do not duplicate each other, and Associated posts have higher priority.
 */

public class RelationService {

    /*
    loads list of relations for post specified with postTO.postAttributionClass, postTO.postObjectId
    CMS
     */
    public static JsonAdminResponse<RelationTO> getRelationTO(PostTO postTO, Session session){

        if (postTO == null || postTO.postAttributionClass == null || postTO.postObjectId == null){
            return JsonAdminResponse.fail("getRelationTO: null argument");
        }

        PostEntity postEntity = PostDao.getPostEntityById(postTO.postObjectId, postTO.getPostAttribution().getAssociatedClass(), session);

        List<RelationVO> relationVOList = listRelationsForPost(postEntity.getIndexId(), session);

        if (relationVOList.isEmpty()){
            return JsonAdminResponse.success(RelationTO.getEmpty(postTO));
        }

        List<RelationVO> postsReferToThis;
        List<RelationVO> currentPostRelatesTo;

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

    /*
    obtains list of relations for post defined by sidePanelRequestTO.postAttribution and sidePanelRequestTO.postId,
    splits them into associated (auto relations) and related, fetches PostVOs for them, and puts these VOs into sidePanelPostsTO
    CDA
     */
    public static void fillRelevantPosts(SidePanelPostsTO sidePanelPostsTO, SidePanelRequestTO sidePanelRequestTO, Session session){

        PostAttribution thisPostAttribution = PostAttribution.getPostAttribution(sidePanelRequestTO.postAttribution);

        List<RelationEntity> relationEntities = RelationDao.listRelationsForPost(thisPostAttribution, sidePanelRequestTO.postId, session);
        if (relationEntities == null || relationEntities.isEmpty()){
            return;
        }

        PostEntity thisPostEntity = PostDao.getPostEntityById(sidePanelRequestTO.postId, thisPostAttribution.getAssociatedClass(), session);
        if (thisPostEntity == null){
            Tools.log("ERROR: relations requested for post which cannot be fetched");
            return;
        }

        Long thisIndexId = thisPostEntity.getIndexId();
        List<Long> associated = relationEntities.stream().filter(relationEntity -> Boolean.TRUE.equals(relationEntity.getRelationClass().getIsAuto()))
                .map(relationEntity -> thisIndexId.equals(relationEntity.getDstIndexId()) ? relationEntity.getSrcIndexId() : relationEntity.getDstIndexId())
                .collect(Collectors.toList());
        List<Long> related = relationEntities.stream().filter(relationEntity -> !Boolean.TRUE.equals(relationEntity.getRelationClass().getIsAuto()))
                .map(relationEntity -> thisIndexId.equals(relationEntity.getDstIndexId()) ? relationEntity.getSrcIndexId() : relationEntity.getDstIndexId())
                .collect(Collectors.toList());

        List<Long> allIds = Stream.concat(associated.stream(), related.stream()).collect(Collectors.toList());
        List<PostEntity> postEntityList = PostDao.getPostEntitiesByIndexIds(allIds, true, session);
        List<PostVO> postVOList = PostService.entitiesToPostVOsFull(postEntityList, ImageFullness.FULLNESS_PANEL, session);

        List<PostVO> associatedVOList = postVOList.stream().filter(postVO -> associated.contains(postVO.getIndexId())).collect(Collectors.toList());
        List<PostVO> relatedVOList = postVOList.stream().filter(postVO -> related.contains(postVO.getIndexId())).collect(Collectors.toList());

        sidePanelPostsTO.associated = associatedVOList;
        sidePanelPostsTO.related = relatedVOList;
    }

    public static List<RelationVO> listRelationsForPost(Long postIndexId, Session session){
        if (postIndexId == null) { return new ArrayList<>(); }

        // 1. obtain list of relation entities
        List<RelationEntity> relationEntities = RelationDao.listRelationsForPost(postIndexId, session);

        // 2. load corresponding post entities
        List<Long> postEntityIds = relationEntities.stream().map(RelationEntity::getDstIndexId).collect(Collectors.toList());
        postEntityIds.addAll(relationEntities.stream().map(RelationEntity::getSrcIndexId).collect(Collectors.toList()));
        List<PostEntity> postEntities = PostDao.getPostEntitiesByIndexIds(postEntityIds, null, session);

        // 3. turn relation entities into relation VOs by adding src and dst post titles
        List<RelationVO> result = new ArrayList<>();
        for (RelationEntity relationEntity : relationEntities){
            RelationVO relationVO = new RelationVO(relationEntity);

            PostEntity srcEntity = postEntities.stream().filter(postEntity -> postEntity.getIndexId().equals(relationVO.srcIndexId)).findAny().orElse(null);
            PostEntity dstEntity = postEntities.stream().filter(postEntity -> postEntity.getIndexId().equals(relationVO.dstIndexId)).findAny().orElse(null);

            if (srcEntity != null){
                relationVO.srcObjectTitle = srcEntity.getTitle();
            }
            if (dstEntity != null){
                relationVO.dstObjectTitle = dstEntity.getTitle();
            }

            result.add(relationVO);
        }

        return result;
    }

    public static JsonAdminResponse<Void> createNewRelation(String guid, RelationVO relationVoPartial, Session session){

        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (authorEntity == null){
            return JsonAdminResponse.fail("user session not found");
        }

        if (!AuthorizationService.userHasGeneralWritePrivileges(authorEntity)){
            return JsonAdminResponse.fail("insufficient privileges");
        }

        RelationEntity relationEntity = relationVoPartialToRelationEntity(relationVoPartial, session);
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

    /*
    filters out entities from postEntities of posts that are already linked to the post defined by postIndexId
     */
    public static List<PostEntity> getSetOfConcernedPosts(Long postIndexId, List<PostEntity> postEntities, Session session){

        if (postIndexId == null || postEntities == null || postEntities.isEmpty()){
            return new ArrayList<>();
        }

        List<RelationVO> relationVOList = listRelationsForPost(postIndexId, session);

        Set<Long> relationVoUsedIndexes = new HashSet<>();
        relationVoUsedIndexes.addAll(relationVOList.stream().map(relationVO -> relationVO.dstIndexId).collect(Collectors.toSet()));
        relationVoUsedIndexes.addAll(relationVOList.stream().map(relationVO -> relationVO.srcIndexId).collect(Collectors.toSet()));

        Set<Long> resultingPostEntityIds = new HashSet<>();
        List<PostEntity> result = new ArrayList<>();

        for (PostEntity postEntity : postEntities){
            Long postEntityIndex = postEntity.getIndexId();
            if (!relationVoUsedIndexes.contains(postEntityIndex)
                    && !resultingPostEntityIds.contains(postEntityIndex) && !postIndexId.equals(postEntityIndex)){
                resultingPostEntityIds.add(postEntityIndex);
                result.add(postEntity);
            }
        }

        return result;
    }

    /**
     * Loads the list of PostVOs of chosen class that can be used for creating a new relation - that is,
     * they are not already related to the post in question
     * @param postTO Identifies the post by postTO.postObjectId and postTO.postAttributionClass
     * @param postClass Posts of which class should be loaded
     * @param session hibernate session
     * @return list of PostVOs
     */
    public static List<PostVO> listConcernedPostVOs(PostTO postTO, PostAttribution postClass, Session session){

        if (postTO == null || postTO.postObjectId == null || postTO.postAttributionClass == null){
            throw new IllegalArgumentException();
        }

        PostAttribution postAttribution = PostAttribution.getPostAttribution(postTO.postAttributionClass);

        PostEntity centerPostEntity = PostDao.getPostEntityById(postTO.postObjectId, postAttribution.getAssociatedClass(), session);
        Long postIndexId = centerPostEntity.getIndexId();

        BasicPostFilter basicPostFilter = BasicPostFilter.fromTO(postTO.basicPostFilterTO, session);
        basicPostFilter.verifyGuid(session);

        List<PostEntity> postEntityList = PostDao.listAllPosts(basicPostFilter, postClass.getAssociatedClass(), session);
        List<PostEntity> availablePostEntities = getSetOfConcernedPosts(postIndexId, postEntityList, session);

        List<PostVO> postVOs = new ArrayList<>();
        for (PostEntity postEntity : availablePostEntities){
            postVOs.add(postEntity.toBaseVO());
        }

        return postVOs;
    }


    /*
    Parses article looking for GALLERY images referenced from bbcode and re-creates auto relations.
    The philosophy is, that linking photos to the article makes little sense, since photo contains
    only one image that is already in the article, while galleries may contain a lot more stuff that
    isn't in the article.
     */
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

            List<Long> srcDstIndex = getSrcAndDstIndex(articleId, PostAttribution.ARTICLE, galleryId, PostAttribution.GALLERY, session);
            relationEntity.setSrcIndexId(srcDstIndex.get(0));
            relationEntity.setDstIndexId(srcDstIndex.get(1));

            session.save(relationEntity);
        }

        session.flush();
    }

    /*
    Source object attribution/id, destination object attribution/id, relation class - related
     */
    public static RelationEntity relationVoPartialToRelationEntity(RelationVO relationVO, Session session){
        RelationEntity relationEntity = new RelationEntity();

        PostAttribution srcPostAttribution = PostAttribution.getPostAttribution(relationVO.srcAttributionClassShort);
        PostAttribution dstPostAttribution = PostAttribution.getPostAttribution(relationVO.dstAttributionClassShort);

        relationEntity.setSrcAttributionClass(srcPostAttribution);
        relationEntity.setSrcObjectId(relationVO.srcObjectId);

        relationEntity.setDstAttributionClass(dstPostAttribution);
        relationEntity.setDstObjectId(relationVO.dstObjectId);

        relationEntity.setRelationClass(RelationClass.RELATION_RELATED);

        List<Long> srcDstIndex = getSrcAndDstIndex(relationVO.srcObjectId, srcPostAttribution, relationVO.dstObjectId, dstPostAttribution, session);
        relationEntity.setSrcIndexId(srcDstIndex.get(0));
        relationEntity.setDstIndexId(srcDstIndex.get(1));

        return relationEntity;
    }

    public static List<Long> getSrcAndDstIndex(Long srcObjectId, PostAttribution srcPostAttribution, Long dstObjectId, PostAttribution dstPostAttribution, Session session){
        PostEntity srcPostEntity = PostDao.getPostEntityById(srcObjectId, srcPostAttribution.getAssociatedClass(), session);
        PostEntity dstPostEntity = PostDao.getPostEntityById(dstObjectId, dstPostAttribution.getAssociatedClass(), session);

        return Arrays.asList(srcPostEntity.getIndexId(), dstPostEntity.getIndexId());
    }

}
