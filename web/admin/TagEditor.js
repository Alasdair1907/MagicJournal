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

$.widget("admin.TagEditor", {
    _template: `
<div class="item-container transparent width-100-pc">
<span class="item-container-heading">Tag Editor</span>
<span class="text">Tags, comma separated list:</span><br />
<input type="text" class="form-control input width-100-pc" value="{{tagListStr}}" data-role="tagsinput"/><br />
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="save-or-update-tags">Save</button>
</div>
    `,
    _create: async function(){
        let self = this;

        // active: true/false
        // attributionClass: short 0/1/2
        // objectId long
        let ops = this.options;

        await self._display(self, ops);

    },

    _init: async function(){
        let self = this;
        let ops = this.options;

        await self._display(self, ops);
    },

    _display: async function(self, ops){

        let tagTORequest = {
            attribution: ops.attributionClass,
            objectId: ops.objectId
        };

        let tagList = await ajax({action: "listTags", data: JSON.stringify(tagTORequest)}, "error loading tags list");

        let hTemplate = Handlebars.compile(self._template);
        self.element.html(hTemplate({tagListStr: tagList.tagListStr}));

        let $saveOrUpdateButton = self.element.find('[data-role=save-or-update-tags]');
        let $tagsInput = self.element.find('[data-role=tagsinput]');

        $saveOrUpdateButton.unbind();
        $saveOrUpdateButton.click(await async function(){

            let buttonText = spinButton($saveOrUpdateButton);

            let newTags = $tagsInput.val();

            let tagTOSave = {
                attribution: ops.attributionClass,
                objectId: ops.objectId,
                tagListStr: newTags
            };

            await ajax({action: "saveOrUpdateTags", guid: Cookies.get("guid"), data: JSON.stringify(tagTOSave)}, "error saving tags");
            unSpinButton($saveOrUpdateButton, buttonText);
        });
    }
});