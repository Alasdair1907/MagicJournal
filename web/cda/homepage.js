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

$.widget("magic.homepage", {
    _init: async function(){
        console.log("Initializing homepage...");
        await this._display(this);
    },
    _display: async function(self){

        console.log("homepage display()");
        let basicPostFilterTOArticles = {
            limit: 0
        };
        let basicPostFilterTOPhotos = {
            limit: 16
        };
        let basicPostFilterTOGalleries = {
            limit: 8,
            galleryRepresentationImages: 8
        };
        let latestPostsFilter = {
            needArticles: true,
            needPhotos: true,
            needGalleries: true,
            needPhotostories: true,
            itemsPerPage: 5
        };

        let latestArticlesAndPhotostories = {
            needArticles: true,
            needPhotostories: true,
            itemsPerPage: 12
        };


        let basicPostFilterTOs = [basicPostFilterTOArticles, basicPostFilterTOPhotos, basicPostFilterTOGalleries]
        let postVOListWithTagListMenu = await ajaxCda({data: JSON.stringify(basicPostFilterTOs), action: "listHomepage"});
        let latestPostVOList = await ajaxCda({data: JSON.stringify(latestPostsFilter), action: "processPagingRequestUnified"});
        let latestArticlesAndPhotostoriesVOList = await ajaxCda({data: JSON.stringify(latestArticlesAndPhotostories), action: "processPagingRequestUnified"});

        //let articleVOList = postVOListWithTagListMenu[0];
        let photoVOList = postVOListWithTagListMenu[1];
        let galleryVOList = postVOListWithTagListMenu[2];
        let tagListMenu = postVOListWithTagListMenu[3];

        let latestPostVO = null;
        let furtherLatestPostVOs = [];
        let hasLatestPosts = false;

        let latestPostTagClass = null;
        let latestPostImage = null;
        let latestPostLinkClass = null;
        let latestPostPreTitle = null;

        // if there's less than 4 posts in the system, we don't display the Latest panel
        if (latestPostVOList && latestPostVOList.posts && latestPostVOList.posts.length >= 4) {

            for (let i = 0; i < latestPostVOList.posts.length; i++){
                let post = latestPostVOList.posts[i];
                post.description = prepareCompactDescription(post.description);
            }

            latestPostVO = latestPostVOList.posts[0];
            furtherLatestPostVOs = latestPostVOList.posts.slice(1);

            if (latestPostVO.hasOwnProperty("isArticle")){
                if (latestPostVO.titleImageVO) {
                    latestPostImage = latestPostVO.titleImageVO.thumbnail;
                }
                latestPostTagClass = "article-tag";
                latestPostLinkClass = "article";
                latestPostPreTitle = "";
            } else if (latestPostVO.hasOwnProperty("isPhoto")){
                if (latestPostVO.imageVO) {
                    latestPostImage = latestPostVO.imageVO.thumbnail;
                }
                latestPostTagClass = "photo-tag";
                latestPostLinkClass = "photo";
                latestPostPreTitle = "Photo: ";
            } else if (latestPostVO.hasOwnProperty("isGallery")){
                if (latestPostVO.galleryRepresentation && latestPostVO.galleryRepresentation.length > 0) {
                    latestPostImage = latestPostVO.galleryRepresentation[0].thumbnail;
                }
                latestPostTagClass = "gallery-tag";
                latestPostLinkClass = "gallery";
                latestPostPreTitle = "Gallery: ";
            } else if (latestPostVO.hasOwnProperty("isPhotostory")){
                if (latestPostVO.titleImageVO) {
                    latestPostImage = latestPostVO.titleImageVO.thumbnail;
                }
                latestPostTagClass = "collage-tag";
                latestPostLinkClass = "collage";
                latestPostPreTitle = "";
            }
            hasLatestPosts = true;
        }

        let hHomepageListing = Handlebars.compile(homepageListing);
        self.element.html(hHomepageListing({
            articlePhotostoryVOList: latestArticlesAndPhotostoriesVOList.posts,
            tagDigestVOList: tagListMenu,
            photoVOList: photoVOList,
            galleryVOList: galleryVOList,
            latestPostVO: latestPostVO,
            furtherLatestPostVOs: furtherLatestPostVOs,
            hasLatestPosts: hasLatestPosts,
            latestPostImage: latestPostImage,
            latestPostTagClass: latestPostTagClass,
            latestPostLinkClass: latestPostLinkClass,
            latestPostPreTitle: latestPostPreTitle
        }));

    }
});