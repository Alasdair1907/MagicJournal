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

let galleryEditSelect = `
<div class="item-container transparent width-full">
<span class="item-container-heading">Existing Galleries</span>
<table class="width-100-pc">

<tr class="list-tr">
<td class="list-entry center-text" style="width: 25%;"><span class="text bold">Info</span></td>
<td class="list-entry center-text" style="width: 40%;"><span class="text bold">Preview</span></td>
<td class="list-entry center-text" style="width: 15%;"><span class="text bold">Status</span></td>
<td class="list-entry center-text" style="width: 20%;"><span class="text bold">Action</span></td>
</tr>

{{#each galleryVOs}}
<tr class="list-tr">

<td class="list-entry structure">
<span class="text">Title: {{this.title}}</span>
<span class="text">Author: {{this.authorVO.displayName}}</span>
<span class="text">Created: {{this.creationDateStr}}</span>
</td>

<td class="list-entry center-text">
{{#each this.galleryRepresentation}}
<img src="/getImage.jsp?filename={{this.thumbnail}}" class="gallery-image-list-item" >
{{/each}}
</td>

<td class="list-entry center-text">
{{#if this.published}}
<span class="text">published</span>
{{else}}
<span class="text">NOT published</span>
{{/if}}

<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-role="gallery-publish-toggle" {{#if testUser}}disabled="disabled"{{/if}}>Toggle</button>
</td>

<td class="list-entry center-text">

<button type="button" class="btn btn-light btn-std" data-id="{{this.id}}" data-role="gallery-edit">Edit</button>
<button type="button" class="btn btn-danger btn-std" data-id="{{this.id}}" data-role="gallery-delete">Delete</button>

</td>
</tr>

{{/each}}
</table>

<button type="button" class="btn btn-light btn-std btn-vertical" data-id="{{this.id}}" data-role="gallery-new">Create new gallery</button>
</div>

<div data-role="gallery-new-edit"></div>
`;

/** extends photoEditSelect **/
let confirmDeleteGallery = `
<div class="modal" data-role="delete-gallery-confirm">
<div class="modal-dialog">
  <div class="modal-content">
  
    <!-- Modal Header -->
    <div class="modal-header">
      <h4 class="modal-title">Confirm Deletion</h4>
      <button type="button" class="close" data-dismiss="modal">&times;</button>
    </div>
    
    <!-- Modal body -->
    <div class="modal-body">
      <span>This will permanently delete the gallery and all its photos. Continue?</span>
    </div>
    
    <!-- Modal footer -->
    <div class="modal-footer">
      <button type="button" class="btn btn-danger btn-std" data-role="delete-confirm" {{#if testUser}}disabled="disabled"{{/if}}>Yes, delete it all!</button>
      <button type="button" class="btn btn-primary btn-std" data-dismiss="modal" {{#if testUser}}disabled="disabled"{{/if}}>No</button>
    </div>
    
  </div>
</div>
</div>
`;

galleryEditSelect += confirmDeleteGallery;


let galleryNewOrEdit = `
<div>

<div class="item-container transparent width-medium highres-line-item">
<span class="item-container-heading">Edit</span>

<input type="hidden" data-role="data-id" value="{{galleryVO.id}}">

<span class="text">Title:</span>
<input type="text" class="form-control input width-100-pc" data-role="data-title" value="{{galleryVO.title}}"><br/>

<span class="text">Description:</span><br />
<textarea class="input-textarea-description width-100-pc" maxlength="1000" rows="20" data-role="data-description">{{galleryVO.description}}</textarea><br />

<span class="text">GPS coordinates:</span>
<input type="text" class="form-control input width-100-pc" data-role="data-gps-coordinates" value="{{galleryVO.gpsCoordinates}}"><br />

<button type="button" class="btn btn-light btn-std btn-vertical" data-role="data-gallery-save-or-update" {{#if testUser}}disabled="disabled"{{/if}}>Save</button>
</div>

<div class="highres-line-item width-medium">
    <div data-role="gallery-tag-editor"></div>
    <div data-role="gallery-relation-editor"></div>
</div>


</div>

<div data-role="gallery-image-manager" class="width-full"></div>
`;
