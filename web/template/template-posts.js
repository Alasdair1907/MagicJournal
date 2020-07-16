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
            <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="article-tag">#{{this.tag}}</span></a>&nbsp;
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

    <div class="post-container">

        <div class="container-primary container-primary-element">
            <span class="item-heading">{{galleryVO.title}}</span>

            <div class="post-render-info-line">
                Author: <a class="main-a" href="author.jsp?author={{galleryVO.authorVO.login}}">{{galleryVO.authorVO.displayName}}</a>,
                date posted: {{galleryVO.creationDateStr}}</span>
            </div>

            <div class="item-tags-subheading-container">
                {{#each galleryVO.tagEntityList}}
                <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="gallery-tag">#{{this.tag}}</span></a>&nbsp;
                {{/each}}
            </div>

            <div class="text-main post-main">
                {{{galleryDescription}}}
            </div>
        </div>

        <div class="container-primary container-primary-element">

            <div class="jg">
                {{#each galleryVO.imageVOList}}
                <span style="--r: {{this.aspectRatio}}">
                    <img class="gallery-image-breadcrumb" data-image="{{this.image}}" data-title="{{this.title}}" data-role="inline-image" src="getImage.jsp?filename={{this.preview}}" alt="{{this.title}}">
                </span>
                {{/each}}
            </div>
        </div>
    </div>

    <div data-role="side-container-div" class="container-primary container-primary-element side-container"></div>
</div>`;

let photoTemplate = `
<div class="post-content-and-panel-container">

    <div class="post-container">

        <div class="container-primary container-primary-element">
            <span class="item-heading">{{photoVO.title}}</span>

            <div class="post-render-info-line">
                Author: <a class="main-a" href="author.jsp?author={{photoVO.authorVO.login}}">{{photoVO.authorVO.displayName}}</a>,
                date posted: {{photoVO.creationDateStr}}</span>
            </div>

            <div class="item-tags-subheading-container">
                {{#each photoVO.tagEntityList}}
                <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="photo-tag">#{{this.tag}}</span></a>&nbsp;
                {{/each}}
            </div>

            <div class="text-main post-main">
                {{{photoDescription}}}
            </div>
        </div>
        
        <div class="container-primary container-primary-element">
            <img class="photo-image" alt="{{photoVO.title}}" src="getImage.jsp?filename={{photoVO.imageVO.preview}}" data-image="{{photoVO.imageVO.image}}" data-title="" data-role="inline-image">
        </div>
    </div>
    <div data-role="side-container-div" class="container-primary container-primary-element side-container"></div>
</div>
`;

let aboutTemplate = `
<div class="post-content-and-panel-container">
    <div class="container-primary container-primary-element post-container">
        <span class="item-heading">About</span>

        <div class="text-main post-main">
            {{{aboutText}}}
        </div>
    </div>

    <div data-role="side-container-div" class="container-primary container-primary-element side-container"></div>
</div>
`;

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
        <div class="width-100-pc">
            <img src="getImage.jsp?filename={{imageSrc}}" alt="{{title}}" class="image-show-image {{#if title}}image-show-image-with-descr{{else}}image-show-image-no-descr{{/if}}">
            {{#if title}}<div class="image-show-title text-main">{{title}}</div>{{/if}}
            <i class="fas fa-times image-show-close" data-role="image-show-close"></i>
        </div>
    </div>
</div>
`;
