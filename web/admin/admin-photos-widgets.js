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
        let demoUser = Cookies.get("privilegeLevelName") === "demo";
        element.html(hPhotoNewOrEdit({photoVO: photoVO, demoUser: demoUser}));

        let $idElem = element.find('[data-role="data-id"]');
        let $titleElem = element.find('[data-role="data-title"]');
        let $tinyDescrElem = element.find('[data-role="data-tinydescription"]');
        let $descrElem = element.find('[data-role=data-description]');

        let $gpsElem = element.find('[data-role=data-gps-coordinates]');
        let $mapSelectLink = element.find('[data-role="select-on-map"]');
        let $modalMapAnchor = element.find('[data-role="map-modal-anchor"]');

        let $fileElem = element.find('[data-role="data-file"]');
        let $submitElem = element.find('[data-role="data-photo-save-or-update"]');
        let $photoImageElem = element.find('[data-role="photo-image"]');
        let $photoImageUploadElem = element.find('[data-role="data-photo-upload-image"]');

        let $tagEditorDiv = element.find('[data-role="photo-tag-editor"]');
        let $relationManagerDiv = element.find('[data-role="photo-relation-manager"]');

        self.$tagEditorDiv = $tagEditorDiv;
        $tagEditorDiv.TagEditor({attributionClass: 1, objectId: photoVO.id});
        $relationManagerDiv.RelationManager({attributionClass: 1, objectId: photoVO.id});

        $mapSelectLink.click(await async function(){
            await mapPick($modalMapAnchor, $gpsElem, $gpsElem.val());
        });

        $photoImageUploadElem.unbind();
        $photoImageUploadElem.click(await async function(){

            let buttonText = spinButton($photoImageUploadElem);

            let file = $fileElem.get(0).files[0];
            let fileData = new FormData();
            fileData.append("fileInput", file);

            let imageUploadTO = {
                imageAttributionClass: 1,
                parentObjectId: photoVO.id,
                sessionGuid: Cookies.get("guid")
            };

            let imageUploadTOJson = await toBase64(JSON.stringify(imageUploadTO));
            Cookies.set("imageUploadTOJson", imageUploadTOJson);

            await $.ajax({
                url: 'fileUpload.jsp',
                type: 'post',
                data: fileData,
                contentType: false,
                processData: false
            });

            Cookies.remove("imageUploadTOJson");
            unSpinButton($photoImageUploadElem, buttonText);

            let imageVO = await self._getPhotoImageVO(photoVO.id);
            if (!imageVO){
                alert("can't load image for photo!");
            }

            $photoImageElem.attr("src", "../getImage.jsp?filename="+imageVO.thumbnail);
        });

        $submitElem.unbind();
        $submitElem.click(await async function(){

            if (!checkCoordinates($gpsElem.val())){
                return;
            }

            spinButton($submitElem);

            let photoTO = {
                id: $idElem.val(),
                title: $titleElem.val(),
                tinyDescription: $tinyDescrElem.val(),
                description: $descrElem.val(),
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };

            let res = await self._saveOrUpdatePhoto(photoTO);
            if (res !== undefined){
                self.element.html('');
                await self._display(self);
            }

        });

    },

    _getPhotoImageVO: async function(photoId){
        return await ajax({data:photoId,action:"getPhotoImageVO"});
    },

    _saveOrUpdatePhoto: async function(photoTO){
        let self = this;

        let photoTOJson = JSON.stringify(photoTO);
        let res = await ajax({data:photoTOJson, action:"saveOrUpdatePhoto"});

        if (photoTO.id !== undefined && photoTO.id !== null){
            let tagsTO = self.$tagEditorDiv.TagEditor('getTagTO');
            await ajax({action: "saveOrUpdateTags", guid: Cookies.get("guid"), data: JSON.stringify(tagsTO)}, "error saving tags");

            let photoVO = await ajax({data:photoTO.id , action:"getPhotoVOByPhotoId", guid:Cookies.get("guid")});
            let renderDiv = document.createElement("div");
            renderDiv.setAttribute("display","none");
            let $renderDiv = $(renderDiv);

            let hPhotoTemplate = Handlebars.compile(photoTemplate);
            $renderDiv.html(hPhotoTemplate({photoVO: photoVO, photoDescription: basicRender(photoVO.description)}));

            let preRender = $renderDiv.html();
            renderDiv.remove();

            photoTO.preRender = preRender;
            return await ajax({data:JSON.stringify(photoTO), action:"saveOrUpdatePhoto"});
        }

        return res;
    },

    _loadPhoto: async function(photoId){
        return await ajax({data:photoId , action:"getPhotoVOByPhotoId", guid:Cookies.get("guid")});
    },

    _display: async function(self, basicPostFilterTO, refreshSearch){

        // this is necessary to pass the function as parameter to postFilter()
        let __display = async function(self, basicPostFilterTO, refreshSearch){
            if (basicPostFilterTO) {
                basicPostFilterTO.userGuid = Cookies.get("guid");
            } else {
                basicPostFilterTO = {userGuid : Cookies.get("guid")};
            }

            let photoVOList = await ajax({action: "listAllPhotoVOs", data: JSON.stringify(basicPostFilterTO), guid: Cookies.get("guid")}, "error listing photos");

            let hPhotoEditSelect = Handlebars.compile(photoEditSelect);
            let demoUser = Cookies.get("privilegeLevelName") === "demo";
            self.element.html(hPhotoEditSelect({photoPosts: photoVOList, demoUser: demoUser}));

            let $searchAnchor = self.element.find('[data-role="search-anchor"]');
            postFilter($searchAnchor, basicPostFilterTO, __display, self, refreshSearch);

            let $photoPostEditButtons = self.element.find('[data-role="photo-post-edit"]');
            let $photoDeleteButtons = self.element.find('[data-role="photo-post-delete"]');
            let $createNewPhotoButton = self.element.find('[data-role="photo-post-new"]');


            let $photoPostPublishToggle = self.element.find('[data-role="photo-publish-toggle"]');

            let $photoDeleteConfirmModal = self.element.find('[data-role="delete-photo-confirm"]');

            if (Cookies.get("privilegeLevelName") === "user") {
                $photoPostPublishToggle.prop("disabled", true);
                $photoDeleteButtons.prop("disabled", true);
                $photoPostEditButtons.prop("disabled", true);

                let myLogin = Cookies.get("login");
                let $ownButtons = self.element.find('[data-owner="'+myLogin+'"]');
                $ownButtons.prop("disabled", false);
            }

            $createNewPhotoButton.unbind();
            $createNewPhotoButton.click(await async function(){

                // create a photo entity
                let photoTO = await self._getEmptyPhotoTO();
                let newId = await self._saveOrUpdatePhoto(photoTO);

                // edit photo entity
                let photoVO = await self._getEmptyPhotoVO(newId);
                await self._edit(self.element, photoVO, self);
            });

            $photoPostEditButtons.unbind();
            $photoPostEditButtons.click(await async function(){
                let photoId = $(this).data('id');
                let photoVO = await self._loadPhoto(photoId);
                await self._edit(self.element, photoVO, self);
            });

            $photoPostPublishToggle.unbind();
            $photoPostPublishToggle.click(await async function(){
                let photoId = $(this).data('id');

                let toggleResultJson = await $.ajax({
                    url: 'jsonApi.jsp',
                    method: 'POST',
                    data: {action: "togglePhotoPublish", guid: Cookies.get("guid"), data: photoId}
                });

                var toggleResult;
                if (toggleResultJson){ toggleResult = JSON.parse(toggleResultJson); }
                if (!toggleResult || !toggleResult.success) {
                    alert("error toggling publish status: " + toggleResult.errorDescription);
                } else {
                    await self._display(self, basicPostFilterTO, true);
                }
            });

            $photoDeleteButtons.unbind();
            $photoDeleteButtons.click(await async function(){
                let photoId = $(this).data('id');

                $photoDeleteConfirmModal.modal('show');

                let $proceedButton = self.element.find('[data-role="delete-confirm"]');
                $proceedButton.unbind();
                $proceedButton.click(await async function(){
                    let deleteResultJson = await $.ajax({
                        url: 'jsonApi.jsp',
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

            if (isDemo()){
                $photoPostPublishToggle.prop("disabled", true);
                $createNewPhotoButton.prop("disabled", true);
            }
        };
        await __display(self, basicPostFilterTO, refreshSearch);


    }

});
