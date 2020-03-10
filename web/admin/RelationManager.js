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
<div class="list-group relation-manager-list-group">

{{#each postsRelateToThisList}}
<a href="#" class="list-group-item list-group-item-action {{#if this.isAuto}}disabled{{/if}}" data-role="link-selection" data-id="{{this.relationId}}">{{this.dstObjectTitle}}</a> <!-- data-object-id="{{this.dstObjectId}}" data-object-class="{{this.dstAttributionClassShort}} -->
{{/each}}
</div>

<span class="text">Current post relates to:</span>
<div class="list-group relation-manager-list-group">

{{#each currentPostRelatesToList}}
<a href="#" class="list-group-item list-group-item-action {{#if this.isAuto}}disabled{{/if}}" data-role="link-selection" data-id="{{this.relationId}}">{{this.dstObjectTitle}}</a> <!-- data-object-id="{{this.dstObjectId}}" data-object-class="{{this.dstAttributionClassShort}}" -->
{{/each}}
</div>

<table class="width-100-pc">
<tr>
<td width="50%;" class="center-text"><button type="button" class="btn btn-danger btn-std" data-role="delete-link" disabled="disabled">Delete Link</button></td>
<td width="50%;" class="center-text"><button type="button" class="btn btn-success btn-std" data-role="new-link" >Add New Link</button></td>
</tr>
</table>
</div>

<div data-role="relation-modal-dock"></div>
    `,

    _postSelectionModal: `
<div class="modal" data-role="relation-select-modal">
<div class="modal-dialog modal-window-huge">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Select the post to link</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span class="modal-title">Select source</span><br />
      
      <button class="btn btn-secondary btn-std btn-vertical" data-role="relation-article" data-id="2">Articles</button>
      <button class="btn btn-secondary btn-std btn-vertical" data-role="relation-photo" data-id="1">Photos</button>
      <button class="btn btn-secondary btn-std btn-vertical" data-role="relation-gallery" data-id="0">Galleries</button> <br />
      
      <div data-role="modal-relation-select-main"></div>
      
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-primary btn-std" data-dismiss="modal">Cancel</button>
    </div>
    
  </div>
</div>
</div>
    `,
    _modalListingTemplate: `
{{#each postVOList}}
<button type="button" class="width-medium btn btn-light btn-std" data-role="relation-target-selected" data-id="{{this.id}}" data-class="{{class}}">{{this.title}} / {{this.authorVO.displayName}}</button><br />
{{/each}}
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

    _relationAdd: async function(self, ops){
        let objectId = ops.objectId;
        let postAttributionClass = ops.attributionClass;

        let $mainElem = self.element.find('[data-role="modal-relation-select-main"]');

        let $articleSrc = self.element.find('[data-role="relation-article"]');
        let $photoSrc = self.element.find('[data-role="relation-photo"]');
        let $gallerySrc = self.element.find('[data-role="relation-gallery"]');

        $articleSrc.unbind();
        $articleSrc.click(await async function(){
            let articleVOList = await ajax({action: "listAllArticleVOsNoFilter"}, "error listing articles");
            if (articleVOList === undefined){
                return;
            }

            let hModalListingTemplate = Handlebars.compile(self._modalListingTemplate);
            $mainElem.html(hModalListingTemplate({postVOList: articleVOList, class: 2}));
        });

        $photoSrc.unbind();
        $photoSrc.click(await async function(){
            let photoVOList = await ajax({action: "listAllPhotoVOsNoFilter"}, "error listing photos");
            if (photoVOList === undefined){
                return;
            }

            let hModalListingTemplate = Handlebars.compile(self._modalListingTemplate);
            $mainElem.html(hModalListingTemplate({postVOList: photoVOList, class: 1}));
        });

        $gallerySrc.unbind();
        $gallerySrc.click(await async function() {
            let galleryVOList = await ajax({action: "listAllGalleryVOsNoFilter"}, "error listing galleries");
            if (galleryVOList === undefined){
                return;
            }

            let hModalListingTemplate = Handlebars.compile(self._modalListingTemplate);
            $mainElem.html(hModalListingTemplate({postVOList: galleryVOList, class: 0}));
        });
    },

    _display: async function(self, ops){

        let postTo = {
            postAttributionClass: ops.attributionClass,
            postObjectId: ops.objectId
        };

        let relationToJson =  await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {action: "listRelationsForPost", data: JSON.stringify(postTo) }
        });

        if (!relationToJson){
            alert("error loading relations list!");
            return;
        }

        let res = JSON.parse(relationToJson);

        if (!res.success){
            alert("error loading relations list: "+res.errorDescription);
            return;
        }

        let relationTo = res.data;

        let hMainTemplate = Handlebars.compile(self._template);
        self.element.html(hMainTemplate({postsRelateToThisList: relationTo.postsReferToThis, currentPostRelatesToList: relationTo.currentPostRelatesTo}));


        let $deleteLinkButton = self.element.find('[data-role="delete-link" ]');
        let $newLinkButton = self.element.find('[data-role="new-link"]');
        let $linkSelection = self.element.find('[data-role="link-selection"]');

        let currentlySelectedLinkId = null;

        $linkSelection.unbind();
        $linkSelection.click(function(){
            $linkSelection.removeClass("active");
            $(this).addClass("active");
            currentlySelectedLinkId = $(this).data('id');
            $deleteLinkButton.prop("disabled", false);
        });

        $deleteLinkButton.unbind();
        $deleteLinkButton.click(await async function(){

        });

        $newLinkButton.unbind();
        $newLinkButton.click(function(){
            let $modalDock = self.element.find('[data-role="relation-modal-dock"]');
            $modalDock.html(self._postSelectionModal);

            let $relationSelectModal = self.element.find('[data-role=relation-select-modal]');
            $relationSelectModal.modal();

            self._relationAdd(self, ops);
        });

    }
});