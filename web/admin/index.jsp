<%@ page import="com.terrestrialjournal.util.Tools" %>
<%@ page import="org.hibernate.SessionFactory" %>
<%@ page import="org.hibernate.Session" %>
<%@ page import="com.terrestrialjournal.to.SettingsTO" %>
<%@ page import="com.terrestrialjournal.util.JsonApi" %><%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 12/15/2019
                                                                                                
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
    <title>TMW site administration</title>

    <jsp:include page="../head-standard.jsp"/>

    <script src="templates.js"></script>
    <script src="templates-author.js"></script>
    <script src="templates-photo.js"></script>
    <script src="templates-gallery.js"></script>
    <script src="templates-articles.js"></script>
    <script src="templates-photostories.js"></script>

    <script src="helper.js"></script>

    <script src="admin.js"></script>
    <script src="admin-authors-widgets.js"></script>
    <script src="admin-photos-widgets.js"></script>
    <script src="admin-gallery-widget.js"></script>
    <script src="admin-articles-widgets.js"></script>
    <script src="admin-photostories-widgets.js"></script>
    <script src="author-profile-editor.js"></script>
    <script src="settings-editor.js"></script>
    <script src="render-control.js"></script>

    <script src="TagEditor.js"></script>
    <script src="ImageManager.js"></script>
    <script src="ImageSelectJs.js"></script>
    <script src="textEditor.js"></script>
    <script src="RelationManager.js"></script>
    <script src="NoFuckingBullshitCheckbox.js"></script>
    <script src="FileSelect.js"></script>
    <script src="PostFilter.js"></script>
    <script src="FileFilter.js"></script>
    <script src="MapPick.js"></script>
    <script src="bbCodeHint.js"></script>

    <script src="../cda/bbcode.js"></script>
    <script src="../template/template-posts.js"></script>
    <script src="../template/template-bbcode.js"></script>

    <link rel="stylesheet" href="main.css">

    <script type="text/javascript">

        var event = null;
        var func = null;

        function GetMap(){

            if (!event) {
                event = new Event('bingMapApiLoaded');
            }

            document.dispatchEvent(event);
        }
    </script>


</head>
<body class="admin">
<div data-role="base" class="width-100-pc"></div>
<script>

    let guid = Cookies.get('guid');

    if (!guid){
        $('div[data-role=base]').loginPanel();
    } else {

        let verifyGuid = $.ajax({
            url: "jsonApi.jsp",
            method: "POST",
            data: {guid: guid, action: "verifySessionGuid"}
        });

        verifyGuid.done(function (adminResponseJson) {
            let adminResponse = JSON.parse(adminResponseJson);
            if (adminResponse.success === true) {
                // load interface widget
                $('div[data-role=base]').editorPanel();
            } else {
                $('div[data-role=base]').loginPanel();
            }
        });
    }


</script>




</body>
</html>
