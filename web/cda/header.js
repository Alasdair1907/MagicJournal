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


$.widget("magic.header", {
    _init: async function(){
        await this._display(this);
    },

    _display: async function(self){

        let settingsTO = await getSettingsTO();

        if (settingsTO === undefined){
            alert('Can not load website settings');
        }

        let hHeaderMain = Handlebars.compile(headerMain);
        self.element.html(hHeaderMain({settingsTO: settingsTO}));

        let $lowresExtraMenu = self.element.find('[data-role="lowres-extra-menu"]');

        $lowresExtraMenu.unbind();
        $lowresExtraMenu.click(function(){

            let hExtraMenuOverlay = Handlebars.compile(extraMenuOverlay);
            let overlayHtml = hExtraMenuOverlay({settingsTO: settingsTO});

            let $newElem = $(overlayHtml);
            $newElem.appendTo("body");
            $("body").css("overflow", "hidden");

            let $extraMenuContainer = $newElem.find('[data-role="extra-menu-container"]');
            $extraMenuContainer.click(function(){
                $newElem.remove();
                $("body").css("overflow", "scroll");
            });
        });
    }
});