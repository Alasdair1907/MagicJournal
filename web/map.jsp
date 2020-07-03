<%--
  Created by IntelliJ IDEA.
  User: Alasdair
  Date: 6/30/2020
  Time: 10:42 PM
                                                                                                
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
    <jsp:include page="head-standard.jsp"/>
    <jsp:include page="head-client.jsp"/>

    <title></title>

    <script type="text/javascript">

        function GetMap(){
            document.dispatchEvent(new Event('bingMapApiLoaded'));
        }

    </script>

    <script src="cda/header.js"></script>
    <script src="cda/map.js"></script>

</head>
<body class="cda">

<div data-role="header-main" class="width-100-pc"></div>
<div data-role="content-main" class="width-100-pc"></div>

<script type="text/javascript">

    $('[data-role="header-main"]').header();

    document.addEventListener('bingMapApiLoaded', function(){
        $('[data-role="content-main"]').CdaMap();
    });

    loadMap();

</script>

</body>
</html>
