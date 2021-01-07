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
        await this._display(this);
    },
    _display: async function(self){

        let basicPostFilterTOArticles = {
            limit: 9
        };
        let basicPostFilterTOPhotos = {
            limit: 8
        };
        let basicPostFilterTOGalleries = {
            limit: 4,
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