<%@page trimDirectiveWhitespaces="true"%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="com.terrestrialjournal.util.JsonApi" %>
<%@ page import="com.terrestrialjournal.util.Tools" %>
<%@ page import="com.terrestrialjournal.util.ServletUtils" %>
<%@ page import="com.fasterxml.jackson.datatype.jsr310.JavaTimeModule" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    SessionFactory sessionFactory = ServletUtils.getSessionFactory(application);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    if (request.getParameter("action") != null){
        String action = request.getParameter("action");
        Tools.log("jsonApi: action: "+action);
        out.print(JsonApi.processRequestWithSerialization(request, application, sessionFactory, objectMapper));
    }
%>