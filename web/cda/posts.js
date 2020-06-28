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

$.widget("magic.posts", {
    _init: async function () {
        await this._display(this);
    },
    _display: async function (self) {

        let params = new URLSearchParams(window.location.search);

        if (params.has("gallery")){
            // load gallery
            let galleryId = params.get("gallery");
            await self._displayGallery(self, galleryId);
            return;

        } else if (params.has("article")){

            // load article
            let articleId = params.get("article");
            await self._displayArticle(self, articleId);
            return;

        } else if (params.has("photo")){
            // load photo
            let photoId = params.get("photo");
            await self._displayPhoto(self, photoId);
            return;

        } else if (params.has("about")){
            await self._displayAbout(self);
            return;

        }

        // search and listing
        let pagingRequestFilter = getPagingRequestFilterFromParams();
        await self._list(pagingRequestFilter, self);

        if (params.has("advanced")){
            let $advancedSearchAnchor = self.element.find('[data-role="advanced-search-anchor"]');
            $advancedSearchAnchor.DynamicSearchCda({pagingRequestFilter: pagingRequestFilter});
        }

    },

    _list: async function(pagingRequestFilter, self){
        let postVOList = await ajaxCda({action: "processPagingRequestUnified", data: JSON.stringify(pagingRequestFilter) });
        let hPostListingTemplate = Handlebars.compile(postListingTemplate);
        let locationHeader = getLocationHeaderByFilter(pagingRequestFilter);

        let posts = [];
        for (let i = 0; i < postVOList.posts.length; i++){
            let post = postVOList.posts[i];
            post.description = prepareCompactDescription(post.description);
            posts.push(post);
        }

        self.element.html(hPostListingTemplate({postVOList: posts, totalItems: postVOList.totalItems, locationHeader: locationHeader}));

        let $pagingAnchor = self.element.find('[data-role="paging-anchor"]');
        $pagingAnchor.pager({pagesTotal: postVOList.totalPages, filter: pagingRequestFilter});
    },

    _displayArticle: async function(self, articleId){
        let articleVO = await ajaxCda({action: "getArticleVOByArticleIdPreprocessed", data: articleId});
        
        let hArticleTemplate = Handlebars.compile(articleTemplate);
        self.element.html(hArticleTemplate({articleVO: articleVO, articleText: render(articleVO.articleText) }));

        postRender(self.element);
        self._handleClickableImages(self);

        let $sidePanelDiv = self.element.find('[data-role="side-container-div"]');
        $sidePanelDiv.sidePanel({latestPostsCount: 10, postAttributionClass: POST_ATTRIBUTION_ARTICLE, postId: articleId});
    },

    _displayGallery: async function(self, galleryId){
        let galleryVO = await ajaxCda({action: "getGalleryVOByGalleryId", data: galleryId});

        let hGalleryTemplate = Handlebars.compile(galleryTemplate);
        self.element.html(hGalleryTemplate({galleryVO: galleryVO, galleryDescription: basicRender(galleryVO.description)}));

        self._handleClickableImages(self);

        let $sidePanelDiv = self.element.find('[data-role="side-container-div"]');
        $sidePanelDiv.sidePanel({latestPostsCount: 10, postAttributionClass: POST_ATTRIBUTION_GALLERY, postId: galleryId});
    },

    _displayPhoto: async function(self, photoId) {
        let photoVO = await ajaxCda({action: "getPhotoVOByPhotoId", data: photoId});

        let hPhotoTemplate = Handlebars.compile(photoTemplate);
        self.element.html(hPhotoTemplate({photoVO: photoVO, photoDescription: basicRender(photoVO.description)}));

        self._handleClickableImages(self);

        let $sidePanelDiv = self.element.find('[data-role="side-container-div"]');
        $sidePanelDiv.sidePanel({latestPostsCount: 10, postAttributionClass: POST_ATTRIBUTION_PHOTO, postId: photoId});
    },

    _displayAbout: async function(self){
        let aboutPreprocessed = await ajaxCda({action: "getAboutPreprocessed"});

        let hAboutTemplate = Handlebars.compile(aboutTemplate);
        self.element.html(hAboutTemplate({aboutText: render(aboutPreprocessed)}));
        postRender(self.element);

        self._handleClickableImages(self);

        let $sidePanelDiv = self.element.find('[data-role="side-container-div"]');
        $sidePanelDiv.sidePanel({latestPostsCount: 15, postAttributionClass: null, postId: null});

    },

    _handleClickableImages: function(self){
        let clickableImages = self.element.find('[data-role="inline-image"]');

        clickableImages.unbind();
        clickableImages.click(function(){
            let imageSrc = $(this).data("image");
            let title = $(this).data("title");

            let hFullScreenImageOverlay = Handlebars.compile(fullScreenImageOverlay);
            let overlayHtml = hFullScreenImageOverlay({imageSrc: imageSrc, title: title});

            let $newElem = $(overlayHtml);
            $newElem.appendTo("body");
            $("body").css("overflow", "hidden");

            let $imageShowClose = $newElem.find('[data-role="image-show-close"]');
            $imageShowClose.click(function(){
                $newElem.remove();
                $("body").css("overflow","auto");
            });
        });
    },

    _advancedSearch: function(self){

    }

});