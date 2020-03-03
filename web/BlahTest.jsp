<%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 2/10/2020
  Time: 2:51 PM
                                                                                                
--%>
<%@ page import="javax.naming.Context" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="javax.naming.directory.*" %>
<%@ page import="javax.naming.NamingEnumeration" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.LinkedHashMap" %>


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
    <title>LDAP test page</title>
    <style>

        .h {
            color: blue;
            font-weight: bold;
            font-size: 36px;
            margin-bottom: 40px;
            margin-top: 20px;
        }

        .b {
            color: blue;
            font-weight: bold;
            font-size: 16px;
        }

        .n {
            color: blue;
            font-weight: normal;
            font-size: 16px;
        }

        .input {
            background: black;
            border-color: blue;
            color: blue;
        }
    </style>
</head>
<body style="background: black;">

<span class="h">LDAP Authorization Test Page</span><br />

<span class="b">Input login to lookup:</span><br />
<form action="index.jsp" method="post">
    <input type="text" name="login" class="input">
    <input type="submit" value="Lookup" class="input">
</form>

<pre class="n">
.     .       .  .   . .   .   . .    +  .
  .     .  :     .    .. :. .___---------___.
       .  .   .    .  :.:. _".^ .^ ^.  '.. :"-_. .
    .  :       .  .  .:../:            . .^  :.:\.
        .   . :: +. :.:/: .   .    .        . . .:\
 .  :    .     . _ :::/:               .  ^ .  . .:\
  .. . .   . - : :.:./.                        .  .:\
  .      .     . :..|:                    .  .  ^. .:|
    .       . : : ..||        .                . . !:|
  .     . . . ::. ::\(                           . :)/
 .   .     : . : .:.|. ######              .#######::|
  :.. .  :-  : .:  ::|.#######           ..########:|
 .  .  .  ..  .  .. :\ ########          :######## :/
  .        .+ :: : -.:\ ########       . ########.:/
    .  .+   . . . . :.:\. #######       #######..:/
      :: . . . . ::.:..:.\           .   .   ..:/
   .   .   .  .. :  -::::.\.       | |     . .:/
      .  :  .  .  .-:.":.::.\             ..:/
 .      -.   . . . .: .:::.:.\.           .:/
.   .   .  :      : ....::_:..:\   ___.  :/
   .   .  .   .:. .. .  .: :.:.:\       :/
     +   .   .   : . ::. :.:. .:.|\  .:/|
     .         +   .  .  ...:: ..|  --.:|
.      . . .   .  .  . ... :..:.."(  ..)"
 .   .       .      :  .   .: ::/  .  .::\
    </pre>

<%



    Hashtable<String, String> contextEnv = new Hashtable<String, String>();
    contextEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    contextEnv.put(Context.PROVIDER_URL, "ldap://dc.catzilla.iqmen.ru:3268");
    contextEnv.put(Context.SECURITY_AUTHENTICATION, "GSSAPI");

    DirContext ctx = new InitialDirContext(contextEnv);

    String currentUser = request.getRemoteUser();

    if (request.getParameter("login") != null){
        currentUser = request.getParameter("login");
    } else if (currentUser == null || currentUser.isEmpty()){
            currentUser = "test";
    }


// UserID - Last Name - First Name - Group - Job Title - Phone Number - Email address
    String[] attrIDs = { "sAMAccountName", "sn", "givenName", "memberOf", "title", "telephoneNumber", "mail"};
    SearchControls searchControls = new SearchControls();
    searchControls.setReturningAttributes(attrIDs);
    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    searchControls.setTimeLimit(6000);

    NamingEnumeration<SearchResult> searchResults = ctx.search("DC=catzilla,DC=iqmen,DC=ru", "(sAMAccountName=" + currentUser + ")", searchControls);

    Map<String, String> dataMap = new LinkedHashMap();

    out.println("<span class='h'>Info for user: "+currentUser+"</span><br />");

    if (searchResults.hasMore()) {
        SearchResult currentSearchResult = searchResults.next();
        Attributes searchResultAttributes = currentSearchResult.getAttributes();

        if (searchResultAttributes.get("sAMAccountName") != null){
            dataMap.put("User ID", searchResultAttributes.get("sAMAccountName").toString());
        }

        if (searchResultAttributes.get("sn") != null){
            dataMap.put("Last Name", searchResultAttributes.get("sn").toString());
        }

        if (searchResultAttributes.get("givenName") != null){
            dataMap.put("First Name", searchResultAttributes.get("givenName").toString());
        }

        if (searchResultAttributes.get("memberOf") != null){
            dataMap.put("Member Of", searchResultAttributes.get("memberOf").toString());
        }

        if (searchResultAttributes.get("title") != null){
            dataMap.put("Job Title", searchResultAttributes.get("title").toString());
        }

        searchResults.close();
    }


    ctx.close();

    out.println("<table style='width:40%;'>");
    for (String key : dataMap.keySet()){
        String val = dataMap.get(key);
        out.println("<tr><td style='width:20%;text-align:right;padding-right:5%;'><span class='b'>"+key+": </span></td><td style='width:80%;text-align:left;'><span class='n'>"+val+"</span></td></tr>");
    }
    out.println("</table>");

%>

</body>
</html>
