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
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-image-upload" {{#if testUser}}disabled="disabled"{{/if}}>Upload</button>
</td>

</tr>

<tr>
<td class="td-tall"><span class="text">Image Title:</span></td><td class="td-tall"><input type="text" data-role="selected-img-title" class="input width-full" disabled="disabled"></td>
</tr>

<tr>
<td class="td-tall"><span class="text">GPS coordinates:</span></td><td class="td-tall"><input type="text" data-role="selected-img-gps" class="input width-full" disabled="disabled"></td>
</tr>

<tr>
<td class="td-tall"><button type="button" class="btn btn-danger btn-std" data-role="selected-img-delete" disabled="disabled">Delete Image</button> </td>
<td class="td-tall"><button type="button" class="btn btn-light btn-std" data-role="selected-img-update" disabled="disabled">Save Changes</button> </td></tr>
</table>
</td>

<td>
<div data-role="selected-img" style="img-select-placeholder">


</div>
</td>

</tr>
</table>

<br />
<div data-role="image-list"></div>
</div>
        `,

    _imageListTemplate: `
    <div></div>
    {{#each imageVOList}}
    <div data-id="{{this.thisObjId}}" data-preview="{{this.preview}}" data-role="gallery-images" style="background-image:url('/getImage.jsp?filename={{this.thumbnail}}'); width: 15vw; height: 15vw; background-position: center; background-size: cover; display: inline-block;">&nbsp;</div>
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

        let hImageListTemplate = Handlebars.compile(this._imageListTemplate);
        $imageListElem.html(hImageListTemplate({imageVOList: imageVOList}));

        let $galleryImages = self.element.find('[data-role="gallery-images"]');
        let $selectedImgDiv = self.element.find('[data-role="selected-img"]');

        let $selectedImgTitle = self.element.find('[data-role="selected-img-title"]');
        let $selectedImgGps = self.element.find('[data-role="selected-img-gps"]');
        let $selectedImgDelete = self.element.find('[data-role="selected-img-delete"]');
        let $selectedImgUpdate = self.element.find('[data-role="selected-img-update"]');

        $galleryImages.unbind();
        $galleryImages.click(await async function () {

            $galleryImages.removeClass("div-image-selected");
            $selectedImgDiv.html("");

            let imageId = $(this).data("id");
            let previewFile = $(this).data("preview");
            $(this).addClass("div-image-selected");

            $selectedImgDiv.html('<img src="/getImage.jsp?filename='+previewFile+'" class="selected-img">');

            $selectedImgDiv.data("id", imageId);

            $selectedImgTitle.prop("disabled", false);
            $selectedImgGps.prop("disabled", false);
            $selectedImgDelete.prop("disabled", false);
            $selectedImgUpdate.prop("disabled", false);

            let jsonAdminResponse = await $.ajax({
                url: '/admin/jsonApi.jsp',
                method: 'POST',
                data: {data: imageId, action: "getImageFileDescrTO"}
            });

            let adminResponse = JSON.parse(jsonAdminResponse);

            if (!adminResponse.success){
                alert("error obtaining file information: "+adminResponse.errorDescription);
            } else {
               $selectedImgTitle.val(adminResponse.data.title);
               $selectedImgGps.val(adminResponse.data.gps);
            }

        });

        $selectedImgDelete.unbind();
        $selectedImgDelete.click(await async function(){
            let id = $selectedImgDiv.data("id");
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
            let id = $selectedImgDiv.data("id");
            let title = $selectedImgTitle.val();
            let gps = $selectedImgGps.val();

            let imageFileDescrTO = {
                imageEntityId: id,
                title: title,
                gps: gps
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
        let testUser = currentUserPrivilegeLevelName === "test";

        self.element.html(hTemplate({testUser: testUser}));
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

            let imageUploadTOJson = btoa(JSON.stringify(imageUploadTO));
            Cookies.set("imageUploadTOJson", imageUploadTOJson);

            await $.ajax({
                url: '/admin/fileUpload.jsp',
                type: 'post',
                data: fileData,
                contentType: false,
                processData: false
            });

            await self._refreshImageList(self, ops);

            $fileElem.val('');
            $photoImageUploadElem.prop("disabled", false);
            $photoImageUploadElem.html(buttonHtml);
        });
    }
});