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

        let basicPostFilterTOs = [basicPostFilterTOArticles, basicPostFilterTOPhotos, basicPostFilterTOGalleries]
        let postVOListWithTagListMenu = await ajaxCda({data: JSON.stringify(basicPostFilterTOs), action: "listHomepage"});

        let articleVOList = postVOListWithTagListMenu[0];
        let photoVOList = postVOListWithTagListMenu[1];
        let galleryVOList = postVOListWithTagListMenu[2];
        let tagListMenu = postVOListWithTagListMenu[3];

        let hHomepageListing = Handlebars.compile(homepageListing);
        self.element.html(hHomepageListing({articleVOList: articleVOList, tagDigestVOList: tagListMenu, photoVOList: photoVOList, galleryVOList: galleryVOList}));

    }
});