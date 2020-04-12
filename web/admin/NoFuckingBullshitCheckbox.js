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

$.widget("admin.NoFuckingBullshitCheckbox", {
    _templateUnchecked: `
<div style="width:2vh;height:2vh;background:white;display:flex;justify-content: center;">
    <div style="align-self: center;width:1vh;height:1vh;background:white;"></div>    
</div>
    `,
    _templateChecked: `
<div style="width:2vh;height:2vh;background:white;display:flex;justify-content: center;">
    <div style="align-self: center;width:1vh;height:1vh;background:#007bff"></div>    
</div>
    `,

    _init: function(){
        this._display(this);
    },
    _create: function(){
        this._display(this);
    },
    _display: function(self){
        if (self.element.prop('checked') === true){
            self.element.html(self._templateChecked);
            self.element.prop('checked', true);
        } else {
            self.element.html(self._templateUnchecked);
            self.element.prop('checked', false);
        }


        self.element.unbind();
        self.element.click(function(){

            if (self.element.attr("disabled") === "disabled"){
                return;
            }

            if (self.element.prop('checked') === false){
                self.element.prop('checked', true);
                self.element.html(self._templateChecked);
            } else {
                self.element.prop('checked', false);
                self.element.html(self._templateUnchecked);
            }
        });
    }
});