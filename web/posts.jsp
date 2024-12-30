<%@page trimDirectiveWhitespaces="true"%>
<%
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setDateHeader("Expires", -1);
%>
<%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 5/2/2020
  Time: 5:56 PM
                                                                                                
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.terrestrialjournal.util.JsonApi" %>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="com.terrestrialjournal.util.Tools" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="com.terrestrialjournal.to.SettingsTO" %>
<%@ page import="com.terrestrialjournal.to.MetaTO" %>
<%@ page import="com.terrestrialjournal.util.ServletUtils" %>

<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
<%@ page import="java.text.*" %>
<%@ page import="com.terrestrialjournal.util.JspApi" %>

<%
    SessionFactory sessionFactory = ServletUtils.getSessionFactory(application);
    SettingsTO settingsTO = ServletUtils.getNoAuthSettingsCached(application);
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
        MetaTO metaTO = ServletUtils.prepareMeta(request, application);

        out.println("<title>"+metaTO.getTitle()+"</title>");

        out.println(String.format("<meta property='og:url' content='%s'>", metaTO.getOgUrl()));
        out.println("<meta property='og:type' content='article'>");
        out.println(String.format("<meta property='og:title' content='%s'>", metaTO.getOgTitle()));
        String description = metaTO.getTwitterDescription();
        if (description == null || description.isBlank()){
            description = metaTO.getOgDescription();
        }
        out.println(String.format("<meta property='og:description' content='%s'>", description));
        out.println(String.format("<meta property='og:image' content='%s'>", metaTO.getOgImage()));

        out.println("<meta property='twitter:card' content='summary_large_image'>");
        out.println(String.format("<meta property='twitter:creator' content='%s'>", metaTO.getTwitterHandle()));
        out.println(String.format("<meta property='twitter:site' content='%s'>", metaTO.getTwitterHandle()));
        out.println(String.format("<meta property='twitter:image' content='%s'>", metaTO.getTwitterImage()));
        out.println(String.format("<meta property='twitter:description' content='%s'>", metaTO.getTwitterDescription()));
        out.println(String.format("<meta property='twitter:title' content='%s'>", metaTO.getTwitterTitle()));

    %>

    <meta name="settingsTOCache" content="<% out.print(JsonApi.toBase64(objectMapper.writeValueAsString(settingsTO))); %>">

    <jsp:include page="head-standard.jsp"/>
    <jsp:include page="head-client.jsp"/>

    <script src="cda/posts.js"></script>
    <script src="cda/header.js"></script>

    <% out.println(settingsTO.headerInjection); %>

    <link rel="alternate" type="application/rss+xml" title="<% out.print(settingsTO.websiteName); %>" href="rss.jsp" />

</head>


<body class="cda">

<div data-role="header-main" class="width-100-pc"></div>
<div data-role="content-main" class="width-100-pc">


    <%
        String numberParam = null;
        Long number = null;


        Map<String, String[]> paramMap = request.getParameterMap();
        if (paramMap.containsKey("gallery")) {
            numberParam = request.getParameter("gallery");
        } else if (paramMap.containsKey("article")) {
            numberParam = request.getParameter("article");
        } else if (paramMap.containsKey("photo")) {
            numberParam = request.getParameter("photo");
        } else if (paramMap.containsKey("collage")) {
            numberParam = request.getParameter("collage");
        } else {
            out.println("<div class='container-primary container-primary-element map-warning'>");
            out.println("<span class='item-heading'>Loading, please wait...</span>");
            out.println("</div>");
        }


        if (numberParam != null) {
            try {
                number = Long.parseLong(numberParam);
            } catch (Exception e) {
                out.println("Invalid number format: " + numberParam + "<br>");
            }
        }


        if (number != null) {
            if (paramMap.containsKey("gallery")) {
                out.println(JspApi.getGalleryRender(number, ServletUtils.getCookieValue(request, "guid"), sessionFactory));
            } else if (paramMap.containsKey("article")) {
                out.println(JspApi.getArticleRender(number, ServletUtils.getCookieValue(request, "guid"), sessionFactory));
            } else if (paramMap.containsKey("photo")) {
                out.println(JspApi.getPhotoRender(number, ServletUtils.getCookieValue(request, "guid"), sessionFactory));
            } else if (paramMap.containsKey("collage")) {
                out.println(JspApi.getPhotostoryRender(number, ServletUtils.getCookieValue(request, "guid"), sessionFactory));
            }
        }

        /*
        Once upon a time, there used to be a nice and clean javascript. But,
        the evil search engines just couldn't be buggered to render jQuery properly.
        And so, the main posts are pre-rendered when they are being saved, and this .jsp
        displays the pre-rendered HTML when the main posts are being requested.
         */
        out.println("<script type=\"text/javascript\">");
        out.println("$('[data-role=\"header-main\"]').header();");
        out.println("$('[data-role=\"content-main\"]').posts();");
        out.println("</script>");
    %>
    
</div>

</body>
</html>