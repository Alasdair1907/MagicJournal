<%@page trimDirectiveWhitespaces="true"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="world.thismagical.service.FileHandlingService" %>
<%@ page import="world.thismagical.to.ImageUploadTO" %>
<%@ page import="world.thismagical.to.OtherFileTO" %>
<%@ page import="world.thismagical.util.Tools" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.util.List" %>
<%

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

    String otherFileTOJson = null;
    OtherFileTO otherFileTO;

    for (Cookie cookie : cookies){
        if (cookie.getName().equals("imageUploadTOJson")){
            imageUploadTOJson = cookie.getValue();
        }

        if (cookie.getName().equals("otherFileTOJson")){
            otherFileTOJson = cookie.getValue();
        }
    }

    Boolean res = Boolean.FALSE;

    try (Session sessionFileUpload = sessionFactory.openSession()) {

        if (imageUploadTOJson != null) {
            String imageUploadTOJsonDecoded = new String(Base64.getDecoder().decode(imageUploadTOJson));
            imageUploadTO = objectMapper.readValue(imageUploadTOJsonDecoded, ImageUploadTO.class);
            res = FileHandlingService.processUpload(context, request, imageUploadTO, sessionFileUpload);
        }

        if (otherFileTOJson != null) {
            String otherFileTOJsonDecoded = new String(Base64.getDecoder().decode(otherFileTOJson));
            otherFileTO = objectMapper.readValue(otherFileTOJsonDecoded, OtherFileTO.class);
            res = FileHandlingService.processUpload(context, request, otherFileTO, sessionFileUpload);
        }
    } catch (Exception ex){
        Tools.handleException(ex);
    }

    if (res){
        out.println("ok");
    } else {
        out.println("err");
    }

%>