<%@page trimDirectiveWhitespaces="true"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="com.terrestrialjournal.service.FileHandlingService" %>
<%@ page import="com.terrestrialjournal.to.ImageUploadTO" %>
<%@ page import="com.terrestrialjournal.to.OtherFileTO" %>
<%@ page import="com.terrestrialjournal.util.Tools" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.util.List" %>
<%@ page import="com.terrestrialjournal.util.JsonApi" %>
<%@ page import="com.terrestrialjournal.to.SettingsTO" %>
<%@ page import="com.terrestrialjournal.util.ServletUtils" %>
<%

    SessionFactory sessionFactory = ServletUtils.getSessionFactory(application);
    ObjectMapper objectMapper = new ObjectMapper();

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
            String imageUploadTOJsonDecoded = JsonApi.fromBase64(imageUploadTOJson);
            imageUploadTO = objectMapper.readValue(imageUploadTOJsonDecoded, ImageUploadTO.class);
            res = FileHandlingService.processUpload(request, imageUploadTO, sessionFileUpload);
        }

        if (otherFileTOJson != null) {
            String otherFileTOJsonDecoded = JsonApi.fromBase64(otherFileTOJson);
            otherFileTO = objectMapper.readValue(otherFileTOJsonDecoded, OtherFileTO.class);
            res = FileHandlingService.processUpload(request, otherFileTO, sessionFileUpload);
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