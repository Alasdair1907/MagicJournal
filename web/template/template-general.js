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
    </div>
    
    <div class="main-listing-column">
        ${tagListMenu}
        ${photoListingHomepage}
    </div>
    
</div>
`;