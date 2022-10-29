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
        sidePanelPostsTO: null
    },

    // use helper.js/getSidepanelData() to get data
    _init: async function(){
        let ops = this.options;
        let self = this;

        let sidePanelPostsTO = ops.sidePanelPostsTO;

        let hSidePanelBase = Handlebars.compile(sidePanelBase);
        self.element.html(hSidePanelBase({associated: sidePanelPostsTO.associated, related: sidePanelPostsTO.related, latest: sidePanelPostsTO.latest}));

    }
});