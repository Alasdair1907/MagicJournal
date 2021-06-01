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

/**
 *
 * modalJQueryElement element where modal html will be placed
 * ID of selected image file
 */
let imageSelect = async function($modalJQueryElement, articleId){

    $modalJQueryElement.html(imageSelectionModal);

    let $imageSelectModal = $modalJQueryElement.find('[data-role="image-select-modal"]');
    let $imageSelectMain = $modalJQueryElement.find('[data-role="modal-imageselect-main"]');

    let $imageSelectFilterAnchor = $modalJQueryElement.find('[data-role="modal-post-filter"]');

    $imageSelectModal.modal('show');

    let $articleSrc = $modalJQueryElement.find('[data-role="image-select-article"]');
    let $photoSrc = $modalJQueryElement.find('[data-role="image-select-photo"]');
    let $gallerySrc = $modalJQueryElement.find('[data-role="image-select-gallery"]');

    let $imageInsertSubmit = $modalJQueryElement.find('[data-role="image-insert"]');
    let $selectedImgInfo = $modalJQueryElement.find('[data-role="selected-img-info"]');

    let $imageSelectPreviewImg = $modalJQueryElement.find('[data-role="image-select-preview-img"]');
    let $recentGalleriesAnchor = $modalJQueryElement.find('[data-role="recent-galleries-anchor"]');

    let refreshRecentGalleries = async function(){
        let recentGalleries = getRecentGalleries();

        if (!recentGalleries || recentGalleries.length < 1){
            return;
        }

        let hRecentGalleriesTemplate = Handlebars.compile(recentGalleriesTemplate);
        $recentGalleriesAnchor.html(hRecentGalleriesTemplate({recentGalleries: recentGalleries}));

        let $recentGalleryLinks = $recentGalleriesAnchor.find('[data-role="recent-gallery-select"]');
        $recentGalleryLinks.click(async function(){
            let recentGalleryId = $(this).data("id");
            await showGallery(recentGalleryId);
        });
    };

    let showGallery = async function(chosenGalleryId){

        refreshRecentGalleries();

        $imageSelectMain.html(modalSpinner);
        let imageVOList = await getImageVOList(chosenGalleryId, 0);
        $imageSelectMain.html("");

        let hImageVOListDisplay = Handlebars.compile(imageVOListDisplay);
        $imageSelectMain.html(hImageVOListDisplay({imageVOList: imageVOList}));

        let $imageSelectList = $modalJQueryElement.find('[data-role="image-select-list"]');

        $imageSelectList.unbind();
        $imageSelectList.click(function(){
            $imageInsertSubmit.prop("disabled", false);
            $imageSelectList.removeClass("div-image-selected");

            let imageId = $(this).data("id");
            let previewFile = $(this).data("preview");
            $(this).addClass("div-image-selected");

            $selectedImgInfo.data("id", imageId);

            $imageSelectPreviewImg.attr("src", "../getImage.jsp?filename="+previewFile);
            $imageSelectPreviewImg.show();
        });
    };

    refreshRecentGalleries();

    if (articleId !== undefined) {
        $articleSrc.unbind();
        $articleSrc.click(await async function () {
            $imageSelectFilterAnchor.html("");
            $imageSelectMain.html(modalSpinner);
            let imageVOList = await getImageVOList(articleId, 2);
            $imageSelectMain.html("");

            let hImageVOListDisplay = Handlebars.compile(imageVOListDisplay);
            $imageSelectMain.html(hImageVOListDisplay({imageVOList: imageVOList}));

            let $imageSelectList = $modalJQueryElement.find('[data-role="image-select-list"]');

            $imageSelectList.unbind();
            $imageSelectList.click(function () {

                if (!isDemo()){
                    $imageInsertSubmit.prop("disabled", false);
                }
                $imageSelectList.removeClass("div-image-selected");

                let imageId = $(this).data("id");
                let previewFile = $(this).data("preview");
                $(this).addClass("div-image-selected");

                $selectedImgInfo.data("id", imageId);

                $imageSelectPreviewImg.attr("src", "../getImage.jsp?filename=" + previewFile);
                $imageSelectPreviewImg.show();
            });

        });
    }

    $photoSrc.unbind();
    $photoSrc.click(await async function(){

        let photoDisplay = async function(ignore, basicPostFilterTO){
            $imageSelectMain.html(modalSpinner);

            basicPostFilterTO.userGuid = Cookies.get("guid");
            let photoVOList = await ajax({action: "listAllPhotoVOs", data: JSON.stringify(basicPostFilterTO)});

            let hPhotoVOListDisplay = Handlebars.compile(photoVOListDisplay);
            $imageSelectMain.html(hPhotoVOListDisplay({photoVOList: photoVOList}));

            let $imageSelectList = $modalJQueryElement.find('[data-role="image-select-list"]');

            $imageSelectList.unbind();
            $imageSelectList.click(function(){
                $imageInsertSubmit.prop("disabled", false);
                $imageSelectList.removeClass("div-image-selected");

                let imageId = $(this).data("id");
                let previewFile = $(this).data("preview");
                $(this).addClass("div-image-selected");

                $selectedImgInfo.data("id", imageId);

                $imageSelectPreviewImg.attr("src", "../getImage.jsp?filename="+previewFile);
                $imageSelectPreviewImg.show();
            });
        };

        $imageSelectMain.html('');
        await postFilter($imageSelectFilterAnchor, null, photoDisplay, null);
    });

    $gallerySrc.unbind();
    $gallerySrc.click(await async function(){

        let galleryDisplay = async function(ignore, basicPostFilterTO){
            $imageSelectMain.html(modalSpinner);

            basicPostFilterTO.userGuid = Cookies.get("guid");
            let galleryVOList = await ajax({action: "listAllGalleryVOs", data: JSON.stringify(basicPostFilterTO)});

            let hGalleryVOListDisplay = Handlebars.compile(galleryVOListDisplay);
            $imageSelectMain.html(hGalleryVOListDisplay({galleryVOList: galleryVOList}));

            let $gallerySelectButtons = $modalJQueryElement.find('[data-role="image-select-gallery-select"]');

            $gallerySelectButtons.unbind();
            $gallerySelectButtons.click(await async function(){
                let chosenGalleryId = $(this).data("id");
                let chosenGalleryTitle = $(this).data("title");

                pushRecentGallery(chosenGalleryId, chosenGalleryTitle);
                showGallery(chosenGalleryId);
            });
        };

        $imageSelectMain.html("");
        postFilter($imageSelectFilterAnchor, null, galleryDisplay, null);
    });

    let selectedId = null;

    $imageInsertSubmit.unbind();
    $imageInsertSubmit.click(function(){
        selectedId = $selectedImgInfo.data("id");
        $imageSelectMain.html('');
        $imageInsertSubmit.prop("disabled","disabled");
        $imageSelectModal.modal('hide');
    });

    while (selectedId === null) {
        await new Promise(r => setTimeout(r, 50));
        if (selectedId) {
            return selectedId;
        }
    }

};

