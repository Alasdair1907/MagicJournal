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
        let testUser = Cookies.get("privilegeLevelName") === "test";
        element.html(hArticleNewOrEdit({articleVO: articleVO, testUser: testUser}));

        let $idElem = element.find('[data-role="data-id"]');
        let $titleElem = element.find('[data-role="data-title"]');
        let $descrElem = element.find('[data-role=data-description]');
        let $textElem = element.find('[data-role="data-article-text"]');
        let $gpsElem = element.find('[data-role=data-gps-coordinates]');
        let $submitElem = element.find('[data-role="data-article-save-or-update"]');
        let $submitElemClose = element.find('[data-role="data-article-save-or-update-close"]');
        let $articleImageElem = element.find('[data-role="article-title-image"]'); // img with the image
        let $tagEditorDiv = element.find('[data-role="article-tag-editor"]');
        let $relationEditorDiv = element.find('[data-role="article-relation-editor"]');
        let $helperImageManager = element.find('[data-role="article-helper-image-manager"]');

        let $modalAnchor = self.element.find('[data-role="modal-anchor"]');
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
                url: '/admin/jsonApi.jsp',
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

            $articleImageElem.attr("src", "/getImage.jsp?filename="+titleImageVO.thumbnail);
        });

        $submitElem.unbind();
        $submitElem.click(await async function(){

            let buttonText = spinButton($submitElem);

            let articleTO = {
                id: $idElem.val(),
                title: $titleElem.val(),
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

            let buttonText = spinButton($submitElemClose);

            let articleTO = {
                id: $idElem.val(),
                title: $titleElem.val(),
                description: $descrElem.val(),
                articleText: $textElem.val(),
                gpsCoordinates: $gpsElem.val(),
                sessionGuid: Cookies.get("guid")
            };

            let res = await self._saveOrUpdateArticle(articleTO);
            unSpinButton($submitElemClose, buttonText);
            if (res !== undefined || res !== null){
                await self._display(self);
            }
        });

        // image select

        let $imageInsertButton = self.element.find('[data-role="image-insert-button"]');

        $imageInsertButton.unbind();
        $imageInsertButton.click(await async function(){
            let currentText = $textElem.val();
            let selectedImgId = await imageSelect($modalAnchor, articleVO.id);
            currentText = currentText + "\n[img id=" + selectedImgId + "]\n";
            $textElem.val(currentText);
        });
    },

    _getArticleTitleImageVO: async function(articleId){
        let jsonAdminResponse = await $.ajax({
            url: '/admin/jsonApi.jsp',
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
            url: '/admin/jsonApi.jsp',
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

    _display: async function(self){

        let adminResponseJson = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {action: "listAllArticleVOs", guid: Cookies.get("guid")}
        });

        let adminResponse = adminResponseJson ? JSON.parse(adminResponseJson) : {data: null};

        if (adminResponse.success === false){
            alert("can't list articles: "+adminResponse.errorDescription);
        } else {
            let hArticleEditSelect = Handlebars.compile(articleEditSelect);
            let testUser = Cookies.get("privilegeLevelName") === "test";
            self.element.html(hArticleEditSelect({articlePosts: adminResponse.data, testUser: testUser}));

            let $articlePostEditButtons = self.element.find('[data-role="article-post-edit"]');
            let $articleDeleteButtons = self.element.find('[data-role="article-post-delete"]');
            let $createNewArticleButton = self.element.find('[data-role="article-post-new"]');

            let $articlePostEditForm = self.element.find('[data-role="article-post-new-edit"]');
            let $articlePostPublishToggle = self.element.find('[data-role="article-publish-toggle"]');

            let $articleDeleteConfirmModal = self.element.find('[data-role="delete-article-confirm"]');

            $createNewArticleButton.unbind();
            $createNewArticleButton.click(await async function(){

                let buttonText = spinButton($createNewArticleButton);

                // create article entity
                let articleTO = await self._getEmptyArticleTO();
                let newId = await self._saveOrUpdateArticle(articleTO);

                // edit article entity
                let articleVO = await self._getEmptyArticleVO(newId);
                await self._edit($articlePostEditForm, articleVO, self);

                unSpinButton($createNewArticleButton, buttonText);
            });

            $articlePostEditButtons.unbind();
            $articlePostEditButtons.click(await async function(){
                let articleId = $(this).data('id');
                let articleVO = await self._loadArticle(articleId);
                await self._edit($articlePostEditForm, articleVO, self);
            });

            $articlePostPublishToggle.unbind();
            $articlePostPublishToggle.click(await async function(){
                let articleId = $(this).data('id');

                let toggleResultJson = await $.ajax({
                    url: '/admin/jsonApi.jsp',
                    method: 'POST',
                    data: {action: "toggleArticlePublish", guid: Cookies.get("guid"), data: articleId}
                });

                var toggleResult;
                if (toggleResultJson){ toggleResult = JSON.parse(toggleResultJson); }
                if (!toggleResult || !toggleResult.success) {
                    alert("error toggling publish status: " + toggleResult.errorDescription);
                } else {
                    await self._display(self);
                }
            });

            $articleDeleteButtons.unbind();
            $articleDeleteButtons.click(await async function(){
                let articleId = $(this).data('id');

                $articleDeleteConfirmModal.modal();

                let $proceedButton = self.element.find('[data-role="delete-confirm"]');
                $proceedButton.unbind();
                $proceedButton.click(await async function(){
                    let deleteResultJson = await $.ajax({
                        url: '/admin/jsonApi.jsp',
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


        }
    }
});