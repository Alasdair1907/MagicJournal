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

let homepagePhonePanelArticleTemplate = `
<a class="general-a" href="posts.jsp?article={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="article-tag side-panel-tag">article</span><span class="mobile-hide"><br /></span>
    <div class="side-panel-image-div side-panel-image-div-size hlat-mobile-panel-image-override" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')"></div>
    <div class="hlat-phone-panel-texts">
        <span class="text-main side-panel-title">{{this.title}}</span><span class="mobile-hide"><br /></span>
        <span class="hlat-panel-date-text">{{this.creationDateStr}}</span>
    </div>
</div>
</a>
`;

let homepagePhonePanelPhotostoryTemplate = `
<a class="general-a" href="posts.jsp?collage={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="collage-tag side-panel-tag">collage</span><span class="mobile-hide"><br /></span>
    <div class="side-panel-image-div side-panel-image-div-size hlat-mobile-panel-image-override" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')"></div>
    <div class="hlat-phone-panel-texts">
        <span class="text-main side-panel-title">{{this.title}}</span><span class="mobile-hide"><br /></span>
        <span class="hlat-panel-date-text">{{this.creationDateStr}}</span>
    </div>
</div>
</a>
`;


let homepagePhonePanelPhotoTemplate = `
<a class="general-a" href="posts.jsp?photo={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="photo-tag side-panel-tag">photo</span><span class="mobile-hide"><br /></span>
    <div class="side-panel-image-div side-panel-image-div-size hlat-mobile-panel-image-override" style="background-image: url('getImage.jsp?filename={{this.imageVO.thumbnail}}')"></div>
    <div class="hlat-phone-panel-texts">
        <span class="text-main side-panel-title">{{this.title}}</span><span class="mobile-hide"><br /></span>
        <span class="hlat-panel-date-text">{{this.creationDateStr}}</span>
    </div>
</div>
</a>
`;
let homepagePhonePanelGalleryTemplate = `
<a class="general-a" href="posts.jsp?gallery={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="gallery-tag side-panel-tag">gallery</span><span class="mobile-hide"><br /></span>
    <div class="side-panel-image-div side-panel-image-div-size hlat-mobile-panel-image-override" style="background-image: url('getImage.jsp?filename={{this.galleryRepresentation.[0].thumbnail}}')"></div>
    <div class="hlat-phone-panel-texts">
        <span class="text-main side-panel-title">{{this.title}}</span><span class="mobile-hide"><br /></span>
        <span class="hlat-panel-date-text">{{this.creationDateStr}}</span>
    </div>
</div>
</a>
`;

let homepagePhonePanelClassSwitch = `
{{#if this.isArticle}}
${homepagePhonePanelArticleTemplate}
{{/if}}

{{#if this.isPhoto}}
${homepagePhonePanelPhotoTemplate}
{{/if}}

{{#if this.isGallery}}
${homepagePhonePanelGalleryTemplate}
{{/if}}

{{#if this.isPhotostory}}
${homepagePhonePanelPhotostoryTemplate}
{{/if}}
`;



let sidePanelArticleTemplate = `
<a class="general-a" href="posts.jsp?article={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="article-tag side-panel-tag">article</span><span class="mobile-hide"><br /></span>
    <div class="side-panel-image-div side-panel-image-div-size" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')"></div>
    <span class="text-main side-panel-title">{{this.title}}</span><span class="mobile-hide"><br /></span>
</div>
</a>
`;

let sidePanelPhotostoryTemplate = `
<a class="general-a" href="posts.jsp?collage={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="collage-tag side-panel-tag">collage</span><span class="mobile-hide"><br /></span>
    <div class="side-panel-image-div side-panel-image-div-size" style="background-image: url('getImage.jsp?filename={{this.titleImageVO.thumbnail}}')"></div>
    <span class="text-main side-panel-title">{{this.title}}</span><span class="mobile-hide"><br /></span>
</div>
</a>
`;

let sidePanelPhotoTemplate = `
<a class="general-a" href="posts.jsp?photo={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="photo-tag side-panel-tag">photo</span><span class="mobile-hide"><br /></span>
    <div class="side-panel-image-div side-panel-image-div-size" style="background-image: url('getImage.jsp?filename={{this.imageVO.thumbnail}}')"></div>
    <span class="text-main side-panel-title">{{this.title}}</span><span class="mobile-hide"><br /></span>
</div>
</a>
`;
let sidePanelGalleryTemplate = `
<a class="general-a" href="posts.jsp?gallery={{this.id}}">
<div class="side-panel-container side-panel-container-size">
    <span class="gallery-tag side-panel-tag">gallery</span><span class="mobile-hide"><br /></span>
    <div class="side-panel-image-div side-panel-image-div-size" style="background-image: url('getImage.jsp?filename={{this.galleryRepresentation.[0].thumbnail}}')"></div>
    <span class="text-main side-panel-title">{{this.title}}</span><span class="mobile-hide"><br /></span>
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

{{#if this.isPhotostory}}
${sidePanelPhotostoryTemplate}
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

{{#if displayLatest}}
    <span class="item-heading">Latest posts:</span>
    {{#if latest}}
        {{#each latest}}
        ${sidePanelClassSwitch}
        {{/each}}
    {{/if}}
{{/if}}
`;

