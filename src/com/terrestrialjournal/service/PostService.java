package com.terrestrialjournal.service;
/*
  User: Alasdair
  Date: 7/15/2020
  Time: 11:34 PM                                                                                                    
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
import com.terrestrialjournal.dao.*;
import com.terrestrialjournal.entity.AuthorEntity;
import com.terrestrialjournal.entity.PostEntity;
import com.terrestrialjournal.entity.PostIndexItem;
import com.terrestrialjournal.to.JsonAdminResponse;
import com.terrestrialjournal.util.ImageFullness;
import com.terrestrialjournal.util.PostAttribution;
import com.terrestrialjournal.util.Tools;
import com.terrestrialjournal.vo.ImageVO;
import com.terrestrialjournal.vo.PostVO;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.terrestrialjournal.service.AuthorizationService.getAuthorEntityBySessionGuid;

public class PostService {

    public static void savePostGeneralProcedures(Boolean newPost, AuthorEntity authorEntity, PostEntity post, PostAttribution postAttribution, Session session){
        if (newPost){
            savePostGeneralProceduresNewPost(authorEntity, post, postAttribution, session);
        } else {
            savePostGeneralProceduresUpdatedPost(authorEntity, post, postAttribution, session);
        }
    }

    /*
    for the new post we want to:
    - save the actual post
    - create the index item
    - update the post with index item
    */
    public static void savePostGeneralProceduresNewPost(AuthorEntity authorEntity, PostEntity post, PostAttribution postAttribution, Session session){
            PostDao.saveEntity(post, session);

            PostIndexItem postIndexItem = new PostIndexItem();
            postIndexItem.setPostId(post.getId());
            postIndexItem.setPostAttribution(postAttribution);
            postIndexItem.setAuthorLogin(authorEntity.getLogin());
            postIndexItem.setCreationDate(post.getCreationDate());
            postIndexItem.setHasGeo(Tools.isValidGeo(post.getGpsCoordinates()));

            PagingDao.savePostIndexItem(postIndexItem, session);

            post.setIndexId(postIndexItem.getId());
            PostDao.saveEntity(post, session);
    }

    /*
    for the updated post:
    - update post
    - update index item (geo)
    - update tag items (geo)
     */
    public static void savePostGeneralProceduresUpdatedPost(AuthorEntity authorEntity, PostEntity post, PostAttribution postAttribution, Session session){
        PostDao.saveEntity(post, session);
        PagingDao.setGeo(post.getGpsCoordinates(), postAttribution, post.getId(), session);
        TagDao.setGeo(post.getGpsCoordinates(), post.getId(), postAttribution, session);
    }


    /*
    - delete images
    - delete tags
    - delete relations
    - delete index
    - delete post
     */
    public static void deletePostGeneralProcedures(Long id, PostAttribution postAttribution, Session session){
        List<ImageVO> postImages = FileDao.getImages(postAttribution, Collections.singletonList(id), session);
        FileHandlingService.deleteImages(postImages, session);

        RelationDao.deleteRelationsInvolvingPost(postAttribution, id, session);
        TagDao.truncateTags(id, postAttribution.getId(), session);
        PagingDao.deleteIndex(postAttribution, id, session);
        PostDao.deleteEntity(id, postAttribution.getAssociatedClass(), session);
    }

    public static JsonAdminResponse<Void> deletePost(Long id, PostAttribution postAttribution, String userGuid, Session session){
        JsonAdminResponse<Void> checkResult = validateAndAuthorize(userGuid, id, postAttribution, session);
        if (!checkResult.success){
            return checkResult;
        }

        deletePostGeneralProcedures(id, postAttribution, session);

        return JsonAdminResponse.success(null);
    }

    /*
    - update post publish status
    - update tags publish status
    - update post index publish status
     */
    public static void togglePostPublishStatusGeneralProcedures(Long id, PostAttribution postAttribution, Session session) {
        Boolean newPublishStatus = PostDao.togglePostPublish(id, postAttribution.getAssociatedClass(), session);
        TagDao.setPublished(newPublishStatus, id, postAttribution, session);
        PagingDao.updatePostPublish(newPublishStatus, postAttribution, id, session);
    }

    public static JsonAdminResponse<Void> togglePostPublishStatus(Long id, PostAttribution postAttribution, String userGuid, Session session){
        JsonAdminResponse<Void> checkResult = validateAndAuthorize(userGuid, id, postAttribution, session);
        if (!checkResult.success){
            return checkResult;
        }

        togglePostPublishStatusGeneralProcedures(id, postAttribution, session);

        return JsonAdminResponse.success(null);
    }

    public static JsonAdminResponse<Integer> updatePostRender(Long postId, String render, PostAttribution postAttribution, String userGuid, Session session){
        JsonAdminResponse<Void> checkResult = validateAndAuthorize(userGuid, postId, postAttribution, session);
        if (!checkResult.success){
            return JsonAdminResponse.fail(checkResult.errorDescription);
        }

        if (postId == null){
            return JsonAdminResponse.fail("postID not provided for render request");
        }
        if (postAttribution == null){
            return JsonAdminResponse.fail("postAttribution not provided for render request");
        }
        if (!Boolean.TRUE.equals(postAttribution.isPreRenderable())){
            return JsonAdminResponse.fail("render update requested for non-prerenderable post type");
        }

        Integer res = PostDao.updateRenderByPostId(postId, render, postAttribution.getAssociatedClass(), session);
        return JsonAdminResponse.success(res);
    }

    public static JsonAdminResponse<Void> validateAndAuthorize(String userGuid, Long id, PostAttribution postAttribution, Session session){
        if (id == null || postAttribution == null){
            return JsonAdminResponse.fail("Error: no post identification provided");
        }

        PostEntity postEntity = PostDao.getPostEntityById(id, postAttribution.getAssociatedClass(), session);
        AuthorEntity currentAuthorEntity = getAuthorEntityBySessionGuid(userGuid, session);

        if (!AuthorizationService.checkPrivileges(postEntity.getAuthor(), currentAuthorEntity)){
            return JsonAdminResponse.fail("Unauthorized action");
        }

        return JsonAdminResponse.success(null);
    }

    @SuppressWarnings("unchecked")
    public static List<PostVO> entitiesToPostVOsFull(List<PostEntity> postEntities, ImageFullness imageFullness, Session session){

        Map<PostAttribution, List<PostEntity>> entitiesByPostAttribution = postEntities.stream()
                .collect(Collectors.groupingBy(PostEntity::getPostAttribution));

        List<PostVO> postVOList = new ArrayList<>();
        entitiesByPostAttribution.entrySet().forEach(e -> {
            switch (e.getKey()){
                case ARTICLE -> postVOList.addAll(ArticleService.articleEntitiesToArticleVOs((List)e.getValue(), session));
                case GALLERY -> postVOList.addAll(GalleryService.entitiesToVos((List)e.getValue(), imageFullness.getGalleryImagesCount(), session));
                case PHOTO -> postVOList.addAll(PhotoService.photoEntitiesToVOs((List)e.getValue(), session));
            }
        });
        return postVOList;
    }


}
