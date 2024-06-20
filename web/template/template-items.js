/**
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
                                                                                                     
    
*/


// to be used at ArticleVO list context
let articleRepresentation = `

<div class="container-primary container-primary-element article-representation-homepage" style="display: flex; flex-direction: column;">

    <div style="flex: 1;">
        <a href="posts.jsp?article={{this.id}}" class="general-a"><div class="item-image-div item-image-div-size anything-link" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')" ></div></a>
        <a href="posts.jsp?article={{this.id}}" class="general-a"><span class="item-heading anything-link">{{this.title}}</span></a>
        <div class="item-tags-subheading-container">
            {{#each this.tagEntityList}}
            <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="article-tag">#{{this.tag}}</span></a>&nbsp;
            {{/each}}
        </div>
        <span class="text-main item-text">{{this.description}}</span>
        <span class="afterfloat">&nbsp;</span>
        
    </div>
    
    <div class="container-footing-tight">
        <table style="width: 100%">
            <tr>
                <!--<td style="width: 50%">
                    <div class="post-info-token"><span class="text-main">Author:</span><br /><a class="main-a text-main" href="author.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a></div>                    
                </td>-->
                <td style="width: 70%">
                    <span class="text-main post-info-token center-block">{{this.creationDateStr}}</span>
                </td>
                <td style="width: 30%">
                    <a class="main-a text-main center-block" href="posts.jsp?article={{this.id}}">Read more</a>
                </td>
            </tr>
        </table>
    </div>
</div>
`;

// ArticleVO
let articleRepresentationCompact = `
<div class="container-primary container-primary-element compact-container compact-item-size">
    <a href="posts.jsp?article={{this.id}}" class="general-a"><div class="compact-item-image-div compact-item-image-div-size" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')" ></div></a>
    <a href="posts.jsp?article={{this.id}}" class="general-a"><span class="item-heading">{{this.title}}</span></a>
    <div class="item-tags-subheading-container">
        {{#each this.tagEntityList}}
        
        <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="article-tag">#{{this.tag}}</span></a>&nbsp;
       
        {{/each}}
    </div>
    <span class="text-main item-text compact-item-text">{{this.description}}</span>
    <div class="post-info-line">
        <!--<a class="main-a text-main post-info-compact-token" href="author.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a>-->
        <span class="text-main post-info-compact-token">{{this.creationDateStr}}</span>
    </div>
    <div class="post-info-line">
        <a class="main-a text-main article-tag" href="posts.jsp?article={{this.id}}">READ ARTICLE</a>
    </div>
</div>
`;

// PhotoVO

let photoRepresentationCompact = `
<div class="container-primary container-primary-element compact-container compact-item-size">
    <a href="posts.jsp?photo={{this.id}}" class="general-a"><div class="compact-item-image-div compact-item-image-div-size" style="background-image: url('getImage.jsp?filename={{this.imageVO.thumbnail}}')" ></div></a>
    <a href="posts.jsp?photo={{this.id}}" class="general-a"><span class="item-heading">{{this.title}}</span></a>
    <div class="item-tags-subheading-container">
        {{#each this.tagEntityList}}
        <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="photo-tag">#{{this.tag}}</span></a>&nbsp;
        {{/each}}
    </div>
    <span class="text-main item-text compact-item-text">{{this.description}}</span>
    <div class="post-info-line">
        <!--<a class="main-a text-main post-info-compact-token" href="author.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a>-->
        <span class="text-main post-info-compact-token">{{this.creationDateStr}}</span>
    </div>
    <div class="post-info-line">
        <a class="main-a text-main photo-tag" href="posts.jsp?photo={{this.id}}">VIEW PHOTO</a>
    </div>
</div>
`;

// GalleryVO

