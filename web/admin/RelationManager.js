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

$.widget("admin.RelationManager", {
    _template: `
<div class="item-container transparent width-100-pc">
<span class="item-container-heading">Tag Editor</span>

<span class="text">Posts that refer current post:</span>


<span class="text">Current post relates to:</span>
<div class="list-group">
{{#each currentPostRelatesToList}}
<a href="#" class="list-group-item list-group-item-action" data-object-id="{{this.dstObjectId}}" data-object-class="{{this.dstAttributionClassShort}}">{{this.dstObjectTitle}}</a>
{{/each}}
</div>

<table class="width-100-pc">
<tr>
<td width="50%;" class="center-text"><button type="button" class="btn btn-danger btn-std" data-role="delete-link" disabled="disabled">Delete Link</button></td>
<td width="50%;" class="center-text"><button type="button" class="btn btn-success btn-std" data-role="new-link" disabled="disabled">Add New Link</button></td>
</tr>
</table>
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

    }
});