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
import world.thismagical.filter.BasicFileFilter;
import world.thismagical.to.*;
import world.thismagical.util.PostAttribution;
import world.thismagical.util.PrivilegeLevel;
import world.thismagical.util.Tools;
import world.thismagical.vo.ImageVO;
import world.thismagical.vo.OtherFileVO;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.tools.Tool;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileHandlingService {

    public static Integer sizeThreshold = 54428800;
    public static Integer sizeMax = 54428800;

    public static void deleteImages(List<ImageVO> imageVOList, Session session){

        if (imageVOList == null || imageVOList.isEmpty()) {
            return;
        }

        SettingsTO settingsTO = SettingsService.getSettings(session);

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

                File exFile = new File(Tools.getPath(settingsTO.imageStoragePath) + fileName);

                if (exFile.exists()){
                    exFile.delete();
                }
            }
        }
        session.flush();
    }

    public static Boolean processUpload(HttpServletRequest request, OtherFileTO otherFileTO, Session session) throws Exception {

        if (otherFileTO == null){
            return Boolean.FALSE;
        }

        SettingsTO settingsTO = SettingsService.getSettings(session);

        if (settingsTO.otherFilesStoragePath == null || settingsTO.otherFilesStoragePath.isEmpty()){
            throw new FileNotFoundException("File storage path is not set!");
        }

        List<PrivilegeLevel> allowedPrivileges = new ArrayList<>();
        allowedPrivileges.add(PrivilegeLevel.PRIVILEGE_SUPER_USER);
        allowedPrivileges.add(PrivilegeLevel.PRIVILEGE_USER);

        if (!AuthorizationService.isSessionValid(otherFileTO.guid, allowedPrivileges, session)){
            return Boolean.FALSE;
        }

        AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(otherFileTO.guid, session);

        OtherFileEntity otherFileEntity = handleUploadOtherFile(request, authorEntity, settingsTO);

        if (otherFileEntity == null){
            return Boolean.FALSE;
        }

        otherFileEntity.setAuthorEntity(authorEntity);
        otherFileEntity.setDescription(otherFileTO.description);
        otherFileEntity.setDisplayName(otherFileTO.name);

        if (!session.getTransaction().isActive()){
            session.beginTransaction();
        }

        session.saveOrUpdate(otherFileEntity);
        session.flush();

        return Boolean.TRUE;
    }

    public static Boolean processUpload(HttpServletRequest request, ImageUploadTO imageUploadTO, Session session) throws Exception {

        if (imageUploadTO == null){
            return Boolean.FALSE;
        }

        PostAttribution imageAttribution = PostAttribution.getPostAttribution(imageUploadTO.imageAttributionClass);

        if (imageAttribution == null){
            return Boolean.FALSE;
        }

        SettingsTO settingsTO = SettingsService.getSettings(session);

        if (settingsTO.imageStoragePath == null || settingsTO.imageStoragePath.isEmpty()){
            throw new FileNotFoundException("Image storage path is not set!");
        }

        List<PrivilegeLevel> allowedPrivileges = new ArrayList<>();
        allowedPrivileges.add(PrivilegeLevel.PRIVILEGE_SUPER_USER);
        allowedPrivileges.add(PrivilegeLevel.PRIVILEGE_USER);

        if (!AuthorizationService.isSessionValid(imageUploadTO.sessionGuid, allowedPrivileges, session)){
            return Boolean.FALSE;
        }


        ImageFileEntity imageFileEntityStub = new ImageFileEntity();
        imageFileEntityStub.setImageAttributionClass(imageAttribution);
        imageFileEntityStub.setParentObjectId(imageUploadTO.parentObjectId);

        List<ImageFileEntity> imageFileEntityList = handleUploadImage(request, imageFileEntityStub, settingsTO);

        if (imageFileEntityList.size() == 0){
            return Boolean.FALSE;
        }

        try {
            if (imageAttribution.equals(PostAttribution.PHOTO) || imageAttribution.equals(PostAttribution.PROFILE)){
                List<ImageVO> existingImages = FileDao.getImages(imageAttribution, Collections.singletonList(imageUploadTO.parentObjectId), session);

                if (existingImages != null && !existingImages.isEmpty()){
                    // verify ownership

                    if (imageAttribution.equals(PostAttribution.PHOTO)){
                        if (!verifyPrivileges(existingImages.get(0).thisObjId, imageUploadTO.sessionGuid, session).success){
                            return Boolean.FALSE;
                        }
                    }
                    if (imageAttribution.equals(PostAttribution.PROFILE)){
                        AuthorEntity authorEntity = AuthorDao.getAuthorEntityById(existingImages.get(0).parentObjId, session);
                        if (authorEntity == null){
                            return Boolean.FALSE;
                        }
                        AuthorEntity currentAuthor = AuthorizationService.getAuthorEntityBySessionGuid(imageUploadTO.sessionGuid, session);
                        if (!AuthorizationService.checkPrivileges(authorEntity, currentAuthor)){
                            return Boolean.FALSE;
                        }
                    }

                    deleteImages(existingImages, session);
                }
            }
            FileDao.flushList(imageFileEntityList, session);
        } catch (Exception e){
            Tools.log("Error processing file upload:");
            Tools.log(e.getMessage());
            return  Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    // expects only one uploaded file. returns partial OtherFileEntity with original file name and the new file name.
    public static OtherFileEntity handleUploadOtherFile(HttpServletRequest request, AuthorEntity uploadAuthor, SettingsTO settingsTO){
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(sizeThreshold);
        factory.setRepository(new File(settingsTO.otherFilesStoragePath));
        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setFileSizeMax(sizeMax);

        OtherFileEntity res = null;

        try {
            FileItemIterator fileItemIterator = upload.getItemIterator(request);
            while (fileItemIterator.hasNext()){

                FileItemStream fis = fileItemIterator.next();

                if (!fis.isFormField()){

                    String fName = fis.getName();
                    InputStream is = fis.openStream();
                    String newFileName = UUID.randomUUID().toString();
                    Path path = Paths.get(settingsTO.otherFilesStoragePath, newFileName);

                    try {
                        if (Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING) > 0) {
                            res = new OtherFileEntity();
                            res.setFileName(newFileName);
                            res.setOriginalFileName(fName);
                        }
                        is.close();
                    } catch (Exception ex){
                        is.close();
                        Files.deleteIfExists(path);
                        throw ex;
                    }
                }
            }
        } catch (Exception ex){
            Tools.log(ex.getMessage());
        }

        return res;
    }

    public static List<ImageFileEntity> handleUploadImage(HttpServletRequest request, ImageFileEntity imageFileEntityStub, SettingsTO settingsTO){
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(sizeThreshold);
        factory.setRepository(new File(settingsTO.temporaryFolderPath));
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

                    Path path = Paths.get(settingsTO.imageStoragePath, newFileName);

                    try {
                        if (Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING) > 0) {

                            Double aspectRatio = ImageResizingService.resize(Tools.getPath(settingsTO.imageStoragePath) + newFileName, Tools.getPath(settingsTO.imageStoragePath) + newFileNamePreview, settingsTO.previewX, settingsTO.previewY);
                            ImageResizingService.resize(Tools.getPath(settingsTO.imageStoragePath) + newFileName, Tools.getPath(settingsTO.imageStoragePath) + newFileNameThumbnail, settingsTO.thumbX, settingsTO.thumbY);

                            ImageFileEntity fileEntity = new ImageFileEntity(imageFileEntityStub);
                            fileEntity.setFileName(newFileName);
                            fileEntity.setPreviewFileName(newFileNamePreview);
                            fileEntity.setThumbnailFileName(newFileNameThumbnail);
                            fileEntity.setOriginalFileName(fName);
                            fileEntity.setAspectRatio(aspectRatio);
                            fileEntity.setOrderNumber(1000L);

                            fileEntities.add(fileEntity);
                        }
                        is.close();
                    } catch (Exception ex){
                        is.close();
                        Files.deleteIfExists(path);
                        throw ex;
                    }
                }
            }
        } catch (Exception ex){
            Tools.log(ex.getMessage());
        }

        return fileEntities;
    }

    /**
     *
     * @param id id of ImageFileEntity
     * @param guid user guid
     * @param session hibernate session
     * @return JsonAdminResponse with success set to either true or false
     */
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
        SettingsTO settingsTO = SettingsService.getSettings(session);
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
                File file = new File(Tools.getPath(settingsTO.imageStoragePath) + fName);
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

        if (id == null){
            return JsonAdminResponse.fail("getImageFileDescrTO: null argument");
        }

        try (Session session = sessionFactory.openSession()) {
            List<ImageFileEntity> imageFileEntityList = FileDao.getImageEntitiesByIds(Collections.singletonList(id), session);
            if (imageFileEntityList == null || imageFileEntityList.isEmpty()){
                return JsonAdminResponse.fail("ImageFileEntity not found");
            }

            ImageFileEntity imageFileEntity = imageFileEntityList.get(0);
            return JsonAdminResponse.success(new ImageFileDescrTO(imageFileEntity));
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error getting image file description");

    }

    public static JsonAdminResponse<List<OtherFileVO>> listOtherFiles(BasicFileFilter basicFileFilter, SessionFactory sessionFactory){
        try (Session session = sessionFactory.openSession()){

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<OtherFileEntity> cq = cb.createQuery(OtherFileEntity.class);
            Root<OtherFileEntity> root = cq.from(OtherFileEntity.class);
            cq.orderBy(cb.desc(root.get("id")));

            if (basicFileFilter != null){

                List<Predicate> predicates = new ArrayList<>();

                if (basicFileFilter.authorLogin != null && !basicFileFilter.authorLogin.isEmpty()){
                    AuthorEntity authorEntity = AuthorDao.getAuthorEntityByLogin(basicFileFilter.authorLogin, session);
                    if (authorEntity == null){
                        return JsonAdminResponse.success(new ArrayList<>());
                    }

                    predicates.add(cb.equal(root.get("authorEntity"), authorEntity));
                }

                if (basicFileFilter.fileDisplayName != null && !basicFileFilter.fileDisplayName.isEmpty()){
                    predicates.add(cb.like(root.get("displayName"), '%' + basicFileFilter.fileDisplayName + '%'));
                }

                if (basicFileFilter.fileOriginalName != null && !basicFileFilter.fileOriginalName.isEmpty()){
                    predicates.add(cb.like(root.get("originalFileName"), '%' + basicFileFilter.fileOriginalName + '%'));
                }

                Predicate and = cb.and(predicates.toArray(new Predicate[predicates.size()]));
                cq.select(root).where(and);
            } else {
                cq.select(root);
            }

            List<OtherFileEntity> otherFileEntityList = session.createQuery(cq).getResultList();

            if (otherFileEntityList == null || otherFileEntityList.isEmpty()){
                return JsonAdminResponse.success(new ArrayList<>());
            }

            List<OtherFileVO> otherFileVOList = new ArrayList<>();

            for (OtherFileEntity otherFileEntity : otherFileEntityList){
                otherFileVOList.add(new OtherFileVO(otherFileEntity));
            }

            return JsonAdminResponse.success(otherFileVOList);

        } catch (Exception ex){
            Tools.handleException(ex);
            return JsonAdminResponse.fail("error obtaining list of files");
        }
    }

    public static JsonAdminResponse<Void> deleteOtherFile(String guid, Long id, SessionFactory sessionFactory){

        if (id == null){
            return JsonAdminResponse.fail("null argument");
        }

        try (Session session = sessionFactory.openSession()){
            AuthorEntity authorEntity = AuthorizationService.getAuthorEntityBySessionGuid(guid, session);

            if (authorEntity == null){
                return JsonAdminResponse.fail("not authorized for this action!");
            }

            OtherFileEntity otherFileEntity = session.get(OtherFileEntity.class, id);

            if (otherFileEntity == null){
                return JsonAdminResponse.fail("file with this id not found!");
            }

            if (!AuthorizationService.checkPrivileges(otherFileEntity.getAuthorEntity(), authorEntity)){
                return JsonAdminResponse.fail("not authorized for this action!");
            }

            SettingsTO settingsTO = SettingsService.getSettings(session);
            Files.delete(Paths.get(settingsTO.otherFilesStoragePath, otherFileEntity.getFileName()));

            if (!session.getTransaction().isActive()){
                session.beginTransaction();
            }

            session.delete(otherFileEntity);
            session.flush();

            return JsonAdminResponse.success(null);
        } catch (Exception ex){
            Tools.handleException(ex);
            return JsonAdminResponse.fail("unknown error");
        }
    }

    public static JsonAdminResponse<OtherFileVO> getOtherFileById(Long id, SessionFactory sessionFactory){

        if (id == null){
            return JsonAdminResponse.fail("null argument");
        }

        try (Session session = sessionFactory.openSession()){
            List<OtherFileEntity> otherFileEntityList = FileDao.getOtherFilesByIds(Collections.singletonList(id), session);
            if (otherFileEntityList != null && !otherFileEntityList.isEmpty()){
                return JsonAdminResponse.success(new OtherFileVO(otherFileEntityList.get(0)));
            }
        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error getting file by id");
    }

    public static JsonAdminResponse<Void> updateOtherFileInfo(OtherFileTO otherFileTO, SessionFactory sessionFactory){

        if (otherFileTO.guid == null || otherFileTO.fileId == null){
            return JsonAdminResponse.fail("null arguments");
        }

        if (otherFileTO.name == null){
            return JsonAdminResponse.fail("file display name must be filled!");
        }

        try (Session session = sessionFactory.openSession()){
            OtherFileEntity otherFileEntity = session.get(OtherFileEntity.class, otherFileTO.fileId);

            if (otherFileEntity == null){
                return JsonAdminResponse.fail("file not found");
            }

            AuthorEntity originalAuthor = otherFileEntity.getAuthorEntity();
            AuthorEntity currentAuthor = AuthorizationService.getAuthorEntityBySessionGuid(otherFileTO.guid, session);
            if (!AuthorizationService.checkPrivileges(originalAuthor, currentAuthor)){
                return JsonAdminResponse.fail("not authorized");
            }

            if (!session.getTransaction().isActive()){
                session.beginTransaction();
            }

            otherFileEntity.setDisplayName(otherFileTO.name);
            otherFileEntity.setDescription(otherFileTO.description);

            session.update(otherFileEntity);
            session.flush();

            return JsonAdminResponse.success(null);

        } catch (Exception ex){
            Tools.handleException(ex);
        }

        return JsonAdminResponse.fail("error updating file info");
    }
}
