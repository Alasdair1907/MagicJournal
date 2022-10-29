// requires settingsTO
let headerMain = `

<div class="width-100-pc header-div">

    <!-- PROPER RESOLUTION -->
    
    <!-- main menu -->
    <div class="header-subdiv">
    <div class="container-primary desktop-menu-div higher">
        

        <a class="general-a desktop-menu-item-a" href="index.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-home desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">home</span>
        </div>
        </a>


        <a class="general-a desktop-menu-item-a" href="posts.jsp?articles=true">
        <div class="desktop-menu-item">
            <i class="far fa-file-alt desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">articles</span>
        </div>
        </a>

        <a class="general-a desktop-menu-item-a" href="posts.jsp?photos=true">
        <div class="desktop-menu-item">
            <i class="far fa-file-image desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">photos</span>
        </div>
        </a>

        <a class="general-a desktop-menu-item-a" href="posts.jsp?galleries=true">
        <div class="desktop-menu-item">
            <i class="fas fa-images desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">galleries</span>
        </div>
        </a>

        <a class="general-a desktop-menu-item-a" href="posts.jsp?about=true">
        <div class="desktop-menu-item">
            <i class="far fa-lightbulb desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">about</span>
        </div>
        </a>

        <a class="general-a desktop-menu-item-a" href="map.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-globe-europe desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">map</span>
        </div>
        </a>
    </div>
    </div>

    <!-- logo -->
    <div class="header-subdiv">
    <!-- add container-primary class below for brighter backgrounds -->
    <a class="general-a" href="index.jsp"><img src="template/logo-full-white.png?v=102922_4" class="header-logo" alt="{{settingsTO.websiteName}}"></a>
    </div>

    <!-- subscriptions menu -->
    <div class="header-subdiv">
    <div class="container-primary desktop-menu-div higher">

        {{#if settingsTO.facebookProfile}}
        <a class="general-a desktop-menu-item-a" target="_blank" href="{{settingsTO.facebookProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-facebook desktop-menu-icon"></i><span class="desktop-menu-text">facebook</span>
        </div>
        </a>
        {{/if}}

        {{#if settingsTO.twitterProfile}}
        <a class="general-a desktop-menu-item-a" target="_blank" href="{{settingsTO.twitterProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-twitter desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">twitter</span>
        </div>
        </a>
        {{/if}}

        {{#if settingsTO.instagramProfile}}
        <a class="general-a desktop-menu-item-a" target="_blank" href="{{settingsTO.instagramProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-instagram desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">instagram</span>
        </div>
        </a>
        {{/if}}
        
        {{#if settingsTO.pinterestProfile}}
        <a class="general-a desktop-menu-item-a" target="_blank" href="{{settingsTO.pinterestProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-pinterest desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">pinterest</span>
        </div>
        </a>
        {{/if}}
        
        {{#if settingsTO.flickrProfile}}
        <a class="general-a desktop-menu-item-a" target="_blank" href="{{settingsTO.flickrProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-flickr desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">flickr</span>
        </div>
        </a>
        {{/if}}
        
        <a class="general-a desktop-menu-item-a" href="rss.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-rss desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">rss</span>
        </div>
        </a>
    </div>
    </div>
</div>
    
    <!-- LOWER RESOLUTION HORIZONTAL -->
<div class="width-100-pc header-div lower">
    <div class="container-primary desktop-menu-div">

        <a class="general-a" href="index.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-home desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">home</span>
        </div>
        </a>

        <a class="general-a" href="posts.jsp?articles=true">
        <div class="desktop-menu-item">
            <i class="far fa-file-alt desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">articles</span>
        </div>
        </a>

        <a class="general-a" href="posts.jsp?photos=true">
        <div class="desktop-menu-item">
            <i class="far fa-file-image desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">photos</span>
        </div>
        </a>

        <a class="general-a" href="posts.jsp?galleries=true">
        <div class="desktop-menu-item">
            <i class="fas fa-images desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">galleries</span>
        </div>
        </a>

        <a class="general-a" href="posts.jsp?about=true">
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
        
        <a class="general-a menu-normal-screen-hide" href="javascript:void(42);" data-role="lowres-extra-menu">
        <div class="desktop-menu-item">
            <i class="fas fa-bell desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">subscribe</span>
        </div>
        </a>
        
        <div class="desktop-menu-item menu-tiny-screen-hide">
            &nbsp;
        </div>
        
        
        {{#if settingsTO.facebookProfile}}
        <a class="general-a menu-tiny-screen-hide" target="_blank" href="{{settingsTO.facebookProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-facebook desktop-menu-icon"></i><span class="desktop-menu-text">facebook</span>
        </div>
        </a>
        {{/if}}

        {{#if settingsTO.twitterProfile}}
        <a class="general-a menu-tiny-screen-hide" target="_blank" href="{{settingsTO.twitterProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-twitter desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">twitter</span>
        </div>
        </a>
        {{/if}}

        {{#if settingsTO.instagramProfile}}
        <a class="general-a menu-tiny-screen-hide" target="_blank" href="{{settingsTO.instagramProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-instagram desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">instagram</span>
        </div>
        </a>
        {{/if}}
        
        {{#if settingsTO.pinterestProfile}}
        <a class="general-a menu-tiny-screen-hide" target="_blank" href="{{settingsTO.pinterestProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-pinterest desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">pinterest</span>
        </div>
        </a>
        {{/if}}
        
        {{#if settingsTO.flickrProfile}}
        <a class="general-a menu-tiny-screen-hide" target="_blank" href="{{settingsTO.flickrProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-flickr desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">flickr</span>
        </div>
        </a>
        {{/if}}
        
        <a class="general-a menu-tiny-screen-hide" href="rss.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-rss desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">rss</span>
        </div>
        </a>
        
    </div>
</div>
    
    `;

let extraMenuOverlay = `
<div class="extra-menu-overlay">
    <div class="extra-menu-container" data-role="extra-menu-container">
        <div class="width-100-pc extra-menu-icons-container">

        {{#if settingsTO.facebookProfile}}
        <a class="general-a " target="_blank" href="{{settingsTO.facebookProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-facebook desktop-menu-icon"></i><span class="desktop-menu-text">facebook</span>
        </div>
        </a>
        {{/if}}

        {{#if settingsTO.twitterProfile}}
        <a class="general-a" target="_blank" href="{{settingsTO.twitterProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-twitter desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">twitter</span>
        </div>
        </a>
        {{/if}}

        {{#if settingsTO.instagramProfile}}
        <a class="general-a " target="_blank" href="{{settingsTO.instagramProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-instagram desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">instagram</span>
        </div>
        </a>
        {{/if}}
        
        {{#if settingsTO.pinterestProfile}}
        <a class="general-a " target="_blank" href="{{settingsTO.pinterestProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-pinterest desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">pinterest</span>
        </div>
        </a>
        {{/if}}
        
        {{#if settingsTO.flickrProfile}}
        <a class="general-a " target="_blank" href="{{settingsTO.flickrProfile}}">
        <div class="desktop-menu-item">
            <i class="fab fa-flickr desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">flickr</span>
        </div>
        </a>
        {{/if}}
        
        <a class="general-a " target="_blank" href="rss.jsp">
        <div class="desktop-menu-item">
            <i class="fas fa-rss desktop-menu-icon">&nbsp;</i>
            <span class="desktop-menu-text">rss</span>
        </div>
        </a>
            
        </div>
    </div>
</div>
`;
