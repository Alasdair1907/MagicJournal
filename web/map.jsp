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

    <title>ThisMagical.world</title> <!-- TODO load from settings -->

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
