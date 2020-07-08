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
<%@ page import="world.thismagical.util.JsonApi" %>
<%@ page import="world.thismagical.to.SettingsTO" %>
<%

    SessionFactory sessionFactory = JsonApi.getSessionFactory(application);
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