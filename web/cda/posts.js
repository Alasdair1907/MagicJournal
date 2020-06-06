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
            return;
        }

        // search and listing
        let pagingRequestFilter = getPagingRequestFilterFromParams();
        let postVOList = await ajaxCda({action: "processPagingRequestUnified", data: JSON.stringify(pagingRequestFilter) });
        let hArticleListing = Handlebars.compile(postListingTemplate);

        /*
        for (let i = 0; i < postVOList.posts.length; i++){
            postVOList.posts[i].description = shrinkDescription(postVOList.posts[i].description);
        }*/

        let locationHeader = getLocationHeaderByFilter(pagingRequestFilter);
        self.element.html(hArticleListing({postVOList: postVOList.posts, totalItems: postVOList.totalItems, locationHeader: locationHeader}));

        let $pagingAnchor = self.element.find('[data-role="paging-anchor"]');
        $pagingAnchor.pager({pagesTotal: postVOList.totalPages, filter: pagingRequestFilter});
    },

    _displayArticle: async function(self, articleId){
        let articleVO = await ajaxCda({action: "getArticleVOByArticleIdPreprocessed", data: articleId});
        
        let hArticleTemplate = Handlebars.compile(articleTemplate);
        self.element.html(hArticleTemplate({articleVO: articleVO}));
        
        let $imageDivs = self.element.find('[data-type=image]');
        $imageDivs.each(function(){
            let imageID = $(this).data("id");
            let preview = $(this).data("preview");
            let image = $(this).data("image");
            let title = $(this).data("title");

            $(this).html(`<img src="getImage.jsp?filename=${preview}" class="article-image">`);
        });

        let $sidePanelDiv = self.element.find('[data-role="side-container-div"]');
        $sidePanelDiv.sidePanel({latestPostsCount: 10, postAttributionClass: POST_ATTRIBUTION_ARTICLE, postId: articleId});
    },

    _displayGallery: async function(self, galleryId){
        let galleryVO = await ajaxCda({action: "getGalleryVOByGalleryId", data: galleryId});

        let hGalleryTemplate = Handlebars.compile(galleryTemplate);
        self.element.html(hGalleryTemplate({galleryVO: galleryVO}));

    }
});