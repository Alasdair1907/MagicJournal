<%@page trimDirectiveWhitespaces="true"%>
<%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 5/2/2020
  Time: 5:56 PM
                                                                                                
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="world.thismagical.util.JsonApi" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="world.thismagical.util.Tools" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="world.thismagical.to.SettingsTO" %>
<%@ page import="world.thismagical.to.MetaTO" %>

<%
    SessionFactory sessionFactory = JsonApi.getSessionFactory(application);
    SettingsTO settingsTO = JsonApi.getNoAuthSettingsCached(application);
    ObjectMapper objectMapper = new ObjectMapper();

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
    <%
        MetaTO metaTO = JsonApi.prepareMeta(request, application);

        out.println(String.format("<meta property='og:url' content='%s' />", metaTO.getOgUrl()));
        out.println("<meta property='og:type' content='article' />");
        out.println(String.format("<meta property='og:title' content='%s' />", metaTO.getOgTitle()));
        out.println(String.format("<meta property='og:description' content='%s' />", metaTO.getOgDescription()));
        out.println(String.format("<meta property='og:image' content='%s' />", metaTO.getOgImage()));

        /*
        out.println(String.format("<meta name='description' content='%s'>", metaTO.getDescription()));
        out.println(String.format("<meta name='keywords' content='%s'>", metaTO.getKeywords()));
        out.println(String.format("<meta name='author' content='%s'>", metaTO.getAuthor()));*/

    %>

    <meta name="settingsTOCache" content="<% out.print(JsonApi.toBase64(objectMapper.writeValueAsString(settingsTO))); %>">

    <jsp:include page="head-standard.jsp"/>
    <jsp:include page="head-client.jsp"/>

    <title></title>

    <script src="cda/posts.js"></script>
    <script src="cda/header.js"></script>

    <% out.println(settingsTO.headerInjection); %>

    <link rel="alternate" type="application/rss+xml" title="<% out.print(settingsTO.websiteName); %>" href="rss.jsp" />

</head>


<body class="cda">

<div data-role="header-main" class="width-100-pc"></div>
<div data-role="content-main" class="width-100-pc"></div>

<script type="text/javascript">

    $('[data-role="header-main"]').header();
    $('[data-role="content-main"]').posts();
</script>

</body>
</html>