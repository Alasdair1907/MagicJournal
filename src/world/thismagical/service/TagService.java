package world.thismagical.service;
/*
  User: Alasdair
  Date: 1/11/2020
  Time: 9:55 PM                                                                                                    
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
import world.thismagical.dao.*;
import world.thismagical.entity.*;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.to.TagTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.PrivilegeLevel;

import java.util.ArrayList;
import java.util.List;

public class TagService {
    public static JsonAdminResponse<List<TagEntity>> listTagsForObject(PostAttribution objectAttribution, Long objectId, Session session){

        JsonAdminResponse<List<TagEntity>> jsonAdminResponse = new JsonAdminResponse<>();
        List<TagEntity> tagEntityList = null;

        if (objectAttribution == null || objectId == null){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "null arguments!";
            return jsonAdminResponse;
        }

        try {
            tagEntityList = TagDao.listTags(objectAttribution, objectId, session);
        } catch (Exception ex){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "unable to load list of tags";
            return jsonAdminResponse;
        }

        jsonAdminResponse.success = true;
        jsonAdminResponse.data = tagEntityList;

        return jsonAdminResponse;
    }

    public static JsonAdminResponse<TagTO> listTagsForObjectStr(PostAttribution objectAttribution, Long objectId, Session session){
        JsonAdminResponse<List<TagEntity>> tagEntityList = listTagsForObject(objectAttribution, objectId, session);
        JsonAdminResponse<TagTO> res = new JsonAdminResponse<>();

        List<String> tagList = new ArrayList<>();
        if (tagEntityList.success){
            for (TagEntity tagEntity: tagEntityList.data){
                tagList.add(tagEntity.getTag());
            }

            TagTO tagTO = new TagTO();
            tagTO.attribution = objectAttribution.getId();
            tagTO.objectId = objectId;
            tagTO.tagListStr = String.join(", ", tagList);

            res.success = true;
            res.data = tagTO;
            return res;
        }

        res.success = false;
        res.errorDescription = tagEntityList.errorDescription;
        return res;
    }

    public static JsonAdminResponse<Void> saveOrUpdateTags(TagTO tagTO, String guid, Session session){
        JsonAdminResponse<Void> jsonAdminResponse = null;

        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);
        if (authorEntity == null){
            jsonAdminResponse = new JsonAdminResponse<>();
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "author session not found!";
            return jsonAdminResponse;
        }

        if (authorEntity.getPrivilegeLevel() == PrivilegeLevel.PRIVILEGE_TEST){
            jsonAdminResponse = new JsonAdminResponse<>();
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "not authorized for that action!";
            return jsonAdminResponse;
        }

        PostEntity postEntity = PostDao.getPostEntityById(tagTO.objectId, PostAttribution.getPostAttribution(tagTO.attribution).getAssociatedClass(), session);
        AuthorEntity postAuthor = postEntity.getAuthor();

        /* TODO test & remove kebab
        if (tagTO.attribution.equals(PostAttribution.PHOTO.getId())){
            PhotoEntity photoEntity = (PhotoEntity) PhotoDao.getPostEntityById(tagTO.objectId, PhotoEntity.class, session);
            postAuthor = photoEntity.getAuthor();
        }

        if (tagTO.attribution.equals(PostAttribution.GALLERY.getId())){
            GalleryEntity galleryEntity = (GalleryEntity) GalleryDao.getPostEntityById(tagTO.objectId, GalleryEntity.class, session);
            postAuthor = galleryEntity.getAuthor();
        }

        if (tagTO.attribution.equals(PostAttribution.ARTICLE.getId())){
            ArticleEntity articleEntity = (ArticleEntity) ArticleDao.getPostEntityById(tagTO.objectId, ArticleEntity.class, session);
            postAuthor = articleEntity.getAuthor();
        }*/

        if (postAuthor.getAuthorId() != authorEntity.getAuthorId()){
            if (authorEntity.getPrivilegeLevel() != PrivilegeLevel.PRIVILEGE_SUPER_USER){
                jsonAdminResponse = new JsonAdminResponse<>();
                jsonAdminResponse.success = false;
                jsonAdminResponse.errorDescription = "not authorized for that action!";
                return jsonAdminResponse;
            }
        }

        String[] tagsArray = tagTO.tagListStr.split(",");

        // why bother?
        TagDao.truncateTags(tagTO.objectId, tagTO.attribution, session);

        if (tagsArray.length != 0){

            for (String tag : tagsArray){
                TagEntity tagEntity = new TagEntity();
                tagEntity.setTag(tag.trim());
                tagEntity.setAttributionClass(PostAttribution.getPostAttribution(tagTO.attribution));
                tagEntity.setParentObjectId(tagTO.objectId);

                TagDao.addOrUpdateTag(tagEntity, session);
            }
        }

        jsonAdminResponse = new JsonAdminResponse<>();
        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

}
