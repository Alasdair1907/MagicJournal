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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/js-cookie@beta/dist/js.cookie.min.js"></script>
    <script src="templates.js"></script>
  <!--  <script src="admin.js"></script> -->

    <style>

        /* Container */
        .container{
            margin: 0 auto;
            border: 0px solid black;
            width: 50%;
            height: 250px;
            border-radius: 3px;
            background-color: ghostwhite;
            text-align: center;
        }
        /* Preview */
        .preview{
            width: 100px;
            height: 100px;
            border: 1px solid black;
            margin: 0 auto;
            background: white;
        }

        .preview img{
            display: none;
        }
        /* Button */
        .button{
            border: 0px;
            background-color: deepskyblue;
            color: white;
            padding: 5px 15px;
            margin-left: 10px;
        }
    </style>

</head>
<body>

<div class="container">
    <form method="post" action="" enctype="multipart/form-data" id="myform">
        <div class='preview' id="preview">
        </div>
        <div >
            <input type="file" id="file" name="file" multiple/>
            <input type="button" class="button" value="Upload" id="but_upload">
        </div>
    </form>
</div>

<script>

    $(document).ready(function(){

        $("#but_upload").click(function(){
/*
            var fd = new FormData();
            var files = $('#file')[0].files[0];
            fd.append('file',files);
*/
            let files = $("#file").get(0).files;
            var fileData = new FormData();

            for (var i = 0; i < files.length; i++) {
                fileData.append("fileInput", files[i]);
            }

            var imageUploadTO = {
                imageAttributionClass: 1,
                parentObjectId: 1234,
                sessionGuid: Cookies.get("guid")
            };

            var imageUploadTOJson = btoa(JSON.stringify(imageUploadTO));
            Cookies.set("imageUploadTOJson", imageUploadTOJson);
            
            $.ajax({
                url: '/admin/fileUpload.jsp',
                type: 'post',
                data: fileData,
                contentType: false,
                processData: false,
                success: function(response){
                    if(response){
                        var pathList = JSON.parse(response);
                        var html = "";
                        for (var t = 0; t < pathList.length; t++){
                            html += "<img src='"+pathList[t]+"' height='300'>";
                        }
                        $('#preview').html(html);
                        Cookies.remove("imageUploadTOJson");
                    }else{
                        alert('file not uploaded');
                        Cookies.remove("imageUploadTOJson");
                    }
                },
            });
        });
    });
</script>


<%

%>

</body>
</html>
