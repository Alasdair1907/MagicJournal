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
            limit: 10
        };
        let basicPostFilterTOPhotos = {
            limit: 16
        };
        let basicPostFilterTOGalleries = {
            limit: 6,
            galleryRepresentationImages: 8
        };
        let latestPostsFilter = {
            needArticles: true,
            needPhotos: true,
            needGalleries: true,
            itemsPerPage: 4
        };

        let basicPostFilterTOs = [basicPostFilterTOArticles, basicPostFilterTOPhotos, basicPostFilterTOGalleries]
        let postVOListWithTagListMenu = await ajaxCda({data: JSON.stringify(basicPostFilterTOs), action: "listHomepage"});
        let latestPostVOList = await ajaxCda({data: JSON.stringify(latestPostsFilter), action: "processPagingRequestUnified"});


        let articleVOList = postVOListWithTagListMenu[0];
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
                latestPostImage = latestPostVO.titleImageVO.preview;
                latestPostTagClass = "article-tag";
                latestPostLinkClass = "article";
                latestPostPreTitle = "";
            } else if (latestPostVO.hasOwnProperty("isPhoto")){
                latestPostImage = latestPostVO.imageVO.preview;
                latestPostTagClass = "photo-tag";
                latestPostLinkClass = "photo";
                latestPostPreTitle = "Photo: ";
            } else if (latestPostVO.hasOwnProperty("isGallery")){
                latestPostImage = latestPostVO.galleryRepresentation[0].preview;
                latestPostTagClass = "gallery-tag";
                latestPostLinkClass = "gallery";
                latestPostPreTitle = "Gallery: ";
            }
            hasLatestPosts = true;
        }

        let hHomepageListing = Handlebars.compile(homepageListing);
        self.element.html(hHomepageListing({
            articleVOList: articleVOList,
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