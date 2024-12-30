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


$.widget("admin.photostoriesWidget", {

    _titleCharLimit: 42,
    _imageCaptionLimit: 100,
    _textBlockLimit: 520,

    _create: function(){
    },
    _init: function(){
        let self = this;
        self._display(self);
    },

    _getEmptyPhotostoryVO: function(id){

        let titleImageVO = {
            preview: "imgAdminPlaceholder.png"
        };

        return {
            id: id,
            title: "",
            description: "",
            gpsCoordinates: "",
            content: "",
            titleImageVO: titleImageVO
        };
    },

    _getEmptyPhotostoryTO: function() {
        return {
            id: null,
            title: null,
            description: null,
            gpsCoordinates: null,
            content: null,
            sessionGuid: Cookies.get("guid")
        };
    },

    _edit: async function(element, photostoryVO, self){
        let hPhotostoryNewOrEdit = Handlebars.compile(photostoryNewOrEdit);
        let demoUser = Cookies.get("privilegeLevelName") === "demo";

        element.html(hPhotostoryNewOrEdit({photostoryVO: photostoryVO, demoUser: demoUser}));

        let $modalAnchor = self.element.find('[data-role="modal-anchor"]');
        let $idElem = element.find('[data-role="data-id"]');
        let $titleElem = element.find('[data-role="data-title"]');
        let $tinyDescrElem = element.find('[data-role="data-tinydescription"]');
        let $descrElem = element.find('[data-role=data-description]');

        let $gpsElem = element.find('[data-role=data-gps-coordinates]');
        let $coordinatesSelectLink = element.find('[data-role="select-on-map"]');
        $coordinatesSelectLink.click(await async function(){
            await mapPick($modalAnchor, $gpsElem, $gpsElem.val());
        });

        let $submitElem = element.find('[data-role="data-photostory-save-or-update"]');
        let $submitElemClose = element.find('[data-role="data-photostory-save-or-update-close"]');
        let $submitElemPreview = element.find('[data-role="data-photostory-save-and-preview"]');

        let $photostoryImageElem = element.find('[data-role="photostory-title-image"]'); // img with the image
        let $tagEditorDiv = element.find('[data-role="photostory-tag-editor"]');
        let $relationEditorDiv = element.find('[data-role="photostory-relation-editor"]');
        let $helperImageManager = element.find('[data-role="photostory-helper-image-manager"]');
        let $photostoryUpdateImageButton = element.find('[data-role="data-photostory-update-image"]');

        self.$tagEditorDiv = $tagEditorDiv;
        $tagEditorDiv.TagEditor({attributionClass: 4, objectId: photostoryVO.id});
        $relationEditorDiv.RelationManager({attributionClass: 4, objectId: photostoryVO.id});
        $helperImageManager.ImageManager({attributionClass: 4, objectId: photostoryVO.id});


        $photostoryUpdateImageButton.unbind();
        $photostoryUpdateImageButton.click(await async function(){

            let newImageData = await imageSelect($modalAnchor, photostoryVO.id, false, true);
            let newImageId = newImageData.selectedId;

            if (!newImageId){
                return;
            }

            let photostoryTo = self._getEmptyPhotostoryTO();
            photostoryTo.id = photostoryVO.id;
            photostoryTo.titleImageId = newImageId;


            let jsonAdminResponse = await $.ajax({
                url: 'jsonApi.jsp',
                method: 'POST',
                data: {data: JSON.stringify(photostoryTo), guid: Cookies.get("guid"), action:"setPhotostoryTitleImageId"}
            });

            let adminResponse = JSON.parse(jsonAdminResponse);
            if (adminResponse.success === false){
                alert("error updating image ID: "+adminResponse.errorDescription);
                return;
            }

            let titleImageVO = await self._getPhotostoryTitleImageVO(photostoryVO.id);
            if (!titleImageVO){
                alert("can't load image for photostory!");
            }

            $photostoryImageElem.attr("src", "../getImage.jsp?filename="+titleImageVO.thumbnail);
        });


        let $editorBoard = self.element.find('[data-role="photostory-editor-board"]');
        let $addTitleBlockBtn = self.element.find('[data-role="add-title-block-button"]');
        let $addImageBlockBtn = self.element.find('[data-role="add-image-block-button"]');
        let $addTextBlockBtn = self.element.find('[data-role="add-text-block-button"]');
        let $bbCodeHintButton = self.element.find('[data-role="bb-code-hint-button"]');

        $bbCodeHintButton.unbind();
        $bbCodeHintButton.click(function(){
            showBBCodeHintModal($modalAnchor);
        });

        self.PSItems = [];
        self.PSEditorVars = {
            maxOrder: 0
        };

        $addTitleBlockBtn.unbind();
        $addTitleBlockBtn.click(function(){
            let PSTitleTO = {
                order: self.PSEditorVars.maxOrder,
                titleText: "",
                itemType: "PS_ITEM_TITLE"
            };
            self.PSEditorVars.maxOrder += 1;
            self.PSItems.push(PSTitleTO);

            boardRefresh();
        });

        $addImageBlockBtn.unbind();
        $addImageBlockBtn.click(function(){
            let PSImageTO = {
                order: self.PSEditorVars.maxOrder,
                caption: "",
                itemType: "PS_ITEM_IMAGE",
                thumbnailFile: "",
                imageID: null
            };
            self.PSEditorVars.maxOrder += 1;
            self.PSItems.push(PSImageTO);

            boardRefresh();
        });

        $addTextBlockBtn.unbind();
        $addTextBlockBtn.click(function(){
            let PSTextTO = {
                order: self.PSEditorVars.maxOrder,
                text: "",
                itemType: "PS_ITEM_TEXT"
            };

            self.PSEditorVars.maxOrder += 1;
            self.PSItems.push(PSTextTO);

            boardRefresh();
        });

        let getElemIndexByOrder = function(order){
            for (let i = 0; i < self.PSItems.length; i++){
                let PSItem = self.PSItems[i];
                if (PSItem.order === order){
                    return i;
                }
            }
            return -1;
        }

        let getPhotostoryTO = function(){
            return {
                id: $idElem.val(),
                title: $titleElem.val(),
                tinyDescription: $tinyDescrElem.val(),
                description: $descrElem.val(),
                content: {PSItems: self.PSItems},
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };
        }

        let boardRefresh = function(){
            $editorBoard.html("");
            let boardHtml = "";

            self.PSItems.sort((itemA, itemB) => itemA.order - itemB.order);

            for (let PSItem of self.PSItems){

                let hPSItem = Handlebars.compile(PSItemTemplate);
                let PSItemHandlerCode = "";

                if (PSItem.itemType === "PS_ITEM_TITLE"){
                    let hPSItemHandler = Handlebars.compile(PSItemTitle);
                    PSItemHandlerCode = hPSItemHandler({
                        titleText: PSItem.titleText,
                        order: PSItem.order
                    });
                }
                if (PSItem.itemType === "PS_ITEM_IMAGE"){
                    let hPSIItemHandler = Handlebars.compile(PSItemImage);
                    PSItemHandlerCode = hPSIItemHandler({
                        imageCaption: PSItem.caption,
                        thumbnailFile: PSItem.thumbnailFile,
                        order: PSItem.order,
                        noFile: !PSItem.thumbnailFile
                    });
                }
                if (PSItem.itemType === "PS_ITEM_TEXT"){
                    let hPSIItemHandler = Handlebars.compile(PSItemText);
                    PSItemHandlerCode = hPSIItemHandler({
                        text: PSItem.text,
                        order: PSItem.order
                    });
                }

                boardHtml += hPSItem({order: PSItem.order, psitemCode: PSItemHandlerCode});
            }

            $editorBoard.html(boardHtml);

            let $trashcanButtons = $editorBoard.find('[data-role="psitem-trashcan"]');
            let $changeImageButtons = $editorBoard.find('[data-role="psitem-change-image"]');
            let $editTitleButtons = $editorBoard.find('[data-role="edit-title-button"]');
            let $editTextButtons = $editorBoard.find('[data-role="psitem-text-edit"]');
            let $editCaptionButtons = $editorBoard.find('[data-role="psitem-change-caption"]');

            let $orderUpButtons = $editorBoard.find('[data-role="psitem-order-moveup"]');
            let $orderDownButtons = $editorBoard.find('[data-role="psitem-order-movedown"]');

            $trashcanButtons.unbind();
            $trashcanButtons.click(function(){
                let order = $(this).data('order');
                let $removeElem = $editorBoard.find('[data-role="psitem-template"][data-order="'+order+'"]');
                $removeElem.remove();
                let itemIndex = getElemIndexByOrder(order);
                if (itemIndex !== -1){
                    self.PSItems.splice(itemIndex, 1);
                    let newMaxOrder = 0;
                    for (let i = 0; i < self.PSItems.length; i++){
                        let PSItem = self.PSItems[i];
                        if (PSItem.order >= itemIndex){
                            PSItem.order -= 1;
                        }
                        if (PSItem.order > newMaxOrder){
                            newMaxOrder = PSItem.order;
                        }
                    }
                    self.PSEditorVars.maxOrder = newMaxOrder + 1;
                }
                boardRefresh();
            });

            $changeImageButtons.unbind();
            $changeImageButtons.click(async function(){
                let order = $(this).data('order');

                let newImageData = await imageSelect($modalAnchor, photostoryVO.id, false, true);
                let imageThumbnail = newImageData.selectedThumbnail;
                let imageId = newImageData.selectedId;

                let $psItemImageElem = $editorBoard.find('[data-role="psitem-image"][data-order="' + order + '"]');
                $psItemImageElem.attr("src", "../getImage.jsp?filename="+imageThumbnail);
                $psItemImageElem.show();

                let itemIndex = getElemIndexByOrder(order);
                self.PSItems[itemIndex].thumbnailFile = imageThumbnail;
                self.PSItems[itemIndex].imageID = imageId;
            });

            $editTitleButtons.unbind();
            $editTitleButtons.click(function(){
                let order = $(this).data('order');
                let $titleTextEdit = $editorBoard.find('[data-role="title-text"][data-order="'+order+'"]');
                $titleTextEdit.TextEditor({charLimit: self._titleCharLimit});

                $titleTextEdit.unbind();
                $titleTextEdit.on("editcomplete", async function(){
                    let itemIndex = getElemIndexByOrder(order);
                    self.PSItems[itemIndex].titleText = $titleTextEdit.val();
                    await self._saveOrUpdatePhotostory(getPhotostoryTO());
                });
            });

            $editTextButtons.unbind();
            $editTextButtons.click(function(){
                let order = $(this).data('order');
                let $textTextarea = $editorBoard.find('[data-role="psitem-text-textarea"][data-order="'+order+'"]');
                $textTextarea.TextEditor({charLimit: self._textBlockLimit});

                $textTextarea.unbind();
                $textTextarea.on("editcomplete", async function(){
                    let itemIndex = getElemIndexByOrder(order);
                    self.PSItems[itemIndex].text = $textTextarea.val();
                    await self._saveOrUpdatePhotostory(getPhotostoryTO());
                });
            });

            $editCaptionButtons.unbind();
            $editCaptionButtons.click(function() {
                let order = $(this).data('order');
                let $captionTextEdit = $editorBoard.find('[data-role="psitem-image-caption"][data-order="' + order + '"]');

                $captionTextEdit.TextEditor({charLimit: self._imageCaptionLimit});

                $captionTextEdit.unbind();
                $captionTextEdit.on("editcomplete", async function () {
                    let itemIndex = getElemIndexByOrder(order);
                    self.PSItems[itemIndex].caption = $captionTextEdit.val();
                    await self._saveOrUpdatePhotostory(getPhotostoryTO());
                });
            });


            $orderUpButtons.unbind();
            $orderUpButtons.click(function(){
                // decrease order
                let order = $(this).data('order');

                if (order < 1){
                    return;
                }

                let itemCurrentIndex = getElemIndexByOrder(order);
                let prevItemIndex = getElemIndexByOrder(order-1);

                if (itemCurrentIndex !== -1 && prevItemIndex !== -1){
                    let currentOrderItem = self.PSItems[itemCurrentIndex];
                    let prevOrderItem = self.PSItems[prevItemIndex];

                    prevOrderItem.order = order;
                    currentOrderItem.order = order-1;
                    boardRefresh();
                }

            });

            $orderDownButtons.unbind();
            $orderDownButtons.click(function (){
                // increase order
                let order = $(this).data('order');

                if (order >= self.PSEditorVars.maxOrder-1){
                    return;
                }

                let itemCurrentIndex = getElemIndexByOrder(order);
                let itemNextIndex = getElemIndexByOrder(order+1);

                if (itemCurrentIndex !== -1 && itemNextIndex !== -1){
                    let currentOrderItem = self.PSItems[itemCurrentIndex];
                    let nextOrderItem = self.PSItems[itemNextIndex];

                    nextOrderItem.order = order;
                    currentOrderItem.order = order+1;
                    boardRefresh();
                }

            });

        };

        if (photostoryVO.content && photostoryVO.content.PSItems){
            self.PSItems = photostoryVO.content.PSItems;
            let newMaxOrder = -1;
            for (let i = 0; i < self.PSItems.length; i++){
                let PSItem = self.PSItems[i];
                if (PSItem.order > newMaxOrder){
                    newMaxOrder = PSItem.order;
                }
            }
            self.PSEditorVars.maxOrder = newMaxOrder + 1;
            boardRefresh();
        }

        $submitElem.unbind();
        $submitElem.click(await async function(){

            if (!checkCoordinates($gpsElem.val())){
                return;
            }

            let buttonText = spinButton($submitElem);

            let photostoryTO = getPhotostoryTO();

            await self._saveOrUpdatePhotostory(photostoryTO);
            $relationEditorDiv.RelationManager({attributionClass: 4, objectId: photostoryVO.id});

            unSpinButton($submitElem, buttonText);
        });

        $submitElemClose.unbind();
        $submitElemClose.click(await async function(){

            if (!checkCoordinates($gpsElem.val())){
                return;
            }

            let buttonText = spinButton($submitElemClose);

            let photostoryTO = getPhotostoryTO();

            let res = await self._saveOrUpdatePhotostory(photostoryTO);
            unSpinButton($submitElemClose, buttonText);
            if (res !== undefined || res !== null){
                self.element.html('');
                await self._display(self);
            }
        });

        $submitElemPreview.unbind();
        $submitElemPreview.click(async function(){
            if (!checkCoordinates($gpsElem.val())){
                return;
            }

            let buttonText = spinButton($submitElemPreview);

            let photostoryTO = getPhotostoryTO();

            let res = await self._saveOrUpdatePhotostory(photostoryTO);
            unSpinButton($submitElemPreview, buttonText);

            if (res !== undefined || res !== null){
                self.element.html('');
                await self._display(self);
            }

            window.open("../posts.jsp?collage="+$idElem.val(), "_blank");

        });

    },

    _getPhotostoryTitleImageVO: async function(photostoryId){
        let jsonAdminResponse = await $.ajax({
            url: 'jsonApi.jsp',
            method: 'POST',
            data: {data:photostoryId, action:"getPhotostoryTitleImageVO"}
        });

        let adminResponse = JSON.parse(jsonAdminResponse);

        if (adminResponse.success === false){
            alert("error getting image data for photostory: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }

    },

    _saveOrUpdatePhotostory: async function(photostoryTO){
        let self = this;

        let photostoryTOJson = JSON.stringify(photostoryTO);
        let res = await ajax({data:photostoryTOJson, action:"saveOrUpdatePhotostory"}, "error saving or updating photostory");
        if (photostoryTO.id !== undefined && photostoryTO.id !== null){
            let tagsTO = self.$tagEditorDiv.TagEditor('getTagTO');
            await ajax({action: "saveOrUpdateTags", guid: Cookies.get("guid"), data: JSON.stringify(tagsTO)}, "error saving tags");

            let photostoryVO = await ajax({data:photostoryTO.id, action:"getPhotostoryVOByPhotostoryId", guid: Cookies.get("guid")});

            let renderDiv = document.createElement("div");
            renderDiv.setAttribute("display","none");
            let $renderDiv = $(renderDiv);

            let hPhotostoryTemplate = Handlebars.compile(photostoryTemplate);

            let PSItems = [];
            if (photostoryVO.content && photostoryVO.content.PSItems){
                PSItems = photostoryVO.content.PSItems;
            }
            PSItems.sort((itemA, itemB) => itemA.order - itemB.order);
            for (let i = 0; i < PSItems.length; i++) {
                let PSItem = PSItems[i];
                if (PSItem.isText) {
                    PSItem.text = basicRender(PSItem.text, false);
                }
            }

            $renderDiv.html(hPhotostoryTemplate(
                {photostoryVO: photostoryVO, PSItems: PSItems }
            ));

            let preRender = $renderDiv.html();
            renderDiv.remove();

            let updateRenderRequest = {
                postId: photostoryTO.id,
                render: preRender
            };
            await ajax({data: JSON.stringify(updateRenderRequest), action:"updatePhotostoryRender", guid: Cookies.get("guid")});
        }
        return res;
    },


    _loadPhotostory: async function(photostoryId){
        let jsonAdminReponse = await $.ajax({
            url: 'jsonApi.jsp',
            method: 'POST',
            data: {data:photostoryId , action:"getPhotostoryVOByPhotostoryId", guid: Cookies.get("guid")}
        });

        let adminResponse = JSON.parse(jsonAdminReponse);

        if (adminResponse.success === false){
            alert("error loading photostory: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }
    },

    _display: async function(self, basicPostFilterTO, refreshSearch){
        // this is necessary to pass the function as parameter to postFilter()
        let __display = async function(self, basicPostFilterTO, refreshSearch){
            if (basicPostFilterTO) {
                basicPostFilterTO.userGuid = Cookies.get("guid");
            } else {
                basicPostFilterTO = {userGuid : Cookies.get("guid")};
            }

            let photostoryVOList = await ajax({action: "listAllPhotostoryVOs", data: JSON.stringify(basicPostFilterTO), guid: Cookies.get("guid")});

            let hPhotostoryEditSelect = Handlebars.compile(photostoryEditSelect);
            let demoUser = Cookies.get("privilegeLevelName") === "demo";
            self.element.html(hPhotostoryEditSelect({photostoryPosts: photostoryVOList, demoUser: demoUser}));


            let $searchAnchor = self.element.find('[data-role="search-anchor"]');
            postFilter($searchAnchor, basicPostFilterTO, __display, self, refreshSearch);

            let $photostoryPostEditButtons = self.element.find('[data-role="photostory-post-edit"]');
            let $photostoryDeleteButtons = self.element.find('[data-role="photostory-post-delete"]');
            let $createNewPhotostoryButton = self.element.find('[data-role="photostory-post-new"]');

            let $photostoryPostPublishToggle = self.element.find('[data-role="photostory-publish-toggle"]');

            let $photostoryDeleteConfirmModal = self.element.find('[data-role="delete-photostory-confirm"]');

            if (Cookies.get("privilegeLevelName") === "user"){
                $photostoryPostPublishToggle.prop("disabled", true);
                $photostoryDeleteButtons.prop("disabled", true);
                $photostoryPostEditButtons.prop("disabled", true);

                let myLogin = Cookies.get("login");
                let $ownButtons = self.element.find('[data-owner="'+myLogin+'"]');
                $ownButtons.prop("disabled", false);
            }

            $createNewPhotostoryButton.unbind();
            $createNewPhotostoryButton.click(await async function(){

                let buttonText = spinButton($createNewPhotostoryButton);

                // create photostory entity
                let photostoryTO = await self._getEmptyPhotostoryTO();
                let newId = await self._saveOrUpdatePhotostory(photostoryTO);

                // edit photostory entity
                let photostoryVO = await self._getEmptyPhotostoryVO(newId);
                await self._edit(self.element, photostoryVO, self);

                unSpinButton($createNewPhotostoryButton, buttonText);
            });

            $photostoryPostEditButtons.unbind();
            $photostoryPostEditButtons.click(await async function(){
                let photostoryId = $(this).data('id');
                let photostoryVO = await self._loadPhotostory(photostoryId);
                await self._edit(self.element, photostoryVO, self);
            });

            $photostoryPostPublishToggle.unbind();
            $photostoryPostPublishToggle.click(await async function(){
                let photostoryId = $(this).data('id');

                let toggleResultJson = await $.ajax({
                    url: 'jsonApi.jsp',
                    method: 'POST',
                    data: {action: "togglePhotostoryPublish", guid: Cookies.get("guid"), data: photostoryId}
                });

                var toggleResult;
                if (toggleResultJson){ toggleResult = JSON.parse(toggleResultJson); }
                if (!toggleResult || !toggleResult.success) {
                    alert("error toggling publish status: " + toggleResult.errorDescription);
                } else {
                    await self._display(self, basicPostFilterTO, true);
                }
            });

            $photostoryDeleteButtons.unbind();
            $photostoryDeleteButtons.click(await async function(){
                let photostoryId = $(this).data('id');

                $photostoryDeleteConfirmModal.modal('show');

                let $proceedButton = self.element.find('[data-role="delete-confirm"]');
                $proceedButton.unbind();
                $proceedButton.click(await async function(){
                    let deleteResultJson = await $.ajax({
                        url: 'jsonApi.jsp',
                        method: 'POST',
                        data: {action: "deletePhotostory", guid: Cookies.get("guid"), data: photostoryId}
                    });

                    if (deleteResultJson) {
                        let deleteResult = JSON.parse(deleteResultJson);
                        if (deleteResult.success === true) {
                            $photostoryDeleteConfirmModal.modal('hide');
                            self._display(self);
                        } else {
                            alert("error deleting: "+deleteResult.errorDescription);
                        }
                    }
                });
            });

            if (isDemo()){
                $photostoryPostPublishToggle.prop("disabled", true);
                $createNewPhotostoryButton.prop("disabled", true);
            }
        };
        await __display(self, basicPostFilterTO, refreshSearch);


    }
});
