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

let homepageListing = `
<div class="width-100-pc" style="display: flex; flex-direction: column;">
   
   {{#if hasLatestPosts}}
       ${latestPostsHomepage}
   {{/if}}
   
   ${tagListMenu}

    
    <div class="container-primary container-primary-element homepage-section-heading">
        <span class="item-container-heading">Latest articles</span>
    </div>

    <div style="width: 100%; display: flex; flex-wrap: wrap; margin-left: auto; margin-right: auto; justify-content: center;">
        {{#each articleVOList}}
            ${articleRepresentation}
        {{/each}}
    </div>
    
    <div class="container-primary container-primary-element homepage-view-all-link-panel">
        <a class="main-a homepage-view-all-link bright-button" href="posts.jsp?articles=true">View all articles</a>
    </div>
        

    ${photoListingHomepage}
    
    <div class="container-primary container-primary-element homepage-section-heading">
        <span class="item-container-heading">Latest galleries</span>
    </div>
    
    <div style="width: 100%; display: flex; flex-wrap: wrap; margin-left: auto; margin-right: auto; justify-content: center;">
        {{#each galleryVOList}}
            ${galleryRepresentation}
        {{/each}}
    </div>
    
    <div class="container-primary container-primary-element homepage-view-all-link-panel">
        <a class="main-a homepage-view-all-link bright-button" href="posts.jsp?galleries=true">View all galleries</a>
    </div> 
        
    
</div>
`;


let pagingInfoLine = `

<table class="width-100-pc">
<tr>
<td style='width: 33%; text-align: center;'>&nbsp;</td>
<td style='width: 33%; text-align: center;'><span class="compact-location-heading">{{locationHeader}}</span></td>
<td style='width: 33%; text-align: center;'><span class="compact-listing-count">Total items: {{totalItems}}</span></td>
</tr>
</table>

`;

let postListingTemplate = `
${pagingInfoLine}

<div data-role="advanced-search-anchor" class="posts-advanced-search-anchor"></div>

<div class="width-100pc item-listing-dedicated">
    {{#each postVOList}}
    
    {{#if this.isArticle}}
        ${articleRepresentationCompact}
    {{/if}}
    
    {{#if this.isPhoto}}
        ${photoRepresentationCompact}
    {{/if}}
    
    {{#if this.isGallery}}
        ${galleryRepresentationCompact}
    {{/if}}
    
    {{/each}}
</div>

<div data-role="paging-anchor"></div>
`;

let dynamicSearchCdaTemplate = `

<div class="container-primary container-primary-element">
    <span class="selectable-heading">Select post types:</span>
    <div class="selectables-container">
        <span class="selectable selectable-default" data-class="selectable-selected-article" data-role="post-type" data-id="articles">Articles</span>
        <span class="selectable selectable-default" data-class="selectable-selected-photo" data-role="post-type" data-id="photos">Photos</span>
        <span class="selectable selectable-default" data-class="selectable-selected-gallery" data-role="post-type" data-id="galleries">Galleries</span>
    </div>
    <span class="selectable-heading">Select tags:</span>
    <div class="selectables-container selectables-tags-container">
        {{#each tags}}
            <span class="selectable selectable-default text-main" data-role="post-tag" data-id="{{this}}">{{this}}</span>
        {{/each}}
    </div>
    
    <button type="button" class="button-cda button-large center-block" data-role="search-posts"><i class="fas fa-search"></i> Search</button>
    
</div>
`;

let cdaMapTemplate = `

<div class="container-primary container-primary-element cda-map-base">
    <div data-role="cda-map" class="cda-map"></div>
</div>

<div data-role="map-search-controller" class="cda-map-base"></div>
`;

let authorPageTemplate = `
<div class="container-primary container-primary-element author-page-container">
    
    <span class="item-container-heading width-100-pc">{{authorVO.displayName}}</span>
    
    {{#if authorVO.pictureFileName}}
        <img src="getImage.jsp?filename={{authorVO.pictureFileName}}" class="author-page-picture">        
    {{/if}}

    <span class="text-main">{{{authorDescription}}}</span>
    
    <span class="afterfloat">&nbsp;</span>
    <span class="text-main">Personal website: </span><a class="main-a text-main" href="{{authorVO.website}}" target="_blank">{{authorVO.website}}</a><br />
    <span class="text-main">Email: {{authorVO.email}}</span>
    
</div>

<div class="container-primary container-primary-element author-page-container">
    <a class="center-block text-main main-a" href="posts.jsp?author={{authorVO.login}}">Search for posts by this author</a>
</div>

`;