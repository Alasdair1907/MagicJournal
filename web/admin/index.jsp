<%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 12/15/2019
  Time: 3:54 PM
                                                                                                
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

    <script src="https://kit.fontawesome.com/fb98a6c4cf.js" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/handlebars@latest/dist/handlebars.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.min.js" integrity="sha256-VazP97ZCwtekAsvgPBSUwPFKdrwD3unUfSGVYrahUqU=" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/js-cookie@beta/dist/js.cookie.min.js"></script>

    <script src="/external/image-picker.min.js"></script>
    <link rel="stylesheet" href="/external/image-picker.css">

    <script src="/external/tagsinput/bootstrap-tagsinput.js"></script>
    <link rel="stylesheet" href="/external/tagsinput/bootstrap-tagsinput.css">

    <script src="helper.js"></script>

    <script src="templates.js"></script>
    <script src="templates-author.js"></script>
    <script src="templates-photo.js"></script>
    <script src="templates-gallery.js"></script>
    <script src="templates-articles.js"></script>

    <script src="admin.js"></script>
    <script src="admin-authors-widgets.js"></script>
    <script src="admin-photos-widgets.js"></script>
    <script src="admin-gallery-widget.js"></script>
    <script src="admin-articles-widgets.js"></script>

    <script src="TagEditor.js"></script>
    <script src="ImageManager.js"></script>
    <script src="ImageSelectJs.js"></script>
    <script src="RelationManager.js"></script>

    <link rel="stylesheet" media="screen and (min-aspect-ratio: 1000/1000)" href="/admin/main.css">
    <!--<link rel="stylesheet" media="screen and (max-aspect-ratio: 999/1000)" href="/main-vertical.css">-->

</head>
<body class="admin">
<div data-role="base" class="width-100-pc"></div>
<script>

    let guid = Cookies.get('guid');

    if (!guid){
        $('div[data-role=base]').loginPanel();
    } else {

        let verifyGuid = $.ajax({
            url: "/admin/jsonApi.jsp",
            method: "POST",
            data: {data: guid, action: "verifySessionGuid"}
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
