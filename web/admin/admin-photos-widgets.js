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

$.widget("admin.photosWidget", {
    _create: function(){
        let self = this;
        self._display(self);

    },
    _init: function(){
        let self = this;
        self._display(self);
    },

    _getEmptyPhotoVO: function(id){

        let imageVO = {
            preview: "imgAdminPlaceholder.png"
        };

        return {
            id: id,
            title: "",
            description: "",
            gpsCoordinates: "",
            imageVO: imageVO
        };
    },

    _getEmptyPhotoTO: function() {
        return {
            id: null,
            title: null,
            description: null,
            gpsCoordinates: null,
            sessionGuid: Cookies.get("guid")
        };
    },

    _edit: async function(element, photoVO, self){
        let hPhotoNewOrEdit = Handlebars.compile(photoNewOrEdit);
        let testUser = Cookies.get("privilegeLevelName") === "test";
        element.html(hPhotoNewOrEdit({photoVO: photoVO, testUser: testUser}));

        let $idElem = element.find('[data-role="data-id"]');
        let $titleElem = element.find('[data-role="data-title"]');
        let $descrElem = element.find('[data-role=data-description]');
        let $gpsElem = element.find('[data-role=data-gps-coordinates]');
        let $fileElem = element.find('[data-role="data-file"]');
        let $submitElem = element.find('[data-role="data-photo-save-or-update"]');
        let $photoImageElem = element.find('[data-role="photo-image"]');
        let $photoImageUploadElem = element.find('[data-role="data-photo-upload-image"]');
        let $tagEditorDiv = element.find('[data-role="photo-tag-editor"]');

        $tagEditorDiv.TagEditor({attributionClass: 1, objectId: photoVO.id});

        $photoImageUploadElem.unbind();
        $photoImageUploadElem.click(await async function(){
            let file = $fileElem.get(0).files[0];
            let fileData = new FormData();
            fileData.append("fileInput", file);

            let imageUploadTO = {
                imageAttributionClass: 1,
                parentObjectId: photoVO.id,
                sessionGuid: Cookies.get("guid")
            };

            let imageUploadTOJson = btoa(JSON.stringify(imageUploadTO));
            Cookies.set("imageUploadTOJson", imageUploadTOJson);

            await $.ajax({
                url: '/admin/fileUpload.jsp',
                type: 'post',
                data: fileData,
                contentType: false,
                processData: false
            });

            let imageVO = await self._getPhotoImageVO(photoVO.id);
            if (!imageVO){
                alert("can't load image for photo!");
            }

            $photoImageElem.attr("src", "/getImage.jsp?filename="+imageVO.thumbnail);

        });

        $submitElem.unbind();
        $submitElem.click(await async function(){
            let photoTO = {
                id: $idElem.val(),
                title: $titleElem.val(),
                description: $descrElem.val(),
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };

            await self._saveOrUpdatePhoto(photoTO);
            await self._display(self);
        });

    },

    _getPhotoImageVO: async function(photoId){
        let jsonAdminResponse = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {data:photoId,action:"getPhotoImageVO"}
        });

        let adminResponse = JSON.parse(jsonAdminResponse);

        if (adminResponse.success === false){
            alert("error getting image data for photo: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }

    },

    _saveOrUpdatePhoto: async function(photoTO){
        let photoTOJson = JSON.stringify(photoTO);
        let jsonAdminResponse = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {data:photoTOJson, action:"saveOrUpdatePhoto"}
        });

        let adminResponse = JSON.parse(jsonAdminResponse);

        if (adminResponse.success === false){
            alert("error saving or updating photo: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }
    },

    _loadPhoto: async function(photoId){
        let jsonAdminReponse = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {data:photoId , action:"getPhotoVOByPhotoId"}
        });

        let adminResponse = JSON.parse(jsonAdminReponse);

        if (adminResponse.success === false){
            alert("error loading photo: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }
    },

    _display: async function(self){

        let adminResponseJson = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {action: "listAllPhotoVOs", guid: Cookies.get("guid")}
        });

        let adminResponse = adminResponseJson ? JSON.parse(adminResponseJson) : {data: null};

        if (adminResponse.success === false){
            alert("can't list photos: "+adminResponse.errorDescription);
        } else {
            let hPhotoEditSelect = Handlebars.compile(photoEditSelect);
            let testUser = Cookies.get("privilegeLevelName") === "test";
            self.element.html(hPhotoEditSelect({photoPosts: adminResponse.data, testUser: testUser}));

            let $photoPostEditButtons = self.element.find('[data-role="photo-post-edit"]');
            let $photoDeleteButtons = self.element.find('[data-role="photo-post-delete"]');
            let $createNewPhotoButton = self.element.find('[data-role="photo-post-new"]');

            let $photoPostEditForm = self.element.find('[data-role="photo-post-new-edit"]');
            let $photoPostPublishToggle = self.element.find('[data-role="photo-publish-toggle"]');

            let $photoDeleteConfirmModal = self.element.find('[data-role="delete-photo-confirm"]');

            $createNewPhotoButton.unbind();
            $createNewPhotoButton.click(await async function(){

                // create a photo entity
                let photoTO = await self._getEmptyPhotoTO();
                let newId = await self._saveOrUpdatePhoto(photoTO);

                // edit photo entity
                let photoVO = await self._getEmptyPhotoVO(newId);
                await self._edit($photoPostEditForm, photoVO, self);
            });

            $photoPostEditButtons.unbind();
            $photoPostEditButtons.click(await async function(){
                let photoId = $(this).data('id');
                let photoVO = await self._loadPhoto(photoId);
                await self._edit($photoPostEditForm, photoVO, self);
            });

            $photoPostPublishToggle.unbind();
            $photoPostPublishToggle.click(await async function(){
                let photoId = $(this).data('id');

                let toggleResultJson = await $.ajax({
                    url: '/admin/jsonApi.jsp',
                    method: 'POST',
                    data: {action: "togglePhotoPublish", guid: Cookies.get("guid"), data: photoId}
                });

                var toggleResult;
                if (toggleResultJson){ toggleResult = JSON.parse(toggleResultJson); }
                if (!toggleResult || !toggleResult.success) {
                    alert("error toggling publish status: " + toggleResult.errorDescription);
                } else {
                    await self._display(self);
                }
            });

            $photoDeleteButtons.unbind();
            $photoDeleteButtons.click(await async function(){
                let photoId = $(this).data('id');

                $photoDeleteConfirmModal.modal();

                let $proceedButton = self.element.find('[data-role="delete-confirm"]');
                $proceedButton.unbind();
                $proceedButton.click(await async function(){
                    let deleteResultJson = await $.ajax({
                        url: '/admin/jsonApi.jsp',
                        method: 'POST',
                        data: {action: "deletePhoto", guid: Cookies.get("guid"), data: photoId}
                    });

                    if (deleteResultJson) {
                        let deleteResult = JSON.parse(deleteResultJson);
                        if (deleteResult.success === true) {
                            $photoDeleteConfirmModal.modal('hide');
                            self._display(self);
                        } else {
                            alert("error deleting: "+deleteResult.errorDescription);
                        }
                    }
                });

            });
        }
    }

});