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
<input type="text" class="input width-100-pc" value="{{tagListStr}}" data-role="tagsinput"/><br />
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="save-or-update-tags">Save or Update</button>
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

        let tagListJsonResult = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {action: "listTags", data: JSON.stringify(tagTORequest)}
        });

        if (!tagListJsonResult){
            alert("error loading tags list!");
            return;
        }

        let tagListResponse = JSON.parse(tagListJsonResult);

        if (!tagListResponse.success){
            alert("error loading tags list: "+tagListResponse.errorDescription);
            return;
        }

        let hTemplate = Handlebars.compile(self._template);
        self.element.html(hTemplate({tagListStr: tagListResponse.data.tagListStr}));

        let $saveOrUpdateButton = self.element.find('[data-role=save-or-update-tags]');
        let $tagsInput = self.element.find('[data-role=tagsinput]');

        $saveOrUpdateButton.unbind();
        $saveOrUpdateButton.click(await async function(){
            let newTags = $tagsInput.val();

            let tagTOSave = {
                attribution: ops.attributionClass,
                objectId: ops.objectId,
                tagListStr: newTags
            };

            let saveResult = await $.ajax({
                url: '/admin/jsonApi.jsp',
                method: 'POST',
                data: {action: "saveOrUpdateTags", guid: Cookies.get("guid"), data: JSON.stringify(tagTOSave)}
            });

            if (!saveResult){
                alert("error saving tags!");
                return;
            }

            let saveResultResponse = JSON.parse(saveResult);
            if (!saveResultResponse.success){
                alert("error saving tags: "+saveResultResponse.errorDescription);
            }
        });
    }
});