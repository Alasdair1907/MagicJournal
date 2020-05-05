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
            <a class="main-a item-container-heading" href="articles.jsp">View all articles</a>
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
            <a class="main-a item-container-heading" href="galleries.jsp">View all galleries</a>
        </div> 
        
    </div>
    
    
</div>
`;

let articleListing = `
<div class="width-100pc item-listing-dedicated">
    {{#each articleVOList}}
        ${articleRepresentationCompact}
    {{/each}}
</div>
`;