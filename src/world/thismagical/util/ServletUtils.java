package world.thismagical.util;
/*
  User: Alasdair
  Date: 8/8/2020
  Time: 12:13 AM                                                                                                    
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
import world.thismagical.service.ArticleService;
import world.thismagical.service.GalleryService;
import world.thismagical.service.PhotoService;
import world.thismagical.service.SettingsService;
import world.thismagical.to.MetaTO;
import world.thismagical.to.SettingsTO;
import world.thismagical.vo.ArticleVO;
import world.thismagical.vo.GalleryVO;
import world.thismagical.vo.PhotoVO;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.time.LocalDateTime;

public class ServletUtils {

    public static final int UPLOAD_BUFFER_SIZE = 100*1000;

    public static class Keys {
        public static final String SITEMAP_CACHE = "sitemapCache";
        public static final String SITEMAP_CACHE_LAST_UPDATE_TIME = "sitemapCacheLastUpdateTime";
        public static final String SETTINGS_TO = "settingsTO";
        public static final String SESSION_FACTORY = "sessionFactory";
    }

    public static SessionFactory getSessionFactory(ServletContext application){

        synchronized (application){

            SessionFactory sessionFactory = (SessionFactory) application.getAttribute(Keys.SESSION_FACTORY);
            if (sessionFactory != null){
                return sessionFactory;
            }

            Tools.log("creating new session factory.");
            sessionFactory = Tools.getSessionfactory();
            application.setAttribute(Keys.SESSION_FACTORY, sessionFactory);
            return sessionFactory;
        }
    }

    public static SettingsTO getNoAuthSettingsCached(ServletContext application){
        return getSettings(application).nullNotForPublicAttributes();
    }

    public static SettingsTO getSettings(ServletContext application){
        synchronized (application){
            SettingsTO settingsTO = (SettingsTO) application.getAttribute(Keys.SETTINGS_TO);
            if (settingsTO != null){
                return settingsTO;
            }

            SessionFactory sessionFactory = getSessionFactory(application);
            try (Session session = sessionFactory.openSession()){
                settingsTO = SettingsService.getSettings(session);
            }

            if (settingsTO == null){
                return new SettingsTO();
            }

            application.setAttribute(Keys.SETTINGS_TO, settingsTO);
            return settingsTO;
        }
    }

    public static MetaTO prepareMeta(HttpServletRequest request, ServletContext application){
        String article = request.getParameter("article");
        String photo = request.getParameter("photo");
        String gallery = request.getParameter("gallery");

        SessionFactory sessionFactory = getSessionFactory(application);
        SettingsTO settingsTO = getSettings(application);

        try (Session session = sessionFactory.openSession()) {
            if (Tools.strIsNumber(article)) {
                Long articleId = Long.parseLong(article);
                ArticleVO articleVO = ArticleService.getArticleVObyArticleId(articleId, session);
                return MetaTO.fromPostVO(articleVO, settingsTO);
            } else if (Tools.strIsNumber(photo)) {
                Long photoId = Long.parseLong(photo);
                PhotoVO photoVO = PhotoService.getPhotoVObyPhotoId(photoId, session);
                return MetaTO.fromPostVO(photoVO, settingsTO);
            } else if (Tools.strIsNumber(gallery)) {
                Long galleryId = Long.parseLong(gallery);
                GalleryVO galleryVO = GalleryService.getGalleryVOByGalleryId(galleryId, session);
                return MetaTO.fromPostVO(galleryVO, settingsTO);
            }
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return new MetaTO();
    }

    public static String generateSitemap(ServletContext application){
        synchronized (application) {
            LocalDateTime lastUpdateTime = (LocalDateTime) application.getAttribute(Keys.SITEMAP_CACHE_LAST_UPDATE_TIME);
            String sitemap = (String) application.getAttribute(Keys.SITEMAP_CACHE);

            if (lastUpdateTime != null && sitemap != null && !sitemap.isEmpty() && LocalDateTime.now().minusHours(1).isBefore(lastUpdateTime)) {
                return sitemap;
            }

            SessionFactory sessionFactory = getSessionFactory(application);
            sitemap = SeoTools.generateSitemap(sessionFactory);
            lastUpdateTime = LocalDateTime.now();

            application.setAttribute(Keys.SITEMAP_CACHE_LAST_UPDATE_TIME, lastUpdateTime);
            application.setAttribute(Keys.SITEMAP_CACHE, sitemap);

            return sitemap;
        }
    }

    public static void handleUpload(File upload, OutputStream responseOutputStream) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(upload);
            byte[] buffer = new byte[UPLOAD_BUFFER_SIZE];
            int read = 0;
            while (true) {
                read = fis.read(buffer, 0, UPLOAD_BUFFER_SIZE);
                if (read < 1){
                    break;
                }

                responseOutputStream.write(buffer, 0, read);
                responseOutputStream.flush();
            }
            fis.close();
        } catch (Exception ex){
            Tools.log("[ERROR] handleUpload() error:" + ex.getMessage());
            Tools.log(Tools.getStackTraceStr(ex));
        }
    }

}
