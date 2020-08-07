<%@page trimDirectiveWhitespaces="true"%>
<%@ page import="world.thismagical.to.SettingsTO" %>
<%@ page import="world.thismagical.util.JsonApi" %>
<%@ page import="world.thismagical.vo.PostVOListUnified" %>
<%@ page import="world.thismagical.vo.PostVO" %>
<%@ page import="world.thismagical.util.Tools" %>
<%@ page import="world.thismagical.util.BBCodeExtractor" %>
<%@ page import="world.thismagical.vo.ImageVO" %>
<%@ page import="world.thismagical.util.ServletUtils" %>
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

            out.println("<item>");

            out.println("<title>"+postVO.getTitle()+"</title>");
            out.println("<link>" + postLink + "</link>");
            out.println("<description>" + postVO.getTinyDescription() + "</description>");
            out.println("<pubDate>" + Tools.formatPubDate(postVO.getCreationDate()) + "</pubDate>");

            out.println("</item>");
        }
    }

%>

    </channel>
</rss>