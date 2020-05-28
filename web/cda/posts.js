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

        if (params.has("gallery")){
            // load gallery
            return;
        } else if (params.has("article")){
            // load article
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
    }
});