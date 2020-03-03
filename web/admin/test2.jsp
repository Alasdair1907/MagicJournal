<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="world.thismagical.util.Tools" %>
<%@ page import="world.thismagical.entity.AuthorEntity" %>
<%@ page import="javax.persistence.criteria.*" %><%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 12/17/2019
  Time: 5:55 PM
                                                                                                
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    <title>Title</title>
</head>
<body>

<%
    List<String> displayNames = new ArrayList<>();
    displayNames.add("abcdef");
    displayNames.add("hrwdfv");
    displayNames.add("7654edf");
    displayNames.add("6hbfd");

    Session hSession = Tools.getNewSession();

    CriteriaBuilder cb = hSession.getCriteriaBuilder();
    CriteriaQuery<AuthorEntity> cq = cb.createQuery(AuthorEntity.class);
    Root<AuthorEntity> root = cq.from(AuthorEntity.class);

    Path<String> displayName = root.get("displayName");
    Path<String> login = root.get("login");

    Predicate loginEq = cb.equal(login, "user2");
    Predicate displayNameIn = displayName.in(displayNames);

    Predicate loginAndDisplayName = cb.and(loginEq, displayNameIn);
    cq.select(root).where(loginAndDisplayName);
    List<AuthorEntity> list = hSession.createQuery(cq).getResultList();

    hSession.close();

    out.println("<table style='border-width:1px;'>");
    for (AuthorEntity authorEntity : list){
        out.println("<tr>");
        out.println("<td>"+authorEntity.getDisplayName()+"</td><td>"+authorEntity.getLogin()+"</td><td>"+authorEntity.getPasswd()+"</td>");
        out.println("</tr>");
    }
    out.println("</table>");
%>

</body>
</html>
