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

$.widget("admin.ImageManager", {

    _template: `
<div class="item-container transparent width-100-pc">
<span class="item-container-heading">Image Manager</span>

<table style="width:100%;">
<tr>
<td style="width:50%;">

<table style="width:100%;">
<tr>
<td class="td-tall" style="width:33%;">
<span class="text">Upload images:</span><br />
<span class="text"><input type="file" data-role="data-file" multiple/></span>
</td>

<td class="td-tall" style="width:67%;">
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-image-upload" {{#if demoUser}}disabled="disabled"{{/if}}>Upload</button>
</td>

</tr>

<tr>
<td class="td-tall"><span class="text">Image Title:</span></td><td class="td-tall"><input type="text" data-role="selected-img-title" class="input width-full" disabled="disabled"></td>
</tr>

<tr>
<td class="td-tall"><span class="text">Coordinates: (latitude, longitude)</span>
 <span data-role="select-on-map" class="link">select on map</span>
</td><td class="td-tall"><input type="text" data-role="selected-img-gps" class="input width-full" disabled="disabled"></td>
</tr>

<tr>
<td class="td-tall"><span class="text">Ordering number:</span></td><td class="td-tall"><input type="text" data-role="selected-img-order-number" class="input width-full" disabled="disabled"></td>
</tr>

<tr>
<td class="td-tall"><button type="button" class="btn btn-danger btn-std" data-role="selected-img-delete" disabled="disabled">Delete Image</button> </td>
<td class="td-tall"><button type="button" class="btn btn-light btn-std" data-role="selected-img-update" disabled="disabled">Save Changes</button> </td></tr>
</table>
</td>

<td>
<img src="../getImage.jsp?filename=imgAdminPlaceholder.png" data-role="selected-img" class="selected-img" alt="Select an image to preview.">


</div>
</td>

</tr>
</table>

<br />
<div data-role="image-list"></div>
</div>
<div data-role="img-manager-modal-anchor"></div>
        `,

    _imageListTemplate: `
    <div></div>
    {{#each imageVOList}}
    <div data-id="{{this.thisObjId}}" data-preview="{{this.preview}}" data-role="gallery-images" class="image-manager-tile" style="background-image:url('../getImage.jsp?filename={{this.thumbnail}}');">&nbsp;</div>
    {{/each}}
    </div>
    `,

    _create: async function () {
        // attributionClass: 0, 2
        // objectId long

        let self = this;
        let ops = this.options;

        await self._display(self, ops);
    },
    _init: async function () {

        let self = this;
        let ops = this.options;

    },

    _getImageVOList: async function(objectId, postAttribution){

        let imageTO = {imageAttributionClass: postAttribution, parentObjectId: objectId, sessionGuid: null};
        let imageTOJson = JSON.stringify(imageTO);

        let jsonAdminResponse = await $.ajax({
            url: '/admin/jsonApi.jsp',
            method: 'POST',
            data: {data: imageTOJson, action: "listImageVOs"}
        });

        let adminResponse = JSON.parse(jsonAdminResponse);

        if (adminResponse.success === false){
            alert("error getting image list: "+adminResponse.errorDescription);
        } else {
            return adminResponse.data;
        }
    },

    _refreshImageList: async function(self, ops){

        let objectId = ops.objectId;
        let attributionClass = ops.attributionClass;

        let $imageListElem = self.element.find('[data-role="image-list"]');

        let imageVOList = await self._getImageVOList(objectId, attributionClass);
        if (!imageVOList){
            alert("can't load imageVOList for object "+objectId+" of class "+attributionClass);
        }

        let enabled = false;

        let hImageListTemplate = Handlebars.compile(this._imageListTemplate);
        $imageListElem.html(hImageListTemplate({imageVOList: imageVOList}));

        let $galleryImages = self.element.find('[data-role="gallery-images"]');
        let $selectedImgElem = self.element.find('[data-role="selected-img"]');

        let $selectedImgTitle = self.element.find('[data-role="selected-img-title"]');
        let $selectedImgGps = self.element.find('[data-role="selected-img-gps"]');
        let $selectedImgOrder = self.element.find('[data-role="selected-img-order-number"]');
        let $selectedImgDelete = self.element.find('[data-role="selected-img-delete"]');
        let $selectedImgUpdate = self.element.find('[data-role="selected-img-update"]');

        let $mapPickLink = self.element.find('[data-role="select-on-map"]');
        let $mapPickModalAnchor = self.element.find('[data-role="img-manager-modal-anchor"]');
        $mapPickLink.click(await async function(){
            if (enabled){
                await mapPick($mapPickModalAnchor, $selectedImgGps, $selectedImgGps.val());
            }
        });

        $galleryImages.unbind();
        $galleryImages.click(await async function () {

            $galleryImages.removeClass("div-image-selected");

            let imageId = $(this).data("id");
            let previewFile = $(this).data("preview");
            $(this).addClass("div-image-selected");

            $selectedImgElem.attr("src", "../getImage.jsp?filename="+previewFile);
            $selectedImgElem.data("id", imageId);


            $selectedImgTitle.prop("disabled", false);
            $selectedImgGps.prop("disabled", false);
            $selectedImgOrder.prop("disabled", false);

            enabled = true;

            if (!isDemo()) {
                $selectedImgDelete.prop("disabled", false);
                $selectedImgUpdate.prop("disabled", false);
            }

            let res = await ajax({data: imageId, action: "getImageFileDescrTO"}, "error obtaining file information");
            if (res === undefined){
                $selectedImgTitle.val("");
                $selectedImgGps.val("");
                $selectedImgOrder.val("");
                return;
            }

            $selectedImgTitle.val(res.title);
            $selectedImgGps.val(res.gps);
            $selectedImgOrder.val(res.orderNumber);

        });

        $selectedImgDelete.unbind();
        $selectedImgDelete.click(await async function(){
            let id = $selectedImgElem.data("id");
            let jsonAdminResponse = await $.ajax({
                url: '/admin/jsonApi.jsp',
                method: 'POST',
                data: {data: id, guid: Cookies.get("guid"), action: "deleteFileEntity"}
            });

            let adminResponse = JSON.parse(jsonAdminResponse);

            if (!adminResponse.success){
                alert("error deleting file: "+adminResponse.errorDescription);
            } else {
                await self._refreshImageList(self, ops);
            }
        });

        $selectedImgUpdate.unbind();
        $selectedImgUpdate.click(await async function(){
            let id = $selectedImgElem.data("id");
            let title = $selectedImgTitle.val();
            let gps = $selectedImgGps.val();
            let orderNumber = $selectedImgOrder.val();

            if (!checkCoordinates(gps)){
                return;
            }

            let imageFileDescrTO = {
                imageEntityId: id,
                title: title,
                gps: gps,
                orderNumber: orderNumber
            };

            let jsonAdminResponse = await $.ajax({
                url: '/admin/jsonApi.jsp',
                method: 'POST',
                data: {data: JSON.stringify(imageFileDescrTO), guid: Cookies.get("guid"), action: "updateFileDescription"}
            });

            let adminResponse = JSON.parse(jsonAdminResponse);

            if (!adminResponse.success){
                alert("error updating file: "+adminResponse.errorDescription);
            } else {
                await self._refreshImageList(self, ops);
            }

        });
    },

    _display: async function(self, ops){

        let objectId = ops.objectId;
        let attributionClass = ops.attributionClass;

        let hTemplate = Handlebars.compile(this._template);

        let currentUserPrivilegeLevelName = Cookies.get("privilegeLevelName");
        let demoUser = currentUserPrivilegeLevelName === "demo";

        self.element.html(hTemplate({demoUser: demoUser}));
        await self._refreshImageList(self, ops);

        let $fileElem = self.element.find('[data-role="data-file"]');
        let $photoImageUploadElem = self.element.find('[data-role="data-image-upload"]');

        $photoImageUploadElem.unbind();
        $photoImageUploadElem.click(await async function(){

            let buttonHtml = $photoImageUploadElem.html();
            $photoImageUploadElem.html("<i class=\"fas fa-cog fa-spin\"></i>");
            $photoImageUploadElem.prop("disabled", true);

            let files = $fileElem.get(0).files;
            let fileData = new FormData();

            for (let t = 0; t < files.length; t++){
                fileData.append("fileInput"+t, files[t]);
            }

            let imageUploadTO = {
                imageAttributionClass: attributionClass,
                parentObjectId: objectId,
                sessionGuid: Cookies.get("guid")
            };

            let imageUploadTOJson = await toBase64(JSON.stringify(imageUploadTO));
            Cookies.set("imageUploadTOJson", imageUploadTOJson);

            await $.ajax({
                url: '/admin/fileUpload.jsp',
                type: 'post',
                data: fileData,
                contentType: false,
                processData: false
            });

            Cookies.remove("imageUploadTOJson");
            await self._refreshImageList(self, ops);

            $fileElem.val('');
            $photoImageUploadElem.prop("disabled", false);
            $photoImageUploadElem.html(buttonHtml);
        });
    }
});