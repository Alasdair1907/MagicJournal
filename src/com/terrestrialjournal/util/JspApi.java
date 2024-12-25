package com.terrestrialjournal.util;

import com.terrestrialjournal.service.ArticleService;
import com.terrestrialjournal.service.GalleryService;
import com.terrestrialjournal.service.PhotoService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class JspApi {
    public static String getArticleRender(Long id, String userGuid, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){
            return ArticleService.getPreRenderByArticleId(id, userGuid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
            return "<span>error loading article</span>";
        }
    }

    public static String getGalleryRender(Long id, String userGuid, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){
            return GalleryService.getPrerenderByGalleryId(id, userGuid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
            return "<span>error loading gallery</span>";
        }
    }


    public static String getPhotoRender(Long id, String userGuid, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){
            return PhotoService.getPrerenderByPhotoId(id, userGuid, session);
        } catch (Exception ex){
            Tools.handleException(ex);
            return "<span>error loading photo</span>";
        }
    }
    
    

}
