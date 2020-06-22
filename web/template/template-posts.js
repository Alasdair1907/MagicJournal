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

let articleTemplate = `<!-- ArticleVO  articleVO, render - rendered articleVO.articleText -->

<div class="post-content-and-panel-container">
    <div class="container-primary container-primary-element post-container">
        <span class="item-heading">{{articleVO.title}}</span>

        <div class="post-render-info-line">
            Author: <a class="main-a" href="author.jsp?author={{articleVO.authorVO.login}}">{{articleVO.authorVO.displayName}}</a>, 
            date posted: {{articleVO.creationDateStr}}</span>
        </div>

        <div class="item-tags-subheading-container">
            {{#each articleVO.tagEntityList}}
            <a href="posts.jsp?tag={{this.tag}}" class="general-a"><span class="article-tag">#{{this.tag}}</span></a>&nbsp;
            {{/each}}
        </div>

        <div class="text-main post-main">
            {{{articleText}}}
        </div>
    </div>

    <div data-role="side-container-div" class="container-primary container-primary-element side-container"></div>
</div>
`;


let galleryTemplate = `
<div class="post-content-and-panel-container">
    <div class="container-primary container-primary-element post-container">

        <div class="jg">
            {{#each galleryVO.imageVOList}}
            <a href="#" style="--r: {{this.aspectRatio}}">
                <img src="getImage.jsp?filename={{this.preview}}" alt="{{this.title}}">
            </a>
            {{/each}}
        </div>
    </div>

    <div data-role="side-container-div" class="container-primary container-primary-element side-container"></div>
</div>`;

let sidePanelArticleTemplate = `
<a class="general-a" href="posts.jsp?article={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="article-tag side-panel-tag">article</span><br />
    <div class="side-panel-image-div side-panel-image-div-size" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')"></div>
    <span class="text-main side-panel-title">{{this.title}}</span><br />
</div>
</a>
`;

let sidePanelPhotoTemplate = `
<a class="general-a" href="posts.jsp?photo={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="photo-tag side-panel-tag">photo</span><br />
    <div class="side-panel-image-div side-panel-image-div-size" style="background-image: url('getImage.jsp?filename={{this.imageVO.thumbnail}}')"></div>
    <span class="text-main side-panel-title">{{this.title}}</span><br />
</div>
</a>
`;

let sidePanelGalleryTemplate = `
<a class="general-a" href="posts.jsp?gallery={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="gallery-tag side-panel-tag">gallery</span><br />
    <div class="side-panel-image-div side-panel-image-div-size" style="background-image: url('getImage.jsp?filename={{this.galleryRepresentation.[0].thumbnail}}')"></div>
    <span class="text-main side-panel-title">{{this.title}}</span><br />
</div>
</a>
`;

let sidePanelClassSwitch = `
{{#if this.isArticle}}
${sidePanelArticleTemplate}
{{/if}}

{{#if this.isPhoto}}
${sidePanelPhotoTemplate}
{{/if}}

{{#if this.isGallery}}
${sidePanelGalleryTemplate}
{{/if}}
`;

//
let sidePanelBase = `
{{#if associated}}
<span class="item-heading">Associated posts:</span>
    {{#each associated}}
    ${sidePanelClassSwitch}
    {{/each}}
{{/if}}

{{#if related}}
<span class="item-heading">Related posts:</span>
    {{#each related}}
    ${sidePanelClassSwitch}
    {{/each}}
{{/if}}

<span class="item-heading">Latest posts:</span>
{{#if latest}}
    {{#each latest}}
    ${sidePanelClassSwitch}
    {{/each}}
{{/if}}
`;

let fullScreenImageOverlay = `
<div class="image-show-base-overlay">
    <div class="image-show-container">
        <div>
            <img src="getImage.jsp?filename={{imageSrc}}" class="image-show-image">
            <div class="image-show-title text-main">{{title}}</div>
            <i class="fas fa-times image-show-close" data-role="image-show-close"></i>
        </div>
    </div>
</div>
`;
