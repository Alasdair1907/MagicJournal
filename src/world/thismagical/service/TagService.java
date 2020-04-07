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
import world.thismagical.util.Tools;

import java.util.ArrayList;
import java.util.List;

public class TagService {
    public static JsonAdminResponse<List<TagEntity>> listTagsForObject(PostAttribution objectAttribution, Long objectId, Session session){

        if (objectAttribution == null || objectId == null){
            return JsonAdminResponse.fail("listTagsForObject: null argument!");
        }

        try {
            return JsonAdminResponse.success(TagDao.listTags(objectAttribution, objectId, session));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error listing tags for object");
    }

    public static JsonAdminResponse<TagTO> listTagsForObjectStr(TagTO request, Session session){

        if (request == null || request.attribution == null || request.objectId == null){
            return JsonAdminResponse.fail("listTagsForObjectStr: null argument");
        }

        PostAttribution objectAttribution = PostAttribution.getPostAttribution(request.attribution);
        Long objectId = request.objectId;

        JsonAdminResponse<List<TagEntity>> tagEntityList = listTagsForObject(objectAttribution, objectId, session);

        List<String> tagList = new ArrayList<>();
        if (tagEntityList.success){
            for (TagEntity tagEntity: tagEntityList.data){
                tagList.add(tagEntity.getTag());
            }

            TagTO tagTO = new TagTO();
            tagTO.attribution = objectAttribution.getId();
            tagTO.objectId = objectId;
            tagTO.tagListStr = String.join(", ", tagList);

            return JsonAdminResponse.success(tagTO);
        }

        return JsonAdminResponse.fail(tagEntityList.errorDescription);
    }

    public static JsonAdminResponse<Void> saveOrUpdateTags(TagTO tagTO, String guid, Session session){

        if (tagTO == null || tagTO.attribution == null || tagTO.objectId == null){
            return JsonAdminResponse.fail("saveOrUpdateTags: null argument");
        }

        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);
        if (authorEntity == null){
            return JsonAdminResponse.fail("author session not found!");
        }

        PostEntity postEntity = PostDao.getPostEntityById(tagTO.objectId, PostAttribution.getPostAttribution(tagTO.attribution).getAssociatedClass(), session);
        AuthorEntity postAuthor = postEntity.getAuthor();

        if (!AuthorizationService.checkPrivileges(postAuthor, authorEntity)){
            return JsonAdminResponse.fail("not authorized for this action!");
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

        return JsonAdminResponse.success(null);
    }

}
