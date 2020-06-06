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
        <div class="text-main post-main">
            {{{articleVO.articleText}}}
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
<span class="text-main">ARTICLE</span><br />
<span class="text-main">{{this.title}}</span><br />
`;

let sidePanelPhotoTemplate = `
<span class="text-main">PHOTO</span><br />
<span class="text-main">{{this.title}}</span><br />
`;

let sidePanelGalleryTemplate = `
<span class="text-main">GALLERY</span><br />
<span class="text-main">{{this.title}}</span><br />
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

