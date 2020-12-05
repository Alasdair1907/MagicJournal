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
import world.thismagical.vo.TagDigestVO;

import javax.tools.Tool;
import java.util.*;
import java.util.stream.Collectors;

public class TagService {

    public static JsonAdminResponse<List<TagDigestVO>> getTagDigestVOList(Session session){
        Map<String, TagDigestVO> tagMap = new LinkedHashMap<>();

        List<TagEntity> tagEntityList = null;
        try {
            tagEntityList = session.createQuery("from TagEntity where parentIsPublished = true order by tag asc", TagEntity.class).getResultList();
        } catch (Exception e){
            Tools.handleException(e);
            return JsonAdminResponse.success(new ArrayList<>());
        }

        for (TagEntity tagEntity : tagEntityList){
            TagDigestVO tagDigestVO;
            String tag = tagEntity.getTag();
            if (tagMap.containsKey(tag)){
                tagDigestVO = tagMap.get(tag);
            } else {
                tagDigestVO = new TagDigestVO(tag);
                tagMap.put(tag, tagDigestVO);
            }

            if (tagEntity.getAttributionClass() == PostAttribution.ARTICLE){
                tagDigestVO.articles += 1;
                tagDigestVO.totalPosts += 1;
            } else if (tagEntity.getAttributionClass() == PostAttribution.PHOTO){
                tagDigestVO.photos += 1;
                tagDigestVO.totalPosts += 1;
            } else if (tagEntity.getAttributionClass() == PostAttribution.GALLERY){
                tagDigestVO.galleries += 1;
                tagDigestVO.totalPosts += 1;
            }
        }

        List<TagDigestVO> tagDigestVOList = new ArrayList<>();
        tagMap.keySet().forEach( it -> tagDigestVOList.add(tagMap.get(it)));

        return JsonAdminResponse.success(tagDigestVOList);
    }

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

            String geo = postEntity.getGpsCoordinates();
            boolean hasGeo = (geo != null) && ( !geo.isEmpty() );

            PostIndexItem postIndexItem = PagingDao.getItem(PostAttribution.getPostAttribution(tagTO.attribution), tagTO.objectId, session);

            for (String tag : tagsArray){
                TagEntity tagEntity = new TagEntity();
                tagEntity.setTag(tag.trim());
                tagEntity.setAttributionClass(PostAttribution.getPostAttribution(tagTO.attribution));
                tagEntity.setParentObjectId(tagTO.objectId);
                tagEntity.setPostIndexItemId(postIndexItem.getId());
                tagEntity.setParentHasGeo(hasGeo);
                tagEntity.setParentIsPublished(postIndexItem.getPublished());

                TagDao.addOrUpdateTag(tagEntity, session);
            }
        }

        return JsonAdminResponse.success(null);
    }

}
