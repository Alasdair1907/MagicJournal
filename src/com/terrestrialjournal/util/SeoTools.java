package com.terrestrialjournal.util;
/*
  User: Alasdair
  Date: 8/7/2020
  Time: 9:16 PM                                                                                                    
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
import com.terrestrialjournal.dao.FileDao;
import com.terrestrialjournal.dao.PostDao;
import com.terrestrialjournal.entity.*;
import com.terrestrialjournal.filter.BasicPostFilter;
import com.terrestrialjournal.service.GalleryService;
import com.terrestrialjournal.service.PhotoService;
import com.terrestrialjournal.service.SettingsService;
import com.terrestrialjournal.to.SettingsTO;
import com.terrestrialjournal.vo.GalleryVO;
import com.terrestrialjournal.vo.ImageVO;
import com.terrestrialjournal.vo.PhotoVO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SeoTools {

    public static String generateSitemap(SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){
            return generateSitemap(session);
        }
    }

    public static String generateSitemap(Session session){
        List<ArticleEntity> articleEntities = (List) PostDao.listAllPosts(null, ArticleEntity.class, session);
        List<PhotoVO> photoVOList = PhotoService.listAllPhotoVOs(new BasicPostFilter(), session);
        List<GalleryVO> galleryVOList = GalleryService.listAllGalleryVOs(new BasicPostFilter(), 0, session);

        SettingsTO settingsTO = SettingsService.getSettings(session);
        if (settingsTO.websiteURL == null || settingsTO.websiteURL.isEmpty()){
            throw new RuntimeException("Can not generate sitemap: set website URL in Settings!");
        }

        StringBuilder res = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        res.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\">");

        for (ArticleEntity articleEntity : articleEntities){

            StringBuilder node = new StringBuilder(getUrlNodeBase(settingsTO, articleEntity.getId(), articleEntity.getLastModifiedDate(), "article"));

            BBCodeExtractor.BBCodeData bbCodeData = BBCodeExtractor.parse(articleEntity.getArticleText());
            if (bbCodeData.imgIds != null && !bbCodeData.imgIds.isEmpty()){
                List<ImageFileEntity> imageFileEntityList = FileDao.getImageEntitiesByIds(bbCodeData.imgIds, session);
                if (imageFileEntityList != null && !imageFileEntityList.isEmpty()){
                    for (ImageFileEntity imageFileEntity : imageFileEntityList){
                        ImageVO imageVO = new ImageVO(imageFileEntity);
                        node.append(getImageNodeFull(settingsTO, imageVO, articleEntity.getTitle()));
                    }
                }
            }

            node.append("</url>");
            res.append(node.toString());
        }


        for (PhotoVO photoVO : photoVOList){
            String node = getUrlNodeBase(settingsTO, photoVO.getId(), photoVO.getLastModified(), "photo");
            node += getImageNodeFull(settingsTO, photoVO.getMainImageVO(), photoVO.getTitle());
            node += "</url>";
            res.append(node);
        }

        for (GalleryVO galleryVO : galleryVOList){
            StringBuilder node = new StringBuilder(getUrlNodeBase(settingsTO, galleryVO.getId(), galleryVO.getLastModified(), "gallery"));

            if (galleryVO.imageVOList != null && !galleryVO.imageVOList.isEmpty()){
                for (ImageVO imageVO : galleryVO.imageVOList){
                    node.append(getImageNodeFull(settingsTO, imageVO, galleryVO.getTitle()));
                }
            }

            node.append("</url>");
            res.append(node);
        }

        res.append("</urlset>");

        return res.toString();
    }

    static String getUrlNodeBase(SettingsTO settingsTO, Long id, LocalDateTime lastModified, String postTypeBase){
        String node = "<url>";
        node += "<loc>" + Tools.normalizeURL(settingsTO.websiteURL)+ "/posts.jsp?" + postTypeBase + "=" + id + "</loc>";
        node += "<lastmod>" + lastModified.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "</lastmod>";
        node += "<changefreq>monthly</changefreq>";

        return node;
    }

    static String getImageNodeFull(SettingsTO settingsTO, ImageVO imageVO, String defaultTitle){
        String imageNode = "<image:image>";
        imageNode += "<image:loc>" + Tools.normalizeURL(settingsTO.websiteURL) + "/getImage.jsp?filename=" + imageVO.preview + "</image:loc>";

        if (imageVO.title != null && !imageVO.title.isEmpty()){
            imageNode += "<image:title>" + imageVO.title + "</image:title>";
        } else if (defaultTitle != null && !defaultTitle.isEmpty()) {
            imageNode += "<image:title>" + defaultTitle + "</image:title>";
        }
        imageNode += "</image:image>";

        return imageNode;
    }
}
