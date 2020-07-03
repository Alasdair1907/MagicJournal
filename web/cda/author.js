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

$.widget("magic.authorPage", {
    _init: async function(){
        let self = this;

        let params = new URLSearchParams(window.location.search);

        if (!params.has("author")){
            return;
        }

        let login = params.get("author");
        let authorVO = await ajaxCda({action: "getAuthorVOByLogin", data: login});

        let hAuthorPageTemplate = Handlebars.compile(authorPageTemplate);
        self.element.html(hAuthorPageTemplate({authorVO: authorVO, authorDescription: newlineToBr(authorVO.bio)}));

        document.title = "About author: " + authorVO.displayName;
    }
});