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

            let galleryId = params.get("gallery");
            await self._displayGallery(self, galleryId);
            return;

        } else if (params.has("article")){

            let articleId = params.get("article");
            await self._displayArticle(self, articleId);
            return;

        } else if (params.has("photo")) {

            let photoId = params.get("photo");
            await self._displayPhoto(self, photoId);
            return;

        } else if (params.has("collage")) {

            let collageId = params.get("collage");
            await self._displayCollage(self, collageId);
            return;


        } else if (params.has("about")){
            await self._displayAbout(self);
            return;

        }

        // search and listing
        let pagingRequestFilter = getPagingRequestFilterFromParams();
        await self._list(pagingRequestFilter, self, params.has("advanced"));

        if (params.has("advanced")){
            let $advancedSearchAnchor = self.element.find('[data-role="advanced-search-anchor"]');
            $advancedSearchAnchor.DynamicSearchCda({pagingRequestFilter: pagingRequestFilter, callback: function(pagingRequestFilter){
                    window.location = getParamsStrFromPagingRequestFilter("posts.jsp", pagingRequestFilter, true);
            }});
        }

    },

    _list: async function(pagingRequestFilter, self, advanced){
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
        $pagingAnchor.pager({pagesTotal: postVOList.totalPages, filter: pagingRequestFilter, advanced: advanced});

        let settingsTO = await getSettingsTO();
        document.title = settingsTO.websiteName + (locationHeader ? (" :: " + locationHeader) : "");
    },

    _displayArticle: async function(self, articleId){
        self._handleClickableImages(self);

        let $basePanelContainer = self.element.find('[data-role="base-panel-container"]');
        let sidePanelData = await getSidepanelData(10, POST_ATTRIBUTION_ARTICLE, articleId);
        $basePanelContainer.sidePanel({sidePanelPostsTO: sidePanelData, bottomPanel: true});
    },

    _displayGallery: async function(self, galleryId){

        let sidePanelData = await getSidepanelData(10, POST_ATTRIBUTION_GALLERY, galleryId);
        let associatedPostVOs = sidePanelData.associated;
        let relatedPostVOs = sidePanelData.related;
        let relatedCount = (!!associatedPostVOs ? associatedPostVOs.length : 0) + (!!relatedPostVOs ? relatedPostVOs.length : 0);

        let $galleryRelatedBlock = self.element.find('div[data-role="gallery-associated-posts"]');
        let hGalleryRelatedBlockTemplate = Handlebars.compile(galleryTemplateRelatedPostsBlock);
        $galleryRelatedBlock.html(hGalleryRelatedBlockTemplate({
            associatedPostVOs: associatedPostVOs,
            relatedPostVOs: relatedPostVOs,
            hasRelated: (relatedCount > 0)
            }));


        let $basePanelContainer = self.element.find('[data-role="base-panel-container"]');
        $basePanelContainer.sidePanel({sidePanelPostsTO: sidePanelData, bottomPanel: true});

        self._handleClickableImages(self, true);
    },

    _displayPhoto: async function(self, photoId) {
        self._handleClickableImages(self);

        let $sidePanelDiv = self.element.find('[data-role="side-container-div"]');
        let sidePanelData = await getSidepanelData(10, POST_ATTRIBUTION_PHOTO, photoId);
        $sidePanelDiv.sidePanel({sidePanelPostsTO: sidePanelData});
    },

    _displayCollage: async function(self, collageId) {

        let $basePanelContainer = self.element.find('[data-role="base-panel-container"]');
        let sidePanelData = await getSidepanelData(10, POST_ATTRIBUTION_PHOTOSTORY, collageId);
        $basePanelContainer.sidePanel({sidePanelPostsTO: sidePanelData, bottomPanel: true});

        self._handleClickableImages(self, true);
    },

    _displayAbout: async function(self){
        let aboutPreprocessed = await ajaxCda({action: "getAboutPreprocessed"});

        let hAboutTemplate = Handlebars.compile(aboutTemplate);
        self.element.html(hAboutTemplate({aboutText: render(aboutPreprocessed)}));
        postRender(self.element);

        self._handleClickableImages(self);

        let $sidePanelDiv = self.element.find('[data-role="side-container-div"]');
        let sidePanelData = await getSidepanelData(15, null, null);
        $sidePanelDiv.sidePanel({sidePanelPostsTO: sidePanelData});

        let settingsTO = await getSettingsTO();
        document.title = "About " + settingsTO.websiteName;
    },

    _handleClickableImages: function(self, isGallery=false){
        let clickableImages = self.element.find('[data-role="inline-image"]');

        clickableImages.unbind();
        clickableImages.click(function(){
            let imageSrc = $(this).data("image");
            let title = $(this).data("title");
            let index = $(this).data("index");
            let dataBoundary = $(this).data("boundary");

            let hFullScreenImageOverlay = Handlebars.compile(fullScreenImageOverlay);
            let hImageOverlayImg = Handlebars.compile(imageOverlayImg);

            let imgHtml = hImageOverlayImg({imageSrc: imageSrc, title: title});
            let overlayHtml = hFullScreenImageOverlay({isGallery: isGallery});

            let $newElem = $(overlayHtml);
            let $imgElem = $newElem.find('[data-role="image-overlay-img"]');
            $imgElem.html(imgHtml);

            $newElem.appendTo("body");
            $("body").css("overflow", "hidden");

            let $imageShowClose = $newElem.find('[data-role="image-show-close"]');
            let $imageShowPrevious = $newElem.find('[data-role="gallery-previous"]');
            let $imageShowNext = $newElem.find('[data-role="gallery-next"]');

            $imageShowClose.click(function(){
                $newElem.remove();
                $("body").css("overflow","auto");
            });

            $imageShowPrevious.click(function(){
                let $previousImage = null;
                if (dataBoundary == "first"){
                    $previousImage = self.element.find('[data-role="inline-image"][data-boundary="last"]');
                } else {
                    let prevIndex = index-1;
                    $previousImage = self.element.find('[data-role="inline-image"][data-index="'+prevIndex+'"]');
                }

                imageSrc = $previousImage.data("image");
                title = $previousImage.data("title");
                index = $previousImage.data("index");
                dataBoundary = $previousImage.data("boundary");

                imgHtml = hImageOverlayImg({imageSrc: imageSrc, title: title});
                $imgElem.html(imgHtml);
            });

            $imageShowNext.click(function(){
                let $nextImage = null;
                if (dataBoundary == "last"){
                    $nextImage = self.element.find('[data-role="inline-image"][data-boundary="first"]');
                } else {
                    let nextIndex = index+1;
                    $nextImage = self.element.find('[data-role="inline-image"][data-index="'+nextIndex+'"]');
                }

                imageSrc = $nextImage.data("image");
                title = $nextImage.data("title");
                index = $nextImage.data("index");
                dataBoundary = $nextImage.data("boundary");

                imgHtml = hImageOverlayImg({imageSrc: imageSrc, title: title});
                $imgElem.html(imgHtml);
            });

        });
    },


    _advancedSearch: function(self){

    }

});