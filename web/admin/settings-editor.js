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

$.widget('admin.settingsEditor', {
    _template: `<div class="item-container transparent width-full item-one-center">
    <span class="item-container-heading">Website Settings</span>
</div>

<div class="width-100vw flex-main">
    <div class="item-container transparent width-medium item-left">
        <span class="text">About:</span><br />
        
        <i class="fas fa-image button-fa-white-large" data-role="image-insert-button"></i><br />
        <textarea class="width-100-pc input-textarea-text-smaller" data-role="settings-about" disabled="disabled"></textarea><br />

        <span class="text">Header injection: (e.g. for google analytics)</span>
        <textarea class="width-100-pc font-size" rows="5" data-role="settings-header-injection" disabled="disabled"></textarea><br />
        
    </div>

    <div class="item-container transparent width-medium item-right">
    
        <span class="text">Image Storage Path:</span><br/><span class="smalltext">must point to imageStorage folder in the installation directory</span><br />
        <input type="text" class="form-control input width-100-pc" value="" data-role="settings-image-storage"  disabled="disabled"/><br />
        <span class="text">Temporary folder:</span><br/><span class="smalltext">for processing images</span><br />
        <input type="text" class="form-control input width-100-pc" value="" data-role="settings-image-tmp"  disabled="disabled"/>
        <br />
        
        <span class="text">Max. dimensions for preview images:</span><br/>
        <span class="smalltext">Recommended value for both dimensions: 1280</span>

        <div class="flex-main">
            <div class="small-item-left width-less-than-half-pc"><span class="smalltext">horizontal: </span><input type="text" class="form-control input" data-role="setting-image-preview-x"  disabled="disabled"/></div>
            <div class="small-item-right width-less-than-half-pc"><span class="smalltext">vertical: </span><input type="text" class="form-control input" data-role="setting-image-preview-y"  disabled="disabled"/></div>
        </div>

        <span class="text">Max. dimensions for thumbnail images:</span><br />
        <span class="smalltext">Recommended value for both dimensions: 800</span>

        <div class="flex-main">
            <div class="small-item-left width-less-than-half-pc"><span class="smalltext">horizontal:</span><input type="text" class="form-control input" data-role="setting-image-thumbnail-x"  disabled="disabled"/></div>
            <div class="small-item-right width-less-than-half-pc"><span class="smalltext">vertical:</span><input type="text" class="form-control input" data-role="setting-image-thumbnail-y"  disabled="disabled"/></div>
        </div>
        
        <span class="text">Note: changes in dimension settings will not affect already uploaded images.</span>
            
        <br />
        <hr class="hr-white">
        
        <div style="display: flex">
            <div data-role="check" disabled="disabled"></div>
            <span class="text" style="margin-top: 0 !important; margin-left: 5px;"> Show cookie warning message</span>
        </div>
        
        
        <span class="text">Cookie warning message text:</span><br />
        <textarea class="width-100-pc font-size" rows="5" data-role="settings-cookie-message" disabled="disabled"></textarea><br />

        <br />
        <hr class="hr-white">
        
        <span class="text">Twitter profile:</span><br />
        <input type="text" class="form-control input width-100-pc" value="" data-role="settings-twitter"  disabled="disabled"/><br />

        <span class="text">Facebook profile:</span><br />
        <input type="text" class="form-control input width-100-pc" value="" data-role="settings-facebook"  disabled="disabled"/><br />

        <span class="text">Instagram profile:</span><br />
        <input type="text" class="form-control input width-100-pc" value="" data-role="settings-instagram"  disabled="disabled"/><br />

        <span class="text">Pinterest profile:</span><br />
        <input type="text" class="form-control input width-100-pc" value="" data-role="settings-pinterest"  disabled="disabled"/><br />

        <button type="button" class="btn btn-light btn-std btn-vertical" data-role="settings-save"  disabled="disabled">Save</button>
    </div>
    <div data-role="imageselect-anchor"></div>
    
</div>`,

    _create: async function(){
        let self = this;
        await self._display(self);
    },
    _init: async function(){
        let self = this;
        await self._display(self);
    },
    _display: async function(self){
        self.element.html(self._template);

        let $imageInsertButton = self.element.find('[data-role="image-insert-button"]');
        let $aboutTextarea = self.element.find('[data-role="settings-about"]');
        let $headerInjection = self.element.find('[data-role="settings-header-injection"]');

        let $imageStoragePath = self.element.find('[data-role="settings-image-storage"]');
        let $tempPath =  self.element.find('[data-role="settings-image-tmp"]');

        let $previewX = self.element.find('[data-role="setting-image-preview-x"]');
        let $previewY = self.element.find('[data-role="setting-image-preview-y"]');
        let $thumbnailX = self.element.find('[data-role="setting-image-thumbnail-x"]');
        let $thumbnailY = self.element.find('[data-role="setting-image-thumbnail-y"]');
        let $check = self.element.find('[data-role="check"]');
        let $cookieTextarea = self.element.find('[data-role="settings-cookie-message"]');
        let $settingsTwitter = self.element.find('[data-role="settings-twitter"]');
        let $settingsFacebook = self.element.find('[data-role="settings-facebook"]');
        let $settingsInstagram = self.element.find('[data-role="settings-instagram"]');
        let $settingsPinterest = self.element.find('[data-role="settings-pinterest"]');
        let $saveSettings = self.element.find('[data-role="settings-save"]');
        let $modalAnchor = self.element.find('[data-role="imageselect-anchor"]');


        // load values
        let settingsTO = await ajax({guid: Cookies.get("guid"), action:"getSettingsAuthed"}, "cannot load settings");

        $aboutTextarea.val(settingsTO.about);
        $headerInjection.val(settingsTO.headerInjection);
        $imageStoragePath.val(settingsTO.imageStoragePath);
        $tempPath.val(settingsTO.temporaryFolderPath);
        $previewX.val(settingsTO.previewX);
        $previewY.val(settingsTO.previewY);
        $thumbnailX.val(settingsTO.thumbX);
        $thumbnailY.val(settingsTO.thumbY);
        $check.prop('checked', settingsTO.showCookieWarning);

        if (settingsTO.showCookieWarning){
            $cookieTextarea.prop("disabled", false);
        }

        $cookieTextarea.val(settingsTO.cookieWarningMessage);
        $settingsTwitter.val(settingsTO.twitterProfile);
        $settingsFacebook.val(settingsTO.facebookProfile);
        $settingsInstagram.val(settingsTO.instagramProfile);
        $settingsPinterest.val(settingsTO.pinterestProfile);

        $check.NoFuckingBullshitCheckbox();

        // enable edit for superusers, except for cookie warning text
        if (Cookies.get("privilegeLevelName") === "superuser"){
            $aboutTextarea.prop("disabled", false);
            $headerInjection.prop("disabled", false);
            $imageStoragePath.prop("disabled", false);
            $tempPath.prop("disabled", false);
            $previewX.prop("disabled", false);
            $previewY.prop("disabled", false);
            $thumbnailX.prop("disabled", false);
            $thumbnailY.prop("disabled", false);
            $check.removeAttr("disabled");
            $settingsTwitter.prop("disabled", false);
            $settingsFacebook.prop("disabled", false);
            $settingsInstagram.prop("disabled", false);
            $settingsPinterest.prop("disabled", false);
            $saveSettings.prop("disabled", false);

            // enable cookie warning text enable on checkbox
            $check.click(function(){

                if ($cookieTextarea.prop("disabled") === true) {
                    $cookieTextarea.prop("disabled", false);
                } else {
                    $cookieTextarea.prop("disabled", true);
                }
            });

            // activate image insert into about textarea
            $imageInsertButton.unbind();
            $imageInsertButton.click(await async function(){
                let currentText = $aboutTextarea.val();
                let selectedImgId = await imageSelect($modalAnchor, undefined);
                currentText = currentText + "\n[img id=" + selectedImgId + "]\n";
                $aboutTextarea.val(currentText);
            });

            // save values on click
            $saveSettings.click(await async function(){

                if (!isNumericOrEmpty($previewX.val()) || !isNumericOrEmpty($previewY.val()) || !isNumericOrEmpty($thumbnailX.val()) || !isNumericOrEmpty($thumbnailY.val())){
                    alert("Dimensions must only contain whole numbers!");
                    return;
                }

                let btnText = spinButton($saveSettings);

                let settingsTO = {
                    about: $aboutTextarea.val(),
                    headerInjection: $headerInjection.val(),
                    imageStoragePath: $imageStoragePath.val(),
                    temporaryFolderPath: $tempPath.val(),
                    previewX: $previewX.val(),
                    previewY: $previewY.val(),
                    thumbX: $thumbnailX.val(),
                    thumbY: $thumbnailY.val(),
                    showCookieWarning: $check.prop('checked'),
                    cookieWarningMessage: $cookieTextarea.val(),
                    twitterProfile: $settingsTwitter.val(),
                    facebookProfile: $settingsFacebook.val(),
                    instagramProfile: $settingsInstagram.val(),
                    pinterestProfile: $settingsPinterest.val()
                };

                let settingsTOStr = JSON.stringify(settingsTO);
                let res = await ajax({action: "saveSettings", guid: Cookies.get("guid"), data: settingsTOStr}, "error saving settings");

                if (res !== undefined){
                    self._display(self);
                }

                unSpinButton($saveSettings, btnText);

            });
        }
    }
});