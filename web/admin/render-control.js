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

$.widget('admin.renderControl', {
    _template: `<div class="item-container transparent width-full item-one-center">
    <span class="item-container-heading">Render Control</span>
</div>

<div class="width-100vw flex-main">
    <div class="item-container transparent width-full item-left">
        <button type="button" class="btn btn-success btn-std button-center" {{#if notSuperUser}}disabled="disabled"{{/if}} data-role="render-init">
        Re-render all posts
        </button>
        
        <br />
        
        <textarea class="width-100-pc input-textarea-text-smaller" data-role="render-verbose"></textarea><br />
    </div>
</div>
`,
    _create: async function(){
    },
    _init: async function(){
        let self = this;
        await self._display(self);
    },

    _logAppend: function(text){
        let self = this;
        let textAreaVal = self.$renderVerboseTextarea.val();
        self.$renderVerboseTextarea.val(textAreaVal + text);
    },

    _display: async function(self) {
        let hTemplate = Handlebars.compile(self._template);

        let notSuperUser = Cookies.get("privilegeLevelName") !== "superuser";
        self.element.html(hTemplate({notSuperUser: notSuperUser}));

        let $renderInitButton = self.element.find('[data-role="render-init"]');
        let $renderVerboseTextarea = self.element.find('[data-role="render-verbose"]');
        self.$renderVerboseTextarea = $renderVerboseTextarea;

        $renderInitButton.unbind();
        $renderInitButton.click(async function(){

            self.$renderVerboseTextarea.val("");

            let pagingRequestFilter = {
                needArticles: true,
                needPhotos: true,
                needGalleries: true,
                requireGeo: false,
                itemsPerPage: 1000000000
            };
            let postVOList = await ajax({action: "processPagingRequestUnified", data: JSON.stringify(pagingRequestFilter) });

            for (let i = 0; i < postVOList.posts.length; i++) {
                let post = postVOList.posts[i];
                if (post.isArticle){
                    self._logAppend("article ID " + post.id + "\r\n");

                    let articleVO = await ajax({data:post.id, action:"getArticleVOByArticleIdPreprocessed", guid: Cookies.get("guid")});
                    let renderDiv = document.createElement("div");
                    renderDiv.setAttribute("display","none");
                    let $renderDiv = $(renderDiv);
                    let hArticleTemplate = Handlebars.compile(articleTemplate);
                    $renderDiv.html(hArticleTemplate({articleVO: articleVO, articleText: render(articleVO.articleText) }));
                    postRender($renderDiv);
                    let preRender = $renderDiv.html();
                    renderDiv.remove();

                    let updateRenderRequest = {
                        postId: post.id,
                        render: preRender
                    };
                    let res = await ajax({data: JSON.stringify(updateRenderRequest), action:"updateArticleRender", guid: Cookies.get("guid")});
                    if (res > 0){
                        self._logAppend("article ID " + post.id + " render updated OK (" + res + ")\r\n");
                    } else {
                        self._logAppend("article ID " + post.id + " render update FAILED\r\n");
                    }

                } else if (post.isGallery){
                    self._logAppend("gallery ID " + post.id + "\r\n");

                    let galleryVO = await ajax({ action: "getGalleryVOByGalleryId", data: post.id, guid: Cookies.get("guid") });
                    let renderDiv = document.createElement("div");
                    renderDiv.setAttribute("display","none");
                    let $renderDiv = $(renderDiv);
                    let hGalleryTemplate = Handlebars.compile(galleryTemplate);
                    $renderDiv.html(hGalleryTemplate({
                        galleryVO: galleryVO,
                        galleryDescription: basicRender(galleryVO.description)
                    }));
                    let preRender = $renderDiv.html();
                    renderDiv.remove();

                    let updateRenderRequest = {
                        postId: post.id,
                        render: preRender
                    };
                    let res = await ajax({data: JSON.stringify(updateRenderRequest), action:"updateGalleryRender", guid: Cookies.get("guid")});
                    if (res > 0){
                        self._logAppend("gallery ID " + post.id + " render updated OK (" + res + ")\r\n");
                    } else {
                        self._logAppend("gallery ID " + post.id + " render update FAILED\r\n");
                    }

                } else if (post.isPhoto) {
                    self._logAppend("photo ID " + post.id + "\r\n");

                    let photoVO = await ajax({data:post.id , action:"getPhotoVOByPhotoId", guid:Cookies.get("guid")});
                    let renderDiv = document.createElement("div");
                    renderDiv.setAttribute("display","none");
                    let $renderDiv = $(renderDiv);
                    let hPhotoTemplate = Handlebars.compile(photoTemplate);
                    $renderDiv.html(hPhotoTemplate({photoVO: photoVO, photoDescription: basicRender(photoVO.description)}));
                    let preRender = $renderDiv.html();
                    renderDiv.remove();

                    let updateRenderRequest = {
                        postId: post.id,
                        render: preRender
                    };
                    let res = await ajax({data: JSON.stringify(updateRenderRequest), action:"updatePhotoRender", guid: Cookies.get("guid")});
                    if (res > 0){
                        self._logAppend("photo ID " + post.id + " render updated OK (" + res + ")\r\n");
                    } else {
                        self._logAppend("photo ID " + post.id + " render update FAILED\r\n");
                    }
                }
            }

        });
    }
});