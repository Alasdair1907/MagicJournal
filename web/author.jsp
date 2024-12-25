<%@page trimDirectiveWhitespaces="true"%>
<%
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setDateHeader("Expires", -1);
%>
<%@ page import="com.terrestrialjournal.util.JsonApi" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="com.terrestrialjournal.to.SettingsTO" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="com.terrestrialjournal.vo.ArticleVO" %>
<%@ page import="com.terrestrialjournal.service.ArticleService" %>
<%@ page import="com.terrestrialjournal.util.ServletUtils" %><%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 7/1/2020
  Time: 11:31 PM
                                                                                                
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    SessionFactory sessionFactory = ServletUtils.getSessionFactory(application);
    SettingsTO settingsTO = ServletUtils.getNoAuthSettingsCached(application);
    ObjectMapper objectMapper = new ObjectMapper();

    String article = request.getParameter("article");
    String gallery = request.getParameter("gallery");
    String photo = request.getParameter("photo");

    if (article != null){

    }

%>

<html>
<!--
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
                                                                                                 
-->
<head>

    <meta name="viewport" content="width=device-width" />
    <meta name="settingsTOCache" content="<% out.print(JsonApi.toBase64(objectMapper.writeValueAsString(settingsTO))); %>">

    <jsp:include page="head-standard.jsp"/>
    <jsp:include page="head-client.jsp"/>

    <title></title>
    <script src="cda/header.js"></script>

    <% out.println(settingsTO.headerInjection); %>

    <link rel="alternate" type="application/rss+xml" title="<% out.print(settingsTO.websiteName); %>" href="rss.jsp" />

</head>
<body class="cda">

<div data-role="header-main" class="width-100-pc"></div>
<div data-role="content-main" class="width-100-pc"></div>

<script type="text/javascript">

    $('[data-role="header-main"]').header();
    $('[data-role="content-main"]').authorPage();

</script>

</body>
</html>
