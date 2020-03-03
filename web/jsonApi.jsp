<%@ page import="java.util.List" %>
<%@ page import="world.thismagical.entity.ArticleEntity" %>
<%@ page import="world.thismagical.entity.AuthorEntity" %>
<%@ page import="world.thismagical.dao.AuthorDao" %>
<%@ page import="world.thismagical.util.JsonApi" %>
<%@ page import="world.thismagical.util.Tools" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>



<%

    if (request.getParameter("action") != null){

        String action = request.getParameter("action");

        Tools.log("jsonApi: action: "+action);

        /*
        if (action.equals("listAllAuthors")){
            out.println(JsonApi.listAllAuthors());
        }*/

    }
%>