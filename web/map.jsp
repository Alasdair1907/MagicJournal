<%@page trimDirectiveWhitespaces="true"%>
<%
    response.setHeader("Pragma", "No-cache");
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setDateHeader("Expires", -1);
%>
<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="com.terrestrialjournal.util.JsonApi" %>
<%@ page import="com.terrestrialjournal.to.SettingsTO" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="com.terrestrialjournal.util.ServletUtils" %><%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 6/30/2020
  Time: 10:42 PM
                                                                                                
--%>

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

    <title></title>

    <script type="text/javascript">

        function GetMap(){
            document.dispatchEvent(new Event('bingMapApiLoaded'));
        }

    </script>

    <script src="cda/header.js?v=ver010125"></script>
    <script src="cda/map.js?v=ver010125"></script>

    <% out.println(settingsTO.headerInjection); %>

    <link rel="alternate" type="application/rss+xml" title="<% out.print(settingsTO.websiteName); %>" href="rss.jsp" />
</head>
<body class="cda">

<div data-role="header-main" class="width-100-pc"></div>
<div data-role="content-main" class="width-100-pc">
    <div class="container-primary container-primary-element map-warning">
        <span class="item-heading" id="map-warning-remove">Map should load in a few seconds. Map functionality may be affected by certain adblock browsers, such as Vivaldi etc.</span>
    </div>
</div>

<script type="text/javascript">

    $('[data-role="header-main"]').header();

    document.addEventListener('bingMapApiLoaded', function(){
        Microsoft.Maps.loadModule("Microsoft.Maps.Clustering", function(){
            Microsoft.Maps.loadModule("Microsoft.Maps.SpatialMath", function () {
                document.getElementById("map-warning-remove").remove();
                $('[data-role="content-main"]').CdaMap();
            });
        });
    });

    loadMap();

</script>

</body>
</html>
