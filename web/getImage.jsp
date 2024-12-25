<%@ page import="java.io.File" %>
<%@ page import="com.terrestrialjournal.util.Tools" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.Paths" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="com.terrestrialjournal.to.SettingsTO" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="com.terrestrialjournal.service.SettingsService" %>
<%@ page import="java.io.FileNotFoundException" %>
<%@ page import="com.terrestrialjournal.util.JsonApi" %>
<%@ page import="com.terrestrialjournal.util.ServletUtils" %><%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 1/2/2020
  Time: 4:50 PM
                                                                                                
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page trimDirectiveWhitespaces="true"%>
<%

    String fileName = request.getParameter("filename");
    if (fileName.equals("imgAdminPlaceholder.png")){
        String imageStorage = request.getSession().getServletContext().getRealPath("/");
        Path path = Paths.get(imageStorage, "resources", fileName);
        byte[] imageData = Files.readAllBytes(path);

        response.setContentType("image/png");
        response.getOutputStream().write(imageData);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    } else {
        SettingsTO settingsTO = ServletUtils.getSettings(application);
        String imageStorage = settingsTO.imageStoragePath;

        if (imageStorage == null || imageStorage.isEmpty()) {
            throw new FileNotFoundException("Image storage path is not set!");
        }

        if (fileName != null && Tools.verifyFileName(fileName) && Files.exists(Paths.get(imageStorage, fileName))) {
            File imageFile = new File(Tools.getPath(imageStorage) + fileName);
            String contentType = Tools.getContentTypeByExtension(Tools.getExtension(fileName));

            if (contentType == null) {
                Tools.log("[WARN] Content type not found for image file " + fileName);
            } else {
                response.setContentType(contentType);
                ServletUtils.handleUpload(imageFile, response.getOutputStream());
                response.getOutputStream().close();
            }
        }
    }
%>