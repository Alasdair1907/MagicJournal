<%@ page import="world.thismagical.util.SeoTools" %>
<%@ page import="world.thismagical.util.JsonApi" %>
<%@ page import="world.thismagical.util.ServletUtils" %>
<%@page trimDirectiveWhitespaces="true"%>
<%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 8/7/2020
  Time: 10:37 PM

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
    out.println(ServletUtils.generateSitemap(application));
%>