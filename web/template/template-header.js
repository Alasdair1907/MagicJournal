// requires settingsTO
let headerMain = `

<div class="width-100-pc header-div">

    <!-- PROPER RESOLUTION -->
    
    <!-- main menu -->
    <div class="header-subdiv">
    <div class="container-primary desktop-menu-div higher">
        

        <a class="general-a" href="index.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-home desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">home</span>
        </div>
        </a>


        <a class="general-a" href="articles.jsp">
        <div class="desktop-menu-item">
            <i class="far fa-file-alt desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">articles</span>
        </div>
        </a>

        <a class="general-a" href="photos.jsp">
        <div class="desktop-menu-item">
            <i class="far fa-file-image desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">photos</span>
        </div>
        </a>

        <a class="general-a" href="galleries.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-images desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">galleries</span>
        </div>
        </a>

        <a class="general-a" href="about.jsp">
        <div class="desktop-menu-item">
            <i class="far fa-lightbulb desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">about</span>
        </div>
        </a>

        <a class="general-a" href="map.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-globe-europe desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">map</span>
        </div>
        </a>
    </div>
    </div>

    <!-- logo -->
    <div class="header-subdiv">
    <img src="template/logo-full-white.png" class="header-logo" alt="ThisMagical.world">
    </div>

    <!-- subscriptions menu -->
    <div class="header-subdiv">
    <div class="container-primary desktop-menu-div higher">

        {{#if settingsTO.facebookProfile}}
        <a class="general-a" href="{{settingsTO.facebookProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-facebook desktop-menu-icon"></i><span class="desktop-menu-text">facebook</span>
        </div>
        </a>
        {{/if}}

        {{#if settingsTO.twitterProfile}}
        <a class="general-a" href="{{settingsTO.twitterProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-twitter desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">twitter</span>
        </div>
        </a>
        {{/if}}

        {{#if settingsTO.instagramProfile}}
        <a class="general-a" href="{{settingsTO.instagramProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-instagram desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">instagram</span>
        </div>
        </a>
        {{/if}}
        
        {{#if settingsTO.pinterestProfile}}
        <a class="general-a" href="{{settingsTO.pinterestProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-pinterest desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">pinterest</span>
        </div>
        </a>
        {{/if}}
        
        <div class="desktop-menu-item">
            <i class="fas fa-rss desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">rss</span>
        </div>
    </div>
    </div>
</div>
    
    <!-- LOWER RESOLUTION HORIZONTAL -->
<div class="width-100-pc header-div lower">
    <div class="container-primary desktop-menu-div">

        <div class="desktop-menu-item">
            <i class="fas fa-home desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">home</span>
        </div>

        <div class="desktop-menu-item">
            <i class="far fa-file-alt desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">articles</span>
        </div>

        <div class="desktop-menu-item">
            <i class="far fa-file-image desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">photos</span>
        </div>

        <div class="desktop-menu-item">
            <i class="fas fa-images desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">galleries</span>
        </div>

        <div class="desktop-menu-item">
            <i class="far fa-lightbulb desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">about</span>
        </div>

        <div class="desktop-menu-item">
            <i class="fas fa-globe-europe desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">map</span>
        </div>
        
        <div class="desktop-menu-item">
            &nbsp;
        </div>
        
        <div class="desktop-menu-item">
            <i class="fab fa-facebook desktop-menu-icon"></i><span class="desktop-menu-text">facebook</span>
        </div>

        <div class="desktop-menu-item">
            <i class="fab fa-twitter desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">twitter</span>
        </div>

        <div class="desktop-menu-item">
            <i class="fab fa-instagram desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">instagram</span>
        </div>
        
        <div class="desktop-menu-item">
            <i class="fab fa-pinterest desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">pinterest</span>
        </div>
        
        <div class="desktop-menu-item">
            <i class="fas fa-rss desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">rss</span>
        </div>
    </div>
</div>
    
    `;