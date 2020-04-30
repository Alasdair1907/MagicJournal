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
            limit: 5
        };

        // TODO load simultaneously
        // TODO add spinner

        // TODO add limits

        let articleVOList = await ajaxCda({data: JSON.stringify(basicPostFilterTOArticles), action: "listAllArticleVOs"});
        if (articleVOList === undefined){
            // TODO something's wrong - need a handler for this
        }

        let tagListMenu = await ajaxCda({action: "getTagDigestVOList"});
        if (tagListMenu === undefined){

        }

        let photoVOList = await ajaxCda({action: "listAllPhotoVOs"});
        if (photoVOList === undefined){

        }

        let hHomepageListing = Handlebars.compile(homepageListing);
        self.element.html(hHomepageListing({articleVOList: articleVOList, tagDigestVOList: tagListMenu, photoVOList: photoVOList}));

    }
});