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


$.widget("admin.galleriesWidget", {
    _create: function(){
        let self = this;
        self._display(self);

    },
    _init: function(){
        let self = this;
        self._display(self);
    },

    _display: async function(self){

        let adminResponseJson = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {action: "listAllGalleryVOs", guid: Cookies.get("guid")}
        });

        let adminResponse = adminResponseJson ? JSON.parse(adminResponseJson) : {data: null};

        if (adminResponse.success === false){
            alert("can't list galleries: "+adminResponse.errorDescription);
        } else {
            let hGalleryEditSelect = Handlebars.compile(galleryEditSelect);
            let testUser = Cookies.get("privilegeLevelName") === "test";
            self.element.html(hGalleryEditSelect({galleryVOs: adminResponse.data, testUser: testUser}));

            let $galleryEditButtons = self.element.find('[data-role="gallery-edit"]');
            let $galleryDeleteButtons = self.element.find('[data-role="gallery-delete"]');
            let $createNewGalleryButton = self.element.find('[data-role="gallery-new"]');

            let $galleryEditForm = self.element.find('[data-role="gallery-new-edit"]');
            let $galleryPublishToggle = self.element.find('[data-role="gallery-publish-toggle"]');

            let $galleryDeleteConfirmModal = self.element.find('[data-role="delete-gallery-confirm"]');

            $createNewGalleryButton.unbind();
            $createNewGalleryButton.click(await async function(){

                // create a gallery entity
                let galleryTO = await self._getEmptyGalleryTO();
                let newId = await self._saveOrUpdateGallery(galleryTO);

                // edit gallery entity
                let galleryVO = await self._getEmptyGalleryVO(newId);
                await self._edit($galleryEditForm, galleryVO, self);
            });

            $galleryEditButtons.unbind();
            $galleryEditButtons.click(await async function(){
                let galleryId = $(this).data('id');
                let galleryVO = await self._loadGallery(galleryId);
                await self._edit($galleryEditForm, galleryVO, self);
            });

            $galleryPublishToggle.unbind();
            $galleryPublishToggle.click(await async function(){
                let galleryId = $(this).data('id');

                let toggleResultJson = await $.ajax({
                    url: '/admin/jsonApi.jsp',
                    method: 'POST',
                    data: {action: "toggleGalleryPublish", guid: Cookies.get("guid"), data: galleryId}
                });

                var toggleResult;
                if (toggleResultJson){ toggleResult = JSON.parse(toggleResultJson); }
                if (!toggleResult || !toggleResult.success) {
                    alert("error toggling publish status: " + toggleResult.errorDescription);
                } else {
                    await self._display(self);
                }
            });

            $galleryDeleteButtons.unbind();
            $galleryDeleteButtons.click(await async function(){
                let galleryId = $(this).data('id');

                $galleryDeleteConfirmModal.modal();

                let $proceedButton = self.element.find('[data-role="delete-confirm"]');
                $proceedButton.unbind();
                $proceedButton.click(await async function(){
                    let deleteResultJson = await $.ajax({
                        url: '/admin/jsonApi.jsp',
                        method: 'POST',
                        data: {action: "deleteGallery", guid: Cookies.get("guid"), data: galleryId}
                    });

                    if (deleteResultJson) {
                        let deleteResult = JSON.parse(deleteResultJson);
                        if (deleteResult.success === true) {
                            $galleryDeleteConfirmModal.modal('hide');
                            self._display(self);
                        } else {
                            alert("error deleting: "+deleteResult.errorDescription);
                        }
                    }
                });

            });
        }
    },

    _getEmptyGalleryTO: function() {
        return {
            id: null,
            title: null,
            description: null,
            gpsCoordinates: null,
            sessionGuid: Cookies.get("guid")
        };
    },

    _getEmptyGalleryVO: function(id){
        return {
            id: id,
            title: "",
            description: "",
            gpsCoordinates: ""
        };
    },

    _loadGallery: async function(galleryId){
        let jsonAdminReponse = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {data:galleryId , action:"getGalleryVOByGalleryId"}
        });

        let adminResponse = JSON.parse(jsonAdminReponse);

        if (adminResponse.success === false){
            alert("error loading gallery: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }
    },

    _saveOrUpdateGallery: async function(galleryTO){
        let galleryTOJson = JSON.stringify(galleryTO);
        let jsonAdminResponse = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {data:galleryTOJson, action:"saveOrUpdateGallery"}
        });

        let adminResponse = JSON.parse(jsonAdminResponse);

        if (adminResponse.success === false){
            alert("error saving or updating gallery: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }
    },

    _edit: async function(element, galleryVO, self){
        let hGalleryNewOrEdit = Handlebars.compile(galleryNewOrEdit);
        let testUser = Cookies.get("privilegeLevelName") === "test";
        element.html(hGalleryNewOrEdit({galleryVO: galleryVO, testUser: testUser}));

        let $idElem = element.find('[data-role="data-id"]');
        let $titleElem = element.find('[data-role="data-title"]');
        let $descrElem = element.find('[data-role=data-description]');
        let $gpsElem = element.find('[data-role=data-gps-coordinates]');
        let $submitElem = element.find('[data-role="data-gallery-save-or-update"]');

        let $tagEditorDiv = element.find('[data-role="gallery-tag-editor"]');
        let $relationEditorDiv = element.find('[data-role="gallery-relation-editor"]');
        let $galleryImageManagerDiv = element.find('[data-role="gallery-image-manager"]');

        $tagEditorDiv.TagEditor({attributionClass: 0, objectId: galleryVO.id});
        $relationEditorDiv.RelationManager({attributionClass: 0, objectId: galleryVO.id});
        $galleryImageManagerDiv.ImageManager({attributionClass: 0, objectId: galleryVO.id});

        $submitElem.unbind();
        $submitElem.click(await async function(){
            let galleryTO = {
                id: $idElem.val(),
                title: $titleElem.val(),
                description: $descrElem.val(),
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };

            await self._saveOrUpdateGallery(galleryTO);
            await self._display(self);
        });
    },
});