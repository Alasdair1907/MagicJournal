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
<%@ page import="world.thismagical.service.SettingsService" %><%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 1/2/2020
  Time: 4:50 PM
                                                                                                
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page trimDirectiveWhitespaces="true"%>
<%

    /*
    !!! SettingsTO must be refreshed by each function that alters image storage path !!!
     */

    SettingsTO settingsTO = (SettingsTO) application.getAttribute("settingsTO");
    if (settingsTO == null){
        SessionFactory sessionFactory = (SessionFactory) application.getAttribute("sessionFactory");
        if (sessionFactory == null){
            Tools.log("creating new session factory.");
            sessionFactory = Tools.getSessionfactory();
            application.setAttribute("sessionFactory", sessionFactory);
        }

        Session sessionGetImage = sessionFactory.openSession();
        settingsTO = SettingsService.getSettings(sessionGetImage);
        sessionGetImage.close();
        application.setAttribute("settingsTO", settingsTO);
    }


    String imageStorage = settingsTO.imageStoragePath;

    String fileName = request.getParameter("filename");

    if (fileName != null && Tools.verifyFileName(fileName)){

        File imageFile = new File(Tools.getPath(imageStorage) + fileName);
        String contentType = Tools.getContentTypeByExtension(Tools.getExtension(fileName));

        if (!imageFile.exists()){
            Tools.log("[WARN] File "+fileName+" doesn't exist!");
        } else if (contentType == null) {
            Tools.log("[WARN] Content type not found for image file " + fileName);
        } else {
                Path path = Paths.get(imageStorage, fileName);
                byte[] imageData = Files.readAllBytes(path);

                response.setContentType(contentType);
                response.getOutputStream().write(imageData);
                response.getOutputStream().flush();
                response.getOutputStream().close();
        }
    }

%>