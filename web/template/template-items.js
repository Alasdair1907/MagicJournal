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
    <a href="posts.jsp?article={{this.id}}" class="general-a"><div class="item-image-div item-image-div-size anything-link" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')" ></div></a>
    <a href="posts.jsp?article={{this.id}}" class="general-a"><span class="item-heading anything-link">{{this.title}}</span></a>
    <div class="item-tags-subheading-container">
        {{#each this.tagEntityList}}
        <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="article-tag">#{{this.tag}}</span></a>&nbsp;
        {{/each}}
    </div>
    <span class="text-main item-text">{{this.description}}</span>
    <span class="afterfloat">&nbsp;</span>
    <div class="post-info-line">
        <div><span class="text-main">Author: </span><a class="main-a text-main" href="author.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a></div>
        <span class="text-main">Date posted: {{this.creationDateStr}}</span>
        <a class="main-a text-main" href="posts.jsp?article={{this.id}}">Read more</a>
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
        
        <a href="search.jsp?tag={{this.tag}}" class="general-a"><span class="article-tag">#{{this.tag}}</span></a>&nbsp;
       
        {{/each}}
    </div>
    <span class="text-main item-text compact-item-text">{{this.description}}</span>
    <div class="post-info-line">
        <a class="main-a text-main" href="search.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a>
        <span class="text-main">{{this.creationDateStr}}</span>
    </div>
    <div class="post-info-line">
        <a class="main-a text-main" href="posts.jsp?article={{this.id}}">READ ARTICLE</a>
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
        <a class="main-a text-main" href="author.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a>
        <span class="text-main">{{this.creationDateStr}}</span>
    </div>
    <div class="post-info-line">
        <a class="main-a text-main" href="posts.jsp?article={{this.id}}">VIEW PHOTO</a>
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
        <a class="main-a text-main" href="author.jsp?author={{this.authorVO.login}}">{{this.authorVO.displayName}}</a>
        <span class="text-main">{{this.creationDateStr}}</span>
    </div>
    <div class="post-info-line">
        <a class="main-a text-main" href="posts.jsp?article={{this.id}}">VIEW GALLERY</a>
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
    <td style="width:33%" class="td-extra-link"><a class="main-a text-main" href="posts.jsp?photos=true">View all photos</a></td>
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
<div class="container-primary container-primary-element">
    <table width="100%;"><tr>
    <td style="width:33%">&nbsp;</td>
    <td style="width:33%;text-align: center"><span class="item-container-heading">{{this.title}}</span></td>
    <td style="width:33%" class="td-extra-link"><a class="main-a text-main" href="posts.jsp?gallery={{this.id}}">View gallery</a></td>
    </tr></table>
    
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