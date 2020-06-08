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

$.widget("magic.sidePanel", {

    options: {
        latestPostsCount: 15,
        postAttributionClass: POST_ATTRIBUTION_ARTICLE,
        postId: 0
    },

    _init: async function(){
        let ops = this.options;
        let self = this;

        let sidePanelRequestTO = {
            postAttribution: ops.postAttributionClass,
            postId: ops.postId,
            limitLatest: ops.latestPostsCount
        };

        let sidePanelPostsTO = await ajaxCda({data: JSON.stringify(sidePanelRequestTO), action: "getSidePanelPosts"}, "error fetching side panel posts");

        let hSidePanelBase = Handlebars.compile(sidePanelBase);
        self.element.html(hSidePanelBase({associated: sidePanelPostsTO.associated, related: sidePanelPostsTO.related, latest: sidePanelPostsTO.latest}));

    }
});