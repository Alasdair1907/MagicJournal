package world.thismagical.service;
/*
  User: Alasdair
  Date: 12/28/2019
  Time: 1:35 AM                                                                                                    
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

import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import world.thismagical.dao.*;
import world.thismagical.entity.*;
import world.thismagical.to.ImageFileDescrTO;
import world.thismagical.to.ImageUploadTO;
import world.thismagical.to.JsonAdminResponse;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.ImageVO;

import javax.persistence.Query;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class FileHandlingService {

    // TODO: move this to web.xml !!!!!!
    public static String storagePath = "C:\\code\\thismagicalworld\\web\\imageStorage\\";
    public static String tempRepository = "C:\\tmp";
    public static Integer sizeThreshold = 54428800;
    public static Integer sizeMax = 54428800;

    public static Integer maxWidthForHorizontalPreview = 1280;
    public static Integer maxHeightForVerticalPreview = 1280;

    public static Integer maxWidthForHorizontalThumbnail = 800;
    public static Integer maxHeightForVerticalThumbnail = 800;

    public static void deleteImages(List<ImageVO> imageVOList, Session session){

        if (imageVOList != null && !imageVOList.isEmpty()){

            if (!session.getTransaction().isActive()){
                session.beginTransaction();
            }

            for (ImageVO imageVO : imageVOList){

                List<String> fileNames = imageVO.getAllFilesList();

                for (String fileName : fileNames){
                    Query query = session.createQuery("delete from ImageFileEntity where parentObjectId= :pObjId and fileName= :fName");
                    query.setParameter("pObjId", imageVO.parentObjId);
                    query.setParameter("fName", fileName);

                    query.executeUpdate();

                    File exFile = new File(storagePath + fileName);

                    if (exFile.exists()){
                        exFile.delete();
                    }
                }
            }
            session.flush();
        }
    }

    public static Boolean processUpload(ServletContext servletContext, HttpServletRequest request, ImageUploadTO imageUploadTO, SessionFactory sessionFactory){

        if (imageUploadTO == null){
            return Boolean.FALSE;
        }

        PostAttribution imageAttribution = PostAttribution.getPostAttribution(imageUploadTO.imageAttributionClass);

        if (imageAttribution == null){
            return Boolean.FALSE;
        }

        Session session = sessionFactory.openSession();

        List<PrivilegeLevel> allowedPrivileges = new ArrayList<>();
        allowedPrivileges.add(PrivilegeLevel.PRIVILEGE_SUPER_USER);
        allowedPrivileges.add(PrivilegeLevel.PRIVILEGE_USER);

        if (!AuthorizationService.isSessionValid(imageUploadTO.sessionGuid, allowedPrivileges, session)){
            session.close();
            return Boolean.FALSE;
        }


        ImageFileEntity imageFileEntityStub = new ImageFileEntity();
        imageFileEntityStub.setImageAttributionClass(imageAttribution);
        imageFileEntityStub.setParentObjectId(imageUploadTO.parentObjectId);

        List<ImageFileEntity> imageFileEntityList = handleUpload(servletContext, request, imageFileEntityStub);

        if (imageFileEntityList.size() == 0){
            session.close();
            return Boolean.FALSE;
        }

        try {
            // TODO: verify ownership
            if (imageAttribution.equals(PostAttribution.PHOTO)){
                List<ImageVO> existingImages = FileDao.getImages(imageAttribution, Collections.singletonList(imageUploadTO.parentObjectId), session);
                deleteImages(existingImages, session);
            }
            FileDao.flushList(imageFileEntityList, session);
        } catch (Exception e){
            Tools.log("Error processing file upload:");
            Tools.log(e.getMessage());
            session.getTransaction().rollback();
            return  Boolean.FALSE;
        } finally {
            session.close();
        }

        return Boolean.TRUE;
    }

    public static List<ImageFileEntity> handleUpload(ServletContext servletContext, HttpServletRequest request, ImageFileEntity imageFileEntityStub){
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(sizeThreshold);
        factory.setRepository(new File(tempRepository));
        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setFileSizeMax(sizeMax);

        List<ImageFileEntity> fileEntities = new ArrayList<>();

        try {
            FileItemIterator fileItemIterator = upload.getItemIterator(request);
            while (fileItemIterator.hasNext()){

                FileItemStream fis = fileItemIterator.next();

                if (!fis.isFormField()){
                    String fName = fis.getName();
                    String extension = Tools.getExtension(fName);

                    if (extension == null) {
                        Tools.log("Could not process file "+fName+": no extension detected.");
                        continue;
                    }

                    InputStream is = fis.openStream();

                    String newFileNameBase = UUID.randomUUID().toString();

                    String newFileName = newFileNameBase + "." + extension;
                    String newFileNamePreview = newFileNameBase + "_preview." + extension;
                    String newFileNameThumbnail = newFileNameBase + "_thumb." + extension;

                    Path path = Paths.get(storagePath, newFileName);
                    if (Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING) > 0){

                        ImageResizingService.resize(storagePath + newFileName, storagePath + newFileNamePreview, maxWidthForHorizontalPreview, maxHeightForVerticalPreview);
                        ImageResizingService.resize(storagePath + newFileName, storagePath + newFileNameThumbnail, maxWidthForHorizontalThumbnail, maxHeightForVerticalThumbnail);

                        ImageFileEntity fileEntity = new ImageFileEntity(imageFileEntityStub);
                        fileEntity.setFileName(newFileName);
                        fileEntity.setPreviewFileName(newFileNamePreview);
                        fileEntity.setThumbnailFileName(newFileNameThumbnail);
                        fileEntity.setOriginalFileName(fName);

                        fileEntities.add(fileEntity);
                    }
                    is.close();
                }
            }
        } catch (Exception ex){
            Tools.log(ex.getMessage());
        }

        return fileEntities;
    }
    
    private static JsonAdminResponse<Void> verifyPrivileges(Long id, String guid, Session session){

        if (id == null || guid == null){
            return JsonAdminResponse.fail("verifyPrivileges() : null argument");
        }
        
        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

        if (authorEntity == null){
            return JsonAdminResponse.fail("session not found!");
        }

        List<ImageFileEntity> imageFileEntityList = FileDao.getImageEntitiesByIds(Collections.singletonList(id), session);
        if (imageFileEntityList == null || imageFileEntityList.isEmpty()){
            return JsonAdminResponse.fail("file not found");
        }

        ImageFileEntity currentPost = imageFileEntityList.get(0);
        AuthorEntity postAuthor = null;

        PostEntity postEntity = PostDao.getPostEntityById(currentPost.getParentObjectId(), currentPost.getImageAttributionClass().getAssociatedClass(), session);
        postAuthor = postEntity.getAuthor();

        if (!AuthorizationService.checkPrivileges(postAuthor, authorEntity)){
            return JsonAdminResponse.fail("unauthorized action");
        }

        return JsonAdminResponse.success(null);
    }
    
    public static JsonAdminResponse<Void> deleteFile(Long id, String guid, SessionFactory sessionFactory){

        Session session = sessionFactory.openSession();
        JsonAdminResponse<Void> jsonAdminResponse;

        try {

            jsonAdminResponse = verifyPrivileges(id, guid, session);
            if (Boolean.FALSE.equals(jsonAdminResponse.success)){
                return jsonAdminResponse;
            }

            List<ImageFileEntity> imageFileEntityList = FileDao.getImageEntitiesByIds(Collections.singletonList(id), session);
            if (imageFileEntityList == null || imageFileEntityList.isEmpty()){
                jsonAdminResponse.success = false;
                jsonAdminResponse.errorDescription = "no object to delete!";
                return jsonAdminResponse;
            }

            ImageFileEntity imageFileEntity = imageFileEntityList.get(0);
            List<String> fileNames = new ArrayList<>();
            fileNames.add(imageFileEntity.getFileName());
            fileNames.add(imageFileEntity.getPreviewFileName());
            fileNames.add(imageFileEntity.getThumbnailFileName());
            for (String fName : fileNames){
                File file = new File(storagePath + fName);
                if (file.exists()){
                    file.delete();
                }
            }

            FileDao.deleteImageEntityById(id, session);

        } finally {
            session.close();
        }

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<Void> updateFileInfo(ImageFileDescrTO imageFileDescrTO, String guid, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        JsonAdminResponse<Void> jsonAdminResponse;

        try {

            jsonAdminResponse = verifyPrivileges(imageFileDescrTO.imageEntityId, guid, session);
            if (Boolean.FALSE.equals(jsonAdminResponse.success)){
                return jsonAdminResponse;
            }

            List<ImageFileEntity> imageFileEntityList = FileDao.getImageEntitiesByIds(Collections.singletonList(imageFileDescrTO.imageEntityId), session);
            if (imageFileEntityList == null || imageFileEntityList.isEmpty()){
                jsonAdminResponse.errorDescription = "ImageFileEntity not found!";
                jsonAdminResponse.success = false;
                return jsonAdminResponse;
            }

            ImageFileEntity toUpdate = imageFileEntityList.get(0);
            toUpdate.setTitle(imageFileDescrTO.title);
            toUpdate.setGpsCoordinates(imageFileDescrTO.gps);
            toUpdate.setOrderNumber(imageFileDescrTO.orderNumber);
            FileDao.saveOrUpdate(toUpdate, session);
        } finally {
            session.close();
        }

        jsonAdminResponse.success = true;
        return jsonAdminResponse;
    }

    public static JsonAdminResponse<ImageFileDescrTO> getImageFileDescrTO(Long id, SessionFactory sessionFactory){
        Session session = sessionFactory.openSession();
        JsonAdminResponse<ImageFileDescrTO> jsonAdminResponse = new JsonAdminResponse<>();

        if (id == null){
            jsonAdminResponse.success = false;
            jsonAdminResponse.errorDescription = "getImageFileDescrTO null id!";
            return jsonAdminResponse;
        }

        try {
            List<ImageFileEntity> imageFileEntityList = FileDao.getImageEntitiesByIds(Collections.singletonList(id), session);
            if (imageFileEntityList == null || imageFileEntityList.isEmpty()){
                jsonAdminResponse.errorDescription = "ImageFileEntity not found!";
                jsonAdminResponse.success = false;
                return jsonAdminResponse;
            }

            ImageFileEntity imageFileEntity = imageFileEntityList.get(0);
            ImageFileDescrTO imageFileDescrTO = new ImageFileDescrTO(imageFileEntity);
            jsonAdminResponse.data = imageFileDescrTO;
            jsonAdminResponse.success = true;
        } finally {
            session.close();
        }

        return jsonAdminResponse;
    }


}