let getImageVOList = async function(objectId, imageAttributionClass){

    let imageTO = {imageAttributionClass: imageAttributionClass, parentObjectId: objectId, sessionGuid: null};
    let imageTOJson = JSON.stringify(imageTO);

    let jsonAdminResponse = await $.ajax({
        url: 'jsonApi.jsp',
        method: 'POST',
        data: {data: imageTOJson, action: "listImageVOs"}
    });

    let adminResponse = JSON.parse(jsonAdminResponse);

    if (adminResponse.success === false){
        alert("error getting image list for gallery: "+adminResponse.errorDescription);
    } else {
        return adminResponse.data;
    }
};

let pushRecentGallery = function(galleryId, galleryTitle){
    let recentJson = Cookies.get("recent");

    let newRecentGallery = {galleryId: galleryId, galleryTitle: galleryTitle};

    if (!recentJson){
        Cookies.set("recent", JSON.stringify([newRecentGallery]));
        return;
    }

    let recentGalleries = JSON.parse(recentJson);

    for (let i = recentGalleries.length-1; i >= 0; i--){
        if (recentGalleries[i].galleryId === galleryId){
            recentGalleries.splice(i, 1);
        }
    }

    let recentGalleriesMax = 2; // this number DOES NOT include gallery that's gonna be added!
    if (recentGalleries.length > recentGalleriesMax){
        recentGalleries.splice(recentGalleriesMax - 1, recentGalleries.length - recentGalleriesMax);
    }

    recentGalleries.unshift(newRecentGallery);
    Cookies.set("recent", JSON.stringify(recentGalleries));
};

