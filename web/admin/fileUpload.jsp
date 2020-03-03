<%@page trimDirectiveWhitespaces="true"%>
<%@ page import="java.io.File" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.FileItem" %>
<%@ page import="java.util.List" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.FileItemIterator" %>
<%@ page import="org.apache.tomcat.util.http.fileupload.FileItemStream" %>
<%@ page import="world.thismagical.service.FileHandlingService" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="world.thismagical.entity.ImageFileEntity" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="world.thismagical.util.Tools" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="world.thismagical.to.ImageUploadTO" %>
<%@ page import="world.thismagical.util.PostAttribution" %>
<%@ page import="java.util.Base64" %><%

    SessionFactory sessionFactory = (SessionFactory) application.getAttribute("sessionFactory");
    if (sessionFactory == null){
        Tools.log("creating new session factory.");
        sessionFactory = Tools.getSessionfactory();
        application.setAttribute("sessionFactory", sessionFactory);
    }

    ObjectMapper objectMapper = new ObjectMapper();

    ServletContext context = pageContext.getServletContext();

    List<Cookie> cookies = Arrays.asList(request.getCookies());

    String imageUploadTOJson = null;
    ImageUploadTO imageUploadTO;

    for (Cookie cookie : cookies){
        if (cookie.getName().equals("imageUploadTOJson")){
            imageUploadTOJson = cookie.getValue();
        }
    }

    Boolean res = Boolean.FALSE;
    if (imageUploadTOJson != null){
        String imageUploadTOJsonDecoded = new String(Base64.getDecoder().decode(imageUploadTOJson));
        imageUploadTO = objectMapper.readValue(imageUploadTOJsonDecoded, ImageUploadTO.class);
        res = FileHandlingService.processUpload(context, request, imageUploadTO, sessionFactory);
    }

    if (res){
        out.println("ok");
    } else {
        out.println("err");
    }

%>