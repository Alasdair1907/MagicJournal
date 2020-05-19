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
<div class="width-100pc paging-info-line" >
<span class="text-main">Total items: {{totalItems}}</span>
</div>
`;

let postListingTemplate = `
${pagingInfoLine}
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



