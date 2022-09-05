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


$.widget("admin.articlesWidget", {
    _create: function(){
        let self = this;
        self._display(self);

    },
    _init: function(){
        let self = this;
        self._display(self);
    },

    _getEmptyArticleVO: function(id){

        let titleImageVO = {
            preview: "imgAdminPlaceholder.png"
        };

        return {
            id: id,
            title: "",
            description: "",
            gpsCoordinates: "",
            articleText: "",
            titleImageVO: titleImageVO
        };
    },

    _getEmptyArticleTO: function() {
        return {
            id: null,
            title: null,
            description: null,
            gpsCoordinates: null,
            articleText: null,
            sessionGuid: Cookies.get("guid")
        };
    },

    _edit: async function(element, articleVO, self){
        let hArticleNewOrEdit = Handlebars.compile(articleNewOrEdit);
        let demoUser = Cookies.get("privilegeLevelName") === "demo";
        element.html(hArticleNewOrEdit({articleVO: articleVO, demoUser: demoUser}));

        let $modalAnchor = self.element.find('[data-role="modal-anchor"]');
        let $idElem = element.find('[data-role="data-id"]');
        let $titleElem = element.find('[data-role="data-title"]');
        let $tinyDescrElem = element.find('[data-role="data-tinydescription"]');
        let $descrElem = element.find('[data-role=data-description]');
        let $textElem = element.find('[data-role="data-article-text"]');

        let $gpsElem = element.find('[data-role=data-gps-coordinates]');
        let $coordinatesSelectLink = element.find('[data-role="select-on-map"]');
        $coordinatesSelectLink.click(await async function(){
            await mapPick($modalAnchor, $gpsElem, $gpsElem.val());
        });

        let $submitElem = element.find('[data-role="data-article-save-or-update"]');
        let $submitElemClose = element.find('[data-role="data-article-save-or-update-close"]');
        let $submitElemPreview = element.find('[data-role="data-article-save-and-preview"]');

        let $articleImageElem = element.find('[data-role="article-title-image"]'); // img with the image
        let $tagEditorDiv = element.find('[data-role="article-tag-editor"]');
        let $relationEditorDiv = element.find('[data-role="article-relation-editor"]');
        let $helperImageManager = element.find('[data-role="article-helper-image-manager"]');
        let $articleUpdateImageButton = element.find('[data-role="data-article-update-image"]');

        $tagEditorDiv.TagEditor({attributionClass: 2, objectId: articleVO.id});
        $relationEditorDiv.RelationManager({attributionClass: 2, objectId: articleVO.id});
        $helperImageManager.ImageManager({attributionClass: 2, objectId: articleVO.id});


        $articleUpdateImageButton.unbind();
        $articleUpdateImageButton.click(await async function(){

            let newImageId = await imageSelect($modalAnchor, articleVO.id);

            if (!newImageId){
                return;
            }

            let articleTo = self._getEmptyArticleTO();
            articleTo.id = articleVO.id;
            articleTo.titleImageId = newImageId;

            let jsonAdminResponse = await $.ajax({
                url: 'jsonApi.jsp',
                method: 'POST',
                data: {data: JSON.stringify(articleTo), guid: Cookies.get("guid"), action:"setArticleTitleImageId"}
            });

            let adminResponse = JSON.parse(jsonAdminResponse);
            if (adminResponse.success === false){
                alert("error updating image ID: "+adminResponse.errorDescription);
                return;
            }

            let titleImageVO = await self._getArticleTitleImageVO(articleVO.id);
            if (!titleImageVO){
                alert("can't load image for article!");
            }

            $articleImageElem.attr("src", "../getImage.jsp?filename="+titleImageVO.thumbnail);
        });

        $submitElem.unbind();
        $submitElem.click(await async function(){

            if (!checkCoordinates($gpsElem.val())){
                return;
            }

            let buttonText = spinButton($submitElem);

            let articleTO = {
                id: $idElem.val(),
                title: $titleElem.val(),
                tinyDescription: $tinyDescrElem.val(),
                description: $descrElem.val(),
                articleText: $textElem.val(),
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };

            await self._saveOrUpdateArticle(articleTO);
            $relationEditorDiv.RelationManager({attributionClass: 2, objectId: articleVO.id});

            unSpinButton($submitElem, buttonText);
        });

        $submitElemClose.unbind();
        $submitElemClose.click(await async function(){

            if (!checkCoordinates($gpsElem.val())){
                return;
            }

            let buttonText = spinButton($submitElemClose);

            let articleTO = {
                id: $idElem.val(),
                title: $titleElem.val(),
                tinyDescription: $tinyDescrElem.val(),
                description: $descrElem.val(),
                articleText: $textElem.val(),
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };

            let res = await self._saveOrUpdateArticle(articleTO);
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

            let articleTO = {
                id: $idElem.val(),
                title: $titleElem.val(),
                tinyDescription: $tinyDescrElem.val(),
                description: $descrElem.val(),
                articleText: $textElem.val(),
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };

            let res = await self._saveOrUpdateArticle(articleTO);
            unSpinButton($submitElemPreview, buttonText);

            if (res !== undefined || res !== null){
                self.element.html('');
                await self._display(self);
            }

            window.open("../posts.jsp?article="+$idElem.val(), "_blank");

        });

        // image select

        let $imageInsertButton = self.element.find('[data-role="image-insert-button"]');
        let $fileInsertbutton = self.element.find('[data-role="file-insert-button"]');
        let $bbCodeHintButton = self.element.find('[data-role="bb-code-hint-button"]');

        $imageInsertButton.unbind();
        $imageInsertButton.click(await async function(){
            let textAreaPosition = $textElem.prop("selectionStart");
            let selectedImgId = await imageSelect($modalAnchor, articleVO.id);
            let bbCode = "[img id=" + selectedImgId + "]";
            insertAtPosition($textElem, textAreaPosition, bbCode);
        });

        $fileInsertbutton.unbind();
        $fileInsertbutton.click(await async function(){
            let textAreaPosition = $textElem.prop("selectionStart");
            let selectedFileId = await fileSelect($modalAnchor);
            let bbCode = "[file id=" + selectedFileId + "]";
            insertAtPosition($textElem, textAreaPosition, bbCode);
        });

        $bbCodeHintButton.unbind();
        $bbCodeHintButton.click(function(){
            showBBCodeHintModal($modalAnchor);
        });
    },

    _getArticleTitleImageVO: async function(articleId){
        let jsonAdminResponse = await $.ajax({
            url: 'jsonApi.jsp',
            method: 'POST',
            data: {data:articleId, action:"getArticleTitleImageVO"}
        });

        let adminResponse = JSON.parse(jsonAdminResponse);

        if (adminResponse.success === false){
            alert("error getting image data for article: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }

    },

    _saveOrUpdateArticle: async function(articleTO){
        let articleTOJson = JSON.stringify(articleTO);
        return await ajax({data:articleTOJson, action:"saveOrUpdateArticle"}, "error saving or updating article");
    },

    _loadArticle: async function(articleId){
        let jsonAdminReponse = await $.ajax({
            url: 'jsonApi.jsp',
            method: 'POST',
            data: {data:articleId , action:"getArticleVOByArticleId"}
        });

        let adminResponse = JSON.parse(jsonAdminReponse);

        if (adminResponse.success === false){
            alert("error loading article: "+adminResponse.errorDescription);
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

            let articleVOList = await ajax({action: "listAllArticleVOs", data: JSON.stringify(basicPostFilterTO), guid: Cookies.get("guid")});

            let hArticleEditSelect = Handlebars.compile(articleEditSelect);
            let demoUser = Cookies.get("privilegeLevelName") === "demo";
            self.element.html(hArticleEditSelect({articlePosts: articleVOList, demoUser: demoUser}));


            let $searchAnchor = self.element.find('[data-role="search-anchor"]');
            postFilter($searchAnchor, basicPostFilterTO, __display, self, refreshSearch);

            let $articlePostEditButtons = self.element.find('[data-role="article-post-edit"]');
            let $articleDeleteButtons = self.element.find('[data-role="article-post-delete"]');
            let $createNewArticleButton = self.element.find('[data-role="article-post-new"]');

            let $articlePostPublishToggle = self.element.find('[data-role="article-publish-toggle"]');

            let $articleDeleteConfirmModal = self.element.find('[data-role="delete-article-confirm"]');

            if (Cookies.get("privilegeLevelName") === "user"){
                $articlePostPublishToggle.prop("disabled", true);
                $articleDeleteButtons.prop("disabled", true);
                $articlePostEditButtons.prop("disabled", true);

                let myLogin = Cookies.get("login");
                let $ownButtons = self.element.find('[data-owner="'+myLogin+'"]');
                $ownButtons.prop("disabled", false);
            }

            $createNewArticleButton.unbind();
            $createNewArticleButton.click(await async function(){

                let buttonText = spinButton($createNewArticleButton);

                // create article entity
                let articleTO = await self._getEmptyArticleTO();
                let newId = await self._saveOrUpdateArticle(articleTO);

                // edit article entity
                let articleVO = await self._getEmptyArticleVO(newId);
                await self._edit(self.element, articleVO, self);

                unSpinButton($createNewArticleButton, buttonText);
            });

            $articlePostEditButtons.unbind();
            $articlePostEditButtons.click(await async function(){
                let articleId = $(this).data('id');
                let articleVO = await self._loadArticle(articleId);
                await self._edit(self.element, articleVO, self);
            });

            $articlePostPublishToggle.unbind();
            $articlePostPublishToggle.click(await async function(){
                let articleId = $(this).data('id');

                let toggleResultJson = await $.ajax({
                    url: 'jsonApi.jsp',
                    method: 'POST',
                    data: {action: "toggleArticlePublish", guid: Cookies.get("guid"), data: articleId}
                });

                var toggleResult;
                if (toggleResultJson){ toggleResult = JSON.parse(toggleResultJson); }
                if (!toggleResult || !toggleResult.success) {
                    alert("error toggling publish status: " + toggleResult.errorDescription);
                } else {
                    await self._display(self, basicPostFilterTO, true);
                }
            });

            $articleDeleteButtons.unbind();
            $articleDeleteButtons.click(await async function(){
                let articleId = $(this).data('id');

                $articleDeleteConfirmModal.modal('show');

                let $proceedButton = self.element.find('[data-role="delete-confirm"]');
                $proceedButton.unbind();
                $proceedButton.click(await async function(){
                    let deleteResultJson = await $.ajax({
                        url: 'jsonApi.jsp',
                        method: 'POST',
                        data: {action: "deleteArticle", guid: Cookies.get("guid"), data: articleId}
                    });

                    if (deleteResultJson) {
                        let deleteResult = JSON.parse(deleteResultJson);
                        if (deleteResult.success === true) {
                            $articleDeleteConfirmModal.modal('hide');
                            self._display(self);
                        } else {
                            alert("error deleting: "+deleteResult.errorDescription);
                        }
                    }
                });
            });

            if (isDemo()){
                $articlePostPublishToggle.prop("disabled", true);
                $createNewArticleButton.prop("disabled", true);
            }
        };
        await __display(self, basicPostFilterTO, refreshSearch);


    }
});
