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

    <div class="container-primary container-primary-element">
        <span class="item-container-heading">{{this.title}}</span>
        <div class="item-image-div item-image-div-size" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')"></div>
        <span class="text-main item-text">{{this.description}}</span>
        <span class="afterfloat">&nbsp;</span>
    </div>
`;

// requires tagDigestVOList
let tagListMenu = `

<div class="container-primary container-primary-element">
    <span class="item-container-heading">Tag Cloud</span>
    {{#each tagDigestVOList}}
        <a class="text-main tag-cloud-a" href="search.jsp?tag={{this.title}}">#{{this.title}}</a>&nbsp;
    {{/each}}
</div>
`;

// requires photoVOList
let photoListingHomepage = `
<div class="container-primary container-primary-element">
    <span class="item-container-heading">Tag Cloud</span>
    <div class="photo-listing-div">
    {{#each photoVOList}}
        <div class="photo-image-div photo-image-div-size" style="background-image: url('getImage.jsp?filename={{this.imageVO.thumbnail}}')"></div>
    {{/each}}
    </div>
</div>
`