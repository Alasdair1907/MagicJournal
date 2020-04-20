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

$.widget("admin.authorProfile", {

    _template: `<div>
    <div class="item-container transparent width-full item-one-center">
        <span class="item-container-heading">User Profile Editor</span>
    </div>

    <div class="width-100vw flex-main">
        <div class="item-container transparent width-medium item-left">
            <span class="text">Profile Picture:</span><br />
            <img src="/getImage.jsp?filename=imgAdminPlaceholder.png" alt="Profile Picture" class="photo-edit-image" data-role="profile-picture"><br />
    
            <span class="text">Upload image:</span><br />
            <input type="file" class="text" data-role="data-file"/><br />
            <button type="button" class="btn btn-light btn-std btn-vertical" data-role="profile-photo-upload-image">Upload</button>
        </div>
    
        <div class="item-container transparent width-medium item-right">
            <span class="text">Short Bio: (max. 1024)</span><br />
            <textarea class="input-textarea-description width-100-pc" maxlength="1000" rows="20" data-role="profile-bio"></textarea><br />
    
            <span class="text">Public email:</span><br />
            <input type="text" class="form-control input width-100-pc" data-role="profile-email"><br />
    
            <span class="text">Personal website:</span><br />
            <input type="text" class="form-control input width-100-pc" data-role="profile-website"><br />
    
            <button type="button" class="btn btn-light btn-std btn-vertical" data-role="profile-save">Save</button><br /><br/>
        </div>
    </div>
</div>
    `,

    _create: function(){
        let self = this;
        self._display(self);
    },
    _init: function(){
        let self = this;
        self._display(self);
    },
    _display: async function(self){
        let guid = Cookies.get("guid");

        let authorVO = await ajax({guid: guid, action: "getAuthorVOByGuid"}, "error loading author info");

        if (authorVO === undefined){
            return;
        }

        self.element.html(self._template);

        let $profilePictureImg = self.element.find('[data-role="profile-picture"]');
        let $profilePictureInput = self.element.find('[data-role="data-file"]');
        let $updateImageButton = self.element.find('[data-role="profile-photo-upload-image"]');

        let $profileBioTextarea = self.element.find('[data-role="profile-bio"]');
        let $profileEmailInput = self.element.find('[data-role="profile-email"]');
        let $profileWebsiteInput = self.element.find('[data-role="profile-website"]');
        let $profileSaveButton = self.element.find('[data-role="profile-save"]');

        if (authorVO.pictureFileName) {
            $profilePictureImg.attr("src", "/getImage.jsp?filename=" + authorVO.pictureFileName);
        }

        $profileBioTextarea.val(authorVO.bio);
        $profileEmailInput.val(authorVO.email);
        $profileWebsiteInput.val(authorVO.website);

        $updateImageButton.unbind();
        $updateImageButton.click(await async function(){

            let buttonText = spinButton($updateImageButton);

            let file = $profilePictureInput.get(0).files[0];
            let fileData = new FormData();
            fileData.append("fileInput", file);

            let imageUploadTO = {
                imageAttributionClass: 3,
                parentObjectId: authorVO.id,
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

            Cookies.remove("imageUploadTOJson");
            // refresh the picture

            let authorVOUpdate = await ajax({guid: guid, action: "getAuthorVOByGuid"}, "error loading author info");
            if (authorVOUpdate.pictureFileName) {
                $profilePictureImg.attr("src", "/getImage.jsp?filename=" + authorVOUpdate.pictureFileName);
            }

            unSpinButton($updateImageButton, buttonText);
        });

        $profileSaveButton.unbind();
        $profileSaveButton.click(await async function(){

            let buttonText = spinButton($profileSaveButton);

            let authorVOPartial = {
                id: authorVO.id,
                login: authorVO.login,
                bio: $profileBioTextarea.val(),
                email: $profileEmailInput.val(),
                website: $profileWebsiteInput.val()
            };


            let res = await ajax({guid: Cookies.get("guid"), action: "updateAuthorProfile", data: JSON.stringify(authorVOPartial)}, "error updating author profile");
            if (res !== undefined){
                self._display(self);
            }

            unSpinButton($profileSaveButton, buttonText);
        });
    }
});