let getRecentGalleries = function(){
    let recentJson = Cookies.get("recent");

    if (!recentJson){
        return [];
    }

    return JSON.parse(recentJson);
};

let imageSelectionModal = `

<div class="modal" data-role="image-select-modal">
    <div class="modal-dialog modal-window-huge">
        <div class="modal-content">

            <!-- Modal Header -->
            <div class="modal-header">
                <span class="modal-h1">Select an image to insert</span>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>

            <!-- Modal body -->
            <div class="modal-body">
                <span class="modal-title">Select source</span><br />

                <button class="btn btn-secondary btn-std btn-vertical" data-role="image-select-article" data-id="2">This article</button>
                <button class="btn btn-secondary btn-std btn-vertical" data-role="image-select-photo" data-id="1">Photos</button>
                <button class="btn btn-secondary btn-std btn-vertical" data-role="image-select-gallery" data-id="0">Galleries</button>
                
                <hr class="hr-black">
                
                <div data-role="recent-galleries-anchor"></div>
                
                <div data-role="modal-post-filter"></div><br />

                <input type="hidden" data-role="selected-img-info">
                <div data-role="modal-imageselect-main"></div>

            </div>

            <!-- Preview -->

            <div class="image-select-preview-div">
                <img src="" data-role="image-select-preview-img" class="image-select-preview-img" style="display: none">
            </div>

            <!-- Modal footer -->
            <div class="modal-footer">
                <button type="button" class="btn btn-success btn-std" data-role="image-insert" disabled="disabled">Insert selected image</button>
                <button type="button" class="btn btn-primary btn-std" data-bs-dismiss="modal">Cancel</button>
            </div>

        </div>
    </div>
</div>
`;

let modalSpinner = `
<span class="width-100-pc center-text center-vertical"><i class="fas fa-cog fa-spin"></i></span>
`;

let recentGalleriesTemplate = `
<span class="modal-title">Recent galleries: </span>

{{#each recentGalleries}}
    <span class="modal-link" data-role="recent-gallery-select" data-id="{{this.galleryId}}">{{this.galleryTitle}}</span>
{{/each}}

<hr class="hr-black">
`;

let imageVOListDisplay = `
{{#each imageVOList}}
<div data-id="{{this.thisObjId}}" data-preview="{{this.preview}}" data-role="image-select-list" class="image-select-tiles" style="background-image:url('../getImage.jsp?filename={{this.thumbnail}}');">&nbsp;</div>
{{/each}}
`;

let photoVOListDisplay = `
{{#each photoVOList}}
<div data-id="{{this.imageVO.thisObjId}}" data-preview="{{this.imageVO.preview}}" data-role="image-select-list" class="image-select-tiles" style="background-image:url('../getImage.jsp?filename={{this.imageVO.thumbnail}}');">&nbsp;</div>
{{/each}}
`;

let galleryVOListDisplay = `
<table class="width-100-pc modal-list">
    <tr>
        <td class="table-lgray table-lgray-heading">Post Title</td>
        <td class="table-lgray table-lgray-heading">Author</td>
        <td class="table-lgray table-lgray-heading">Date</td>
        <td class="table-lgray table-lgray-heading"></td>
    </tr>

    {{#each galleryVOList}}
    <tr>
        <td class="table-lgray">{{this.title}}</td>
        <td class="table-lgray">{{this.authorVO.displayName}} ({{this.authorVO.login}})</td>
        <td class="table-lgray">{{this.creationDateStr}}</td>
        <td class="table-lgray center-text">
            <button type="button" class="btn btn-success btn-std" data-role="image-select-gallery-select" data-id="{{this.id}}" data-title="{{this.title}}">Select</button>            
        </td>
    </tr>
    {{/each}}
</table>
`;