let galleryRepresentationCompact = `
<div class="container-primary container-primary-element compact-container compact-item-size">

    <div class="gallery-representation-compact-images">
        {{#each this.galleryRepresentation}}
            <a href="posts.jsp?gallery={{../id}}" class="general-a"><div class="compact-gallery-image-div compact-gallery-image-div-size" style="background-image: url('getImage.jsp?filename={{this.thumbnail}}')"></div></a>
        {{/each}}
    </div>
    
    <a href="posts.jsp?gallery={{this.id}}" class="general-a"><span class="item-heading">{{this.title}}</span></a>
    <div class="item-tags-subheading-container">
        {{#each this.tagEntityList}}
        <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="gallery-tag">#{{this.tag}}</span></a>&nbsp;
        {{/each}}
    </div>
    <span class="text-main item-text compact-item-text">{{this.description}}</span>
    <div class="post-info-line">
        <!--<a class="main-a text-main post-info-compact-token" href="author.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a>-->
        <span class="text-main post-info-compact-token">{{this.creationDateStr}}</span>
    </div>
    <div class="post-info-line">
        <a class="main-a text-main gallery-tag" href="posts.jsp?gallery={{this.id}}">VIEW GALLERY</a>
    </div>
</div>
`;


let latestPostsHomepage = `

<div class="container-primary container-primary-element container-tag-list">
    <div style="width: 100%; display: flex; flex-wrap: wrap; margin-left: auto; margin-right: auto; justify-content: space-between;">
            <!-- the latest and greatest post goes here -->
                <table class="hlat-table hlat-mobile-hide">
                <tr>
                    <td class="hlat-toppost-image-td">
                        <a href="posts.jsp?{{latestPostLinkClass}}={{latestPostVO.id}}">
                        <div class="hlat-picture" style="background-position: center; 
                        background-size: cover; background-image: url('getImage.jsp?filename={{latestPostImage}}')"></div>
                         </a>
                    </td>
                    <td class="hlat-toppost-content-td">
                        <span class="item-container-heading hlat-latest-header">Latest Posts</span>
                        <a href="posts.jsp?{{latestPostLinkClass}}={{latestPostVO.id}}" class="general-a"><span class="item-heading anything-link hlat-item-heading">{{latestPostPreTitle}}{{latestPostVO.title}}</span></a>
                        <div class="item-tags-subheading-container hlat-subheading-container">
                            {{#each latestPostVO.tagEntityList}}
                            <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="{{../latestPostTagClass}}">#{{this.tag}}</span></a>&nbsp;
                            {{/each}}
                        </div>
                        <span class="text-main item-text hlat-item-text">{{latestPostVO.description}}</span>
                        
                        <div class="container-footing-tight hlat-footing-container">
                            <table style="width: 100%">
                                <tr>
                                    <td style="width: 70%">
                                        <span class="text-main post-info-token center-block">{{latestPostVO.creationDateStr}}</span>
                                    </td>
                                    <td style="width: 30%">
                                        <a class="main-a text-main center-block" href="posts.jsp?{{latestPostLinkClass}}={{latestPostVO.id}}">View {{latestPostLinkClass}}</a>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
                </table>
                
                <div class="hlat-nonmobile-hide hlat-tablet-width">
                    <div>
                        <a href="posts.jsp?{{latestPostLinkClass}}={{latestPostVO.id}}" class="general-a"><span class="item-heading anything-link hlat-item-heading">{{latestPostPreTitle}}{{latestPostVO.title}}</span></a>
                        <div class="item-tags-subheading-container hlat-subheading-container">
                            {{#each latestPostVO.tagEntityList}}
                            <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="{{../latestPostTagClass}}">#{{this.tag}}</span></a>&nbsp;
                            {{/each}}
                        </div>
                    </div>
                    <div>
                        <a href="posts.jsp?{{latestPostLinkClass}}={{latestPostVO.id}}" class="hlat-mobile-float">
                        <div class="hlat-picture" style="background-position: center; 
                        background-size: cover; background-image: url('getImage.jsp?filename={{latestPostImage}}')"></div>
                         </a>
                         
                         <span class="text-main item-text hlat-item-text">{{latestPostVO.description}}</span>
                         
                    </div>
                
                    <div class="container-footing-tight hlat-footing-container">
                        <table style="width: 100%">
                            <tr>
                                <td style="width: 70%">
                                    <span class="text-main post-info-token center-block">{{latestPostVO.creationDateStr}}</span>
                                </td>
                                <td style="width: 30%">
                                    <a class="main-a text-main center-block" href="posts.jsp?{{latestPostLinkClass}}={{latestPostVO.id}}">View {{latestPostLinkClass}}</a>
                                </td>
                            </tr>
                        </table>
                    </div>
                
                </div>
                
                <!-- remaining posts go here -->
                <div class="hlat-panel hlat-phone-hide">
                    {{#each furtherLatestPostVOs}}
                        ${sidePanelClassSwitch}
                    {{/each}}
                </div>
                <div class="hlat-panel hlat-nonphone-hide">
                    {{#each furtherLatestPostVOs}}
                        ${homepagePhonePanelClassSwitch}
                    {{/each}}
                </div>
    </div>                
</div>
`;

