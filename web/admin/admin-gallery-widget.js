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

    _display: async function(self, basicPostFilterTO, refreshSearch){
        // this is necessary to pass the function as parameter to postFilter()
        let __display = async function(self, basicPostFilterTO, refreshSearch){
            if (basicPostFilterTO) {
                basicPostFilterTO.userGuid = Cookies.get("guid");
            } else {
                basicPostFilterTO = {userGuid : Cookies.get("guid")};
            }

            let galleryVOList = await ajax({action: "listAllGalleryVOs", data: JSON.stringify(basicPostFilterTO), guid: Cookies.get("guid")});

            let hGalleryEditSelect = Handlebars.compile(galleryEditSelect);
            let demoUser = Cookies.get("privilegeLevelName") === "demo";
            self.element.html(hGalleryEditSelect({galleryVOs: galleryVOList, demoUser: demoUser}));

            let $searchAnchor = self.element.find('[data-role="search-anchor"]');
            postFilter($searchAnchor, basicPostFilterTO, __display, self, refreshSearch);

            let $galleryEditButtons = self.element.find('[data-role="gallery-edit"]');
            let $galleryDeleteButtons = self.element.find('[data-role="gallery-delete"]');
            let $createNewGalleryButton = self.element.find('[data-role="gallery-new"]');

            let $galleryPublishToggle = self.element.find('[data-role="gallery-publish-toggle"]');

            let $galleryDeleteConfirmModal = self.element.find('[data-role="delete-gallery-confirm"]');

            if (Cookies.get("privilegeLevelName") === "user") {
                $galleryPublishToggle.prop("disabled", true);
                $galleryDeleteButtons.prop("disabled", true);
                $galleryEditButtons.prop("disabled", true);

                let myLogin = Cookies.get("login");
                let $ownButtons = self.element.find('[data-owner="'+myLogin+'"]');
                $ownButtons.prop("disabled", false);
            }

            $createNewGalleryButton.unbind();
            $createNewGalleryButton.click(await async function(){

                let buttonText = spinButton($createNewGalleryButton);
                // create a gallery entity
                let galleryTO = await self._getEmptyGalleryTO();
                let newId = await self._saveOrUpdateGallery(galleryTO);

                // edit gallery entity
                let galleryVO = await self._getEmptyGalleryVO(newId);
                await self._edit(self.element, galleryVO, self);

                unSpinButton($createNewGalleryButton, buttonText);
            });

            $galleryEditButtons.unbind();
            $galleryEditButtons.click(await async function(){

                let buttonText = spinButton($galleryEditButtons);

                let galleryId = $(this).data('id');
                let galleryVO = await self._loadGallery(galleryId);
                await self._edit(self.element, galleryVO, self);

                unSpinButton($galleryEditButtons, buttonText);
            });

            $galleryPublishToggle.unbind();
            $galleryPublishToggle.click(await async function(){
                let galleryId = $(this).data('id');

                let toggleResultJson = await $.ajax({
                    url: 'jsonApi.jsp',
                    method: 'POST',
                    data: {action: "toggleGalleryPublish", guid: Cookies.get("guid"), data: galleryId}
                });

                var toggleResult;
                if (toggleResultJson){ toggleResult = JSON.parse(toggleResultJson); }
                if (!toggleResult || !toggleResult.success) {
                    alert("error toggling publish status: " + toggleResult.errorDescription);
                } else {
                    await self._display(self, basicPostFilterTO, true);
                }
            });

            $galleryDeleteButtons.unbind();
            $galleryDeleteButtons.click(await async function(){
                let galleryId = $(this).data('id');

                $galleryDeleteConfirmModal.modal('show');

                let $proceedButton = self.element.find('[data-role="delete-confirm"]');
                $proceedButton.unbind();
                $proceedButton.click(await async function(){
                    let deleteResultJson = await $.ajax({
                        url: 'jsonApi.jsp',
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

            if (isDemo()){
                $galleryPublishToggle.prop("disabled", true);
                $createNewGalleryButton.prop("disabled", true);
            }
        };
        await __display(self, basicPostFilterTO, refreshSearch);

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
            url: 'jsonApi.jsp',
            method: 'POST',
            data: {data:galleryId , action:"getGalleryVOByGalleryId", guid:Cookies.get("guid")}
        });

        let adminResponse = JSON.parse(jsonAdminReponse);

        if (adminResponse.success === false){
            alert("error loading gallery: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }
    },

    _saveOrUpdateGallery: async function(galleryTO){
        let self = this;
        let galleryTOJson = JSON.stringify(galleryTO);
        let res = await ajax({data:galleryTOJson, action:"saveOrUpdateGallery", guid:Cookies.get("guid")});

        if (galleryTO.id !== undefined && galleryTO.id !== null) {

            let tagsTO = self.$tagEditorDiv.TagEditor('getTagTO');
            await ajax({action: "saveOrUpdateTags", guid: Cookies.get("guid"), data: JSON.stringify(tagsTO)}, "error saving tags");

            let galleryVO = await ajax({
                action: "getGalleryVOByGalleryId",
                data: galleryTO.id,
                guid: Cookies.get("guid")
            });

            let renderDiv = document.createElement("div");
            renderDiv.setAttribute("display","none");
            let $renderDiv = $(renderDiv);

            let hGalleryTemplate = Handlebars.compile(galleryTemplate);
            $renderDiv.html(hGalleryTemplate({
                galleryVO: galleryVO,
                galleryDescription: basicRender(galleryVO.description)
            }));

            let preRender = $renderDiv.html();
            renderDiv.remove();

            galleryTO.preRender = preRender;
            return await ajax({data:JSON.stringify(galleryTO), action:"saveOrUpdateGallery", guid:Cookies.get("guid")});
        }

        return res;

    },

    _edit: async function(element, galleryVO, self){
        let hGalleryNewOrEdit = Handlebars.compile(galleryNewOrEdit);
        let demoUser = Cookies.get("privilegeLevelName") === "demo";
        element.html(hGalleryNewOrEdit({galleryVO: galleryVO, demoUser: demoUser}));

        let $modalAnchor = element.find('[data-role="modal-anchor"]');

        let $idElem = element.find('[data-role="data-id"]');
        let $titleElem = element.find('[data-role="data-title"]');
        let $tinyDescrElem = element.find('[data-role="data-tinydescription"]');
        let $descrElem = element.find('[data-role=data-description]');
        let $seDescrElem = element.find('[data-role="data-se-description"]');

        let $submitElem = element.find('[data-role="data-gallery-save-or-update"]');
        let $submitElemPreview = element.find('[data-role="data-gallery-save-or-update-preview"]');

        let $gpsElem = element.find('[data-role=data-gps-coordinates]');
        let $mapPickLink = element.find('[data-role="select-on-map"]');

        $mapPickLink.click(await async function(){
            await mapPick($modalAnchor, $gpsElem, $gpsElem.val());
        });

        let $tagEditorDiv = element.find('[data-role="gallery-tag-editor"]');
        let $relationEditorDiv = element.find('[data-role="gallery-relation-editor"]');
        let $galleryImageManagerDiv = element.find('[data-role="gallery-image-manager"]');

        self.$tagEditorDiv = $tagEditorDiv;
        $tagEditorDiv.TagEditor({attributionClass: 0, objectId: galleryVO.id});
        $relationEditorDiv.RelationManager({attributionClass: 0, objectId: galleryVO.id});
        $galleryImageManagerDiv.ImageManager({attributionClass: 0, objectId: galleryVO.id});

        let getGalleryTO = function(){
            return {
                id: $idElem.val(),
                title: $titleElem.val(),
                tinyDescription: $tinyDescrElem.val(),
                description: $descrElem.val(),
                seDescription: $seDescrElem.val(),
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };
        };

        $submitElem.unbind();
        $submitElem.click(await async function(){
            if (!checkCoordinates($gpsElem.val())){
                return;
            }
            spinButton($submitElem);
            let galleryTO = getGalleryTO();
            let res = await self._saveOrUpdateGallery(galleryTO);
            if (res !== undefined){
                self.element.html('');
                await self._display(self);
            }

        });

        $submitElemPreview.unbind();
        $submitElemPreview.click(await async function(){
            if (!checkCoordinates($gpsElem.val())){
                return;
            }
            let buttonText = spinButton($submitElemPreview);
            let galleryTO = getGalleryTO();
            let res = await self._saveOrUpdateGallery(galleryTO);
            unSpinButton($submitElemPreview, buttonText)
            window.open("../posts.jsp?gallery="+$idElem.val(), "_blank");
        });

    },
});
