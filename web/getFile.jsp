<%@ page import="java.io.File" %>
<%@ page import="world.thismagical.util.Tools" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="world.thismagical.to.SettingsTO" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="world.thismagical.service.SettingsService" %>
<%@ page import="world.thismagical.entity.OtherFileEntity" %>
<%@ page import="java.io.FileNotFoundException" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page trimDirectiveWhitespaces="true"%>
<%

    /*
    !!! SettingsTO must be refreshed by each function that alters image storage path !!!
     */
    SettingsTO settingsTO = (SettingsTO) application.getAttribute("settingsTO");

    SessionFactory sessionFactory = (SessionFactory) application.getAttribute("sessionFactory");
    if (sessionFactory == null){
        Tools.log("creating new session factory.");
        sessionFactory = Tools.getSessionfactory();
        application.setAttribute("sessionFactory", sessionFactory);
    }

    try (Session sessionGetFile = sessionFactory.openSession()) {

        if (settingsTO == null) {
            settingsTO = SettingsService.getSettings(sessionGetFile);
            application.setAttribute("settingsTO", settingsTO);
        }

        String fileStorage = settingsTO.otherFilesStoragePath;
        String fileIdStr = request.getParameter("id");

        if (fileStorage == null || fileStorage.isEmpty()){
            throw new FileNotFoundException("File storage path is not set!");
        }

        if (fileIdStr != null) {

            Integer fileId = Integer.parseInt(fileIdStr);
            OtherFileEntity otherFileEntity = sessionGetFile.get(OtherFileEntity.class, fileId);

            if (otherFileEntity == null){
                throw new IllegalArgumentException("No file with ID "+fileIdStr+" found.");
            }

            String fileName = otherFileEntity.getFileName();

            File file = new File(Tools.getPath(fileStorage) + fileName);

            if (!file.exists()) {
                Tools.log("[WARN] File " + fileName + " doesn't exist!");
            } else {
                Path path = Paths.get(fileStorage, fileIdStr);
                byte[] fileData = Files.readAllBytes(path);

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; fileId=\""+otherFileEntity.getDisplayName()+"\"");
                response.getOutputStream().write(fileData);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }
        }
    } catch (Exception ex){
        Tools.handleException(ex);
    }

%>