let bottomPanelBase = `

{{#if hasRelated}}
<span class="item-heading">Related posts</span>
<div class="side-panel-item-wrappers-container">
    {{#if associated}}
    {{#each associated}}
        <div class="side-panel-item-wrapper">
            ${sidePanelClassSwitch}
        </div>
    {{/each}}
    {{/if}}
    
    {{#if related}}
    {{#each related}}
        <div class="side-panel-item-wrapper">
            ${sidePanelClassSwitch}
        </div>
    {{/each}}
    {{/if}}
</div>
{{/if}}

{{#if latest}}
<span class="item-heading">Latest posts</span>
<div class="side-panel-item-wrappers-container">

    {{#each latest}}
    <div class="side-panel-item-wrapper">
    ${sidePanelClassSwitch}
    </div>
    {{/each}}
{{/if}}

</div>

`;

let articleTemplate = `<!-- ArticleVO  articleVO, render - rendered articleVO.articleText -->

<div class="post-content-and-panel-container">
    <div class="container-primary-article container-primary-element post-container-article">
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

    <!--<div data-role="side-container-div" class="container-primary container-primary-element side-container"></div>-->
</div>

<div data-role="base-panel-container" class="container-primary-article container-primary-element post-container-article"></div>


`;

let galleryTemplateRelatedPostsBlock = `
{{#if hasRelated}}
<span class="item-heading item-heading-related-gallery">Related posts</span>
<div class="side-panel-item-wrappers-container">
    {{#each associatedPostVOs}}
        <div class="side-panel-item-wrapper">
            ${sidePanelClassSwitch}
        </div>
    {{/each}}
    {{#each relatedPostVOs}}
        <div class="side-panel-item-wrapper">
            ${sidePanelClassSwitch}
        </div>
    {{/each}}
</div>
{{/if}}
`;


let photostoryTemplate = `

<div class="post-content-and-panel-container">
    <div class="post-container-gallery">
        <div class="container-primary-article container-primary-element container-photostory-post-heading">
            <span class="item-heading">{{photostoryVO.title}}</span>

            <div class="post-render-info-line">
                Author: <a class="main-a" href="author.jsp?author={{photostoryVO.authorVO.login}}">{{photostoryVO.authorVO.displayName}}</a>,
                date posted: {{photostoryVO.creationDateStr}}</span>
            </div>

            <div class="item-tags-subheading-container">
                {{#each photostoryVO.tagEntityList}}
                <a href="posts.jsp?tags={{this.tag}}" class="general-a"><span class="collage-tag">#{{this.tag}}</span></a>&nbsp;
                {{/each}}
            </div>            
        </div>
        
        <div class="photostory-items-container">
        {{#each PSItems}}
            {{#if this.isTitle}}
            <div class="photostory-title-container">
                <div class="container-primary-article container-primary-element photostory-title-subcontainer">
                    <span class="photostory-title-span">{{this.titleText}}</span>
                </div>
            </div>
            {{/if}}

            {{#if this.isImage}}
                <div class="container-primary-article photostory-universal-container-div">
                    <img alt="{{this.caption}}" 
                        class="photostory-image-img" 
                        src="getImage.jsp?filename={{this.previewFile}}" 
                        data-role="inline-image" 
                        data-image="{{this.mainFile}}" 
                        data-title=""
                        data-index="{{this.index}}" data-boundary="{{#if this.first}}first{{/if}}{{#if this.last}}last{{/if}}"
                         />
                    <span class="text-main photostory-image-caption">{{this.caption}}</span>
                </div>
            {{/if}}
            
            {{#if this.isText}}
                <div class="container-primary-article photostory-universal-container-div">
                    <span class="text-main photostory-text">{{{this.text}}}</span>
                </div>
            {{/if}}
        
        {{/each}}
        </div>
        
    </div>
    
    
    
</div>

<div data-role="base-panel-container" class="post-container-gallery container-primary-article container-primary-element"></div>

`;

let galleryTemplate = `

<div class="post-content-and-panel-container">
    <div class="post-container-gallery">
        <div class="container-primary-article container-primary-element">
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
            
            <div data-role="gallery-associated-posts"></div>
            
        </div>

        <div class="container-primary container-primary-element">

            <div class="jg">
                {{#each galleryVO.imageVOList}}
                <span style="--r: {{this.aspectRatio}}">
                    <img class="gallery-image-breadcrumb" data-image="{{this.image}}" data-index="{{@index}}" data-boundary="{{#if @first}}first{{/if}}{{#if @last}}last{{/if}}" data-title="{{this.title}}" data-role="inline-image" src="getImage.jsp?filename={{this.preview}}" alt="{{this.title}}">
                </span>
                {{/each}}
            </div>
        </div>
    </div>
</div>

<div data-role="base-panel-container" class="post-container-gallery container-primary-article container-primary-element"></div>

`;

let photoTemplate = `


<div class="post-content-and-panel-container">

    <div class="post-container">

        <div class="container-primary-article container-primary-element">
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
            <img class="photo-image" alt="{{photoVO.title}}" src="getImage.jsp?filename={{photoVO.imageVO.image}}" data-image="{{photoVO.imageVO.image}}" data-title="" data-role="inline-image">
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

let fullScreenImageOverlay = `
<div class="image-show-base-overlay">
    <div class="image-show-container">
        <div class="width-100-pc">
            <div data-role="image-overlay-img"></div>
            <i class="fas fa-times image-show-close" data-role="image-show-close"></i>
            {{#if isGallery}}
                <i class="fas fa-arrow-left image-show-previous" data-role="gallery-previous"></i>
                <i class="fas fa-arrow-right image-show-next" data-role="gallery-next"></i>
            {{/if}}
        </div>
    </div>
</div>
`;

let imageOverlayImg = `
<img src="getImage.jsp?filename={{imageSrc}}" alt="{{title}}" class="image-show-image {{#if title}}image-show-image-with-descr{{else}}image-show-image-no-descr{{/if}}">
{{#if title}}<div class="image-show-title text-main">{{title}}</div>{{/if}}
`;