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
        <a href="articles.jsp?article={{this.id}}" class="general-a"><div class="item-image-div item-image-div-size anything-link" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')" ></div></a>
        <a href="articles.jsp?article={{this.id}}" class="general-a"><span class="item-heading anything-link">{{this.title}}</span></a>
        <span class="text-main item-text">{{this.description}}</span>
        <span class="afterfloat">&nbsp;</span>
        <div class="post-info-line">
        <div><span class="text-main">Author: </span><a class="main-a text-main" href="search.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a></div>
        <span class="text-main">Date posted: {{this.creationDateStr}}</span>
        <a class="main-a text-main" href="articles.jsp?article={{this.id}}">Read more</a>
        </div>
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

    <table style="width: 100%;">
    <tr>
    <td style="width:33%">&nbsp;</td>
    <td style="width:33%;text-align: center;"><span class="item-container-heading">Latest Photos</span></td>
    <td style="width:33%" class="td-extra-link"><a class="main-a text-main" href="photos.jsp">View all photos</a></td>
    </tr>
    </table>
    
   
    <div class="photo-listing-div">
    {{#each photoVOList}}
        <a class="general-a" href="photos.jsp?photo={{this.id}}">
            <div class="photo-image-div photo-image-div-size" style="background-image: url('getImage.jsp?filename={{this.imageVO.thumbnail}}')"></div>
        </a>
    {{/each}}
    </div>
</div>
`;

// to be used at GalleryVO context
let galleryRepresentation = `
<div class="container-primary container-primary-element">
    <table width="100%;"><tr>
    <td style="width:33%">&nbsp;</td>
    <td style="width:33%;text-align: center"><span class="item-container-heading">{{this.title}}</span></td>
    <td style="width:33%" class="td-extra-link"><a class="main-a text-main" href="galleries.jsp?gallery={{this.id}}">View gallery</a></td>
    </tr></table>
    
    <div class="photo-listing-div">
    {{#each this.galleryRepresentation}}
        <a class="general-a" href="galleries.jsp?gallery={{../this.id}}">
            <div class="photo-image-div photo-image-div-size" style="background-image: url('getImage.jsp?filename={{this.thumbnail}}')"></div>
        </a>
    {{/each}}
    </div>
</div>
`;
