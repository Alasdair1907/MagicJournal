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
<span class="item-container-heading">Relations Editor</span>

<span class="text">Posts that refer current post:</span>
<div class="list-group relation-manager-list-group">

{{#each postsRelateToThisList}}
<a class="list-group-item list-group-item-action rel-list-item" data-auto="{{#if this.isAuto}}true{{/if}}" data-id="{{this.relationId}}">{{#if this.isAuto}}[AUTO]{{/if}}[{{this.srcAttributionClassStr}}] {{this.srcObjectTitle}}</a> <!-- data-object-id="{{this.dstObjectId}}" data-object-class="{{this.dstAttributionClassShort}} -->
{{/each}}
</div>

<span class="text">Current post relates to:</span>
<div class="list-group relation-manager-list-group">

{{#each currentPostRelatesToList}}
<a class="list-group-item list-group-item-action rel-list-item" data-auto="{{#if this.isAuto}}true{{/if}}" data-role="link-selection" data-id="{{this.relationId}}">{{#if this.isAuto}}[AUTO]{{/if}}[{{this.dstAttributionClassStr}}] {{this.dstObjectTitle}}</a> <!-- data-object-id="{{this.dstObjectId}}" data-object-class="{{this.dstAttributionClassShort}}" -->
{{/each}}
</div>

<table class="width-100-pc">
<tr>
<td width="50%;" class="center-text"><button type="button" class="btn btn-danger btn-std btn-vertical" data-role="delete-link" disabled="disabled">Delete Link</button></td>
<td width="50%;" class="center-text"><button type="button" class="btn btn-success btn-std btn-vertical" data-role="new-link" >Add New Link</button></td>
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
      <span class="modal-h1">Select the post to link</span>
      <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span class="modal-title">Select source</span><br />
      
      <button class="btn btn-secondary btn-std btn-vertical" data-role="relation-article" data-id="2">Articles</button>
      <button class="btn btn-secondary btn-std btn-vertical" data-role="relation-photo" data-id="1">Photos</button>
      <button class="btn btn-secondary btn-std btn-vertical" data-role="relation-gallery" data-id="0">Galleries</button> <br />
      <hr class="hr-black">
      
      <div data-role="modal-posts-search"></div><br />
      <div data-role="modal-relation-select-main"></div>
      
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-primary btn-std" data-bs-dismiss="modal">Cancel</button>
    </div>
    
  </div>
</div>
</div>
    `,
    _modalListingTemplate: `
<table class="width-100-pc modal-list">
    <tr>
        <td class="table-lgray table-lgray-heading">Post Title</td>
        <td class="table-lgray table-lgray-heading">Author</td>
        <td class="table-lgray table-lgray-heading">Date</td>
        <td class="table-lgray table-lgray-heading"></td>
    </tr>

    {{#each postVOList}}
    <tr>
        <td class="table-lgray">{{this.title}}</td>
        <td class="table-lgray">{{this.authorVO.displayName}} ({{this.authorVO.login}})</td>
        <td class="table-lgray">{{this.creationDateStr}}</td>
        <td class="table-lgray center-text">
            <button type="button" class="btn btn-success btn-std" data-role="relation-target-selected" data-id="{{this.id}}" data-class="{{../class}}">Select</button>
        </td>
    </tr>
    {{/each}}
</table>
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

    _relationAdd: async function(self, ops, $modalElem){
        let $mainElem = self.element.find('[data-role="modal-relation-select-main"]');
        let $searchAnchor = self.element.find('[data-role="modal-posts-search"]');

        let $articleSrc = self.element.find('[data-role="relation-article"]');
        let $photoSrc = self.element.find('[data-role="relation-photo"]');
        let $gallerySrc = self.element.find('[data-role="relation-gallery"]');



        $articleSrc.unbind();
        $articleSrc.click(await async function(){


            let articleDisplay = async function(ignore, basicPostFilterTO){

                let postTO = {postAttributionClass: ops.attributionClass, postObjectId: ops.objectId, basicPostFilterTO: basicPostFilterTO};
                let postTOJson = JSON.stringify(postTO);

                let articleVOList = await ajax({action: "listConcernedArticlesVOs", data: postTOJson}, "error listing articles");
                if (articleVOList === undefined){
                    return;
                }

                let hModalListingTemplate = Handlebars.compile(self._modalListingTemplate);
                $mainElem.html(hModalListingTemplate({postVOList: articleVOList, class: "2"}));

                let $selectables = $mainElem.find('[data-role="relation-target-selected"]');
                $selectables.unbind();
                $selectables.click(await async function(){
                    let dstObjectId = $(this).data('id');
                    let dstObjectClassShort = $(this).data('class');

                    await self._processSelect(self, ops, dstObjectId, dstObjectClassShort, $modalElem);
                });
            };

            $mainElem.html('');
            postFilter($searchAnchor, {userGuid : Cookies.get("guid")}, articleDisplay, null);

        });

        $photoSrc.unbind();
        $photoSrc.click(await async function(){

            let photoDisplay = async function(ignore, basicPostFilterTO) {

                let postTO = {postAttributionClass: ops.attributionClass, postObjectId: ops.objectId, basicPostFilterTO: basicPostFilterTO};
                let postTOJson = JSON.stringify(postTO);

                let photoVOList = await ajax({
                    action: "listConcernedPhotosVOs",
                    data: postTOJson
                }, "error listing photos");
                if (photoVOList === undefined) {
                    return;
                }

                let hModalListingTemplate = Handlebars.compile(self._modalListingTemplate);
                $mainElem.html(hModalListingTemplate({postVOList: photoVOList, class: 1}));

                let $selectables = $mainElem.find('[data-role="relation-target-selected"]');
                $selectables.unbind();
                $selectables.click(await async function () {
                    let dstObjectId = $(this).data('id');
                    let dstObjectClassShort = $(this).data('class');

                    await self._processSelect(self, ops, dstObjectId, dstObjectClassShort, $modalElem);

                });
            };

            $mainElem.html('');
            postFilter($searchAnchor, {userGuid : Cookies.get("guid")}, photoDisplay, null);

        });

        $gallerySrc.unbind();
        $gallerySrc.click(await async function() {

            let galleryDisplay = async function(ignore, basicPostFilterTO) {

                let postTO = {postAttributionClass: ops.attributionClass, postObjectId: ops.objectId, basicPostFilterTO: basicPostFilterTO};
                let postTOJson = JSON.stringify(postTO);

                let galleryVOList = await ajax({
                    action: "listConcernedGalleryVOs",
                    data: postTOJson
                }, "error listing galleries");
                if (galleryVOList === undefined) {
                    return;
                }

                let hModalListingTemplate = Handlebars.compile(self._modalListingTemplate);
                $mainElem.html(hModalListingTemplate({postVOList: galleryVOList, class: 0}));

                let $selectables = $mainElem.find('[data-role="relation-target-selected"]');
                $selectables.unbind();
                $selectables.click(await async function () {
                    let dstObjectId = $(this).data('id');
                    let dstObjectClassShort = $(this).data('class');

                    await self._processSelect(self, ops, dstObjectId, dstObjectClassShort, $modalElem);
                });
            };

            $mainElem.html('');
            postFilter($searchAnchor, {userGuid : Cookies.get("guid")}, galleryDisplay, null);

        });

    },

    _processSelect: async function(self, ops, selectedId, selectedClass, $modalElem){
        let objectId = ops.objectId;
        let postAttributionClass = ops.attributionClass;

        let guid = Cookies.get("guid");
        let relationVo = {
            srcAttributionClassShort: postAttributionClass,
            srcObjectId: objectId,

            dstAttributionClassShort: selectedClass,
            dstObjectId: selectedId
        };
        let relationVoJson = JSON.stringify(relationVo);
        let res = await ajax({guid: guid, action: "createNewRelation", data: relationVoJson}, "error creating new relation");

        if (res === undefined){
            return;
        }

        $modalElem.modal('hide');
        $modalElem.html('');
        self.element.html('');
        self.element.RelationManager({attributionClass: postAttributionClass, objectId: objectId});

    },

    _display: async function(self, ops){

        let postTo = {
            postAttributionClass: ops.attributionClass,
            postObjectId: ops.objectId,
            basicPostFilterTO: {userGuid: Cookies.get("guid")}
        };

        let relationTo = await ajax({action: "listRelationsForPost", data: JSON.stringify(postTo) }, "error listing relations for post");

        let hMainTemplate = Handlebars.compile(self._template);
        self.element.html(hMainTemplate({postsRelateToThisList: relationTo.postsReferToThis, currentPostRelatesToList: relationTo.currentPostRelatesTo}));


        let $deleteLinkButton = self.element.find('[data-role="delete-link" ]');
        let $newLinkButton = self.element.find('[data-role="new-link"]');
        let $linkSelection = self.element.find('[data-role="link-selection"]');

        let currentlySelectedLinkId = null;

        $linkSelection.unbind();
        $linkSelection.click(function(){

            let auto = $(this).data("auto");
            if (auto === true){
                return;
            }

            $linkSelection.removeClass("active");
            $(this).addClass("active");
            currentlySelectedLinkId = $(this).data('id');
            if (!isDemo()) {
                $deleteLinkButton.prop("disabled", false);
            }
        });

        $deleteLinkButton.unbind();
        $deleteLinkButton.click(await async function(){
            if (currentlySelectedLinkId == null){
                return;
            }

            let guid = Cookies.get("guid");
            let res = await ajax({action:"deleteRelation", guid: guid, data: currentlySelectedLinkId}, "error deleting relation");
            if (res === undefined){
                return;
            }

            self.element.html("");
            self.element.RelationManager({attributionClass: ops.attributionClass, objectId: ops.objectId});
        });

        $newLinkButton.unbind();
        $newLinkButton.click(function(){
            let $modalDock = self.element.find('[data-role="relation-modal-dock"]');
            $modalDock.html(self._postSelectionModal);

            let $relationSelectModal = self.element.find('[data-role=relation-select-modal]');
            $relationSelectModal.modal('show');

            self._relationAdd(self, ops, $relationSelectModal);
        });
    }
});
