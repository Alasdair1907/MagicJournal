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

    <script src="cda/homepage.js"></script>
    <script src="cda/header.js"></script>

  </head>
  <body class="cda">

  <div data-role="header-main" class="width-100-pc"></div>
  <div data-role="content-main" class="width-100-pc"></div>

  <script type="text/javascript">
    $('[data-role="header-main"]').header();
    $('[data-role="content-main"]').homepage();
  </script>

  </body>
</html>
