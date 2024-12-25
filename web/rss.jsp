<%@page trimDirectiveWhitespaces="true"%>
<%@ page import="com.terrestrialjournal.to.SettingsTO" %>
<%@ page import="com.terrestrialjournal.util.JsonApi" %>
<%@ page import="com.terrestrialjournal.vo.PostVOListUnified" %>
<%@ page import="com.terrestrialjournal.vo.PostVO" %>
<%@ page import="com.terrestrialjournal.util.Tools" %>
<%@ page import="com.terrestrialjournal.util.BBCodeExtractor" %>
<%@ page import="com.terrestrialjournal.vo.ImageVO" %>
<%@ page import="com.terrestrialjournal.util.ServletUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 7/14/2020
  Time: 7:32 PM

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

--%>
<%@ page contentType="text/xml;charset=UTF-8" language="java" %>
<%
    SettingsTO settingsTO = ServletUtils.getNoAuthSettingsCached(application);
%>
<?xml version="1.0" encoding="UTF-8" ?>
<rss version="2.0">

    <channel>
        <title><% out.print(settingsTO.websiteName); %></title>
        <link><% out.print(settingsTO.websiteURL); %></link>
<%
    PostVOListUnified postVOListUnified = JsonApi.latest(50, ServletUtils.getSessionFactory(application));

    if (postVOListUnified.posts != null){
        for (PostVO postVO : postVOListUnified.posts){

            String postLink = Tools.normalizeURL(settingsTO.websiteURL) + "/posts.jsp?" + postVO.getPostAttributionStr().toLowerCase() + "=" + postVO.getId();

            String description = Tools.escapeLtGt(postVO.getDescription());
            if (description.length() > 300){
                description = description.substring(0,297)+"...";
            }

            out.println("<item>");

            out.println("<title>"+Tools.escapeLtGt(postVO.getTitle())+"</title>");
            out.println("<link>" + postLink + "</link>");
            out.println("<description>" + description + "</description>");
            out.println("<pubDate>" + Tools.formatPubDate(postVO.getCreationDate()) + "</pubDate>");

            out.println("</item>");
        }
    }

%>

    </channel>
</rss>