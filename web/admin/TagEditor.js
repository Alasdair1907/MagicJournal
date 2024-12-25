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
<span class="text">The tag list gets saved when the post is saved</span><br />
</div>
    `,
    _create: async function(){
        console.log("TagEditor() _create");
        let self = this;

        // active: true/false
        // attributionClass: short 0/1/2
        // objectId long
        let ops = this.options;

        await self._display(self, ops);

    },

    _init: async function(){
        console.log("TagEditor() _init");
        let self = this;
        let ops = this.options;

        await self._display(self, ops);
    },

    getTagTO: function() {
        console.log("TagEditor() getTagTO");
        let self = this;
        let ops = this.options;

        let $tagsInput = self.element.find('[data-role=tagsinput]');
        let newTags = $tagsInput.val();
        let tagTOSave = {
            attribution: ops.attributionClass,
            objectId: ops.objectId,
            tagListStr: newTags
        };

        return tagTOSave;
    },

    _display: async function(self, ops){

        console.log("TagEditor() _display");

        let tagTORequest = {
            attribution: ops.attributionClass,
            objectId: ops.objectId
        };

        let tagList = await ajax({action: "listTags", data: JSON.stringify(tagTORequest)}, "error loading tags list");

        let hTemplate = Handlebars.compile(self._template);
        self.element.html(hTemplate({tagListStr: tagList.tagListStr}));
    }
});