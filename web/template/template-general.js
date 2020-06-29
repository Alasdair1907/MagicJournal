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
<div class="width-100-pc" style="display: flex">
    
    <div class="main-listing-column">
    
        <div class="container-primary container-primary-element">
            <span class="item-container-heading">Latest articles</span>
        </div>
    
        {{#each articleVOList}}
            ${articleRepresentation}
        {{/each}}
        
        <div class="container-primary container-primary-element">
            <a class="main-a item-container-heading" href="posts.jsp?articles=true">View all articles</a>
        </div>
        
    </div>
    
    <div class="main-listing-column">
        ${tagListMenu}
        ${photoListingHomepage}
        
        <div class="container-primary container-primary-element">
            <span class="item-container-heading">Latest galleries</span>
        </div>
        
        {{#each galleryVOList}}
            ${galleryRepresentation}
        {{/each}}
        
        <div class="container-primary container-primary-element">
            <a class="main-a item-container-heading" href="posts.jsp?galleries=true">View all galleries</a>
        </div> 
        
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
    <div class="selectables-container">
        {{#each tags}}
            <span class="selectable selectable-default text-main" data-role="post-tag" data-id="{{this}}">{{this}}</span>
        {{/each}}
    </div>
    
    <button type="button" class="button-cda button-large center-block" data-role="search-posts"><i class="fas fa-search"></i> Search</button>
    
</div>
`;

