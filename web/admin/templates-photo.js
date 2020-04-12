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

let photoEditSelect = `
<div class="item-container transparent width-full">
<span class="item-container-heading">Existing Photo Posts</span>
<table class="width-100-pc">

<tr class="list-tr">
<td class="list-entry center-text" style="width: 35%;"><span class="text bold">Info</span></td>
<td class="list-entry center-text" style="width: 15%;"><span class="text bold">Image</span></td>
<td class="list-entry center-text" style="width: 20%;"><span class="text bold">Status</span></td>
<td class="list-entry center-text" style="width: 30%;"><span class="text bold">Action</span></td>
</tr>

{{#each photoPosts}}
<tr class="list-tr">

<td class="list-entry structure">
<span class="text">Title: {{this.title}}</span>
<span class="text">Author: {{this.authorVO.displayName}}</span>
<span class="text">Created: {{this.creationDateStr}}</span>
</td>

<td class="list-entry"><img src="/getImage.jsp?filename={{this.imageVO.thumbnail}}" alt="[no image]" class="photo-entry"></td>

<td class="list-entry center-text">
{{#if this.published}}
<span class="text">published</span>
{{else}}
<span class="text">NOT published</span>
{{/if}}
<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-role="photo-publish-toggle" {{#if testUser}}disabled="disabled"{{/if}}>Toggle</button>
</td>

<td class="list-entry center-text">

<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-role="photo-post-edit">Edit</button>
<button type="button" class="btn btn-danger btn-std" data-id="{{this.id}}" data-role="photo-post-delete">Delete</button>

</td>
</tr>
{{/each}}
</table>

<button type="button" class="btn btn-light btn-std btn-vertical" data-id="{{this.id}}" data-role="photo-post-new">Create new photo</button>
</div>

<div data-role="photo-post-new-edit" class="width-100-pc flex-main"></div>
`;

let photoNewOrEdit = `
<div class="item-container transparent highres-line-item width-medium item-left">
<span class="item-container-heading">Edit</span>

<input type="hidden" data-role="data-id" value="{{photoVO.id}}">

<span class="text">Title:</span>
<input type="text" class="form-control input width-100-pc" data-role="data-title" value="{{photoVO.title}}"><br/>

<span class="text">Description:</span><br />
<textarea class="input-textarea-description width-100-pc" maxlength="1000" rows="20" data-role="data-description">{{photoVO.description}}</textarea><br />

<span class="text">GPS coordinates:</span>
<input type="text" class="form-control input width-100-pc" data-role="data-gps-coordinates" value="{{photoVO.gpsCoordinates}}"><br />

<span class="text">Image:</span><br />
<img src="/getImage.jsp?filename={{photoVO.imageVO.preview}}" class="photo-edit-image" data-role="photo-image"><br />

<span class="text">Upload image:</span><br />
<input type="file" class="text" data-role="data-file"/><br />
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-photo-upload-image" {{#if testUser}}disabled="disabled"{{/if}}>Upload</button>
<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-photo-save-or-update" {{#if testUser}}disabled="disabled"{{/if}}>Save</button>
</div>


<div class="highres-line-item width-medium item-right">
    <div data-role="photo-tag-editor"></div>
    <div data-role="photo-relation-manager"></div>
</div>

`;

/** extends photoEditSelect **/
let confirmDelete = `
<div class="modal" data-role="delete-photo-confirm">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Confirm Deletion</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span>This will permanently delete the photo. Continue?</span>
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-danger btn-std" data-role="delete-confirm" {{#if testUser}}disabled="disabled"{{/if}}>Yes, delete!</button>
      <button type="button" class="btn btn-primary btn-std" data-dismiss="modal" {{#if testUser}}disabled="disabled"{{/if}}>No</button>
    </div>
    
  </div>
</div>
</div>
`;

photoEditSelect += confirmDelete;