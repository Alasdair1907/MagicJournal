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

let fileSelect = async function($modalAnchor){
    $modalAnchor.html(fileSelectModal);

    let selectedId = null;

    let $modal = $modalAnchor.find('[data-role="file-select-modal"]');
    $modal.modal();

    let $fileManagerMain = $modalAnchor.find('[data-role="file-manager-main"]');

    let $selectFileButton = $modalAnchor.find('[data-role="select-a-file"]');
    let $uploadNewFile = $modalAnchor.find('[data-role="upload-new-file"]');

    $selectFileButton.unbind();
    $selectFileButton.click(await async function(){
        let otherFileVOs = await ajax({action: "listOtherFiles"});
        if (otherFileVOs === undefined){
            return;
        }

        let hFileListingTemplate = Handlebars.compile(fileListingTemplate);
        $fileManagerMain.html(hFileListingTemplate({otherFileVOList: otherFileVOs}));

        let $deleteButton = $fileManagerMain.find('[data-role="file-delete"]');
        let $insertButton = $fileManagerMain.find('[data-role="image-insert"]');

        $insertButton.click(function(){
            $fileManagerMain.html("");
            $modal.modal('hide');
            selectedId = $(this).data('id');
        });

        $deleteButton.click(await async function(){

            let $confirmDeleteAnchor = $fileManagerMain.find('[data-role="confirm-delete-anchor"]');
            $confirmDeleteAnchor.html(confirmDeleteFile);

            let $confirmDeleteModal = $confirmDeleteAnchor.find('[data-role="delete-file-confirm"]');
            $confirmDeleteModal.modal();

            let $deleteConfirmButton = $confirmDeleteAnchor.find('[data-role="file-delete-confirm"]');
            $deleteConfirmButton.unbind();
            $deleteConfirmButton.click(await async function(){
                let txt = spinButton($deleteConfirmButton);
                let res = await ajax({guid: Cookies.get("guid"), data: $deleteButton.data("id"), action: "deleteOtherFile"});
                unSpinButton($deleteConfirmButton, txt);
                if (res !== undefined){
                    $confirmDeleteModal.modal('hide');
                    $confirmDeleteAnchor.html('');
                    $selectFileButton.click();
                }
            });

            let $noDeleteButton = $confirmDeleteAnchor.find('[data-role="file-delete-no"]');
            $noDeleteButton.unbind();
            $noDeleteButton.click(function(){
                $confirmDeleteModal.modal('hide');
                $confirmDeleteAnchor.html('');
                $selectFileButton.click();
            });
        });
    });

    $uploadNewFile.unbind();
    $uploadNewFile.click(await async function(){
        $fileManagerMain.html(uploadNewfile);

        let $displayNameInput = $fileManagerMain.find('[data-role="file-displayname"]');
        let $descriptionTextarea = $fileManagerMain.find('[data-role="file-description"]');
        let $fileInput = $fileManagerMain.find('[data-role="data-file"]');
        let $uploadAndSaveButton = $fileManagerMain.find('[data-role="data-upload"]');

        $uploadAndSaveButton.unbind();
        $uploadAndSaveButton.click(await async function(){

            let buttonText = spinButton($uploadAndSaveButton);

            let fileDisplayName = $displayNameInput.val();
            let fileDescription = $descriptionTextarea.val();

            if (!fileDisplayName ){
                alert("Enter file name");
                unSpinButton($uploadAndSaveButton, buttonText);
                return;
            }

            let file = $fileInput.get(0).files[0];
            let fileData = new FormData();
            fileData.append("fileInput", file);

            let otherFileTO = {
                guid: Cookies.get("guid"),
                name: fileDisplayName,
                description: fileDescription
            };

            let otherFileTOJson = btoa(JSON.stringify(otherFileTO));
            Cookies.set("otherFileTOJson", otherFileTOJson);

            let res = await $.ajax({
                url: '/admin/fileUpload.jsp',
                type: 'post',
                data: fileData,
                contentType: false,
                processData: false
            });

            Cookies.remove("imageUploadTOJson");
            unSpinButton($uploadAndSaveButton, buttonText);

            if (res.includes("ok")){
                $selectFileButton.click();
            } else {
                alert("Error uploading file.");
            }
        });
    });

    $selectFileButton.click();

    while (selectedId === null) {
        await new Promise(r => setTimeout(r, 50));
        if (selectedId) {
            return selectedId;
        }
    }

};


let fileSelectModal = `

<div class="modal" data-role="file-select-modal">
    <div class="modal-dialog modal-window-huge">
        <div class="modal-content">

            <!-- Modal Header -->
            <div class="modal-header">
                <span class="modal-h1">File Manager</span>
                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>

            <!-- Modal body -->
            <div class="modal-body">
                <span class="modal-title">Select action</span><br />

                <button class="btn btn-secondary btn-std btn-vertical" data-role="select-a-file">Select a file to insert</button>
                <button class="btn btn-secondary btn-std btn-vertical" data-role="upload-new-file">Upload a new file</button>

                <hr class="hr-black">
                
                <div data-role="file-manager-main"></div>

            </div>

            <!-- Modal footer -->
            <div class="modal-footer">
                <button type="button" class="btn btn-primary btn-std" data-dismiss="modal">Cancel</button>
            </div>

        </div>
    </div>
</div>
`;

let uploadNewfile = `
<span class="modal-title">File display name:</span><br />
<input type="text" class="form-control input width-medium" data-role="file-displayname"><br />

<span class="modal-title">File description: (max. 256 chars)</span><br />
<textarea class="width-medium form-control font-size" rows="5" maxlength="256" data-role="file-description"></textarea><br />

<span class="modal-title">File:</span>
<input type="file" class="text-black" data-role="data-file"/><br />

<button type="button" class="btn btn-success btn-std btn-vertical" data-role="data-upload">Upload and Save</button>
`;

let fileListingTemplate = `
<table class="width-100-pc">
    <tr>
        <td class="table-lgray table-lgray-heading">File Display Name</td>
        <td class="table-lgray table-lgray-heading">Original File Name</td>
        <td class="table-lgray table-lgray-heading">Author</td>
        <td class="table-lgray table-lgray-heading">Actions</td>
    </tr>

    {{#each otherFileVOList}}
    <tr>
        <td class="table-lgray">{{this.displayName}}</td>
        <td class="table-lgray">{{this.originalFileName}}</td>
        <td class="table-lgray">{{this.authorVO.displayName}}</td>
        <td class="table-lgray center-text">
            <button type="button" class="btn btn-primary btn-std btn-vertical" onclick="location.href='/getFile.jsp?id={{this.fileId}}'">Download</button>
            <button type="button" class="btn btn-danger btn-std btn-vertical" data-role="file-delete" data-id="{{this.fileId}}">Delete</button>
            <button type="button" class="btn btn-success btn-std" data-role="image-insert" data-id="{{this.fileId}}">Insert</button>
        </td>
    </tr>
    {{/each}}
</table>

<div data-role="confirm-delete-anchor"></div>
`;

let confirmDeleteFile = `
<div class="modal" data-role="delete-file-confirm">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Confirm Deletion</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span>This will permanently delete the file. Continue?</span>
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-danger btn-std" data-role="file-delete-confirm">Yes, delete!</button>
      <button type="button" class="btn btn-primary btn-std" data-role="file-delete-no">No</button>
    </div>
    
  </div>
</div>
</div>
`;