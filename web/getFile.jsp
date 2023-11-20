<%@ page import="world.thismagical.util.Tools" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="world.thismagical.to.SettingsTO" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="world.thismagical.service.SettingsService" %>
<%@ page import="world.thismagical.entity.OtherFileEntity" %>
<%@ page import="world.thismagical.util.JsonApi" %>
<%@ page import="world.thismagical.util.ServletUtils" %>
<%@ page import="java.io.*" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page trimDirectiveWhitespaces="true"%>
<%
    SessionFactory sessionFactory = ServletUtils.getSessionFactory(application);
    SettingsTO settingsTO = ServletUtils.getSettings(application);

    try (Session sessionGetFile = sessionFactory.openSession()) {

        String fileStorage = settingsTO.otherFilesStoragePath;
        String fileIdStr = request.getParameter("id");

        if (fileStorage == null || fileStorage.isEmpty()){
            throw new FileNotFoundException("File storage path is not set!");
        }

        if (fileIdStr != null) {
            Long fileId = Long.parseLong(fileIdStr);
            OtherFileEntity otherFileEntity = sessionGetFile.get(OtherFileEntity.class, fileId);

            if (otherFileEntity == null){
                throw new IllegalArgumentException("No file with ID "+fileIdStr+" found.");
            }

            String fileName = otherFileEntity.getFileName();

            File file = new File(Tools.getPath(fileStorage) + fileName);

            if (!file.exists()) {
                Tools.log("[WARN] File " + fileName + " doesn't exist!");
            } else {

                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=\""+otherFileEntity.getDisplayName()+"\"");

                ServletUtils.handleUpload(file, response.getOutputStream());
                response.getOutputStream().close();
            }
        }
    } catch (Exception ex){
        Tools.handleException(ex);
    }

%>