<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="world.thismagical.util.JsonApi" %>
<%@ page import="world.thismagical.to.SettingsTO" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="world.thismagical.util.ServletUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

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
    <meta name="settingsTOCache" content="<% out.print(JsonApi.toBase64(objectMapper.writeValueAsString(settingsTO))); %>">

    <jsp:include page="head-standard.jsp"/>
    <jsp:include page="head-client.jsp"/>

    <title><% out.print(settingsTO.websiteName); %></title>

    <script src="cda/homepage.js"></script>
    <script src="cda/header.js"></script>

    <% out.println(settingsTO.headerInjection); %>

    <link rel="alternate" type="application/rss+xml" title="<% out.print(settingsTO.websiteName); %>" href="rss.jsp" />

  </head>
  <body class="cda">

  <div data-role="header-main" class="width-100-pc"></div>
  <div data-role="content-main" class="width-100-pc"></div>

  <script type="text/javascript">
    $('[data-role="header-main"]').header();
    $('[data-role="content-main"]').homepage();
  </script>

  </body>
</html>