// requires tagDigestVOList
let tagListMenu = `

<div class="container-primary container-primary-element container-tag-list">
    <span class="item-container-heading">Tag Cloud</span>
    {{#each tagDigestVOList}}
        <a class="text-main tag-cloud-a" href="posts.jsp?tags={{this.title}}">#{{this.title}}</a>&nbsp;
    {{/each}}
    <a class="main-a text-main tag-cloud-advanced-link bright-button" href="posts.jsp?advanced=true">Search All Posts</a>
</div>
`;

// requires photoVOList
let photoListingHomepage = `
<div class="container-primary container-primary-element photo-listing-homepage-container">

    <table style="width: 100%;">
    <tr>
    <td style="width:33%">&nbsp;</td>
    <td style="width:33%;text-align: center;"><span class="item-container-heading">Latest Photos</span></td>
    <td style="width:33%" class="td-extra-link"><a class="main-a text-main bright-button" href="posts.jsp?photos=true">View all photos</a></td>
    </tr>
    </table>
    
   
    <div class="photo-listing-div">
    {{#each photoVOList}}
        <a class="general-a" href="posts.jsp?photo={{this.id}}">
            <div class="photo-image-div photo-image-div-size" style="background-image: url('getImage.jsp?filename={{this.imageVO.thumbnail}}')"></div>
        </a>
    {{/each}}
    </div>
</div>
`;

// to be used at GalleryVO context
let galleryRepresentation = `
<div class="container-primary container-primary-element gallery-representation-homepage">
    <table class="container-heading-normal" width="100%;"><tr>
    <td style="width:20%">&nbsp;</td>
    <td style="width:60%;text-align: center"><span class="item-container-heading">{{this.title}}</span></td>
    <td style="width:20%" class="td-extra-link"><a class="main-a text-main" href="posts.jsp?gallery={{this.id}}">View gallery</a></td>
    </tr></table>
    
    <div class="container-heading-tight">
        <span class="item-container-heading">{{this.title}}</span>
        <a class="main-a text-main" href="posts.jsp?gallery={{this.id}}">View gallery</a>
    </div>
    
    <div class="photo-listing-div">
    {{#each this.galleryRepresentation}}
        <a class="general-a" href="posts.jsp?gallery={{../this.id}}">
            <div class="photo-image-div photo-image-div-size" style="background-image: url('getImage.jsp?filename={{this.thumbnail}}')"></div>
        </a>
    {{/each}}
    </div>
</div>
`;

let pagerTemplate = `
<div class="width-100pc ">
<div class="container-primary container-primary-element pager-container">
{{#each pages}}
<a class="general-a" href="{{this.paramsStr}}">
{{#if this.isActive}}<span class="pager-link-current">{{this.pageNum}}</span>{{else}}<span class="pager-link-normal">{{this.pageNum}}</span>{{/if}}
</a>&nbsp;
{{/each}}
</div>

</div>
`;