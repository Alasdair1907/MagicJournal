package world.thismagical.service;
/*
  User: Alasdair
  Date: 5/13/2020
  Time: 8:06 PM                                                                                                    
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
import world.thismagical.dao.PagingDao;
import world.thismagical.entity.PostIndexItem;
import world.thismagical.filter.BasicPostFilter;
import world.thismagical.filter.PagingRequestFilter;
import world.thismagical.to.SettingsTO;
import world.thismagical.util.PostAttribution;
import world.thismagical.vo.PagingVO;
import world.thismagical.vo.PostVO;
import world.thismagical.vo.PostVOList;
import world.thismagical.vo.PostVOListUnified;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PagingService {

    public static PostVOList get(PagingRequestFilter pagingRequestFilter, Session session){
        PostVOList postVOList = preLoad(pagingRequestFilter, session);
        return populate(postVOList, session);
    }

    public static PostVOList preLoad(PagingRequestFilter pagingRequestFilter, Session session){

        SettingsTO settingsTO = SettingsService.getSettings(session);

        if (pagingRequestFilter.page == null){
            pagingRequestFilter.page = 0;
        }

        PostVOList postVOList = new PostVOList();

        Integer totalItems = PagingDao.count(pagingRequestFilter, session);
        Integer itemsPerPage = (pagingRequestFilter.itemsPerPage == null) ? settingsTO.itemsPerPage : pagingRequestFilter.itemsPerPage;

        double dPages = totalItems.doubleValue() / itemsPerPage.doubleValue();

        postVOList.totalPages = (int) Math.ceil(dPages);
        postVOList.totalItems = totalItems;
        postVOList.currentPage = pagingRequestFilter.page;

        postVOList.articles = new PagingVO(PostAttribution.ARTICLE);
        postVOList.photos = new PagingVO(PostAttribution.PHOTO);
        postVOList.galleries = new PagingVO(PostAttribution.GALLERY);

        if (totalItems == 0){
            return postVOList;
        }

        Integer fromResult = pagingRequestFilter.page * itemsPerPage;
        List<PostIndexItem> postIndexItemList = PagingDao.load(pagingRequestFilter, fromResult, itemsPerPage, session);

        postIndexItemList.forEach(postIndexItem -> {

            PostAttribution pa = postIndexItem.getPostAttribution();
            switch (pa){
                case ARTICLE:
                    postVOList.articles.ids.add(postIndexItem.getPostId());
                    break;
                case PHOTO:
                    postVOList.photos.ids.add(postIndexItem.getPostId());
                    break;
                case GALLERY:
                    postVOList.galleries.ids.add(postIndexItem.getPostId());
                    break;
            }
        });

        return postVOList;
    }

    public static PostVOList populate(PostVOList postVOList, Session session){
        if (postVOList.articles.ids != null && !postVOList.articles.ids.isEmpty()){
            BasicPostFilter basicPostFilter = new BasicPostFilter();
            basicPostFilter.ids = postVOList.articles.ids;

            postVOList.articles.page = (List) ArticleService.listAllArticleVOs(basicPostFilter, session);
        }

        if (postVOList.photos.ids != null && !postVOList.photos.ids.isEmpty()){
            BasicPostFilter basicPostFilter = new BasicPostFilter();
            basicPostFilter.ids = postVOList.photos.ids;

            postVOList.photos.page = (List) PhotoService.listAllPhotoVOs(basicPostFilter, session);
        }

        if (postVOList.galleries.ids != null && !postVOList.galleries.ids.isEmpty()){
            BasicPostFilter basicPostFilter = new BasicPostFilter();
            basicPostFilter.ids = postVOList.galleries.ids;

            postVOList.galleries.page = (List) GalleryService.listAllGalleryVOs(basicPostFilter, 4, session);
        }

        return postVOList;
    }

    public static PostVOListUnified unify(PostVOList postVOList){
        List<PostVO> postVOs = new ArrayList<>();

        postVOs.addAll( (List) postVOList.articles.page);
        postVOs.addAll( (List) postVOList.photos.page);
        postVOs.addAll( (List) postVOList.galleries.page);

        postVOs.sort( (postA, postB) -> {
            if (postA.getCreationDate().isAfter(postB.getCreationDate())){
                return -1;
            }

            if (postA.getCreationDate().isBefore(postB.getCreationDate())){
                return 1;
            }

            return 0;
        });

        PostVOListUnified postVOListUnified = new PostVOListUnified();
        postVOListUnified.currentPage = postVOList.currentPage;
        postVOListUnified.totalItems = postVOList.totalItems;
        postVOListUnified.totalPages = postVOList.totalPages;

        postVOListUnified.posts = postVOs;

        return postVOListUnified;
    }

}
